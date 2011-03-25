package com.becker.simulation.liquid.model;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 *  A region of space containing particles, walls, or liquid.
 *  Adapted from work by Nick Foster.
 *
 *               ^  vjp_[cur_]     (positive v direction)
 *               |
 *          _____________
 *         |            |
 *    <--  |      p     |  --> uip_[cur_]   (positive u dir)
 *         |            |
 *          ------------
 *               |
 *               v
 *
 *          |--- dx ---|
 *
 *  @author Barry Becker
 */
public class Cell {

    /**  4 if 2d, 6 if 3d */
    private static final int NUM_CELL_FACES = 4;

    private static final double CELL_SIZE = 1.0;

    /** type of cell  */
    private CellStatus status;

    /** size of a cell */
    private final double dx;
    private final double dy;

    /** squares of edge lengths */
    private final double dxSq;
    private final double dySq;

    /** pressure at the center of the cell */
    private double pressure;

    /** velocities in x, y directions defined at the center of each face */
    private CellVelocity velocity;

    /** Number of particles in this cell */
    private int numParticles;

    /**
     * constructor
     */
    public Cell()  {
        pressure = 0;
        velocity = new CellVelocity();

        numParticles = 0;
        status = CellStatus.EMPTY;

        // cell dimensions
        dx = dy = CELL_SIZE;
        dxSq = dx * dx;
        dySq = dy * dy;
    }

    /**
     *  global swap of fields (use with care). (hack)
     */
    public void swap()  {
        velocity.step();
    }

    public void setPressure( double p ) {
        pressure = p;
    }
    public double getPressure() {
        return pressure;
    }

    public void initializeU(double u) {
        velocity.initializeU(u);
    }
    public void initializeV(double v) {
         velocity.initializeV(v);
    }

    public void initializeVelocity(double u, double v) {
        velocity.initialize(u, v);
    }

    public double getU() {
        return velocity.getU();
    }
    public double getV() {
       return velocity.getV();
    }

    public void incParticles() {
        numParticles++;
    }
    public void decParticles() {
        numParticles--;
        assert numParticles >=0;
    }

    public int getNumParticles() {
        return numParticles;
    }

    public CellStatus getStatus() {
        return status;
    }

    public boolean isSurface() {
        return status == CellStatus.SURFACE;
    }
    public boolean isEmpty() {
        return status == CellStatus.EMPTY;
    }
    public boolean isFull() {
        return status == CellStatus.FULL;
    }
    public boolean isObstacle() {
        return status == CellStatus.OBSTACLE;
    }
    public boolean isIsolated() {
        return status == CellStatus.ISOLATED;
    }

    public void setStatus( CellStatus status ) {
        this.status = status;
    }

    /**
     * Compute the cell's new status based on numParticles inside and
     * the status of neighbors.
     * RISK: 1
     */
    public void updateStatus( CellNeighbors neighbors ) {

        assert (numParticles >= 0) : "num particles less than 0.";
        
        if ( status == CellStatus.OBSTACLE ) {
            // obstacles never change status
        }
        else if ( numParticles == 0 ) {
            status = CellStatus.EMPTY;
        }
        else {
            if ( neighbors.allHaveParticles() ) {
                status = CellStatus.FULL;
            }
            else if (neighbors.noneHaveParticles() ) {
                // warning: not present in original foster code.
                status = CellStatus.ISOLATED;
            }
            else {
                status = CellStatus.SURFACE;
            }
        }
    }

