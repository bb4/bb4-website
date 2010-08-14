package com.becker.simulation.reactiondiffusion.algorithm;


/**
 * This is the core of the Gray-Scott reaction diffusion simulation implementation.
 * based on an implementation by Joakim Linde and modified by Barry Becker.
 */
final class GrayScottAlgorithm {

    private static final double DU = 2.0e-5;
    private static final double DV = 1.0e-5;

    GrayScottModel model_;

    private double duDivh2;
    private double dvDivh2;


    /**
     * Constructor
     */
    GrayScottAlgorithm(GrayScottModel model) {
        model_ = model;
    }


    public void computeNextTimeStep(int minX, int maxX, double dt) {
        double uv2;
        for (int x = minX; x <= maxX; x++) {
            for (int y = 1; y < model_.getHeight() - 1; y++) {
                uv2 = model_.tmpU[x][y] * model_.tmpV[x][y] * model_.tmpV[x][y];
                model_.u[x][y] = calcNewCenter(model_.tmpU, x, y, duDivh2, true, uv2, dt);
                model_.v[x][y] = calcNewCenter(model_.tmpV, x, y, dvDivh2, false, uv2, dt);
            }
        }
    }

    public void computeNewEdgeValues(double dt) {

        // top and bottom edges
        for (int x = 0; x < model_.getWidth(); x++) {
            calcEdge(x, 0, dt);
            calcEdge(x, model_.getHeight() - 1, dt);
        }

         // left and right edges
        for (int y = 0; y < model_.getHeight(); y++) {
            calcEdge(0, y, dt);
            calcEdge(model_.getWidth() - 1, y, dt);
        }
    }


    public void setH(double h) {
        double h2 = h * h;
        duDivh2 = DU / h2;
        dvDivh2 = DV / h2;
    }


    /**
     * Calculate new values on an edge.
     */
    private void calcEdge(int x, int y, double dt) {

        double uv2 = model_.tmpU[x][y] * model_.tmpV[x][y] * model_.tmpV[x][y];
        model_.u[x][y] = calcNewEdge(model_.tmpU, x, y, model_.getWidth(), model_.getHeight(), duDivh2, true, uv2, dt);
        model_.v[x][y] = calcNewEdge(model_.tmpV, x, y, model_.getWidth(), model_.getHeight(), dvDivh2, false, uv2, dt);
    }


    private double calcNewCenter(double[][] tmp, int x, int y,
                                 double dDivh2, boolean useF, double uv2, double dt) {

        double sum = tmp[x + 1][y] + tmp[x - 1][y] +
                tmp[x][y + 1] + tmp[x][y - 1] -
                4 * tmp[x][y];
        return calcNewAux(tmp, x, y, sum, dDivh2, useF, uv2, dt);
    }


    private double calcNewEdge(double[][] tmp, int x, int y, int ww, int hh,
                               double dDivh2, boolean useF, double uv2, double dt) {

        double sum = tmp[getPeriodicXValue(x + 1, ww)][y] + tmp[getPeriodicXValue(x - 1, ww)][y] +
                tmp[x][getPeriodicXValue(y + 1, hh)] + tmp[x][getPeriodicXValue(y - 1, hh)] -
                4 * tmp[x][y];

        return calcNewAux(tmp, x, y, sum, dDivh2, useF, uv2, dt);
    }


    private double calcNewAux(double[][] tmp, int x, int y, double sum,
                              double dDivh2, boolean useF, double uv2, double dt) {
        double txy = tmp[x][y];
        double c = useF ? -uv2 + model_.getF() * (1.0 - txy)
                        :  uv2 - model_.getK() * txy;

        double newVal = txy + dt * (dDivh2 * sum  + c);
        if (newVal < 0) {
            return 0;
        } else if (newVal > 1.0 || Double.isInfinite(newVal)) {
            return 1.0;
        } else {
            return newVal;
        }
    }

    /**
     * Periodic boundary conditions.
     * @return new x value taking into account wrapping boundaries.
     */
    private static int getPeriodicXValue(int x, int max) {
        int xp = x;
        while (xp < 0) {
            xp += max;
        }
        while (xp >= max) {
            xp -= max;
        }
        return xp;
    }
}
