package com.becker.apps.spirograph;

import com.becker.ui.sliders.ColorChangeListener;
import java.awt.*;

/**
 * Hold shared state information for the spirograph.
 * The Model.
 *
 * @author Barry Becker Date: Jul 15, 2006
 */
public class GraphState implements ColorChangeListener {

    public static final int INITIAL_LINE_WIDTH = 10;
    public static final int VELOCITY_MAX = 100;
    public static final int DEFAULT_NUM_SEGMENTS = 200;
    private Color color_;

    private float r1_,    r2_,    pos_,    theta_,    phi_,    x_,    y_;
    private float oldR1_, oldR2_, oldPos_, oldSign_, oldTheta_, oldPhi_, oldx_, oldy_;
    private int sign_;
    private int numSegmentsPerRev_;
    private boolean showAxes_ = true;

    private int velocity_ = 2;
    int width_ = INITIAL_LINE_WIDTH;

    public GraphState() {}

    public void colorChanged(Color color)
    {
        setColor(color);
    }

    public synchronized Color getColor() {
        return color_;
    }

    public synchronized  void setColor(Color color) {
        this.color_ = color;
    }

    public float getR1() {
        return r1_;
    }

    public void setR1(float r1) {
        r1_ = r1;
    }

    public float getR2() {
        return r2_;
    }

    public void setR2(float r2) {
        r2_ = r2;
    }

    public float getPos() {
        return pos_;
    }

    public void setPos(float p) {
        pos_ = p;
    }

    public int getSign() {
        return sign_;
    }

    public void setSign(int sign) {
        this.sign_ = sign;
    }

    public float getTheta() {
        return theta_;
    }

    public void setTheta(float theta) {
        this.theta_ = theta;
    }

    public float getPhi() {
        return phi_;
    }

    public void setPhi(float phi) {
        this.phi_ = phi;
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


    public int getVelocity() {
        return velocity_;
    }

    public void setVelocity(int velocity) {
        this.velocity_ = velocity;
    }

    public int getWidth() {
        return width_;
    }

    public void setWidth(int width) {
        this.width_ = width;
    }

    public void setNumSegmentsPerRev(int numSegments) {
       numSegmentsPerRev_ = numSegments;
    }

    public int getNumSegmentsPerRev() {
        return numSegmentsPerRev_;
    }

    public boolean showAxes() {
        return showAxes_;
    }

    public void setShowAxes(boolean show) {
        showAxes_ = show;
    }

    // allow access of old values

    public float getOldR1() {
        return oldR1_;
    }
    public float getOldR2() {
        return oldR2_;
    }
    public float getOldPos() {
        return oldPos_;
    }
    public float getOldSign() {
        return oldSign_;
    }
    public float getOldTheta() {
        return oldTheta_;
    }
    public float getOldPhi() {
        return oldPhi_;
    }
    public float getOldX() {
        return oldx_;
    }
    public float getOldY() {
        return oldy_;
    }

    /**
     * reset to initial values.
     */
    public void reset() {
        setTheta(0.0f);
        setPhi(0.0f);
    }
    /**
     * set the old values from the current.
     */
    public void recordValues() {
        oldR1_ = r1_;
        oldR2_ = r2_;
        oldPos_ = pos_;
        oldSign_ = sign_;
        oldTheta_ = theta_;
        oldPhi_ = phi_;
        oldx_ = x_;
        oldy_ = y_;
    }

}
