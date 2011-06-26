package com.becker.simulation.henonphase.algorithm;

import com.becker.common.ColorMap;
import com.becker.ui.renderers.OfflineGraphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Everything we need to know to compute the henon phase diagram.
 *
 * @author Barry Becker
 */
public class HenonModel  {

    public int width;
    public int height;

    /** offline rendering is fast  */
    private OfflineGraphics offlineGraphics_;

    private List<Traveler> travelers;
    private int numTravelors;
    private double angle;

    private boolean useUniformSeeds = true;


    public HenonModel(int width, int height, double angle, boolean uniformSeeds, int numTraverlors) {

        this.width = width;
        this.height = height;
        this.angle = angle;
        this.numTravelors = numTraverlors;
        this.useUniformSeeds = uniformSeeds;

        travelers = new ArrayList<Traveler>(numTravelors);
        offlineGraphics_ = new OfflineGraphics(new Dimension(width, height), Color.BLACK);
    }

    public void reset() {

        travelers.clear();

        double inc = 1.0/numTravelors;
        double xpos = 0.0;
        ColorMap cmap = new HenonColorMap();
        for (int i=0; i < numTravelors; i++) {

            if (useUniformSeeds) {
                Color color = cmap.getColorForValue(xpos);
                travelers.add(new Traveler(xpos, 0, color, angle));
            }
            else {
                double randXPos = Math.random();
                Color color = cmap.getColorForValue(randXPos);
                travelers.add(new Traveler(randXPos, 0, color, angle));
            }
            xpos += inc;
        }
    }

    public BufferedImage getImage() {
        return offlineGraphics_.getOfflineImage();
    }


    /**
     * @param numSteps  number of steps to increment each traveler
     */
    public void increment(int numSteps) {

        for (Traveler traveler : travelers) {

            offlineGraphics_.setColor(traveler.color);

            for (int i=0; i< numSteps; i++)   {
                int xpos = (int)(width * (traveler.x/2.0 + 0.5));
                int ypos = (int)(height * (traveler.y/2.0 + 0.5));
                offlineGraphics_.drawPoint(xpos, ypos);
                traveler.increment();
            }
        }
    }
    public void increment() {

        for (Traveler traveler : travelers) {

            int xpos = (int)(width * (traveler.x/2.0 + 0.5));
            int ypos = (int)(height * (traveler.y/2.0 + 0.5));

            offlineGraphics_.setColor(traveler.color);
            offlineGraphics_.drawPoint(xpos, ypos);

            traveler.increment();
        }
    }

}
