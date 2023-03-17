package cn.ken.auth.request;

import cn.ken.auth.cache.AuthStateCache;
import cn.ken.auth.cache.DefaultAuthStateCache;
import cn.ken.auth.config.AuthConstant;
import cn.ken.auth.config.AuthPlatformConfig;
import cn.ken.auth.config.AuthPlatformInfo;
import cn.ken.auth.enums.AuthExceptionCode;
import cn.ken.auth.exception.AuthException;
import cn.ken.auth.model.AuthCallback;
import cn.ken.auth.model.AuthResponse;
import cn.ken.auth.model.AuthToken;
import cn.ken.auth.model.AuthUserInfo;
import cn.ken.auth.util.HttpClientUtil;
import cn.ken.auth.util.UrlBuilder;
import cn.ken.auth.util.UserInfoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * <pre>
 *
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
    public void checkState(String state) {
        
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback callback) throws AuthException {
        String url = UrlBuilder.fromBaseUrl(source.accessToken())
                .add(AuthConstant.GRANT_TYPE, AuthConstant.GrantType.ACCESS)
                .add(AuthConstant.CLIENT_ID, config.getClientId())
                .add(AuthConstant.CLIENT_SECRET, config.getClientSecret())
                .add(AuthConstant.CODE, callback.getCode())
                .add(AuthConstant.REDIRECT_URI, config.getRedirectUri())
                .build();
        String response = HttpClientUtil.doGet(url);
        System.out.println(response);
        Map<String, String> map = HttpClientUtil.parseResponseEntity(response);
        if (map.containsKey("error")) {
            throw new AuthException(map.get("error_description"));
        }
        return AuthToken.builder().accessToken(map.get(AuthConstant.ACCESS_TOKEN)).refreshToken(map.get(AuthConstant.REFRESH_TOKEN)).expireIn(Integer.parseInt(map.get(AuthConstant.EXPIRE))).build();
    }

    @Override
    public AuthResponse refresh(AuthToken authToken) {
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
        return AuthResponse.builder().code(AuthExceptionCode.SUCCESS.getCode()).data(newAuthToken).build();
    }

    @Override
    protected AuthUserInfo getUserInfo(AuthToken accessToken) throws AuthException {
        setOpenId(accessToken);
        String url = UrlBuilder.fromBaseUrl(source.userInfo())
                .add(AuthConstant.ACCESS_TOKEN, accessToken.getAccessToken())
                .add("oauth_consumer_key", config.getClientId())
                .add("openid", accessToken.getOpenId())
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
        return AuthUserInfo.builder()
                .rawUserInfo(JSON.parseObject(response))
                .username(map.get("nickname"))
                .nickname(map.get("nickname"))
                .avatar(avatar)
                .location(location)
                .uuid(accessToken.getOpenId())
                .gender(UserInfoUtil.getRealGender(map.get("gender")).getCode())
                .token(accessToken)
                .source(source.toString())
                .build();
    }

    private void setOpenId(AuthToken accessToken) throws AuthException {
        String url = UrlBuilder.fromBaseUrl(source.openId()).add(AuthConstant.ACCESS_TOKEN, accessToken.getAccessToken()).add("fmt", "json").build();
        String response = HttpClientUtil.doGet(url);
        Map<String, String> map = HttpClientUtil.parseResponseEntityJson(response);
        if (map.containsKey("code")) {
            throw new AuthException(map.get("msg"));
        }
        accessToken.setOpenId(map.get("openid"));
    }
}
