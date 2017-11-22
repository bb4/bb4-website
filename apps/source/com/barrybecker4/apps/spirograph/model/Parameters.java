/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph.model;

import java.awt.geom.Point2D;

/**
 * Hold parameters that define the current spirograph state.
 *
 * @author Barry Becker
 */
public class Parameters {

    private float r1, r2, pos, theta, phi, x, y;
    private int sign = 1;

    public Parameters() {}

    public void initialize(int width, int height) {
        resetAngle();
        setX((width >> 1) + r1 + (r2 + sign) + pos);
        setY(height >> 1);
    }

    /** @return radius of the main circle */
    public float getR1() {
        return r1;
    }

    public void setR1(float r1) {
        this.r1 = r1;
    }

    /** @return radius of the secondary outer circle */
    public float getR2() {
        return r2;
    }

    public void setR2(float r2) {
        this.r2 = r2;
        setSign( r2 < 0 ? -1 : 1);
    }

    /** @return offset position from main circle */
    public float getPos() {
        return pos;
    }

    public void setPos(float p) {
        pos = p;
    }

    /** @return angle of the main circle for current state. */
    public float getTheta() {
        return theta;
    }

    public void setTheta(float theta) {
        this.theta = theta;
    }

    /** @return angle of the outer secondary circle for current state. */
    public float getPhi() {
        return phi;
    }

    public void setPhi(float phi) {
        this.phi = phi;
    }

    public int getSign() {
        return sign;
    }

    private void setSign(int sign)  {
        this.sign = sign;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    /**
     * reset to initial values.
     */
    public void resetAngle() {
        setTheta(0.0f);
        setPhi(0.0f);
    }

    public Point2D getCenter(int width, int height) {
        float r1 = getR1();
        float r2 = getR2();
        float theta = getTheta();
        return new Point2D.Double((width >> 1) + (r1 + r2) * Math.cos( theta ),
                                  (height >> 1) - (r1 + r2) * Math.sin( theta ));
    }

    /**
     * set our values from another parameters instance.
     */
    public void copyFrom(Parameters other) {
        r1 = other.getR1();
        r2 = other.getR2();
        pos = other.getPos();
        theta = other.getTheta();
        phi = other.getPhi();
        sign = other.getSign();
        x = other.getX();
        y = other.getY();
    }
}