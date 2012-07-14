/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph;


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