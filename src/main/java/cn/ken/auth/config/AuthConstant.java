package cn.ken.auth.config;

/**
 * <pre>
 *
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 20:06
 */
public class AuthConstant {

    public static String RESPONSE_TYPE = "response_type";

    public static String CLIENT_ID = "client_id";

    public static String CLIENT_SECRET = "client_secret";

    public static String REDIRECT_URI = "redirect_uri";

    public static String STATE = "state";

    public static String CODE = "code";

    public static String ACCESS_TOKEN = "access_token";

    public static String REFRESH_TOKEN = "refresh_token";

    public static String GRANT_TYPE = "grant_type";

    public static String EXPIRE = "expires_in";

    public static class GrantType {

        public static String ACCESS = "authorization_code";

        public static String REFRESH = "refresh_token";
    }

}
