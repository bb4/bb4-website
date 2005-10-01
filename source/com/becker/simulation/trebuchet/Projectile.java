package com.becker.simulation.trebuchet;

import java.awt.*;
import java.awt.geom.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class Projectile extends RenderablePart {


    private double mass_;
    private double radius_;
    private Point2D.Double position_ = new Point2D.Double();
    private boolean isOnRamp_ = true;

    private static final BasicStroke LEVER_STROKE = new BasicStroke(10.0f);
    private static final Color BORDER_COLOR = new Color(140, 50, 110);
    private static final Color FILL_COLOR = new Color(80, 150, 10);


    public Projectile(double projectileMass) {
        mass_ = projectileMass;
        radius_ = 0.05 * Math.cbrt(mass_);
    }


    public void setX(double x) {
        position_.x = x;
    }

    public double getX() {
        return position_.getX();
    }

    public void setY(double y) {
        position_.y = y;
    }

    public double getY() {
        return position_.getY();
    }

    public void setPosition(Point2D.Double position) {
        position_  = position;
    }

    public double getMass() {
        return mass_;
    }

    public double getRadius() {
        return radius_;
    }


    public boolean isOnRamp() {
        return isOnRamp_;
    }

    public void setOnRamp(boolean onRamp) {
        isOnRamp_ = onRamp;
    }


    public void render(Graphics2D g2) {

        int radius = (int) (SCALE_FACTOR * radius_);
        g2.setColor(BORDER_COLOR);
        g2.drawOval((int) (position_.getX() - radius), (int) (position_.getY() - radius), 2*radius, 2*radius);
        g2.setColor(FILL_COLOR);
        g2.fillOval((int) (position_.getX() - radius), (int) (position_.getY() - radius), 2*radius, 2*radius);
    }


}
