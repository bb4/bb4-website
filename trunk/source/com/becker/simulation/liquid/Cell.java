package com.becker.simulation.liquid;
import javax.vecmath.*;

/**
 *  A region of space containing particles, walls, or liquid.
 *  Adapted from work by Nick Foster.
 *
 *                   ^  vjp_[cur_]     pos v direction
 *                   |
 *              _______
 *             |             |
 *     <--  |      p      |  --> uip_[cur_]
 *             |             |
 *              -----------
 *                   |
 *                   v
 *
 *              |-- dx --|
 *
 *  @author Barry Becker
 */
public class Cell
{

    /**  4 if 2d, 6 if 3d, 12 if 4d */
    private static final int NUM_CELL_FACES = 4;

    private static final double CELL_SIZE = 1.0;

    /** type of cell  */
    private CellStatus status_;

    /** size of a cell */
    private final double dx_;
    private final double dy_;

    /** squares of edge lengths */
    private final double dxSq_;
    private final double dySq_;

    /** pressure at the center of the cell */
    private double pressure_;

    /**
     * velocities in x, y directions
     * defined at the center of each face
     * uip_ = u(i+0.5, j, k)
     * vjp_ = v(i, j+0.5, k)
     */
    private final double[] uip_ = new double[2];
    private final double[] vjp_ = new double[2];

    private int numParticles_;

    /** use this to switch between current and last copies of fields. (hack) */
    private static int current_;

    /**
     * constructor
     */
    public Cell()
    {
        pressure_ = 0;
        uip_[0] = uip_[1] = 0;
        vjp_[0] = vjp_[1] = 0;

        numParticles_ = 0;
        status_ = CellStatus.EMPTY;

        // cell dimensions
        dx_ = dy_ = CELL_SIZE;
        dxSq_ = dx_ * dx_;
        dySq_ = dy_ * dy_;
    }

    /**
     *  global swap of fields (use with care). (hack)
     */
    public static void swap()
    {
        current_ = 1 - current_;
    }

    public void setPressure( double p )
    {
        pressure_ = p;
    }
    public double getPressure()
    {
        return pressure_;
    }

    public void setUip( double u )
    {
        uip_[0] = uip_[1] = u;
    }
    public void setVjp( double v )
    {
        vjp_[0] = vjp_[1] = v;
    }

    public double getUip()
    {
        return uip_[current_];
    }
    public double getVjp()
    {
        return vjp_[current_];
    }

    public void incParticles()
    {
        numParticles_++;
    }
    public void decParticles()
    {
        numParticles_--;
    }

    public int getNumParticles()
    {
        return numParticles_;
    }

    public CellStatus getStatus()
    {
        return status_;
    }

    public boolean isSurface() {
        return status_ == CellStatus.SURFACE;
    }
    public boolean isEmpty() {
        return status_ == CellStatus.EMPTY;
    }
    public boolean isFull() {
        return status_ == CellStatus.FULL;
    }
    public boolean isObstacle() {
        return status_ == CellStatus.OBSTACLE;
    }
    public boolean isIsolated() {
        return status_ == CellStatus.ISOLATED;
    }

    public void setStatus( CellStatus status )
    {
        status_ = status;
    }

    /**
     * Compute the cell's new status based on numParticles inside and
     * the status of neighbors.
     * cXp1 stands for the neighbor cell that is +1 in the x direction.
     * cYm1 stands for the neighbor cell that is -1 in the y direction.
     * RISK: 1
     */
    public void updateStatus( Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1 )
    {
        assert (numParticles_ >= 0) : "num particles less than 0.";
        
        if ( status_ == CellStatus.OBSTACLE ) {
            // obstacles never change status
            return;
        }
        else if ( numParticles_ == 0 ) {
            status_ = CellStatus.EMPTY;
        }
        else {
            if ( cXp1.getNumParticles() > 0
                    && cXm1.getNumParticles() > 0
                    && cYp1.getNumParticles() > 0
                    && cYm1.getNumParticles() > 0 ) {
                status_ = CellStatus.FULL;
            }
            else if ( cXp1.getNumParticles() == 0
                    && cXm1.getNumParticles() == 0 &&
                    cYp1.getNumParticles() == 0
                    && cYm1.getNumParticles() == 0 ) {
                // warning: not present in original foster code.
                status_ = CellStatus.ISOLATED;   
            }
            else {
                status_ = CellStatus.SURFACE;
            }
        }
    }

