package cn.ken.thirdauth.model;

import cn.ken.thirdauth.enums.AuthExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <pre>
 * 授权响应信息统一封装类
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 17:34
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse<T> implements Serializable {

    /**
     * 授权响应状态码
     */
    private int code;

    /**
     * 授权响应信息
     */
    private String msg;

    /**
     * 授权响应数据，当且仅当 code = 200 时返回
     */
    private T data;

    public AuthResponse<T> exceptionStatus(AuthExceptionCode exceptionCode, T data) {
        this.code = exceptionCode.getCode();
        this.msg = exceptionCode.getMsg();
        this.data = data;
        return this;
    }
}