    /**
     * compute velocity at next time step given neighboring cells.
     *      cXm1Yp1    top
     *       left       M      right
     *        0      bottom    cXp1Ym1
     *
     * The formulas here equate to a numerical solution
     * of the Navier-Stokes equation.
     * RISK:5
     */
    public void updateTildeVelocities(CellNeighbors neighbors,
                                      Cell cXm1Yp1, Cell cXp1Ym1,
                                      double dt, double forceX, double forceY, double viscosity ) {

        // if not FULL, then the old velocity will be the new.
        if ( status != CellStatus.FULL ) {
            velocity.passThrough();
            return;
        }
        assert  (dt > 0.0000001) : "dt got too small";

        // u
        // u(i, j) = 0.5*(u(i+0.5, j) + u(i-0.5, j))
        double u_i = (getU() + neighbors.getLeft().getU()) / 2.0;
        
        // u(i+1, j) = 0.5*(u(i+1.5, j) + u(i+0.5, j))
        double u_ip1 = (neighbors.getRight().getU() + getU()) / 2.0;

        // u(i+0.5, j-0.5) = 0.5*(u(i+0.5, j) + u(i+0.5, j-1))
        double u_ipjm = (getU() + neighbors.getBottom().getU()) / 2.0;

        // v(i+0.5, j-0.5) = 0.5*(v(i, j-0.5) + v(i+1, j-0.5))
        double v_ipjm = (neighbors.getBottom().getV() + cXp1Ym1.getV()) / 2.0;

        // u(i+0.5, j+0.5) = 0.5*(u(i+0.5, j) + u(i+0.5, j+1))
        double u_ipjp = (getU() + neighbors.getTop().getU()) / 2.0;

        // v(i+0.5, j+0.5) = 0.5*(v(i, j+0.5) + v(i+1, j+0.5))
        double v_ipjp = (getV() + neighbors.getRight().getV()) / 2.0;

        if ( !neighbors.getRight().isObstacle() ) {
            double xNume = (u_i * u_i  -  u_ip1 * u_ip1);
            double yNume = (u_ipjm * v_ipjm  -  u_ipjp * v_ipjp);
            double v1 = (neighbors.getRight().getU() - 2 * getU()
                                 + neighbors.getLeft().getU()) / dxSq;
            double v2 = (neighbors.getTop().getU() - 2 * getU()
                                 + neighbors.getBottom().getU()) / dySq;
            double pf = xNume/ dx + yNume / dy + forceX
                               + (pressure + neighbors.getRight().getPressure()) / dx
                               + viscosity * (v1 + v2);
            double newu =  getU() + dt * pf;
            /*
            if (Math.abs(pf) > 10) {
                System.out.println("much bigger x change than expected. oldu ="
                  + uip_[current_] + " newu="+ newu
                        + " forceX=" + forceX + " forceY="+forceY
                        + "\ncXp1=" + cXp1 + " cXm1=" + cXm1
                        + "\ncXp1=" + cYp1 + " cXm1=" + cYm1
                        + "\ncXp1Ym1=" + cXp1Ym1 + " cXm1Yp1=" + cXm1Yp1);
            } */
            velocity.setNewU(newu);
        }

        // v
        // u(i-0.5, j+0.5) = 0.5*(u(i-0.5, j) + u(i-0.5, j+1))
        double u_imjp = (neighbors.getLeft().getU() + cXm1Yp1.getU()) / 2.0;

        // v(i-0.5, j+0.5) = 0.5*(v(i, j+0.5) + v(i-1, j+0.5))
        double v_imjp = (getV() + neighbors.getBottom().getV()) / 2.0;

        // v(i, j) = 0.5*(v(i, j-0.5) + v(i, j+0.5))
        double v_j = (neighbors.getBottom().getV() + getV()) / 2.0;

        // v(i, j+1) = 0.5*(v(i, j+0.5) + v(i, j+1.5) // / 2.0 was not here originally
        double v_jp1 = (getV() + neighbors.getTop().getV()) / 2.0;

        if ( !neighbors.getTop().isObstacle() ) {
            double xNume = (u_imjp * v_imjp - u_ipjp * v_ipjp);
            double yNume = (v_j * v_j - v_jp1 * v_jp1);
            double v1 =  (neighbors.getRight().getV() - 2 * getV()
                       + neighbors.getLeft().getV()) / dxSq;
            double v2 =  (neighbors.getTop().getV() - 2 * getV()
                        + neighbors.getBottom().getV()) / dySq;
            double pf = xNume / dx + yNume / dy + forceY
                    + (pressure - neighbors.getTop().getPressure()) / dy
                    + viscosity * (v1 + v2);
            double newv =  getV() + dt * pf;
            /*
            if (Math.abs(pf) > 5.0) {
                System.out.println("much bigger y change than expected. oldv ="
                      + vjp_[current_] + " newv="+ newv
                        + "\nforceX=" + forceX + " forceY="+forceY
                        + "\ncXp1=" + cXp1 + " cXm1=" + cXm1
                        + "\ncXp1=" + cYp1 + " cXm1=" + cYm1
                        + "\ncXp1Ym1=" + cXp1Ym1 + " cXm1Yp1=" + cXm1Yp1);
            } */
             velocity.setNewV(newv);
        }
    }

    /**
     * Update pressure and velocities to satisfy mass conservation.
     * What is the intuitive meaning of b0?
     * RISK:3
     * @return the amount of divergence from the cell that
     * we will need to dissipate.
     */
    public double updateMassConservation( double b0, double dt,  CellNeighbors neighbors) {

        if ( !isFull() ) {
            return 0;
        }

        // divergence of fluid within the cell.
        double divergence = (neighbors.getLeft().getU() - getU()) / dx +
                (neighbors.getBottom().getV() - getV()) / dy;

        double b = b0 / (dt * (2.0 / dxSq + 2.0 / dySq));

        // the change in pressure for a cell.
        double dp = b * divergence;
        double dpdx = dt *dp / dx;
        double dpdy = dt *dp / dy;

        if ( !neighbors.getRight().isObstacle() ) {
            velocity.incrementU(dpdx);
        }

        if ( !neighbors.getLeft().isObstacle() ) {
            neighbors.getLeft().velocity.incrementU(-dpdx);
        }

        if ( !neighbors.getTop().isObstacle() ) {
            velocity.incrementV(dpdy);
        }

        if ( !neighbors.getBottom().isObstacle() ) {
            neighbors.getBottom().velocity.incrementV(-dpdy);
        }

        pressure += dp;
        return Math.abs( divergence );
    }

