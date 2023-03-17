package cn.ken.thirdauth.request;

import cn.ken.thirdauth.cache.AuthStateCache;
import cn.ken.thirdauth.cache.DefaultAuthStateCache;
import cn.ken.thirdauth.config.AuthConstant;
import cn.ken.thirdauth.config.AuthPlatformConfig;
import cn.ken.thirdauth.config.AuthPlatformInfo;
import cn.ken.thirdauth.exception.AuthException;
import cn.ken.thirdauth.model.AuthCallback;
import cn.ken.thirdauth.model.AuthToken;
import cn.ken.thirdauth.model.AuthUserInfo;
import cn.ken.thirdauth.util.HttpClientUtil;
import cn.ken.thirdauth.util.UrlBuilder;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 20:44
 */
public class GithubAuthRequest extends DefaultAuthRequest {

    public GithubAuthRequest(AuthPlatformConfig config) {
        super(AuthPlatformInfo.GITHUB, config, DefaultAuthStateCache.INSTANCE);
    }

    public GithubAuthRequest(AuthPlatformConfig config, AuthStateCache cache) {
        super(AuthPlatformInfo.GITHUB, config, cache);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback callback) throws AuthException {
        String url = UrlBuilder.fromBaseUrl(source.accessToken())
                .add(AuthConstant.CLIENT_ID, config.getClientId())
                .add(AuthConstant.CLIENT_SECRET, config.getClientSecret())
                .add(AuthConstant.CODE, callback.getCode())
                .add(AuthConstant.REDIRECT_URI, config.getRedirectUri())
                .build();
        String response = HttpClientUtil.doGet(url);
        Map<String, String> map = HttpClientUtil.parseResponseEntity(response);
        if (map.containsKey("error")) {
            throw new AuthException(map.get("error_description"));
        }
        return AuthToken.builder().accessToken(map.get(AuthConstant.ACCESS_TOKEN)).build();
    }

    @Override
    protected AuthUserInfo getUserInfo(AuthToken authToken) throws AuthException {
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "token " + authToken.getAccessToken());
        String response = HttpClientUtil.doGetWithHeaders(UrlBuilder.fromBaseUrl(source.userInfo()).build(), headers);
        Map<String, String> userInfo = HttpClientUtil.parseResponseEntityJson(response);
        return AuthUserInfo.builder()
                .rawUserInfo(JSON.parseObject(response))
                .uuid(userInfo.get("id"))
                .username(userInfo.get("login"))
                .avatar(userInfo.get("avatar_url"))
                .blog(userInfo.get("blog"))
                .nickname(userInfo.get("name"))
                .company(userInfo.get("company"))
                .location(userInfo.get("location"))
                .email(userInfo.get("email"))
                .remark(userInfo.get("bio"))
                .gender(-1)
                .token(authToken)
                .source(source.toString())
                .build();
    }

}
