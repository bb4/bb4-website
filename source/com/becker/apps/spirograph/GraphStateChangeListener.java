package com.becker.apps.spirograph;


/**
 * Methods when you something about the graph has changed - either the defining parameters or if done rendering.
 * @author Barry Becker
 */
public interface GraphStateChangeListener {

    /**
     * Called when one of r1, r2, or position has changed defining the graph shape.
     */
    void parameterChanged();

    /**
     * Called when the graph is done rendering.
     */
    void renderingComplete();
}