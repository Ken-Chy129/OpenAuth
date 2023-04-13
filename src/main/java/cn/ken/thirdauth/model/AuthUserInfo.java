package cn.ken.thirdauth.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <pre>
 * 平台用户信息
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 21:51
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserInfo implements Serializable {

    /**
     * 用户第三方系统的唯一id。在调用方集成该组件时，可以用uuid + source唯一确定一个用户
     *
     * @since 1.3.3
     */
    private String uuid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 手机号码
     */
    private String mobilePhone;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户网址
     */
    private String blog;
    /**
     * 所在公司
     */
    private String company;
    /**
     * 位置
     */
    private String location;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户备注（各平台中的用户个人介绍）
     */
    private String remark;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 用户来源
     */
    private String source;
    /**
     * 用户授权的token信息
     */
    private AuthToken token;
    /**
     * 第三方平台返回的原始用户信息
     */
    private JSONObject rawUserInfo;
}
