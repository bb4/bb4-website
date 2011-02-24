package com.becker.apps.spirograph;

/**
 * Represents 2 parametric equations for defining a  epicycloid curve.
 * In other words a spriograph curve.
 *
 * @author Barry Becker
 */
public class ParametricEquations {

    String xEquation;
    String yEquation;

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
            xEquation = new StringBuilder().append("x(t)=").append(combinedRad).
                    append("cos(t)").append(sign1).append(actualPosition).
                    append("cos(").append(combinedRad).append("t / ").
                    append(actualRadius).append(')').toString();
            yEquation = new StringBuilder().append("y(t)=").append(combinedRad).
                    append("sin(t)").append(sign2).append(actualPosition).
                    append("sin(").append(combinedRad).append("t / ").
                    append(actualRadius).append(')').toString();
        }
    }

    public String getXEquation() {
        return xEquation;
    }

    public String getYEquation() {
        return yEquation;
    }
}
