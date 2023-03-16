package cn.ken.auth.exception;

import cn.ken.auth.enums.AuthExceptionCode;

/**
 * <pre>
 * 通用异常类
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 17:24
 */
public class AuthException extends RuntimeException {
    
    private int code;
    
    private String msg;

    public AuthException(String msg) {
        this(AuthExceptionCode.FAILURE.getCode(), msg);
    }

    public AuthException(AuthExceptionCode status) {
        this(status.getCode(), status.getMsg());
    }
    
    public AuthException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
