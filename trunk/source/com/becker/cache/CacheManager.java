package com.becker.cache;

import java.io.*;
import java.util.*;

/**
 * Class for storing cached data.  The cache may be created with a maximum
 * number of members.  When this maximum is reached, the least recently used
 * member is removed to make room for the new member.
 */

public class CacheManager implements Cache, Serializable {
    // Serialize the sizing information but not the actual data.

    private transient Map entries_ = null;
    // pivot_.next_ is MRU, pivot_.prev_ is LRU
    private transient CacheEntry pivot_ = null;
    private int max_ = 0;
    private int checkSeconds_ = 0;
    private int byteSize_ = 0;
    private boolean checkExpiration_ = false;
    private boolean isEnabled_ = true;
    private boolean isReadOnly_ = false;
    private boolean isSized_ = false;
    private static final String serialVersionUID = "uidForSerializable";

    /**
     * Default constructor.  Create a cache with no limit on the number
     * of members.
     */
    public CacheManager() {
        this(0, 0);
    }

    /**
     * Create a cache with a limit on the number of members.  Upon reaching
     * the limit, the cache will remove the least recently used member to
     * make room for a new one.
     *
     * @param  max  maximum number of members
     */
    public CacheManager(int max) {
        this(max, false);
    }

    /**
     * Create a cache with a limit either on the number of members or on the
     * total size of the entries.  The type of limit is controlled by the
     * <B><TT>sized</TT></B> flag.  Upon reaching the limit, the cache will
     * remove the least recently used members to make room for a new one.
     *
     * @param  max  either the maximum number of entries of the maximum total size
     * @param  sized  <B><TT>true</TT></B> to use the maximum total size, <B><TT>false</TT></B> to use the maximum number of entries
     */
    public CacheManager(int max, boolean sized) {
        this(max, 0, sized);
    }

    /**
     * Create a cache with a limit on the number of members.  Upon reaching
     * the limit, the cache will remove the least recently used member to
     * make room for a new one.
     *
     * @param  max  maximum number of members
     * @param  checkSeconds  number of seconds to wait before expiring an entry
     */
    public CacheManager(int max, int checkSeconds) {
        this(max, checkSeconds, false);
    }

    /**
     * Create a cache with a limit either on the number of members or on the
     * total size of the entries.  The type of limit is controlled by the
     * <B><TT>sized</TT></B> flag.  Upon reaching the limit, the cache will
     * remove the least recently used members to make room for a new one.
     *
     * @param  max  either the maximum number of entries of the maximum total size
     * @param  checkSeconds  number of seconds to wait before expiring an entry
     * @param  sized  <B><TT>true</TT></B> to use the maximum total size, <B><TT>false</TT></B> to use the maximum number of entries
     */
    public CacheManager(int max, int checkSeconds, boolean sized) {
        init();
        max_ = max;
        checkSeconds_ = checkSeconds;
        isSized_ = sized;
    }

    /**
     * Set whether or not the cache is enabled.  When disabled, all successive
     * data placed in the cache will be ignored.  Data already existing in the
     * cache may still be retrieved and removed.
     *
     * @param  enabled  whether or not the cache is enabled
     */
    public void setEnabled(boolean enabled) {
        isEnabled_ = enabled;
    }

    /**
     * Set the maximum number of entries allowed in the cache.
     *
     * @param  max  maximum number of entries
     */
    public synchronized void setMax(int max) {
        // Resize the cache if necessary.
        max_ = max;
        resize(null);
    }

    /**
     * Set the check seconds.
     *
     * @param  checkSeconds  number of seconds to leave an entry in the cache
     */
    public synchronized void setCheckSeconds(int checkSeconds) {

        if (checkSeconds < 0) {
            checkSeconds_ = 0;
        } else {
            checkSeconds_ = checkSeconds;
        }
    }

    /**
     * Set whether the members of the cache are considered read only.
     */
    public void setReadOnly() {
        isReadOnly_ = true;
    }

    /**
     * Remove all of my entries.
     */
    public synchronized void clear() {
        getEntries().clear();
        pivot_ = new CacheEntry();
        checkExpiration_ = false;
    }


    /**
     * Does the cache contain an entry associated with the give key.  Returns
     * true for both null and non-null values.
     *
     * @param  key  key used to lookup the object
     *
     * @return  <B>true</B> if the cache contains the entry, otherwise <B>false</B>
     */
    public synchronized boolean containsKey(Object key) {

        // Attempt to get the entry, then check whether or not it has expired.
        CacheEntry entry = (CacheEntry) getEntries().get(key);
        if (entry == null) {
            return false;
        } else {
            return checkCacheEntry(entry, true);
        }
    }

