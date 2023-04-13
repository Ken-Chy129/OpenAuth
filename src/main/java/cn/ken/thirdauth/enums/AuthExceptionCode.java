package cn.ken.thirdauth.enums;

/**
 * <pre>
 *
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 17:27
 */
public enum AuthExceptionCode {

    SUCCESS(200, "Success"),
    FAILURE(500, "Failure"),
    /**
     * 请求开放平台接口失败
     */
    REQUEST_ERROR(10000, "Failed to request the open-platform interface"),
    /**
     * 方法未定义
     */
    NOT_IMPLEMENTED(5001, "Not Implemented"),
    /**
     * 参数不合法
     */
    PARAMETER_INCOMPLETE(5002, "Parameter incomplete"),
    /**
     * 方法不支持
     */
    UNSUPPORTED(5003, "Unsupported operation"),
    /**
     * 授权源信息不能为空
     */
    NO_AUTH_SOURCE(5004, "AuthDefaultSource cannot be null"),
    /**
     * 未定义的开放平台
     */
    UNIDENTIFIED_PLATFORM(5005, "Unidentified platform"),
    ILLEGAL_REDIRECT_URI(5006, "Illegal redirect uri"),
    ILLEGAL_REQUEST(5007, "Illegal request"),
    ILLEGAL_CODE(5008, "Illegal code"),
    ILLEGAL_STATUS(5009, "Illegal state"),
    REQUIRED_REFRESH_TOKEN(5010, "The refresh token is required; it must not be null"),
    ILLEGAL_TOKEN(5011, "Invalid token"),
    ;

    private final int code;
    private final String msg;

    AuthExceptionCode(int code, String msg) {
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
