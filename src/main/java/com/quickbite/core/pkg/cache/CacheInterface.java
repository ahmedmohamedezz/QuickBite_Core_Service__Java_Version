package com.quickbite.core.pkg.cache;

public interface CacheInterface {
    Object get(String key);

    void set(String key, Object value);

    void del(String key);
}
