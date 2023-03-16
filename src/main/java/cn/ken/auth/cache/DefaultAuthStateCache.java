package cn.ken.auth.cache;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <pre>
 * 默认state缓存实现
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/16 16:49
 */
public class DefaultAuthStateCache implements AuthStateCache {

    private static ConcurrentHashMap<String, CacheState> stateCache = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock(true);

    private final Lock writeLock = cacheLock.writeLock();

    private final Lock readLock = cacheLock.readLock();

    public static DefaultAuthStateCache INSTANCE = new DefaultAuthStateCache();

    private DefaultAuthStateCache() {

    }

    @Override
    public void set(String key, String value) {
        set(key, value, AuthCacheConfig.timeout);
    }

    @Override
    public void set(String key, String value, long timeout) {
        writeLock.lock();
        try {
            stateCache.put(key, new CacheState(value, timeout));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String get(String key) {
        readLock.lock();
        try {
            CacheState cacheState = stateCache.get(key);
            if (null == cacheState || cacheState.isExpired()) {
                return null;
            }
            return cacheState.getState();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsKey(String key) {
        readLock.lock();
        try {
            CacheState cacheState = stateCache.get(key);
            return null != cacheState && !cacheState.isExpired();
        } finally {
            readLock.unlock();
        }
    }

    @Data
    private class CacheState implements Serializable {
        private String state;
        private long expire;

        CacheState(String state, long expire) {
            this.state = state;
            // 实际过期时间等于当前时间加上有效期
            this.expire = System.currentTimeMillis() + expire;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > this.expire;
        }
    }
}
