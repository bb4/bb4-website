package com.becker.cache;

/**
 * Class representing an expiring entry in an LRU cache.
 */
class CacheExpireEntry extends CacheEntry {
    static final int TYPE_LRU_EXPIRE = 2;

    long expire_ = -1;

    CacheExpireEntry() {
    }

    CacheExpireEntry(long e) {
        expire_ = e;
    }

    long getExpiration() {
       return expire_; 
    }

    CacheExpireEntry(Object k, Object v, long e, CacheEntry before, CacheEntry after) {
        super(k, v, before, after);
        expire_ = e;
    }

    int getType() {
        return TYPE_LRU_EXPIRE;
    }
}
