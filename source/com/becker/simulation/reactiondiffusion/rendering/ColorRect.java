package com.becker.simulation.reactiondiffusion.rendering;

import com.becker.common.util.ImageUtil;

import java.awt.*;

/**
 * Renders the state of the GrayScottController model to the screen.
 * @author Barry Becker
 */
public  class ColorRect {

    private int[] pixels;

    private int width;
    private int height;

    /**
     * Constructor
     */
    ColorRect(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
    }


    public void setColor(int x, int y, Color c) {

        int location = y * width + x;
        pixels[location] = c.getRGB() ;
    }

    /**
     * @return  an image representing this rectangle of colors.
     */
    public Image getAsImage() {

        return ImageUtil.getImageFromPixelArray(pixels, width, height);
    }

}
