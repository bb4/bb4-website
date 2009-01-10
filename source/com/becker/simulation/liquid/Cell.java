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
        dx_ = dy_ = 1.0;
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

    public void setStatus( CellStatus status )
    {
        status_ = status;
    }

    /**
     * Compute the cells new status based on numParticles inside and the status of neighbors.
     * cXp1 stands for the neighbor cell that is +1 in the x direction.
     * cYm1 stands for the neighbor cell that is -1 in the y direction.
     */
    public void updateStatus( Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1 )
    {
        if ( numParticles_ < 0 ) {
            assert false : "num particles less than 0";
        }
        else if ( status_ == CellStatus.OBSTACLE ) {
            // obstacles never change status
        }
        else if ( numParticles_ == 0 ) {
            status_ = CellStatus.EMPTY;
        }
        else {
            if ( cXp1.getNumParticles() > 0 && cXm1.getNumParticles() > 0 &&
                    cYp1.getNumParticles() > 0 && cYm1.getNumParticles() > 0 ) {
                status_ = CellStatus.FULL;
            }
            else if ( cXp1.getNumParticles() == 0 && cXm1.getNumParticles() == 0 &&
                    cYp1.getNumParticles() == 0 && cYm1.getNumParticles() == 0 ) {
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
     * The formulas here equate to a numerical solution of the Navioer-Stokes equation.
     * RISK:5
     */
    public void updateTildeVelocities( Cell cXp1, Cell cXm1,
                                       Cell cYp1, Cell cYm1,
                                       Cell cXp1Ym1, Cell cXm1Yp1,
                                       double dt, double forceX, double forceY, double viscosity )
    {
        // only for FULL cells.
        if ( status_ != CellStatus.FULL ) {
            uip_[1 - current_] = uip_[current_] + dt * forceX;
            vjp_[1 - current_] = vjp_[current_] + dt * forceY;
            return;
        }
        assert  (dt > 0.000001) : "dt got too small";

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

        double xNume = (u_i * u_i  -  u_ip1 * u_ip1);
        double yNume = (u_ipjm * v_ipjm  -  u_ipjp * v_ipjp);
        double v1 = (cXp1.uip_[current_] - 2 * uip_[current_] + cXm1.uip_[current_]) / dxSq_;
        double v2 = (cYp1.uip_[current_] - 2 * uip_[current_] + cYm1.uip_[current_]) / dySq_;
        double pf = xNume/ dx_ + yNume / dy_ +
                    forceX + (pressure_ + cXp1.getPressure()) / dx_ + viscosity * (v1 + v2);

        if ( cXp1.getStatus() != CellStatus.OBSTACLE ) {
            double newu =  uip_[current_] + dt * ( pf );
            /*
            if (Math.abs(pf) > 10) {
                System.out.println("much bigger x change than expected. oldu ="+ uip_[current_] + " newu="+ newu + " forceX=" + forceX + " forceY="+forceY
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
        // v(i, j+1) = 0.5*(v(i, j+0.5) + v(i, j+1.5))
        double v_jp1 = (vjp_[current_] + cYp1.vjp_[current_]) / 2.0;    // / 2.0 was not here originally

        xNume = (u_imjp * v_imjp - u_ipjp * v_ipjp);
        yNume = (v_j * v_j - v_jp1 * v_jp1);
        v1 =  (cXp1.vjp_[current_] - 2 * vjp_[current_] + cXm1.vjp_[current_]) / dxSq_;
        v2 =  (cYp1.vjp_[current_] - 2 * vjp_[current_] + cYm1.vjp_[current_]) / dySq_;
        pf = xNume / dx_ + yNume / dy_ +
                forceY + (pressure_ + cYp1.getPressure()) / dy_ + viscosity * (v1 + v2);

        if ( cYp1.getStatus() != CellStatus.OBSTACLE ) {
            double newv =  vjp_[current_] + dt * ( pf );
            /*
            if (Math.abs(pf) > 10) {
                System.out.println("much bigger y change than expected. oldv ="+ vjp_[current_] + " newv="+ newv + " forceX=" + forceX + " forceY="+forceY
                        + "\ncXp1=" + cXp1 + " cXm1=" + cXm1
                        + "\ncXp1=" + cYp1 + " cXm1=" + cYm1
                        + "\ncXp1Ym1=" + cXp1Ym1 + " cXm1Yp1=" + cXm1Yp1);
            } */
            vjp_[1 - current_] = newv;
        }
        //String message = ("updateTilde: new vel is:"+v_jp[1-cur_]+", forceY="+dt*forceY);
        //JOptionPane.showMessageDialog(null,message);
        //System.err.println("updateTilde: new vel is:"+v_jp[1-cur_]+", forceY="+dt*forceY);
        /*
        if (uip_[1 - current_]!=0 || vjp_[1 - current_]!=0) {
            System.out.println("updateTilde: new vel is:"+uip_[1-current_]+", "+vjp_[1-current_]);
            System.out.println("updateTilde: u_i="+u_i+" u_ip1="+u_ip1+" u_ipjm="+u_ipjm+" v_ipjm="+v_ipjm+"u_ipjp="+u_ipjp+"v_ipjp="+v_ipjp);
            System.out.println("updateTilde: v_j="+v_j+" v_imjp="+u_imjp+" v_imjp="+v_imjp+" v_jp1="+v_jp1);
            System.out.println(this);
            System.out.println("");
        }*/
    }

    /**
     * Update pressure and velocities to satisfy mass conservation.
     * What is the intuitive meaning of b0?
     * RISK:5
     */
    public double updateMassConservation( double b0, double dt,
                                          Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1 )
    {
        if ( status_ != CellStatus.FULL ) {
            return 0;
        }

        // divergence of fluid within the cell.
        double divergence = (cXm1.uip_[current_] - uip_[current_]) / dx_ +
                (cYm1.vjp_[current_] - vjp_[current_]) / dy_;

        double b = b0 / (dt * (2.0 / dxSq_ + 2.0 / dySq_));

        // the change in pressure for a cell.
        double dp = b * divergence;
        double dpdx = (dt / dx_) * dp;
        double dpdy = (dt / dy_) * dp;

        if ( cXp1.getStatus() != CellStatus.OBSTACLE ) {
            uip_[current_] += dpdx;
        }

        if ( cXm1.getStatus() != CellStatus.OBSTACLE ) {
            cXm1.uip_[current_] -= dpdx;
        }

        if ( cYp1.getStatus() != CellStatus.OBSTACLE ) {
            vjp_[current_] += dpdy;
        }

        if ( cYm1.getStatus() != CellStatus.OBSTACLE ) {
            cYm1.vjp_[current_] -= dpdy;
        }

        pressure_ += dp;
        return Math.abs( divergence );
    }

    /**
     * linearly interpolate the velocity of the particle based on its position
     * relative to 4 neighboring velocity vectors.
     * There are 4 cases: The numbers indicate the parameter
     *   case 1: particle in the upper right corner: 6 distinct cells are passed in this pattern
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
     * @param cX either one forward or one back in the x direction depending on the position of the particle. [1]
     * @param cY either one forward or one back in the y direction depending on the position of the particle. [2]
     * @param cXm1  x- 1  (always the cell to the left)                                                                                                [3]
     * @param cXm1y x - 1 and either one forward or one back in the y direction depending on the position of the particle. [4]
     * @param cYm1  y - 1  (always the cell to the bottom)                                                                                                [5]
     * @param cYm1x  y - 1 and either one forward or one back in the x direction depending on the position of the particle.  [6]
     */
    public Vector2d interpolateVelocity( Point2d particle,
                                     Cell cX, Cell cY,
                                     Cell cXm1, Cell cXm1y, // u
                                     Cell cYm1, Cell cYm1x)  // v
    {
        assert ( status_ != CellStatus.OBSTACLE && status_ != CellStatus.EMPTY && numParticles_ >= 0 ) :
             "Error: interpVelocity cell status=" + status_ + " num particles = " + numParticles_ ;

        double x = particle.x - (int) particle.x;
        double y = particle.y - (int) particle.y;

        double xx = (x > 0.5) ? (1.5 - x) : (0.5 + x);
        double yy = (y > 0.5) ? (1.5 - y) : (0.5 + y);
        double x1 = (1.0 - x) * cXm1.uip_[current_] + x * uip_[current_];
        double x2 = (1.0 - x) * cXm1y.uip_[current_] + x * cY.uip_[current_];
        double pu = x1 * yy + x2 * (1.0 - yy);
        double y1 = (1.0 - y) * cYm1.vjp_[current_] + y * vjp_[current_];
        double y2 = (1.0 - y) * cYm1x.vjp_[current_] + y * cX.vjp_[current_];
        double pv = y1 * xx + y2 * (1.0 - xx);

        /*if (pu!=0 || pv!=0) {
            System.out.println(" nearby velocities:
                            ip="+u_ip[cur_]+", jp="+v_jp[cur_]+",
                            xm1_ip="+cXm1.u_ip[cur_]+", xm1_jp="+cXm1.v_jp[cur_]+",
                            xm1y_ip="+cXm1y.u_ip[cur_]+", xym1_jp="+cXym1.v_jp[cur_]);
            System.out.println(" interpolated velocity = "+pu+", "+pv);
        }*/
        return new Vector2d(pu, pv);
    }

    /**
     * Force no divergence in surface cells, by updating velocities directly.
     * RISK 5
     */
    public void updateSurfaceVelocities( Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1,
                                         double pressure0 )
    {
        if ( status_ != CellStatus.SURFACE ) {
            return;
        }

        int count = 0;
        double overflow = 0;

        if ( cXp1.getStatus() != CellStatus.EMPTY ) {
            count++;
            overflow += uip_[current_] / dx_;
        }
        if ( cXm1.getStatus() != CellStatus.EMPTY ) {
            count++;
            overflow -= cXm1.uip_[current_] / dx_;
        }
        if ( cYp1.getStatus() != CellStatus.EMPTY ) {
            count++;
            overflow += vjp_[current_] / dy_;
        }
        if ( cYm1.getStatus() != CellStatus.EMPTY ) {
            count++;
            overflow -= cYm1.vjp_[current_] / dy_;
        }

        if ( count < NUM_CELL_FACES && Math.abs( overflow ) > 0.0 ) {
            dissipateOverflow( (NUM_CELL_FACES - count), overflow, cXp1, cXm1, cYp1, cYm1 );
        }

        pressure_ = pressure0;
    }

    /**
     * Ensure that what comes in must also go out.
     * cXp1 stands for the neighbor cell that is located at +1 in the X direction.
     * @param numSurfaces number of empty adjacent cells. In other words the number of surfaces we have.
     * @param overflow the overflow to dissapate out the surface sides
     * RISK:5
     */
    private void dissipateOverflow( int numSurfaces, double overflow,
                                    Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1 )
    {
        if (Math.abs(overflow) > 1000) {
            System.out.println("dissipating large overflow ="+overflow);
        }
        if ( cXp1.getStatus() == CellStatus.EMPTY ) {
            if ( numSurfaces == 1 )
                uip_[current_] = -dx_ * overflow;
            else if ( (numSurfaces == 3 && cXm1.getStatus() != CellStatus.EMPTY) || (numSurfaces == 2) )
                uip_[current_] = cXm1.uip_[current_];
        }
        if ( cXm1.getStatus() == CellStatus.EMPTY ) {
            if ( numSurfaces == 1 )
                cXm1.uip_[current_] = dx_ * overflow;
            else if ( (numSurfaces == 3 && cXp1.getStatus() != CellStatus.EMPTY) || (numSurfaces == 2) )
                cXm1.uip_[current_] = uip_[current_];
        }

        if ( cYp1.getStatus() == CellStatus.EMPTY ) {
            if ( numSurfaces == 1 )
                vjp_[current_] = -dy_ * overflow;
            else if ( (numSurfaces == 3 && cYm1.getStatus() != CellStatus.EMPTY) || (numSurfaces == 2) )
                vjp_[current_] = cYm1.vjp_[current_];
        }
        if ( cYm1.getStatus() == CellStatus.EMPTY ) {
            if ( numSurfaces == 1 )
                cYm1.vjp_[current_] = dy_ * overflow;
            else if ( (numSurfaces == 3 && cYp1.getStatus() != CellStatus.EMPTY) || (numSurfaces == 2) )
                cYm1.vjp_[current_] = vjp_[current_];
        }
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
        sb.append(" velocity="+ this.uip_[current_] +", " + this.vjp_[current_]);
        return sb.toString();
    }
}