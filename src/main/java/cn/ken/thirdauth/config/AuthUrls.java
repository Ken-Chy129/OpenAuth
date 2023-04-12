package cn.ken.thirdauth.config;

import cn.ken.thirdauth.enums.AuthExceptionCode;
import cn.ken.thirdauth.exception.AuthException;

/**
 * <pre>
 * 获取开放平台授权、申请令牌等API地址的抽象接口
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 18:58
 */
public interface AuthUrls {

    /**
     * 授权的api
     *
     * @return 开放平台授权api的地址
     */
    String authorize();

    /**
     * 获取accessToken的api
     *
     * @return 开放平台申请令牌api的地址
     */
    String accessToken();

    /**
     * 获取用户信息的api
     *
     * @return 开放平台获取用户信息api的url
     */
    String userInfo();

    /**
     * 刷新accessToken
     *
     * @return 开放平台刷新令牌api的url
     */
    default String refresh() {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }

    /**
     * 部分平台可能会有获取openId的操作
     *
     * @return 开放平台获取openId的url
     */
    default String openId() {
        throw new AuthException(AuthExceptionCode.NOT_IMPLEMENTED);
    }

}
