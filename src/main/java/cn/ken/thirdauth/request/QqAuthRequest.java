package cn.ken.thirdauth.request;

import cn.ken.thirdauth.cache.AuthStateCache;
import cn.ken.thirdauth.cache.DefaultAuthStateCache;
import cn.ken.thirdauth.config.AuthConstant;
import cn.ken.thirdauth.config.AuthPlatformConfig;
import cn.ken.thirdauth.config.AuthPlatformInfo;
import cn.ken.thirdauth.enums.AuthExceptionCode;
import cn.ken.thirdauth.exception.AuthException;
import cn.ken.thirdauth.model.AuthResponse;
import cn.ken.thirdauth.model.AuthToken;
import cn.ken.thirdauth.model.AuthUserInfo;
import cn.ken.thirdauth.util.HttpClientUtil;
import cn.ken.thirdauth.util.UrlBuilder;
import cn.ken.thirdauth.util.UserInfoUtil;
import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * <pre>
 * QQ授权请求: <a href="https://wiki.connect.qq.com/">官方文档</a>
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/17 14:28
 */
public class QqAuthRequest extends DefaultAuthRequest {

    public QqAuthRequest(AuthPlatformConfig config) {
        super(AuthPlatformInfo.QQ, config, DefaultAuthStateCache.INSTANCE);
    }

    public QqAuthRequest(AuthPlatformConfig config, AuthStateCache cache) {
        super(AuthPlatformInfo.QQ, config, cache);
    }

    @Override
    protected UrlBuilder accessTokenUrlBuilder(String accessToken) {
        String openId = getOpenId(accessToken);
        return UrlBuilder.fromBaseUrl(source.userInfo())
                .add(AuthConstant.ACCESS_TOKEN, accessToken)
                .add("oauth_consumer_key", config.getClientId())
                .add("openid", openId);
    }

    @Override
    public AuthResponse<AuthUserInfo> getUserInfo(String accessToken) throws AuthException {
        String openId = getOpenId(accessToken);
        String url = UrlBuilder.fromBaseUrl(source.userInfo())
                .add(AuthConstant.ACCESS_TOKEN, accessToken)
                .add("oauth_consumer_key", config.getClientId())
                .add("openid", openId)
                .build();
        String response = HttpClientUtil.doGet(url);
        Map<String, String> map = HttpClientUtil.parseResponseEntityJson(response);
        if (Integer.parseInt(map.get("ret")) != 0) {
            throw new AuthException(map.get("msg"));
        }
        String avatar = map.get("figureurl_qq_2");
        if (avatar == null || avatar.isEmpty()) {
            avatar = map.get("figureurl_qq_1");
        }
        String location = String.format("%s-%s", map.get("province"), map.get("city"));
        AuthUserInfo userInfo = AuthUserInfo.builder()
                .rawUserInfo(JSON.parseObject(response))
                .token(accessToken)
                .username(map.get("nickname"))
                .nickname(map.get("nickname"))
                .avatar(avatar)
                .location(location)
                .uuid(openId)
                .gender(UserInfoUtil.getRealGender(map.get("gender")).getCode())
                .source(source.toString())
                .build();
        return new AuthResponse<AuthUserInfo>().exceptionStatus(AuthExceptionCode.SUCCESS, userInfo);
    }
    
    @Override
    public AuthResponse<AuthToken> refresh(AuthToken authToken) {
        String url = UrlBuilder.fromBaseUrl(source.refresh())
                .add(AuthConstant.GRANT_TYPE, AuthConstant.GrantType.REFRESH)
                .add(AuthConstant.CLIENT_ID, config.getClientId())
                .add(AuthConstant.CLIENT_SECRET, config.getClientSecret())
                .add(AuthConstant.REFRESH_TOKEN, authToken.getRefreshToken())
                .add(AuthConstant.REDIRECT_URI, config.getRedirectUri())
                .build();
        String response = HttpClientUtil.doGet(url);
        Map<String, String> map = HttpClientUtil.parseResponseEntity(response);
        if (!map.containsKey(AuthConstant.ACCESS_TOKEN) || map.containsKey(AuthConstant.CODE)) {
            throw new AuthException(map.get("msg"));
        }
        AuthToken newAuthToken = AuthToken.builder()
                .accessToken(map.get(AuthConstant.ACCESS_TOKEN))
                .expireIn(Integer.parseInt(map.getOrDefault(AuthConstant.EXPIRE, "0")))
                .refreshToken(map.get(AuthConstant.REFRESH_TOKEN))
                .build();
        return new AuthResponse<>(AuthExceptionCode.SUCCESS.getCode(), AuthExceptionCode.SUCCESS.getMsg(), newAuthToken);
    }

    private String getOpenId(String accessToken) throws AuthException {
        String url = UrlBuilder.fromBaseUrl(source.openId()).add(AuthConstant.ACCESS_TOKEN, accessToken).add("fmt", "json").build();
        String response = HttpClientUtil.doGet(url);
        Map<String, String> map = HttpClientUtil.parseResponseEntityJson(response);
        if (map.containsKey("code")) {
            throw new AuthException(map.get("msg"));
        }
        return map.get("openid");
    }
}
