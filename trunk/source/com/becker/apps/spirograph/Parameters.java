package com.becker.apps.spirograph;

import java.awt.geom.Point2D;

/**
 * Hold parameters that define the current spirograph state.
 *
 * @author Barry Becker
 */
public class Parameters {

    private float r1_, r2_, pos_, theta_, phi_, x_, y_;
    private int sign_ = 1;

    public Parameters() {}

    public void initialize(int width, int height) {
        resetAngle();
        setX((width >> 1) + r1_ + (r2_ + sign_) + pos_);
        setY(height >> 1);
    }
   
    /** @return radius of the main circle */
    public float getR1() {
        return r1_;
    }

    public void setR1(float r1) {
        r1_ = r1;
    }

    /** @return radius of the secondary outer circle */
    public float getR2() {
        return r2_;
    }

    public void setR2(float r2) {
        r2_ = r2;
        setSign( r2 < 0 ? -1 : 1);
    }

    /** @return offset position from main circle */
    public float getPos() {
        return pos_;
    }

    public void setPos(float p) {
        pos_ = p;
    }

    /** @return angle of the main circle for current state. */
    public float getTheta() {
        return theta_;
    }

    public void setTheta(float theta) {
        this.theta_ = theta;
    }

    /** @return angle of the outer secondary circle for current state. */
    public float getPhi() {
        return phi_;
    }

    public void setPhi(float phi) {
        this.phi_ = phi;
    }

    public int getSign() {
        return sign_;
    }

    private void setSign(int sign)  {
        sign_ = sign;
    }

    public float getX() {
        return x_;
    }

    public void setX(float x) {
        this.x_ = x;
    }

    public float getY() {
        return y_;
    }

    public void setY(float y) {
        this.y_ = y;
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
        r1_ = other.getR1();
        r2_ = other.getR2();
        pos_ = other.getPos();
        theta_ = other.getTheta();
        phi_ = other.getPhi();
        sign_ = other.getSign();
        x_ = other.getX();
        y_ = other.getY();
    }
}