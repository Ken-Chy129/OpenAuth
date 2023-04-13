package cn.ken.thirdauth.request;

import cn.ken.thirdauth.enums.AuthExceptionCode;
import cn.ken.thirdauth.exception.AuthException;
import cn.ken.thirdauth.model.AuthCallback;
import cn.ken.thirdauth.model.AuthResponse;
import cn.ken.thirdauth.model.AuthToken;
import cn.ken.thirdauth.model.AuthUserInfo;

/**
 * <pre>
 * 默认请求接口，所有开放平台请求都需要实现该接口
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 17:21
 */
public interface AuthRequest {

    /**
     * 获取开放平台的授权地址（不携带state）<br>
     * 一般用于注册或者已授权的用户进行登录
     * 
     * @return 开放平台的授权地址
     */
    String authorizeUrl();

    /**
     * 获取开放平台的授权地址（携带state）<br>
     * 一般用于已经登录的用户授权关联开放平台，携带state以防止csrf攻击
     * 
     * @param authorizer 授权者唯一标识，用于与state相关联
     * @return 开放平台的授权地址
     */
    String authorizeUrl(String authorizer);

    /**
     * 通过授权码获取用户令牌，无需校验state
     * 
     * @param callback 用于接收回调参数的实体(包括授权码和state等）
     * @return 用户访问令牌
     */
    AuthResponse<AuthToken> getAccessToken(AuthCallback callback);

    /**
     * 通过授权码获取用户令牌，需校验state
     * 
     * @param authorizer 授权者唯一标识
     * @param callback 用于接收回调参数的实体(包括授权码和state等）
     * @return 用户访问令牌
     */
    AuthResponse<AuthToken> getAccessToken(String authorizer, AuthCallback callback);
    
    /**
     * 根据用户访问令牌获取用户信息
     *
     * @param accessToken 用户访问令牌
     * @return 用户信息
     */
    AuthResponse<AuthUserInfo> getUserInfo(String accessToken);

    /**
     * 撤销授权，非必要实现
     *
     * @param authToken 登录成功后返回的Token信息
     * @return 是否撤销成功
     */
    default AuthResponse<Boolean> revoke(AuthToken authToken) {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }

    /**
     * 刷新access token（续期），非必要实现
     *
     * @param authToken 登录成功后返回的Token信息
     * @return 用户访问令牌 
     */
    default AuthResponse<AuthToken> refresh(AuthToken authToken) {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }
}
