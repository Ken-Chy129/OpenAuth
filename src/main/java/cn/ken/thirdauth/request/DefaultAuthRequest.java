package cn.ken.thirdauth.request;

import cn.ken.thirdauth.cache.AuthStateCache;
import cn.ken.thirdauth.cache.DefaultAuthStateCache;
import cn.ken.thirdauth.config.AuthConstant;
import cn.ken.thirdauth.config.AuthPlatformConfig;
import cn.ken.thirdauth.config.AuthUrls;
import cn.ken.thirdauth.enums.AuthExceptionCode;
import cn.ken.thirdauth.exception.AuthException;
import cn.ken.thirdauth.model.AuthCallback;
import cn.ken.thirdauth.model.AuthResponse;
import cn.ken.thirdauth.model.AuthToken;
import cn.ken.thirdauth.model.AuthUserInfo;
import cn.ken.thirdauth.util.HttpClientUtil;
import cn.ken.thirdauth.util.UrlBuilder;
import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.UUID;

/**
 * <pre>
 * 默认授权类
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 18:50
 */
public abstract class DefaultAuthRequest implements AuthRequest {
    
    protected final AuthUrls source;

    protected final AuthPlatformConfig config;

    protected final AuthStateCache cache;

    public DefaultAuthRequest(AuthPlatformConfig config, AuthUrls source) {
        this(source, config, DefaultAuthStateCache.INSTANCE);
    }

    public DefaultAuthRequest(AuthUrls source, AuthPlatformConfig config, AuthStateCache cache) {
        this.source = source;
        this.config = config;
        this.cache = cache;
    }

    @Override
    public String authorizeUrl() {
        return authorizeUrl(null);
    }

    @Override
    public String authorizeUrl(String authorizer) {
        return UrlBuilder.fromBaseUrl(source.authorize())
                .add(AuthConstant.RESPONSE_TYPE, "code")
                .add(AuthConstant.CLIENT_ID, config.getClientId())
                .add(AuthConstant.REDIRECT_URI, config.getRedirectUri())
                .add(AuthConstant.STATE, authorizer != null ? generateAndPutState(authorizer) : null)
                .build();
    }

    /**
     * 生成随机字符串作为state，并与userId关联保存在缓存中<br>
     * 如果不使用state则不需要重写
     *
     * @param authorizer 授权者唯一标识
     * @return 随机字符串state
     */
    protected String generateAndPutState(String authorizer) {
        String state = UUID.randomUUID().toString();
        cache.set(authorizer, state);
        return state;
    }

    /**
     * 根据userId校验state是否合法，即判断是否是当前用户执行的操作，如果不合法则抛出异常
     *
     * @param authorizer 当前操作的用户的唯一标识
     * @param state      调用该操作时传入的身份证明，可以在开放平台确认授权后的回调地址参数中拿到
     */
    protected void checkState(String authorizer, String state) {
        if (state == null || state.isBlank() || cache.get(authorizer).equals(state)) {
            throw new AuthException(AuthExceptionCode.ILLEGAL_STATUS);
        }
    }

    @Override
    public AuthResponse<AuthToken> getAccessToken(AuthCallback callback) {
        return getAccessToken(null, callback);
    }

    @Override
    public AuthResponse<AuthToken> getAccessToken(String authorizer, AuthCallback callback) {
        try {
            // 如果传入了授权者，则表明需要校验state
            if (authorizer != null) {
                checkState(authorizer, callback.getState());
            }
            // 根据不同平台的参数需求不同，可选择重写生成请求路径的策略
            String url = accessTokenUrlBuilder(callback.getCode()).build();
            String response = HttpClientUtil.doGet(url);
            Map<String, String> responseMap = HttpClientUtil.parseResponseEntity(response);
            // 请求发送错误时不同平台响应不同，故委托给使用者实现，如果有错误则抛出异常
            dealWithResponseException(responseMap);
            // 请求成功则对响应结果进行封装
            AuthToken authToken = setAuthToken(responseMap);
            return new AuthResponse<AuthToken>().exceptionStatus(AuthExceptionCode.SUCCESS, authToken);
        } catch (AuthException e) {
            return new AuthResponse<>(e.getCode(), e.getMsg(), null);
        }
    }

    @Override
    public AuthResponse<AuthUserInfo> getUserInfo(String accessToken) {
        try {
            String response;
            if (setUserInfoHeaders(accessToken) == null) {
                response = HttpClientUtil.doGet(userInfoUrlBuilder(accessToken).build());
            } else {
                response = HttpClientUtil.doGetWithHeaders(userInfoUrlBuilder(accessToken).build(), setUserInfoHeaders(accessToken));
            }
            Map<String, String> responseMap = HttpClientUtil.parseResponseEntityJson(response);
            dealWithResponseException(responseMap);
            AuthUserInfo authUserInfo = setUserInfo(responseMap);
            authUserInfo.setRawUserInfo(JSON.parseObject(response));
            authUserInfo.setToken(accessToken);
            return new AuthResponse<>(AuthExceptionCode.SUCCESS.getCode(), AuthExceptionCode.SUCCESS.getMsg(), authUserInfo);
        } catch (AuthException e) {
            return new AuthResponse<>(e.getCode(), e.getMsg(), null);
        }
    }

    protected void dealWithResponseException(Map<String, String> responseMap) {
        if (responseMap.containsKey("error")) {
            throw new AuthException(responseMap.get("error_description"));
        }
    }

    /**
     * 通过授权码生成请求令牌的url
     *
     * @param code 授权码
     * @return 请求令牌的url
     */
    protected UrlBuilder accessTokenUrlBuilder(String code) {
        return UrlBuilder.fromBaseUrl(source.accessToken())
                .add(AuthConstant.CLIENT_ID, config.getClientId())
                .add(AuthConstant.CLIENT_SECRET, config.getClientSecret())
                .add(AuthConstant.CODE, code)
                .add(AuthConstant.REDIRECT_URI, config.getRedirectUri());
    }

    protected AuthToken setAuthToken(Map<String, String> responseMap) {
        return AuthToken.builder()
                .accessToken(responseMap.get(AuthConstant.ACCESS_TOKEN))
                .refreshToken(responseMap.get(AuthConstant.REFRESH_TOKEN))
                .expireIn(Integer.parseInt(responseMap.get(AuthConstant.EXPIRE))).build();
    }

    /**
     * 通过访问令牌生成请求用户信息的url
     *
     * @param accessToken 访问令牌
     * @return 请求用户信息的url
     */
    protected UrlBuilder userInfoUrlBuilder(String accessToken) {
        return UrlBuilder.fromBaseUrl(source.userInfo()).add(AuthConstant.ACCESS_TOKEN, accessToken);
    }

    /**
     * 请求用户信息时携带的请求头，默认访问令牌拼接在请求路径中，请求头为空
     *
     * @param accessToken 访问令牌
     * @return 发送获取用户信息请求时携带的请求头
     */
    protected Map<String, String> setUserInfoHeaders(String accessToken) {
        return null;
    }

    /**
     * 根据不同开放平台响应结果的不同封装AuthUserInfo
     *
     * @param responseMap 原始响应体的键值对
     * @return 封装后的AuthUserInfo对象
     */
    protected AuthUserInfo setUserInfo(Map<String, String> responseMap) {
        throw new AuthException(AuthExceptionCode.UNSUPPORTED);
    }

}
