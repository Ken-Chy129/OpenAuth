package cn.ken.auth.request;

import cn.ken.auth.cache.AuthStateCache;
import cn.ken.auth.cache.DefaultAuthStateCache;
import cn.ken.auth.config.AuthPlatformConfig;
import cn.ken.auth.config.AuthPlatformInfo;
import cn.ken.auth.enums.AuthExceptionCode;
import cn.ken.auth.exception.AuthException;
import cn.ken.auth.model.AuthUserInfo;
import cn.ken.auth.util.HttpClientUtil;
import cn.ken.auth.util.UrlBuilder;
import cn.ken.auth.config.AuthConfigConstant;
import cn.ken.auth.model.AuthCallback;
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
 * @since 2023/3/16 17:46
 */
public class GiteeAuthRequest extends DefaultAuthRequest {

    public GiteeAuthRequest(AuthPlatformConfig config) {
        super(AuthPlatformInfo.GITEE, config, DefaultAuthStateCache.INSTANCE);
    }
    
    public GiteeAuthRequest(AuthPlatformConfig config, AuthStateCache cache) {
        super(AuthPlatformInfo.GITEE, config, cache);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback callback) throws AuthException {
        String url = UrlBuilder.fromBaseUrl(source.accessToken())
                .add(AuthConfigConstant.GRANT_TYPE, "authorization_code")
                .add(AuthConfigConstant.CLIENT_ID, config.getClientId())
                .add(AuthConfigConstant.CLIENT_SECRET, config.getClientSecret())
                .add(AuthConfigConstant.CODE, callback.getCode())
                .add(AuthConfigConstant.REDIRECT_URI, config.getRedirectUri())
                .build();
        String response;
        try {
            response = HttpClientUtil.doPost(url);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionCode.REQUEST_ERROR);
        }
        HashMap<String, String> map = JSON.parseObject(response, HashMap.class);
        if (map.containsKey("error")) {
            throw new AuthException(map.get("error_description"));
        }
        return AuthToken.builder().accessToken(map.get(AuthConfigConstant.ACCESS_TOKEN)).build();
    }

    @Override
    protected AuthUserInfo getUserInfo(AuthToken accessToken) throws AuthException {
        String url = UrlBuilder.fromBaseUrl(source.userInfo()).add(AuthConfigConstant.ACCESS_TOKEN, accessToken.getAccessToken()).build();
        String response;
        try {
            response = HttpClientUtil.doGet(url);
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
                .location(userInfo.get("address"))
                .email(userInfo.get("email"))
                .remark(userInfo.get("bio"))
                .gender(-1)
                .token(accessToken)
                .source(source.toString())
                .build();
    }
}
