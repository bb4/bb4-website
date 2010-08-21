package com.becker.simulation.reactiondiffusion.algorithm;


/**
 * This is the core of the Gray-Scott reaction diffusion simulation implementation.
 * based on an implementation by Joakim Linde and modified by Barry Becker.
 */
final class GrayScottAlgorithm {

    /** We could add scrollbars to scale these */
    private static final double DU = 2.0e-5;
    private static final double DV = 1.0e-5;

    private GrayScottModel model_;

    private double duDivh2;
    private double dvDivh2;

    private int width;
    private int height;


    /**
     * Constructor
     */
    GrayScottAlgorithm(GrayScottModel model) {
        model_ = model;
    }


    public void computeNextTimeStep(int minX, int maxX, double dt) {

        double uv2;
        double[][] u = model_.tmpU;
        double[][] v = model_.tmpV;
        width = model_.getWidth();
        height = model_.getHeight();
        for (int x = minX; x <= maxX; x++) {
            for (int y = 1; y < height - 1; y++) {
                uv2 = u[x][y] * v[x][y] * v[x][y];
                model_.u[x][y] = calcNewCenter(u, x, y, duDivh2, true, uv2, dt);
                model_.v[x][y] = calcNewCenter(v, x, y, dvDivh2, false, uv2, dt);
            }
        }
    }

    public void computeNewEdgeValues(double dt) {

        // top and bottom edges
        for (int x = 0; x < width; x++) {
            calcEdge(x, 0, dt);
            calcEdge(x, height - 1, dt);
        }

         // left and right edges
        for (int y = 0; y < height; y++) {
            calcEdge(0, y, dt);
            calcEdge(width - 1, y, dt);
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
        model_.u[x][y] = calcNewEdge(model_.tmpU, x, y, width, height, duDivh2, true, uv2, dt);
        model_.v[x][y] = calcNewEdge(model_.tmpV, x, y, width, height, dvDivh2, false, uv2, dt);
    }


    private double calcNewCenter(double[][] tmp, int x, int y,
                                 double dDivh2, boolean useF, double uv2, double dt) {

        double sum = tmp[x + 1][y]
                + tmp[x - 1][y]
                + tmp[x][y + 1]
                + tmp[x][y - 1]
                - 4 * tmp[x][y];
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
        return Math.min(1.0, Math.max(0.0, newVal));

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
