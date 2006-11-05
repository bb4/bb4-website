package com.becker.simulation.reactiondiffusion;

/**
 * This is the core of the Gray-Scott reaction diffusion simulation.
 */
final class GrayScott {

    /** default values for constants. */
    public static final double K0 = 0.079;
    public static final double F0 = 0.02;

    private static final double DU = 2.0e-5;
    private static final double DV = 1.0e-5;

    double[][] u_;
    double[][] v_;
    private double[][] tmpU_;
    private double[][] tmpV_;

    private double k_ = 0.059;
    private double f_ = 0.02;

    private double duDivh2_;
    private double dvDivh2_;
    int width_, height_;

    GrayScott(int width, int height, double f, double k, double h) {
        this.width_ = width;
        this.height_ = height;
        this.f_ = f;
        this.k_ = k;

        double h2 = h * h;
        duDivh2_ = DU / h2;
        dvDivh2_ = DV / h2;
        u_ = new double[width][height];
        v_ = new double[width][height];
        tmpU_ = new double[width][height];
        tmpV_ = new double[width][height];

        initialState();
    }

    public void initialState() {


        for (int x = 0; x < width_; x++) {
            for (int y = 0; y < height_; y++) {
                tmpU_[x][y] = 1;
                tmpV_[x][y] = 0;
            }
        }
        int w3 = width_ / 3;
        int h3 = height_ / 3;
        for (int x = 0; x < w3; x++) {
            for (int y = 0; y < h3; y++) {
                tmpU_[w3 + x][h3 + y] = 0.5;
                tmpV_[w3 + x][h3 + y] = 0.25;
            }
        }
        double w7 = (double) width_ / 7;
        double h5 = (double) height_ / 5;
        for (int x = 0; x < w7; x++) {
            for (int y = 0; y < h5; y++) {
                tmpU_[(int) (5 * w7) + x][(int) (3 * h5) + y] = 0.5;
                tmpV_[(int) (5 * w7) + x][(int) (3 * h5) + y] = 0.25;
            }
        }
    }

    public int getWidth() {
        return width_;
    }
    public int getHeight() {
        return height_;
    }

    public double getF() {
        return f_;
    }
    public void setF(double f) {
        this.f_ = f;
    }

    public double getK() {
        return k_;
    }
    public void setK(double k) {
        this.k_ = k;
    }


    /**
     * Advance one time step increment.
     * @param dt time step in seconds.
     */
    public void timeStep(double dt) {

        double uv2;
        /*center*/
        for (int x = 1; x < width_ - 1; x++) {
            for (int y = 1; y < height_ - 1; y++) {
                uv2 = tmpU_[x][y] * tmpV_[x][y] * tmpV_[x][y];
                u_[x][y] = calcNewCenter(tmpU_, x, y, duDivh2_, true, uv2, dt);
                v_[x][y] = calcNewCenter(tmpV_, x, y, dvDivh2_, false, uv2, dt);
            }
        }

        /*edges*/
        int x, y;
        for (x = 0; x < width_; x++) {
            calcEdge(x, 0, dt);
            calcEdge(x, height_ - 1, dt);
        }

        for (y = 0; y < height_; y++) {
            calcEdge(0, y, dt);
            calcEdge(width_ - 1, y, dt);
        }

        for (x = 0; x < width_; x++) {
            for (y = 0; y < height_; y++) {
                tmpU_[x][y] = u_[x][y];
                tmpV_[x][y] = v_[x][y];
            }
        }
    }

    /**
     * Calculate new values on an edge.
     */
    private void calcEdge(int x, int y, double dt) {

        double uv2 = tmpU_[x][y] * tmpV_[x][y] * tmpV_[x][y];
        u_[x][y] = calcNewEdge(tmpU_, x, y, width_, height_, duDivh2_, true, uv2, dt);
        v_[x][y] = calcNewEdge(tmpV_, x, y, width_, height_, dvDivh2_, false, uv2, dt);
    }


    private double calcNewEdge(double[][] tmp, int x, int y, int ww, int hh,
                           double dDivh2, boolean useF, double uv2, double dt) {


        double sum = tmp[pBC(x + 1, ww)][y] + tmp[pBC(x - 1, ww)][y] +
                tmp[x][pBC(y + 1, hh)] + tmp[x][pBC(y - 1, hh)] -
                4 * tmp[x][y];

        return calcNewAux(tmp, x, y, sum, dDivh2, useF, uv2, dt);
    }


    private double calcNewCenter(double[][] tmp, int x, int y,
                                 double dDivh2, boolean useF, double uv2, double dt) {

        double sum = tmp[x + 1][y] + tmp[x - 1][y] +
                tmp[x][y + 1] + tmp[x][y - 1] -
                4 * tmp[x][y];
        return calcNewAux(tmp, x, y, sum, dDivh2, useF, uv2, dt);
    }

    private double calcNewAux(double[][] tmp, int x, int y, double sum,
                                     double dDivh2, boolean useF, double uv2, double dt) {
        double txy = tmp[x][y];
        double c = useF ? -uv2 + f_ * (1.0 - txy)
                        :  uv2 - k_ * txy;

        double newVal = txy + dt * (dDivh2 * sum  + c);
        return (newVal < 0) ? 0 : newVal;
    }

    /**
     * Periodic boundary conditions.
     */
    private static int pBC(int x, int max) {
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
