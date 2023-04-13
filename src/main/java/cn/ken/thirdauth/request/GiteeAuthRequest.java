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
                .add(AuthConstant.GRANT_TYPE, AuthConstant.GrantType.ACCESS)
                .add(AuthConstant.CLIENT_ID, config.getClientId())
                .add(AuthConstant.CLIENT_SECRET, config.getClientSecret())
                .add(AuthConstant.CODE, callback.getCode())
                .add(AuthConstant.REDIRECT_URI, config.getRedirectUri())
                .build();
        String response = HttpClientUtil.doPost(url);
        HashMap<String, String> map = JSON.parseObject(response, HashMap.class);
        if (map.containsKey("error")) {
            throw new AuthException(map.get("error_description"));
        }
        return AuthToken.builder().accessToken(map.get(AuthConstant.ACCESS_TOKEN)).build();
    }

    @Override
    protected AuthUserInfo getUserInfo(AuthToken accessToken) throws AuthException {
        String url = UrlBuilder.fromBaseUrl(source.userInfo()).add(AuthConstant.ACCESS_TOKEN, accessToken.getAccessToken()).build();
        String response = HttpClientUtil.doGet(url);
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