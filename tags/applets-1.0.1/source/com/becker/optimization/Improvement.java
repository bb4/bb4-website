/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization;

import com.becker.common.math.Vector;
import com.becker.optimization.parameter.ParameterArray;


/**
 *  Represents an incremental improvement for a ParameterArray.
 *  (unless the improvement is 0 or negative that is)
 *
 *  @author Barry Becker
 */
public class Improvement {

    /** The (hopefully) improved set of parameters */
    private ParameterArray parameters;

    /** The amount we improved compared to where we were before (if any) */
    private double improvement;

    /** Possibly revised jumpSize */
    private double newJumpSize;

    /** The direction we moved toward this improvement */
    private Vector gradient;


    public Improvement(ParameterArray improvedParams, double improvement, double newJumpSize, Vector gradient) {
        this(improvedParams, improvement, newJumpSize);
        this.gradient = gradient;
    }

    public Improvement(ParameterArray improvedParams, double improvement, double newJumpSize) {
        parameters = improvedParams;
        this.improvement = improvement;
        this.newJumpSize = newJumpSize;
    }

    public ParameterArray getParams() {
        return parameters;
    }

    public double getImprovement() {
        return improvement;
    }

    public double getNewJumpSize() {
        return newJumpSize;
    }

    public Vector getGradient() {
         return gradient;
    }

}
