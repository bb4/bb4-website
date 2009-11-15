package com.becker.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Map that maintains the order in which the entries are added to it.
 * It does not implement the SortedMap interface but is in some ways similar.
 *
 * @author becker
 */
public class OrderedMap<K, V> implements Map<K, V> {

     /** maps scene name to the scene */
    private Map<K, V> map_;

    /** This list allows us to retrieve the scenes in the same order they were loaded. */
    private List<K> keys_;

    public OrderedMap(int capacity) {
        map_ = new HashMap<K, V>(capacity);
        keys_ = new ArrayList<K>(capacity);
    }

    /**
     * Copy constructor
     * @param capacity
     */
    public OrderedMap(OrderedMap map) {
        this(map.size());
        putAll(map);
    }


    public int size() {
        return keys_.size();
    }

    public boolean isEmpty() {
        return keys_.isEmpty();
    }

    public boolean containsKey(Object key) {
        return keys_.contains(key);
    }

    public boolean containsValue(Object value) {
        return map_.containsValue(value);
    }

    public V get(Object key) {
        return map_.get(key);
    }

    public V put(K key, V value) {
        keys_.add(key);
        return map_.put(key, value);
    }

    public V remove(Object key) {
        keys_.remove(key);
        return map_.remove(key);
    }

    public V getFirst() {
          return map_.get(keys_.get(0));
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
             keys_.add(e.getKey());
             map_.put(e.getKey(), e.getValue());
        }
    }

    public void putAll(OrderedMap<? extends K, ? extends V> m) {
        for (K key : m.keyList()) {
             keys_.add(key);
             map_.put(key, m.get(key));
        }
    }

    public void clear() {
        map_.clear();
        keys_.clear();
    }

    /**
     * Perefer using keyList instead of this method
     * @return the key in no particular order.
     */
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<K>(keys_.size());
        keySet.addAll(keys_);
        return keySet;
    }

    /**
     * Perefer using keyList instead of this method
     * @return the key in no particular order.
     */
    public List<K> keyList() {
        return keys_;
    }

    /**
     * @return the map values in no particular order.
     */
    public Collection<V> values() {
        return map_.values();
    }

    /**
     * @return map in proper order.
     */
    public Set<Entry<K, V>> entrySet() {
        return map_.entrySet();
    }

}
