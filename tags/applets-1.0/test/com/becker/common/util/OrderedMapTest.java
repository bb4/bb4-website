// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.common.util;


import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

/**
 * Test LRUCache behavior.
 * @author Barry Becker
 */
public class OrderedMapTest extends TestCase {

    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    private static final String FOUR = "four";
    private static final String FIVE= "five";

    private OrderedMap<String,String> map;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        map = new OrderedMap<String,String>(3);

        map.put("2", TWO);
        map.put("1", ONE);
        map.put("3", THREE);
    }

    public void testNumEntries() {

        assertEquals("Unexpected number of entries. ", 3, map.size());

        map.put ("4", FOUR);
        assertEquals("Unexpected number of entries. ", 4, map.size());
    }


    public void testKeyOrder() {

        List<String> keys = map.keyList();

        assertEquals("2", keys.get(0));
        assertEquals("1", keys.get(1));
        assertEquals("3", keys.get(2));
    }

    public void testKeyOrderAfterModify() {

        map.remove("1");
        map.put("5", FIVE);
        map.put("1", ONE);
        List<String> keys = map.keyList();

        System.out.println("keys="+ keys);
        assertEquals("2", keys.get(0));
        assertEquals("3", keys.get(1));
        assertEquals("5", keys.get(2));
        assertEquals("1", keys.get(3));
    }


}
