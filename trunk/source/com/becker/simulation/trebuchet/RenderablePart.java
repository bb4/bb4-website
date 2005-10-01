package com.becker.simulation.trebuchet;

import java.awt.*;
import static com.becker.simulation.trebuchet.TrebuchetConstants.*;

/**
 * A physical piece of an object.
 *
 * @author Barry Becker Date: Sep 25, 2005
 */
public abstract class RenderablePart {

    protected static final double SCALE_FACTOR = 100;

    protected static final int BASE_X = 40;
    protected static final int STRUT_BASE_X = 300;

    protected static double height_ = HEIGHT;
    protected static double angle_;

    public RenderablePart() {
        //height_ = height;
        //angle_ = angle;
    }


    public static double getHieght() {
        return height_;
    }

    public static void setHeight(double height) {
        height_ = height;
    }

    public static double getAngle() {
        return angle_;
    }

    public static void setAngle(double angle) {
        angle_ = angle;
    }


    protected abstract void render(Graphics2D g2);

}
