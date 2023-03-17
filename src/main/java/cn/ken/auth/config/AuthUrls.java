package cn.ken.auth.config;

import cn.ken.auth.enums.AuthExceptionCode;
import cn.ken.auth.exception.AuthException;

/**
 * <pre>
 *
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 18:58
 */
public interface AuthUrls {

    /**
     * 授权的api
     *
     * @return url
     */
    String authorize();

    /**
     * 获取accessToken的api
     *
     * @return url
     */
    String accessToken();

    /**
     * 获取用户信息的api
     *
     * @return url
     */
    String userInfo();

    /**
     * 刷新accessToken
     * @return url
     */
    default String refresh() {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }
    
    default String openId() {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }
    
}
