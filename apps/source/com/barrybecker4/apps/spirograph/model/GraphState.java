/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph.model;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.ui.sliders.ColorChangeListener;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Hold shared state information for the spirograph.
 * The view model.
 *
 * @author Barry Becker
 */
public class GraphState implements ColorChangeListener {

    public static final int INITIAL_LINE_WIDTH = 10;
    public static final int VELOCITY_MAX = 120;
    public static final int DEFAULT_NUM_SEGMENTS = 200;

    public Parameters params;
    public Parameters oldParams;

    private boolean isRendering_ = false;
    private int width_ = INITIAL_LINE_WIDTH;
    private Color color_;

    private int numSegmentsPerRev_;
    private boolean showDecoration_ = true;
    private int velocity_ = 2;
    private List<GraphStateChangeListener> listeners_;

    public GraphState() {
        params = new Parameters();
        oldParams = new Parameters();
        listeners_ = new LinkedList<GraphStateChangeListener>();
    }

    public void initialize(int width, int height) {
        params.initialize(width, height);
        recordValues();
    }

    public void addStateListener(GraphStateChangeListener listener) {
       listeners_.add(listener);
    }

    @Override
    public void colorChanged(Color color) {
        setColor(color);
    }

    public synchronized Color getColor() {
        return color_;
    }

    public synchronized void setColor(Color color) {
        this.color_ = color;
    }

    public void setR1(float r1) {
        params.setR1(r1);
        notifyParameterChanged();
    }

    public void setR2(float r2) {
        params.setR2(r2);
        notifyParameterChanged();
    }

    public void setPos(float pos) {
        params.setPos(pos);
        notifyParameterChanged();
    }

    public void setVelocity(int velocity) {
        this.velocity_ = velocity;
        if (isMaxVelocity()) {
            notifyParameterChanged();
        }
    }

    public boolean isMaxVelocity() {
        return velocity_ == GraphState.VELOCITY_MAX;
    }

    /**
     * @return  number of milliseconds to delay between animation steps to slow rendering based on velocity value.
     */
    public int getDelayMillis() {
        int delay;
        if (velocity_ < GraphState.VELOCITY_MAX/2) {
            delay = 5 * (GraphState.VELOCITY_MAX + (GraphState.VELOCITY_MAX/2 - velocity_)) / velocity_;
        } else {
            delay = (5 * GraphState.VELOCITY_MAX) / velocity_ - 5;
        }
        return delay;
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


    public boolean isRendering() {
        return isRendering_;
    }

    public void setRendering(boolean isRendering) {
        isRendering_ = isRendering;
        if (!isRendering)
            notifyRenderingComplete();
    }

    public boolean showDecoration() {
        return showDecoration_;
    }

    public void setShowDecoration(boolean show) {
        showDecoration_ = show;
    }

    /**
     * @return the number of complete revolutions needed until the curve will overwrite itself.
     */
    public int getNumRevolutions() {
        int sign = params.getSign();
        long gcd = MathUtil.gcd( (long) params.getR1(), (long) (sign * params.getR2()) );
        return (int)((sign * params.getR2()) / gcd);
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

    private void notifyParameterChanged() {
        for (GraphStateChangeListener listener :  listeners_) {
            listener.parameterChanged();
        }
    }

    private void notifyRenderingComplete() {
        for (GraphStateChangeListener listener :  listeners_) {
            listener.renderingComplete();
        }
    }
}
