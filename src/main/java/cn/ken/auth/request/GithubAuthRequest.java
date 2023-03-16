package cn.ken.auth.request;

import cn.ken.auth.cache.AuthStateCache;
import cn.ken.auth.cache.DefaultAuthStateCache;
import cn.ken.auth.config.AuthPlatformConfig;
import cn.ken.auth.config.AuthPlatformInfo;
import cn.ken.auth.enums.AuthExceptionCode;
import cn.ken.auth.exception.AuthException;
import cn.ken.auth.model.AuthCallback;
import cn.ken.auth.model.AuthResponse;
import cn.ken.auth.model.AuthUserInfo;
import cn.ken.auth.util.HttpClientUtil;
import cn.ken.auth.util.UrlBuilder;
import cn.ken.auth.config.AuthConfigConstant;
import cn.ken.auth.model.AuthToken;
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
                .add(AuthConfigConstant.CLIENT_ID, config.getClientId())
                .add(AuthConfigConstant.CLIENT_SECRET, config.getClientSecret())
                .add(AuthConfigConstant.CODE, callback.getCode())
                .add(AuthConfigConstant.REDIRECT_URI, config.getRedirectUri())
                .build();
        String response;
        try {
            response = HttpClientUtil.doGet(url);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionCode.REQUEST_ERROR);
        }
        Map<String, String> map = HttpClientUtil.parseResponseEntity(response);
        if (map.containsKey("error")) {
            throw new AuthException(map.get("error_description"));
        }
        return AuthToken.builder().accessToken(map.get(AuthConfigConstant.ACCESS_TOKEN)).build();
    }
    
    @Override
    protected AuthUserInfo getUserInfo(AuthToken authToken) throws AuthException {
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "token " + authToken.getAccessToken());
        String response;
        try {
            response = HttpClientUtil.doGetWithHeaders(UrlBuilder.fromBaseUrl(source.userInfo()).build(), headers);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionCode.REQUEST_ERROR);
        }
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