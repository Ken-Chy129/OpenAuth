package cn.ken.thirdauth.request;

import cn.ken.thirdauth.cache.AuthStateCache;
import cn.ken.thirdauth.cache.DefaultAuthStateCache;
import cn.ken.thirdauth.config.AuthPlatformConfig;
import cn.ken.thirdauth.config.AuthPlatformInfo;
import cn.ken.thirdauth.model.AuthGet;
import cn.ken.thirdauth.model.AuthToken;
import cn.ken.thirdauth.model.AuthUserInfo;
import cn.ken.thirdauth.util.UrlBuilder;

import java.util.Map;

/**
 * <pre>
 * 百度授权请求:<a href="https://openauth.baidu.com/doc/doc.html">官方文档</a>
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/4/13 11:28
 */
public class BaiduAuthRequest extends DefaultAuthRequest {

    public BaiduAuthRequest(AuthPlatformConfig config) {
        super(AuthPlatformInfo.BAIDU, config, DefaultAuthStateCache.INSTANCE);
    }

    public BaiduAuthRequest(AuthPlatformConfig config, AuthStateCache cache) {
        super(AuthPlatformInfo.BAIDU, config, cache);
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
                UrlBuilder.baseUserInfoUrlBuilder(source, authToken.getAccessToken()).build(),
                null
        );
    }

    @Override
    protected AuthGet generateRefreshRequest(AuthToken authToken) {
        return new AuthGet(
                UrlBuilder.baseRefreshTokenBuilder(source, config, authToken.getRefreshToken()).build(),
                null
        );
    }

    @Override
    protected AuthUserInfo parseUserInfo(Map<String, String> responseMap) {
        return AuthUserInfo.builder()
                .uuid(responseMap.get("openid"))
                .username(responseMap.get("username"))
                .mobilePhone(responseMap.get("securemobile"))
                .avatar(responseMap.get("portrait"))
                .remark(responseMap.get("userdetail"))
                .gender(Integer.valueOf(responseMap.get("sex")))
                .build();
    }

}
