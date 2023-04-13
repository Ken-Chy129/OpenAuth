package cn.ken.thirdauth.util;

import cn.ken.thirdauth.config.AuthConstant;
import cn.ken.thirdauth.config.AuthPlatformConfig;
import cn.ken.thirdauth.config.AuthUrls;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/15 18:55
 */
public class UrlBuilder {

    private StringBuilder baseUrl;

    private Map<String, String> params;

    public UrlBuilder(String baseUrl) {
        this.baseUrl = new StringBuilder(baseUrl);
        this.params = new HashMap<>();
    }

    public static UrlBuilder fromBaseUrl(String baseUrl) {
        return new UrlBuilder(baseUrl);
    }

    public UrlBuilder add(String key, String value) {
        if (value != null) {
            this.params.put(key, value);
        }
        return this;
    }

    public String build() {
        if (params.isEmpty()) {
            return baseUrl.toString();
        }
        baseUrl.append("?");
        for (String key : params.keySet()) {
            baseUrl.append(key)
                    .append("=")
                    .append(params.get(key))
                    .append("&");
        }
        String url = baseUrl.toString();
        return url.substring(0, url.length() - 1);
    }
    
    public static UrlBuilder baseAccessTokenBuilder(AuthUrls source, AuthPlatformConfig config, String code) {
        return UrlBuilder.fromBaseUrl(source.accessToken())
                .add(AuthConstant.CLIENT_ID, config.getClientId())
                .add(AuthConstant.CLIENT_SECRET, config.getClientSecret())
                .add(AuthConstant.CODE, code)
                .add(AuthConstant.REDIRECT_URI, config.getRedirectUri())
                .add(AuthConstant.GRANT_TYPE, "authorization_code");
    }

    public static UrlBuilder baseUserInfoUrlBuilder(AuthUrls source, String accessToken) {
        return UrlBuilder.fromBaseUrl(source.userInfo())
                .add(AuthConstant.ACCESS_TOKEN, accessToken);
    }
}
