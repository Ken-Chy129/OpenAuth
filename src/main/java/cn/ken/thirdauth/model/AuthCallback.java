package cn.ken.thirdauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;




/**
 * <pre>
 * 授权回调返回值
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 17:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthCallback implements Serializable {

    /**
     * 访问AuthorizeUrl后回调时带的参数code
     */
    private String code;

    /**
     * 访问AuthorizeUrl后回调时带的参数state，用于鉴定请求者的身份，防止CSRF攻击
     */
    private String state;

}
