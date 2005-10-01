package com.becker.simulation.trebuchet;

import java.awt.*;
import java.awt.geom.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class Lever extends RenderablePart {

    // the angle of the leverl wrt horizontal (0 being horizontal)
    private double counterWeightLeverLength_;
    private double slingLeverLength_ ;


    private static final int BASE_Y = 400;


    private static final BasicStroke LEVER_STROKE = new BasicStroke(10.0f);
    private static final Color LEVER_COLOR = new Color(140, 50, 110);


    public Lever(double counterWightLeverLength, double slingLeverLength) {

        counterWeightLeverLength_ = counterWightLeverLength;
        slingLeverLength_ = slingLeverLength;
    }



    public void render(Graphics2D g2) {


        g2.setStroke(LEVER_STROKE);
        g2.setColor(LEVER_COLOR);

        Point2D.Double fulcrumPt = new Point2D.Double(STRUT_BASE_X, BASE_Y - (int) (SCALE_FACTOR * height_));

        double cos = SCALE_FACTOR * Math.cos(angle_);
        double sin = SCALE_FACTOR * Math.sin(angle_);

        g2.drawLine((int) (fulcrumPt.getX() + cos * counterWeightLeverLength_),
                    (int) (fulcrumPt.getY() + sin * counterWeightLeverLength_),
                    (int) (fulcrumPt.getX() - cos * slingLeverLength_),
                    (int) (fulcrumPt.getY() - sin * slingLeverLength_));

    }

}
