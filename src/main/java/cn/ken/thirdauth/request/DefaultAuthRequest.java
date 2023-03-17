package cn.ken.thirdauth.request;

import cn.ken.thirdauth.cache.AuthStateCache;
import cn.ken.thirdauth.cache.DefaultAuthStateCache;
import cn.ken.thirdauth.config.AuthConstant;
import cn.ken.thirdauth.config.AuthPlatformConfig;
import cn.ken.thirdauth.config.AuthUrls;
import cn.ken.thirdauth.enums.AuthExceptionCode;
import cn.ken.thirdauth.exception.AuthException;
import cn.ken.thirdauth.model.AuthCallback;
import cn.ken.thirdauth.model.AuthResponse;
import cn.ken.thirdauth.model.AuthToken;
import cn.ken.thirdauth.model.AuthUserInfo;
import cn.ken.thirdauth.util.UrlBuilder;

/**
 * <pre>
 * 默认权限登录
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 18:50
 */
public abstract class DefaultAuthRequest implements AuthRequest {

    protected final AuthUrls source;

    protected final AuthPlatformConfig config;

    protected final AuthStateCache cache;

    public DefaultAuthRequest(AuthPlatformConfig config, AuthUrls source) {
        this(source, config, DefaultAuthStateCache.INSTANCE);
    }

    public DefaultAuthRequest(AuthUrls source, AuthPlatformConfig config, AuthStateCache cache) {
        this.source = source;
        this.config = config;
        this.cache = cache;
    }

    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(source.authorize())
                .add(AuthConstant.RESPONSE_TYPE, "code")
                .add(AuthConstant.CLIENT_ID, config.getClientId())
                .add(AuthConstant.STATE, putState(state))
                .add(AuthConstant.REDIRECT_URI, config.getRedirectUri())
                .build();
    }

    @Override
    public String putState(String state) {
        if (state == null || state.isBlank()) {
            throw new AuthException(AuthExceptionCode.ILLEGAL_STATUS);
        }
        cache.set(state, state);
        return state;
    }

    @Override
    public void checkState(String state) {
        if (state == null || state.isBlank() || !cache.containsKey(state)) {
            throw new AuthException(AuthExceptionCode.ILLEGAL_STATUS);
        }
    }

    @Override
    public AuthResponse<AuthUserInfo> login(AuthCallback authCallback) {
        try {
            checkState(authCallback.getState());
            AuthToken accessToken = getAccessToken(authCallback);
            AuthUserInfo userInfo = getUserInfo(accessToken);
            return new AuthResponse<>(AuthExceptionCode.SUCCESS.getCode(), AuthExceptionCode.SUCCESS.getMsg(), userInfo);
        } catch (AuthException e) {
            return new AuthResponse<>(e.getCode(), e.getMsg(), null);
        }
    }

    @Override
    public AuthResponse revoke(AuthToken authToken) {
        return AuthRequest.super.revoke(authToken);
    }

    @Override
    public AuthResponse refresh(AuthToken authToken) {
        return AuthRequest.super.refresh(authToken);
    }

    protected abstract AuthToken getAccessToken(AuthCallback callback) throws AuthException;

    protected abstract AuthUserInfo getUserInfo(AuthToken accessToken) throws AuthException;
}
