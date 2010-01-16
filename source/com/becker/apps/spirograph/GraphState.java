package com.becker.apps.spirograph;

import com.becker.ui.sliders.ColorChangeListener;

import java.awt.*;

/**
 * Hold shared state information for the spirograph.
 * The Model.
 *
 * @author Barry Becker
 */
public class GraphState implements ColorChangeListener {

    public static final int INITIAL_LINE_WIDTH = 10;
    public static final int VELOCITY_MAX = 100;
    public static final int DEFAULT_NUM_SEGMENTS = 200;

    public Parameters params;
    public Parameters oldParams;

    private Color color_;

    private int numSegmentsPerRev_;
    private boolean showAxes_ = true;

    private int velocity_ = 2;
    int width_ = INITIAL_LINE_WIDTH;


    public GraphState() {
        params = new Parameters();
        oldParams = new Parameters();
    }

    public void initialize(int width, int height) {
        params.initialize(width, height);
        recordValues();
    }

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

    /**
     * reset to initial values.
     */
    public void reset() {
        params.resetAngle();
    }
    /**
     * set the old values from the current.
     */
    public void recordValues() {
        oldParams.copyFrom(params);
    }
}
