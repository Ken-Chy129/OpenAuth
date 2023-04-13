package cn.ken.thirdauth.model;

import lombok.*;

/**
 * <pre>
 * 授权需要的参数
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 17:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

    private String accessToken;
    private int expireIn;
    private String refreshToken;
    private String scope;
    private int refreshTokenExpireIn;
    private String uid;
    private String openId;
    private String accessCode;
    private String unionId;
    private String sessionKey;
    private String sessionSecret;

}
