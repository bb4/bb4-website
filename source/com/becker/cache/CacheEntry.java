package com.becker.cache;


/**
 * Class representing a non-expiring entry in an LRU cache.
 */
class CacheEntry {
    static final int TYPE_LRU = 1;

    CacheEntry next_;
    CacheEntry previous_;
    Object key_;
    Object value_;

    CacheEntry() {
        next_ = this;
        previous_ = this;
    }

    CacheEntry(Object k, Object v, CacheEntry before, CacheEntry after) {
        key_ = k;
        value_ = v;
        insert(before, after);
    }

    CacheEntry getNext() {
        return next_;
    }

    CacheEntry getPrevious() {
        return previous_;
    }

    Object getKey() {
        return key_;
    }

    Object getValue() {
        return value_;
    }

    final void remove() {
        next_.previous_ = previous_;
        previous_.next_ = next_;
        previous_ = null;
        next_ = null;
    }

    final void insert(CacheEntry before, CacheEntry after) {
        before.next_ = this;
        previous_ = before;
        next_ = after;
        after.previous_ = this;
    }

    int getType() {
        return TYPE_LRU;
    }
}

