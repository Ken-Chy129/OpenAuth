package cn.ken.thirdauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * <pre>
 * Get请求封装体
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/4/13 18:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthGet {
    
    private String url;
    
    private Map<String, String> headers;
}
