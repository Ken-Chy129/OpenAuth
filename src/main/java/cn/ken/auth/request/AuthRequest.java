package cn.ken.auth.request;

import cn.ken.auth.enums.AuthExceptionCode;
import cn.ken.auth.exception.AuthException;
import cn.ken.auth.model.AuthCallback;
import cn.ken.auth.model.AuthResponse;
import cn.ken.auth.model.AuthToken;
import cn.ken.auth.model.AuthUserInfo;

/**
 * <pre>
 * 默认请求接口，所有三方平台请求都需要实现该接口
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 17:21
 */
public interface AuthRequest {

    /**
     * 返回带state的授权url,授权回调时会带上这个state
     *
     * @param state 验证授权流程的参数，可以防止csrf
     * @return 返回授权地址
     */
    default String authorize(String state) {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }

    default String putState(String state) {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }

    default void checkState(String state) {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }

    /**
     * 第三方登录
     *
     * @param authCallback 用于接收回调参数的实体
     * @return 返回登录成功后的用户信息
     */
    default AuthResponse<AuthUserInfo> login(AuthCallback authCallback) {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }

    /**
     * 撤销授权
     *
     * @param authToken 登录成功后返回的Token信息
     * @return AuthResponse
     */
    default AuthResponse revoke(AuthToken authToken) {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }

    /**
     * 刷新access token （续期）
     *
     * @param authToken 登录成功后返回的Token信息
     * @return AuthResponse
     */
    default AuthResponse refresh(AuthToken authToken) {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }
}
