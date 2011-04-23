package com.becker.simulation.common;

import com.becker.common.util.ImageUtil;

import java.awt.*;

/**
 * Renders the state of the  model to the screen.
 * @author Barry Becker
 */
public class ColorRect {

    private int[] pixels;

    private int width;
    private int height;

    /**
     * Constructor
     */
    public ColorRect(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
    }


    public void setColor(int x, int y, Color c) {

        setColor(x, y,  c.getRGB()) ;
    }

    /**
     * Set the color for a whole rectangular region.
     */
    public void setColorRect(int x, int y, int width, int height, Color c) {
        int color = c.getRGB();
        for (int i=x; x<x+width; i++) {
            for (int j=y; y<y+height; j++) {
                setColor(x, y, color);
            }
        }
    }

    private void setColor(int x, int y, int color) {
        int location = y * width + x;
        pixels[location] = color ;
    }

    /**
     * @return  an image representing this rectangle of colors.
     */
    public Image getAsImage() {

        return ImageUtil.getImageFromPixelArray(pixels, width, height);
    }

}
