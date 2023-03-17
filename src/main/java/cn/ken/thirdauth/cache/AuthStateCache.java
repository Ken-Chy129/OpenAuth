package cn.ken.thirdauth.cache;

/**
 * <pre>
 *
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/16 1:22
 */
public interface AuthStateCache {

    void set(String key, String value);

    void set(String key, String value, long timeout);

    String get(String key);

    boolean containsKey(String key);
}