    /**
     * Does the cache contain a null entry associated with the give key.  Used
     * to differentiate between null values and non-existent entries.
     *
     * @param  key  key used to look up the entry
     *
     * @return  <B>true</B> if the cache contains a null entry, otherwise <B>false</B>
     */
    public synchronized boolean containsNull(Object key) {
        // Attempt to get the entry, then check whether or not it has expired.

        CacheEntry entry = (CacheEntry) getEntries().get(key);
        if (entry == null) {
            return false;
        } else {
            return (checkCacheEntry(entry, true) && entry.getValue() == null);
        }
    }

    /**
     * Put an object in the cache associated with the given key.
     *
     * @param  key  key used to store the object
     * @param  object  object to store
     */
    public synchronized void put(Object key, Object object) {
        if (!isEnabled_) return;

        // Do a put with the default expiration seconds from the config file.
        // If not available do without an expiration timestamp.

        if (checkSeconds_ > 0) {
            long expireTime = System.currentTimeMillis() + (1000 * checkSeconds_);
            put(key, object, expireTime);
        } else {
            put(key, object, null);
        }
    }

    /**
     * Put an object in the cache associated with the given key.  Expire
     * the entry after the given effective date.
     *
     * @param  key  key used to store the object
     * @param  object  object to store
     * @param  expire  date after which to expire the entry (<B>null</B> may be passed for no expiration)
     */
    public synchronized void put(Object key, Object object, Calendar expire) {
      long expireTime;

      if( expire != null )
	      expireTime = expire.getTime().getTime();
	  else
	      expireTime = 0;

      put(key, object, expireTime);
    }

    /**
     * Put an object in the cache associated with the given key.  Expire
     * the entry after the given effective date.
     *
     * @param  key  key used to store the objectch
     * @param  object  object to store
     * @param  expireTime  date after which to expire the entry.  Expressed in milliseconds from the Epoch. 0 means no expiration.
     */
    public synchronized void put(Object key, Object object, long expireTime) {
        if (!isEnabled_) return;

        // Do not allow null keys.  This will cause the hash table and the
        // linked list to get out of synch.
        if (key == null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
           (new Exception()).printStackTrace(new PrintWriter(bos, true));
            //String stackTrace = bos.toString();
            return;
        }

        // Put the new one in the lookup table.
        CacheEntry entry;
        if (expireTime != 0) {
            entry = new CacheExpireEntry(key, object, expireTime, pivot_, pivot_.getNext());
            checkExpiration_ = true;
        } else {
            entry = new CacheEntry(key, object, pivot_, pivot_.getNext());
        }
        CacheEntry former = (CacheEntry) getEntries().put(key, entry);

        // If a former entry existed, remove it from the used list.

        if (former != null) {
            former.remove();
        }

        // If the max is reached, remove the least recently used.
        resize(object);
    }

    /**
     * Get an object from the cache associated with the given key.
     *
     * @param  key  key used to lookup the object
     *
     * @return  the associated object, or <B>null</B> if not found or is expired
     */
    public synchronized Object get(Object key) {
        // Attempt to get the entry, then check whether or not it has expired.

        CacheEntry entry = (CacheEntry) getEntries().get(key);
        if (entry == null) {
            return null;
        } else {
            if (checkCacheEntry(entry, true)) {
                // Make the entry the most recently used.
                entry.remove();
                entry.insert(pivot_, pivot_.getNext());

                return entry.getValue();
            } else {
                return null;
            }
        }
    }

    /**
     * Returns the least recently accessed key.
     *
     * @return  key for the least recently used cache member
     */
    public synchronized Object getLRUKey() {
        return pivot_.getPrevious().getKey();
    }

    /**
     * Get an object from the cache associated with the given key.
     *
     * @param  key  key used to lookup the object
     *
     * @return  the associated object, or <B>null</B> if not found or is expired
     */
    public synchronized Object remove(Object key) {
        // Attempt to remove the entry, then check whether or not it has expired.

        CacheEntry entry = (CacheEntry) getEntries().remove(key);
        if (entry == null) {
            return null;
        } else {
            entry.remove();
            Object object = checkCacheEntry(entry, false) ? entry.getValue() : null;
            if (getEntries().size() == 0) checkExpiration_ = false;

            return object;
        }
    }

    private synchronized Map getEntries() {
        return entries_;
    }

    /**
     * Called prior to destroying a cache.
     */
    public void destroy() {}

    /**
     * Get the number of entries in the cache.
     *
     * @return  the number of cache entries
     */
    public int size() {
        return getEntries().size();
    }

