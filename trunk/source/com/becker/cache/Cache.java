package com.becker.cache;

import java.util.Calendar;

/**
 * Interface for creating cache classes.
 * Each class implements its own storage mechanism.
 */
public interface Cache
{

    /**
     * Set whether or not the cache is enabled.  When disabled, all successive
     * data placed in the cache will be ignored.  Data already existing in the
     * cache may still be retrieved and removed.
     */
     void setEnabled( boolean enabled );

    /**
     * Set whether the objects in the cache are to be considered read only.
     * Each implementation may use this flag in a different manner.
     */
     void setReadOnly();

    /**
     * Remove all of my entries.
     */
     void clear();


    /**
     * Does the cache contain an entry associated with the give key.  Returns
     * true for both null and non-null values.
     *
     * @param  key  key used to look up the entry
     *
     * @return  <B>true</B> if the cache contains the entry, otherwise <B>false</B>
     */
     boolean containsKey( Object key );

    /**
     * Does the cache contain a null entry associated with the give key.  Used
     * to differentiate between null values and non-existent entries.
     *
     * @param  key  key used to look up the entry
     *
     * @return  <B>true</B> if the cache contains a null entry, otherwise <B>false</B>
     */
     boolean containsNull( Object key );

    /**
     * Put an object in the cache associated with the given key.
     *
     * @param  key  key used to store the object
     * @param  object  object to store
     */
     void put( Object key, Object object );

    /**
     * Put an object in the cache associated with the given key.  Expire
     * the entry after the given effective date.
     *
     * @param  key  key used to store the object
     * @param  object  object to store
     * @param  expire  date after which to expire the entry (<B>null</B> may be passed for no expiration)
     */
     void put( Object key, Object object, Calendar expire );

    /**
     * Put an object in the cache associated with the given key.  Expire
     * the entry after the given number of seconds.
     *
     * @param  key  key used to store the object
     * @param  object  object to store
     * @param  expireTime  date after which to expire the entry. Expressed in milliseconds from the Epoch. 0 means no expiration.
     */
     void put( Object key, Object object, long expireTime );

    /**
     * Get an object from the cache associated with the given key.
     *
     * @param  key  key used to lookup the object
     *
     * @return  the associated object, or <B>null</B> if not found or is expired
     */
     Object get( Object key );

    /**
     * Remove an object from the cache associated with the given key.
     *
     * @param  key  key used to lookup the object
     *
     * @return  the associated object, or <B>null</B> if not found or is expired
     */
     Object remove( Object key );

    /**
     * Called prior to destroying a cache.
     */
     void destroy();

    /**
     * Return the number of entries in the cache.
     *
     * @return  the cache size
     */

     int size();

    /**
     * Return statistics info.
     */
     String getStats();

    /**
     * Return a shallow copy of the cache.  Cache keys and values should be the
     * same, but the internal map should be different.
     *
     * If not implement, just call super.clone(), which will throw an exception
     * as long as you don't implement the Cloneable interface (which is not recommended).
     */
     Object clone() throws CloneNotSupportedException;
}
