package cn.ken.thirdauth.request;

import cn.ken.thirdauth.cache.AuthStateCache;
import cn.ken.thirdauth.cache.DefaultAuthStateCache;
import cn.ken.thirdauth.config.AuthConstant;
import cn.ken.thirdauth.config.AuthPlatformConfig;
import cn.ken.thirdauth.config.AuthPlatformInfo;
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

/**
 * <pre>
 * Gitee授权请求: <a href="https://gitee.com/api/v5/oauth_doc#/">官方文档</a>
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
    protected UrlBuilder accessTokenUrlBuilder(String code) {
        return super.accessTokenUrlBuilder(code).add(AuthConstant.GRANT_TYPE, AuthConstant.GrantType.ACCESS);
    }

    @Override
    protected AuthUserInfo setUserInfo(Map<String, String> responseMap) {
        return  AuthUserInfo.builder()
                .uuid(responseMap.get("id"))
                .username(responseMap.get("getUserInfo"))
                .avatar(responseMap.get("avatar_url"))
                .blog(responseMap.get("blog"))
                .nickname(responseMap.get("name"))
                .company(responseMap.get("company"))
                .location(responseMap.get("address"))
                .email(responseMap.get("email"))
                .remark(responseMap.get("bio"))
                .gender(-1)
                .source(source.toString())
                .build();
    }
    
}