    /**
     * Get the total size in bytes of all the entries in the cache.  Only
     * returns a valid value if this was initialized as a sized cache.  The
     * value itself is an approximation based on the following:
     * <P>
     * <UL>
     * <LI>String values are (length * 2) bytes
     * <LI>char[] values are (length * 2) bytes
     * <LI>byte[] values are (length * 2) bytes
     * <LI>All other objects are based on object size
     * </UL>
     *
     * @return  the number of cache entries
     */
    public int byteSize() {
        return byteSize_;
    }


    private void resize(Object value) {
        Map entries = getEntries();
        if (max_ > 0) {
            if (isSized_) {
                if (value != null) {
                    // Increment the total cache size.  Special case for simple
                    // objects.  Default is to use total object size.

                    if (value instanceof String) {
                        byteSize_ += (((String) value).length() << 1);
                    } else if (value instanceof char[]) {
                        byteSize_ += (((char[]) value).length << 1);
                    } else if (value instanceof byte[]) {
                        byteSize_ += ((byte[]) value).length;
                    } else {
                        byteSize_ += ObjectSizer.deepSizeOf(value);
                    }
                }

                // Continue removing objects until the maximum size is no
                // longer exceeded.

                while (byteSize_ > max_) {
                    CacheEntry lru = pivot_.previous_;
                    entries.remove(lru.key_);
                    lru.remove();

                    // Decrement the total cache size.  Special case for simple
                    // objects.  Default is to use total object size.

                    Object lruValue = lru.value_;
                    if (lruValue instanceof String) {
                        byteSize_ -= (((String) lruValue).length() << 1);
                    } else if (lruValue instanceof char[]) {
                        byteSize_ -= (((char[]) lruValue).length << 1);
                    } else if (lruValue instanceof byte[]) {
                        byteSize_ -= ((byte[]) lruValue).length;
                    } else {
                        byteSize_ -= ObjectSizer.deepSizeOf(lruValue);
                    }
                }
            } else {
                // Continue removing objects until the maximum number of
                // elements is no longer exceeded.

                while (entries.size() > max_) {
                    CacheEntry lru = pivot_.previous_;

                    if (lru.key_ == null) {
                        // This should never happen.  Workaround is to
                        // re-initialize the cache.  Log some information
                        // to try to determine the state of the cache
                        // at this point.

                        CacheEntry current = pivot_.next_;
                        while ((current != null) && (current != pivot_)) {
                           current = current.next_;
                        }
                        current = pivot_.previous_;
                        while ((current != null) && (current != pivot_)) {
                            current = current.previous_;
                        }

                        init();

                        break;
                    } else {
                        entries.remove(lru.key_);
                        lru.remove();
                    }
                }
            }
        }
    }

    /**
     * Check whether or not the given entry has expired.
     *
     * @param  entry  the cache entry
     * @param  remove  whether or not to remove invalid entries
     *
     * @return  <B>true</B> if the entry is valid, <B>false</B> if not
     */
    private boolean checkCacheEntry(CacheEntry entry, boolean remove) {
        if (!(checkExpiration_) || !(entry.getType() == CacheExpireEntry.TYPE_LRU_EXPIRE)) {
            return true;
        }

        CacheExpireEntry expireEntry = (CacheExpireEntry) entry;
        long date = expireEntry.expire_;
        long now = System.currentTimeMillis();
        if (now > date) {
            if (remove) {
                getEntries().remove(entry.key_);
                entry.remove();
            }
            return false;
        } else {
            return true;
        }
    }

    public synchronized Iterator iterator() {
        return getEntries().keySet().iterator();
    }

    public synchronized String getStats() {
        String stats = "size" + getEntries().size() +'\n';
        stats += "maxSize"+ max_;
        return stats;
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        init();
        in.defaultReadObject();
    }

    private synchronized void init() {
        entries_ = new HashMap();
        pivot_ = new CacheEntry();
    }

    /**
     * Creates a shallow copy of the cache.
     */
    public synchronized Object clone() throws CloneNotSupportedException {
        super.clone();
        CacheManager cache = new CacheManager(max_, checkSeconds_, isSized_);

        // Put entries in reverse order so LRU order is right
        CacheEntry entry = pivot_.getPrevious();
        while (entry != pivot_) {
            if (entry.getType() == CacheExpireEntry.TYPE_LRU_EXPIRE) {
                cache.put(entry.getKey(), entry.getValue(), ((CacheExpireEntry)entry).getExpiration());
            } else {
                cache.put(entry.getKey(), entry.getValue());
            }
        }
        if (isReadOnly_) cache.setReadOnly();
        cache.setEnabled(isEnabled_);

        // Everything else will be set in the put (e.g. byteSize_, pivot_, checkExpiration_)

        return cache;
    }
}