    /**
     * compute velocity at next time step given nieghboring cells.
     *         O        cYp1  cXp1Yp1
     *       cXm1       M       cXp1
     *     cXm1Yp1  cYm1     O
     *
     * The formulas here equate to a numerical solution
     * of the Navioer-Stokes equation.
     * RISK:5
     */
    public void updateTildeVelocities(
                          Cell cXp1, Cell cXm1,
                           Cell cYp1, Cell cYm1,
                           Cell cXp1Ym1, Cell cXm1Yp1,
                           double dt, double forceX, double forceY, double viscosity )
    {
        // only for FULL cells.
        if ( status_ != CellStatus.FULL ) {
            uip_[1 - current_] = uip_[current_]; // + dt * forceX;
            vjp_[1 - current_] = vjp_[current_]; // + dt * forceY;
            return;
        }
        assert  (dt > 0.0000001) : "dt got too small";

        // u
        // u(i, j) = 0.5*(u(i+0.5, j) + u(i-0.5, j))
        double u_i = (uip_[current_] + cXm1.uip_[current_]) / 2.0;
        
        // u(i+1, j) = 0.5*(u(i+1.5, j) + u(i+0.5, j))
        double u_ip1 = (cXp1.uip_[current_] + uip_[current_]) / 2.0;

        // u(i+0.5, j-0.5) = 0.5*(u(i+0.5, j) + u(i+0.5, j-1))
        double u_ipjm = (uip_[current_] + cYm1.uip_[current_]) / 2.0;

        // v(i+0.5, j-0.5) = 0.5*(v(i, j-0.5) + v(i+1, j-0.5))
        double v_ipjm = (cYm1.vjp_[current_] + cXp1Ym1.vjp_[current_]) / 2.0;

        // u(i+0.5, j+0.5) = 0.5*(u(i+0.5, j) + u(i+0.5, j+1))
        double u_ipjp = (uip_[current_] + cYp1.uip_[current_]) / 2.0;

        // v(i+0.5, j+0.5) = 0.5*(v(i, j+0.5) + v(i+1, j+0.5))
        double v_ipjp = (vjp_[current_] + cXp1.vjp_[current_]) / 2.0;

        if ( !cXp1.isObstacle() ) {
            double xNume = (u_i * u_i  -  u_ip1 * u_ip1);
            double yNume = (u_ipjm * v_ipjm  -  u_ipjp * v_ipjp);
            double v1 = (cXp1.uip_[current_] - 2 * uip_[current_]
                                 + cXm1.uip_[current_]) / dxSq_;
            double v2 = (cYp1.uip_[current_] - 2 * uip_[current_]
                                 + cYm1.uip_[current_]) / dySq_;
            double pf = xNume/ dx_ + yNume / dy_ + forceX
                               + (pressure_ + cXp1.getPressure()) / dx_
                               + viscosity * (v1 + v2);
            double newu =  uip_[current_] + dt * pf;
            /*
            if (Math.abs(pf) > 10) {
                System.out.println("much bigger x change than expected. oldu ="
                  + uip_[current_] + " newu="+ newu
                        + " forceX=" + forceX + " forceY="+forceY
                        + "\ncXp1=" + cXp1 + " cXm1=" + cXm1
                        + "\ncXp1=" + cYp1 + " cXm1=" + cYm1
                        + "\ncXp1Ym1=" + cXp1Ym1 + " cXm1Yp1=" + cXm1Yp1);
            } */
            uip_[1 - current_] = newu;
        }

        // v
        // u(i-0.5, j+0.5) = 0.5*(u(i-0.5, j) + u(i-0.5, j+1))
        double u_imjp = (cXm1.uip_[current_] + cXm1Yp1.uip_[current_]) / 2.0;

        // v(i-0.5, j+0.5) = 0.5*(v(i, j+0.5) + v(i-1, j+0.5))
        double v_imjp = (vjp_[current_] + cYm1.vjp_[current_]) / 2.0;

        // v(i, j) = 0.5*(v(i, j-0.5) + v(i, j+0.5))
        double v_j = (cYm1.vjp_[current_] + vjp_[current_]) / 2.0;

        // v(i, j+1) = 0.5*(v(i, j+0.5) + v(i, j+1.5) // / 2.0 was not here originally
        double v_jp1 = (vjp_[current_] + cYp1.vjp_[current_]) / 2.0;   

        

        if ( !cYp1.isObstacle() ) {
            double xNume = (u_imjp * v_imjp - u_ipjp * v_ipjp);
            double yNume = (v_j * v_j - v_jp1 * v_jp1);
            double v1 =  (cXp1.vjp_[current_] - 2 * vjp_[current_]
                       + cXm1.vjp_[current_]) / dxSq_;
            double v2 =  (cYp1.vjp_[current_] - 2 * vjp_[current_]
                        + cYm1.vjp_[current_]) / dySq_;
            double pf = xNume / dx_ + yNume / dy_ + forceY
                    + (pressure_ - cYp1.getPressure()) / dy_
                    + viscosity * (v1 + v2);
            double newv =  vjp_[current_] + dt * pf;
            /*
            if (Math.abs(pf) > 5.0) {
                System.out.println("much bigger y change than expected. oldv ="
                      + vjp_[current_] + " newv="+ newv
                        + "\nforceX=" + forceX + " forceY="+forceY
                        + "\ncXp1=" + cXp1 + " cXm1=" + cXm1
                        + "\ncXp1=" + cYp1 + " cXm1=" + cYm1
                        + "\ncXp1Ym1=" + cXp1Ym1 + " cXm1Yp1=" + cXm1Yp1);
            } */
            vjp_[1 - current_] = newv;
        }
        /*
        if (uip_[1 - current_]!=0 || vjp_[1 - current_]!=0) {
            System.out.println("updateTilde: new vel is:"
                +uip_[1-current_]+", "+vjp_[1-current_]);
            System.out.println("updateTilde: u_i="+u_i+" u_ip1="+u_ip1
                 +" u_ipjm="+u_ipjm+" v_ipjm="+v_ipjm
                 +"u_ipjp="+u_ipjp+"v_ipjp="+v_ipjp);
            System.out.println("updateTilde: v_j="+v_j+" v_imjp="
                 +u_imjp+" v_imjp="+v_imjp+" v_jp1="+v_jp1);
            System.out.println(this);
            System.out.println("");
        }*/
    }

