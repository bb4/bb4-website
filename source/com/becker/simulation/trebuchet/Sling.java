package com.becker.simulation.trebuchet;

import java.awt.*;
import java.awt.geom.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class Sling extends RenderablePart {


    private double length_;
    private double slingLeverLength_;
    private Projectile projectile_;
    private double releaseAngle_;

    private static final int BASE_Y = 400;

    private static final BasicStroke STROKE = new BasicStroke(2.0f);
    private static final Color COLOR = new Color(0, 30, 0);

    public Sling(double slingLength, double slingLeverLength, double releaseAngle, Projectile p) {
        length_ = slingLength;
        slingLeverLength_ = slingLeverLength;
        releaseAngle_ = releaseAngle;
        projectile_ = p;

        Point2D.Double attachPt = getAttachPoint();
        p.setX(attachPt.getX() + SCALE_FACTOR * length_);
        p.setY(attachPt.getY() - SCALE_FACTOR * p.getRadius());

    }

    public Point2D.Double  getAttachPoint() {
        double cos = - SCALE_FACTOR * slingLeverLength_* Math.cos(angle_);
        double sin = - SCALE_FACTOR * slingLeverLength_ * Math.sin(angle_);
        Point2D.Double attachPt = new Point2D.Double(STRUT_BASE_X + cos, BASE_Y - (int) (SCALE_FACTOR * height_) + sin);
        return attachPt;
    }

    public void render(Graphics2D g2) {

        g2.setStroke(STROKE);
        g2.setColor(COLOR);

        Point2D.Double attachPt = getAttachPoint();

        g2.drawLine((int) (attachPt.getX()),
                    (int) (attachPt.getY()),
                    (int) (projectile_.getX()),
                    (int) (projectile_.getY()));


    }
}
