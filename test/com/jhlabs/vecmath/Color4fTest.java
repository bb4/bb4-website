package com.jhlabs.vecmath;


import com.becker.common.LRUCache;

import java.awt.*;
import java.util.Map;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class Color4fTest extends TestCase {

    /** class under test. */
    private Color4f color;

    private static Color TEST_COLOR =  new Color(0.1f, 0.2f, 0.3f, 0.4f);


    public void testNoArgConstruction() {

        color = new Color4f();
        assertEquals("Unexpected color.", new Color(0,0,0,0), color.get());
    }


    public void testConstructionWithInitializer() {

        color = new Color4f(0.1f, 0.2f, 0.3f, 0.4f);
        assertEquals("Unexpected color.", TEST_COLOR, color.get());
    }


    public void testChangeColor() {

        color = new Color4f();
        color.set(TEST_COLOR);
        Color actColor = color.get();
        assertEquals("Unexpected color after changed.", TEST_COLOR.getRed(), actColor.getRed());
        assertEquals("Unexpected color after changed.", TEST_COLOR.getGreen(), actColor.getGreen());
        assertEquals("Unexpected color after changed.", TEST_COLOR.getBlue(), actColor.getBlue());
        assertEquals("Unexpected color after changed.", TEST_COLOR.getAlpha(), actColor.getAlpha());
    }
}