    /**
     * Update pressure and velocities to satisfy mass conservation.
     * What is the intuitive meaning of b0?
     * RISK:3
     * @return the amount of divergence from the cell that
     * we will need to dissapate.
     */
    public double updateMassConservation( double b0, double dt,
                                          Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1 )
    {
        if ( !isFull() ) {
            return 0;
        }

        // divergence of fluid within the cell.
        double divergence = (cXm1.uip_[current_] - uip_[current_]) / dx_ +
                (cYm1.vjp_[current_] - vjp_[current_]) / dy_;

        double b = b0 / (dt * (2.0 / dxSq_ + 2.0 / dySq_));

        // the change in pressure for a cell.
        double dp = b * divergence;
        double dpdx = dt *dp / dx_;
        double dpdy = dt *dp / dy_;

        if ( !cXp1.isObstacle() ) {
            uip_[current_] += dpdx;
        }

        if ( !cXm1.isObstacle() ) {
            cXm1.uip_[current_] -= dpdx;
        }

        if ( !cYp1.isObstacle() ) {
            vjp_[current_] += dpdy;
        }

        if ( !cYm1.isObstacle() ) {
            cYm1.vjp_[current_] -= dpdy;
        }

        pressure_ += dp;
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
     *       1/3  c   X
     *         6   5   X
     *  case 3: lower right.
     *          X   X   X
     *          3   c   1
     *          4 2/5 6
     *  case 4: particle in lower left.
     *         X    X    X
     *       1/3   c    X
     *       4/6 2/5  X
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
     */
    public Vector2d interpolateVelocity( Point2d particle,
                                     Cell cX, Cell cY,
                                     Cell cXm1, Cell cXm1y, // u
                                     Cell cYm1, Cell cYm1x)  // v
    {
        assert ( status_ != CellStatus.OBSTACLE
                      && status_ != CellStatus.EMPTY && numParticles_ >= 0 ) :
             "Error: interpVelocity cell status=" + status_
               + " num particles = " + numParticles_ ;

        double x = particle.x - Math.floor(particle.x);
        double y = particle.y - Math.floor(particle.y);

        double xx = (x > 0.5) ? (1.5 - x) : (0.5 + x);
        double yy = (y > 0.5) ? (1.5 - y) : (0.5 + y);
        double x1 = (1.0 - x) * cXm1.uip_[current_] + x * uip_[current_];
        double x2 = (1.0 - x) * cXm1y.uip_[current_] + x * cY.uip_[current_];
        double pu = x1 * yy + x2 * (1.0 - yy);
        double y1 = (1.0 - y) * cYm1.vjp_[current_] + y * vjp_[current_];
        double y2 = (1.0 - y) * cYm1x.vjp_[current_] + y * cX.vjp_[current_];
        double pv = y1 * xx + y2 * (1.0 - xx);

        return new Vector2d(pu, pv);
    }

    /**
     * Force no divergence in surface cells, by updating velocities directly.
     * Any overflow will be dissipated.
     * @param cXp1 cell to the right
     * @param cXm1 cell to the left
     * @param cYp1 cell below (?)
     * @param cYm1 cell above (?)
     * @param pressure0 base pressure to set after dissipating overflow.
     * RISK:1
     */
    public void updateSurfaceVelocities( 
                                    Cell cXp1, Cell cXm1,
                                    Cell cYp1, Cell cYm1,
                                    double pressure0 )
    {
        // only surface cells can have overflow dissipated.
        if ( !(isSurface() || isIsolated()) ) {
            return;
        }

        int count = 0;
        double overflow = 0;

        if ( !cXp1.isEmpty() ) {
            count++;
            overflow += uip_[current_] / dx_;
        }
        if ( !cXm1.isEmpty() ) {
            count++;
            overflow -= cXm1.uip_[current_] / dx_;
        }
        if ( !cYp1.isEmpty() ) {
            count++;
            overflow += vjp_[current_] / dy_;
        }
        if ( !cYm1.isEmpty() ) {
            count++;
            overflow -= cYm1.vjp_[current_] / dy_;
        }

        if ( count < NUM_CELL_FACES && Math.abs( overflow ) > 0.0 ) {
            dissipateOverflow( (NUM_CELL_FACES - count),
                                          overflow, cXp1, cXm1, cYp1, cYm1 );
        }

        pressure_ = pressure0;
    }

    /**
     * Ensure that what comes in must also go out.
     * cXp1 stands for the neighbor cell that is located at +1 in X direction.
     * The overflow is equally distributed to the open adjacent surfaces.
     * @param numSurfaces number of empty adjacent cells. In other
     *     words the number of surfaces we have.
     * @param overflow the overflow to dissapate out the
     *     surface sides that do not have liquid.
     * RISK:3
     */
    private void dissipateOverflow( int numSurfaces, double overflow,
                                    Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1 )
    {
        if (Math.abs(overflow) > 100) {
            System.out.println("dissipating large overflow ="+overflow);
        }

        int count = 0;
        double overflowX = dx_ * overflow / numSurfaces;
        double overflowY = dy_ * overflow / numSurfaces;

        if ( cXp1.isEmpty() ) {
            count++;
            uip_[current_] = -overflowX;
        }
        if (cXm1.isEmpty() ) {
           count++;
           cXm1.uip_[current_] = overflowX;
        }

        if ( cYp1.isEmpty() ) {
           count++;
           vjp_[current_] = -overflowY;
        }
        if ( cYm1.isEmpty() ) {
            count++;
            cYm1.vjp_[current_] = overflowY;
        }
        assert (count == numSurfaces);
    }

    public void setVelocityP( double u, double v )
    {
        uip_[0] = u;
        uip_[1] = u;
        vjp_[0] = v;
        vjp_[1] = v;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Cell:");
        sb.append(status_);
        sb.append(" num particles="+numParticles_);
        sb.append(" pressure="+pressure_);
        sb.append(" velocity="+ uip_[current_] +", " + vjp_[current_]);
        return sb.toString();
    }
}