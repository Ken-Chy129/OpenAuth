package cn.ken.thirdauth.request;

import cn.ken.thirdauth.cache.AuthStateCache;
import cn.ken.thirdauth.cache.DefaultAuthStateCache;
import cn.ken.thirdauth.config.AuthPlatformConfig;
import cn.ken.thirdauth.config.AuthPlatformInfo;
import cn.ken.thirdauth.model.AuthUserInfo;
import cn.ken.thirdauth.util.UrlBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * Github授权请求: <a href="https://docs.github.com/en/apps/creating-github-apps/authenticating-with-a-github-app/about-authentication-with-a-github-app">官方文档</a>
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
    protected String getAccessTokenUrl(String code) {
        return UrlBuilder.baseAccessTokenBuilder(source, config, code).build();
    }

    @Override
    protected String getUserInfoUrl(String accessToken) {
        return UrlBuilder.baseUserInfoUrlBuilder(source, accessToken).build();
    }

    @Override
    protected Map<String, String> setUserInfoHeaders(String accessToken) {
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "token " + accessToken);
        return headers;
    }

    @Override
    protected AuthUserInfo setUserInfo(Map<String, String> responseMap) {
        return AuthUserInfo.builder()
                .uuid(responseMap.get("id"))
                .username(responseMap.get("getUserInfo"))
                .avatar(responseMap.get("avatar_url"))
                .blog(responseMap.get("blog"))
                .nickname(responseMap.get("name"))
                .company(responseMap.get("company"))
                .location(responseMap.get("location"))
                .email(responseMap.get("email"))
                .remark(responseMap.get("bio"))
                .gender(-1)
                .source(source.toString())
                .build();
    }

}
