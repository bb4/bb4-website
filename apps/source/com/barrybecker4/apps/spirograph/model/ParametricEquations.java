/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph.model;

/**
 * Represents 2 parametric equations for defining a  epicycloid curve.
 * In other words a spriograph curve.
 *
 * @author Barry Becker
 */
public class ParametricEquations {

    private String xEquation;
    private String yEquation;

    /**
     * Creates the equation expressions at construction time.
     * Immutable.
     * @param radius  radius of main circle
     * @param combinedRad  main circle radius plus outer circle radius
     * @param position position of spoke.
     */
    public ParametricEquations(int radius, int combinedRad, int position) {

        String sign1 = "-";
        String sign2 = "-";
        int actualRadius = radius;
        int actualPosition = position;

        if ( radius == 0 ) {
            xEquation = "x(t)=undefined";
            yEquation = "y(t)=undefined";
        }
        else if ( position == 0 ) {
            xEquation = "x(t)=" + combinedRad + "cos(t)";
            yEquation = "y(t)=" + combinedRad + "sin(t)";
        }
        else {
            if ( position < 0 && radius < 0 ) {
                actualPosition *= -1;
                actualRadius *= -1;
                sign1 = "+";
            }
            else if ( position < 0 && radius > 0 ) {
                actualPosition *= -1;
                sign1 = "+";
                sign2 = "+";
            }
            else if ( position > 0 && radius < 0 ) {
                actualRadius *= -1;
                sign2 = "+";
            }
            xEquation = "x(t)=" + combinedRad +
                    "cos(t)" + sign1 + actualPosition +
                    "cos(" + combinedRad + "t / " +
                    actualRadius + ')';
            yEquation = "y(t)=" + combinedRad +
                    "sin(t)" + sign2 + actualPosition +
                    "sin(" + combinedRad + "t / " +
                    actualRadius + ')';
        }
    }

    public String getXEquation() {
        return xEquation;
    }

    public String getYEquation() {
        return yEquation;
    }
}
