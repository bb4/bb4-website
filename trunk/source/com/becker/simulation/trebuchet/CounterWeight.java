package com.becker.simulation.trebuchet;

import java.awt.*;
import java.awt.geom.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class CounterWeight extends RenderablePart {

    private double cwLeverLength_;
    private double mass_;


    private static final int BASE_Y = 400;
    private static final int WEIGHT_LENGTH = 20;

    private static final BasicStroke STROKE = new BasicStroke(4.0f);
    private static final Color COLOR = new Color(170, 40, 40);
    private static final Color FILL_COLOR = new Color(170, 180, 130);

    public CounterWeight(double counterWeightLeverLength, double mass) {
        cwLeverLength_ = counterWeightLeverLength;
        mass_ = mass;
    }

    public void render(Graphics2D g2) {

        g2.setStroke(STROKE);
        g2.setColor(COLOR);

        double cos = SCALE_FACTOR * cwLeverLength_* Math.cos(angle_);
        double sin = SCALE_FACTOR * cwLeverLength_ * Math.sin(angle_);
        Point2D.Double attachPt = new Point2D.Double(STRUT_BASE_X + cos, BASE_Y - (int) (SCALE_FACTOR * height_) + sin);


        g2.drawLine((int) (attachPt.getX()),
                    (int) (attachPt.getY()),
                    (int) (attachPt.getX()),
                    (int) (attachPt.getY()) + WEIGHT_LENGTH);

        int radius = (int) (SCALE_FACTOR * 0.05 *Math.cbrt( mass_));
        g2.setColor(COLOR);
        g2.drawOval((int) (attachPt.getX() - radius), (int) (attachPt.getY() + WEIGHT_LENGTH), 2*radius, 2*radius);
        g2.setColor(FILL_COLOR);
        g2.fillOval((int) (attachPt.getX() - radius), (int) (attachPt.getY() + WEIGHT_LENGTH), 2*radius, 2*radius);

    }
}
