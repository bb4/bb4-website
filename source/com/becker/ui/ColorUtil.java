package com.becker.ui;

import com.becker.common.ClassLoaderSingleton;
import com.becker.common.util.FileUtil;
import com.becker.common.util.ImageUtil;
import com.becker.ui.themes.BarryTheme;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Static utility functions for manipulating colors.
 *
 * @author Barry Becker
 */
public final class ColorUtil
{
    private ColorUtil() {}

    /**
     * gets a color from a hexadecimal string like "AABBCC"
     * or "AABBCCDD". The DD in this case gives the opacity value
     * if only rgb are given, then FF is asumed for the opacity
     * @param sColor color to convert
     * @param defaultColor  color to use if sColor has a problem
     * @return the color object
     */
    public static Color getColorFromHTMLColor(String sColor, Color defaultColor) {
        if (sColor==null || sColor.length()<6 || sColor.length()>8)
            return defaultColor;

        long intColor;
        try {
            intColor = Long.decode("0x" + sColor);
        }
        catch (NumberFormatException e) {
            System.out.println("bad color format: "+sColor);
            System.out.println(e.getMessage());
            return defaultColor;
        }
        int blue =  (int)(intColor % 256);
        int green = (int)((intColor >> 8 ) % 256);
        int red = (int)((intColor >> 16 ) % 256);
        int opacity = 255;
        if (sColor.length()>6) {
           opacity = (int)(intColor >> 24);
        }
        return new Color(red, green, blue, opacity);
    }

    /**
     * returns a hexadecimal string representation of the color - eg "AABBCC" or "DDAABBCC"
     * The DD in this case gives the opacity value
     * @return html color
     */
    public static String getHTMLColorFromColor(Color color)
    {
        int intval = color.getRGB();
        intval -= 0xFF000000;
        //System.out.println("NodePres getString from Color = "+Integer.toHexString(intval).toUpperCase());
        return '#'+Integer.toHexString(intval).toUpperCase();
    }


    public static Color invertColor(Color cColor)
    {
        return invertColor(cColor, 255);
    }

    public static Color invertColor(Color cColor, int trans)
    {
        return new Color( 255-cColor.getRed(), 255-cColor.getGreen(), 255-cColor.getBlue(), trans);
    }

    /**
     *
     * @param color
     * @return the hue (in HSB space) for a given color.
     */
    public static float getColorHue(Color color)
    {
        float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return hsv[0];
    }
}