package cn.ken.auth.request;

import cn.ken.auth.cache.AuthStateCache;
import cn.ken.auth.cache.DefaultAuthStateCache;
import cn.ken.auth.config.AuthConstant;
import cn.ken.auth.config.AuthPlatformConfig;
import cn.ken.auth.config.AuthUrls;
import cn.ken.auth.enums.AuthExceptionCode;
import cn.ken.auth.exception.AuthException;
import cn.ken.auth.model.AuthCallback;
import cn.ken.auth.model.AuthResponse;
import cn.ken.auth.model.AuthToken;
import cn.ken.auth.model.AuthUserInfo;
import cn.ken.auth.util.UrlBuilder;

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
