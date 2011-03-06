package com.becker.simulation.snake.rendering;

import static com.becker.simulation.snake.SnakeConstants.*;

/**
 * Tweakable rendering parameters.
 *
 * @author Barry Becker
 */
public class RenderingParameters {

    private boolean showVelocityVectors_ = false;
    private boolean showForceVectors_ = false;
    private boolean drawMesh_ = false;
    private double scale_ = SCALE;


    public void setScale( double scale ) {
        scale_ = scale;
    }

    public double getScale() {
        return scale_;
    }

    public void setShowVelocityVectors( boolean show ) {
        showVelocityVectors_ = show;
        System.out.println("setting show velocity to " + show);
    }

    public boolean getShowVelocityVectors() {
        return showVelocityVectors_;
    }

    public void setShowForceVectors( boolean show ) {
        showForceVectors_ = show;
    }

    public boolean getShowForceVectors() {
        return showForceVectors_;
    }

    public void setDrawMesh( boolean use ) {
        drawMesh_ = use;
    }

    public boolean getDrawMesh() {
        return drawMesh_;
    }
}
