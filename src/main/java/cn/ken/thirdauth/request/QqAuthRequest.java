package cn.ken.thirdauth.request;

import cn.ken.thirdauth.cache.AuthStateCache;
import cn.ken.thirdauth.cache.DefaultAuthStateCache;
import cn.ken.thirdauth.config.AuthConstant;
import cn.ken.thirdauth.config.AuthPlatformConfig;
import cn.ken.thirdauth.config.AuthPlatformInfo;
import cn.ken.thirdauth.exception.AuthException;
import cn.ken.thirdauth.model.AuthGet;
import cn.ken.thirdauth.model.AuthToken;
import cn.ken.thirdauth.model.AuthUserInfo;
import cn.ken.thirdauth.util.HttpClientUtil;
import cn.ken.thirdauth.util.UrlBuilder;
import cn.ken.thirdauth.util.UserInfoUtil;

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
    protected AuthGet generateAccessTokenRequest(String code) {
        return new AuthGet(
                UrlBuilder.baseAccessTokenBuilder(source, config, code).build(),
                null
        );
    }

    @Override
    protected AuthGet generateUserInfoRequest(AuthToken authToken) {
        return new AuthGet(
                UrlBuilder.fromBaseUrl(source.userInfo())
                        .add(AuthConstant.Token.ACCESS_TOKEN, authToken.getAccessToken())
                        .add("oauth_consumer_key", config.getClientId())
                        .add("openid", authToken.getOpenId()).build(),
                null
        );
    }

    @Override
    protected AuthGet generateRefreshRequest(AuthToken authToken) {
        return new AuthGet(
                UrlBuilder.baseRefreshTokenBuilder(source, config, authToken.getRefreshToken())
                        .add(AuthConstant.REDIRECT_URI, config.getRedirectUri())
                        .build(),
                null
        );
    }

    @Override
    protected void parseResponseException(Map<String, String> responseMap) {
        if (Integer.parseInt(responseMap.get("ret")) != 0) {
            throw new AuthException(Integer.parseInt(responseMap.get("ret")), responseMap.get("msg"));
        }
        if (responseMap.containsKey("code")) {
            throw new AuthException(Integer.parseInt(responseMap.get("code")), responseMap.get("msg"));
        }
    }

    @Override
    protected AuthUserInfo parseUserInfo(Map<String, String> responseMap) {
        String avatar = responseMap.get("figureurl_qq_2");
        if (avatar == null || avatar.isEmpty()) {
            avatar = responseMap.get("figureurl_qq_1");
        }
        String location = String.format("%s-%s", responseMap.get("province"), responseMap.get("city"));
        return AuthUserInfo.builder()
                .username(responseMap.get("nickname"))
                .nickname(responseMap.get("nickname"))
                .avatar(avatar)
                .location(location)
                .uuid(responseMap.get("openid"))
                .gender(UserInfoUtil.getRealGender(responseMap.get("gender")).getCode())
                .source(source.toString())
                .build();
    }

    @Override
    protected void setOpenId(AuthToken authToken) throws AuthException {
        String url = UrlBuilder.fromBaseUrl(source.openId()).add(AuthConstant.Token.ACCESS_TOKEN, authToken.getAccessToken()).add("fmt", "json").build();
        String response = HttpClientUtil.doGet(url);
        Map<String, String> map = HttpClientUtil.parseResponseEntityJson(response);
        if (map.containsKey("code")) {
            throw new AuthException(map.get("msg"));
        }
        authToken.setOpenId(map.get("openid"));
    }
}
