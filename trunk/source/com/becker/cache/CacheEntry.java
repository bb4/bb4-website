package com.becker.cache;


/**
 * Class representing a non-expiring entry in an LRU cache.
 */

class CacheEntry {
    static final int TYPE_LRU = 1;

    CacheEntry next_;
    CacheEntry prev_;
    Object key_;
    Object value_;

    CacheEntry() {
        next_ = prev_ = this;
        return;
    }

    CacheEntry(Object k, Object v, CacheEntry before, CacheEntry after) {
        key_ = k;
        value_ = v;
        insert(before, after);
        return;
    }

    final void remove() {
        next_.prev_ = prev_;
        prev_.next_ = next_;
        prev_ = null;
        next_ = null;
        return;
    }

    final void insert(CacheEntry before, CacheEntry after) {
        before.next_ = this;
        prev_ = before;
        next_ = after;
        after.prev_ = this;
        return;
    }

    int getType() {
        return TYPE_LRU;
    }
}

