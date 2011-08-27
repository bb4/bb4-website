package com.becker.simulation.habitat;

import com.becker.simulation.habitat.creatures.Creature;
import com.becker.simulation.habitat.creatures.Population;
import com.becker.simulation.habitat.creatures.Populations;

import java.awt.*;

/**
 * This class draws a the global habitat and all the creatures in it.
 *
 * @author Barry Becker
 */
public class HabitatRenderer  {

    private static final double SIZE_SCALE = 0.001;
    Populations populations;

    int width_;
    int height_;


    /**
     * Constructor.
     */
    public HabitatRenderer(Populations populations) {
        this.populations = populations;
    }

    public void setSize(int width, int height) {
        width_ = width;
        height_ = height;
    }

    /** draw the cartesian functions */
    public void paint(Graphics g) {

        if (g == null)  return;
        Graphics2D g2 = (Graphics2D) g;

        for (Population pop : populations) {
            g2.setColor(pop.getType().getColor());
            for (Creature creature : pop.getCreatures())  {
                int w = (int) (creature.getSize() * width_ * SIZE_SCALE + 1.0);
                int h = (int) (creature.getSize() * height_ * SIZE_SCALE + 1.0);
                g2.fillOval((int)(creature.getLocation().getX() * width_),
                            (int)(creature.getLocation().getY() * height_),
                             w, h);
            }
        }
    }

}