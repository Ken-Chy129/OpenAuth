package cn.ken.thirdauth.config;

/**
 * <pre>
 *
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 20:06
 */
public class AuthConstant {

    public static final String CLIENT_ID = "client_id";

    public static final String CLIENT_SECRET = "client_secret";

    public static final String REDIRECT_URI = "redirect_uri";
    
    public static final String DISPLAY = "display";
    
    public static class Authorize {
        
        public static final String RESPONSE_TYPE = "response_type";
        
        public static final String STATE = "state";
    }
    
    public static class Token {

        public static final String CODE = "code";

        public static final String GRANT_TYPE = "grant_type";

        public static final String EXPIRE = "expires_in";

        public static final String ACCESS_TOKEN = "access_token";
        
        public static final String REFRESH_TOKEN = "refresh_token";
        
        public static final String SCOPE = "scope";
    }
    
    public static class GrantType {

        public static final String ACCESS = "authorization_code";

        public static final String REFRESH = "refresh_token";
    }

}