    /**
     * linearly interpolate the velocity of the particle based on its position
     * relative to 4 neighboring velocity vectors.
     * There are 4 cases: The numbers indicate the parameter
     *   case 1: particle in the upper right corner: 6 distinct cells are
     *                passed in this pattern
     *          4  2  X
     *          3  c  1
     *          X  5  6
     *  case 2: particle in upper left
     *         4   2   X
     *        1/3  c   X
     *         6   5   X
     *  case 3: lower right.
     *          X   X   X
     *          3   c   1
     *          4  2/5  6
     *  case 4: particle in lower left.
     *         X    X   X
     *        1/3   c   X
     *        4/6 2/5   X
     *
     * RISK: 1
     * @param cX either one forward or one back in the x direction
     *      depending on the position of the particle. [1]
     * @param cY either one forward or one back in the y direction
     *       depending on the position of the particle. [2]
     * @param cXm1  x- 1  (always the cell to the left)      [3]
     * @param cXm1y x - 1 and either one forward or one back in the y 
     *       direction depending on the position of the particle. [4]
     * @param cYm1  y - 1  (always the cell to the bottom)       [5]
     * @param cYm1x  y - 1 and either one forward or one back in the x 
     *       direction depending on the position of the particle.  [6]
     * @return the interpolated velocity vector.
     */
    public Vector2d interpolateVelocity( Point2d particle,
                                     Cell cX, Cell cY,
                                     Cell cXm1, Cell cXm1y, // u
                                     Cell cYm1, Cell cYm1x)  // v
    {
        assert ( status != CellStatus.OBSTACLE
                      && status != CellStatus.EMPTY && numParticles >= 0 ) :
             "Error: interpVelocity cell status=" + status
               + " num particles = " + numParticles;

        double x = particle.x - Math.floor(particle.x);
        double y = particle.y - Math.floor(particle.y);

        double xx = (x > 0.5) ? (1.5 - x) : (0.5 + x);
        double yy = (y > 0.5) ? (1.5 - y) : (0.5 + y);
        double x1 = (1.0 - x) * cXm1.getU() + x *getU();
        double x2 = (1.0 - x) * cXm1y.getU() + x * cY.getU();
        double pu = x1 * yy + x2 * (1.0 - yy);
        double y1 = (1.0 - y) * cYm1.getV() + y * getV();
        double y2 = (1.0 - y) * cYm1x.getV() + y * cX.getV();
        double pv = y1 * xx + y2 * (1.0 - xx);

        return new Vector2d(pu, pv);
    }

    /**
     * Force no divergence in surface cells, by updating velocities directly.
     * Any overflow will be dissipated.
     * @param neighbors the cell's immediate neighbors
     * @param pressure0 base pressure to set after dissipating overflow.
     * RISK:1
     */
    public void updateSurfaceVelocities( CellNeighbors neighbors, double pressure0 ) {

        // only surface cells can have overflow dissipated.
        if ( !(isSurface() || isIsolated()) ) {
            return;
        }

        int count = 0;
        double overflow = 0;

        if ( !neighbors.getRight().isEmpty() ) {
            count++;
            overflow += getU() / dx;
        }
        if ( !neighbors.getLeft().isEmpty() ) {
            count++;
            overflow -= neighbors.getLeft().getU() / dx;
        }
        if ( !neighbors.getTop().isEmpty() ) {
            count++;
            overflow += getV() / dy;
        }
        if ( !neighbors.getBottom().isEmpty() ) {
            count++;
            overflow -= neighbors.getBottom().getV() / dy;
        }

        if ( count < NUM_CELL_FACES && Math.abs( overflow ) > 0.0 ) {
            dissipateOverflow( (NUM_CELL_FACES - count), overflow, neighbors );
        }

        pressure = pressure0;
    }

    /**
     * Ensure that what comes in must also go out.
     * cXp1 stands for the neighbor cell that is located at +1 in X direction.
     * The overflow is equally distributed to the open adjacent surfaces.
     * @param numSurfaces number of empty adjacent cells. In other
     *     words the number of surfaces we have.
     * @param overflow the overflow to dissipate out the
     *     surface sides that do not have liquid.
     * RISK:3
     */
    private void dissipateOverflow(int numSurfaces, double overflow,
                                   CellNeighbors neighbors) {

        if (Math.abs(overflow) > 100) {
            System.out.println("dissipating large overflow ="+overflow);
        }

        int count = 0;
        double overflowX = dx * overflow / numSurfaces;
        double overflowY = dy * overflow / numSurfaces;

        if ( neighbors.getRight().isEmpty() ) {
            count++;
            velocity.setCurrentU(-overflowX);
        }
        if (neighbors.getLeft().isEmpty() ) {
           count++;
           neighbors.getLeft().velocity.setCurrentU(overflowX);
        }

        if ( neighbors.getTop().isEmpty() ) {
           count++;
           velocity.setCurrentV(-overflowY);
        }
        if ( neighbors.getBottom().isEmpty() ) {
            count++;
            neighbors.getBottom().velocity.setCurrentV(overflowY);
        }
        assert (count == numSurfaces);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Cell:");
        sb.append(status);
        sb.append(" num particles=").append(numParticles);
        sb.append(" pressure=").append(pressure);
        sb.append(" ").append(velocity);
        return sb.toString();
    }
}