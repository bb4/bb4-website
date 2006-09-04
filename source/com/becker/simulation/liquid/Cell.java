package com.becker.simulation.liquid;

import javax.vecmath.*;

/**
 *  A region of space containing particles, walls, or liquid.
 *  Adapted from work by Nick Foster.
 *
 *                  ^  vjp_[cur_]     pos v direction
 *                  |
 *              ________
 *              |      |
 *         <--  |   p  |  --> uip[cur_]
 *              |      |
 *              --------
 *                 |
 *                 v
 *
 *             |-- dx --|
 *
 *  @author Barry Becker
 */
public class Cell
{

    // 4 if 2d, 6 if 3d, 12 if 4d
    public static final int NUM_CELL_FACES = 4;

    // type of cell
    private CellStatus status_;

    // size of a cell
    private double dx_;
    private double dy_;
    // squares of edge lengths
    private double dxSq_;
    private double dySq_;

    // pressure at the center of the cell
    private double pressure_;

    // velocities in x, y directions
    // defined at the center of each face
    // uip_ = u(i+0.5, j, k)
    // vjp_ = v(i, j+0.5, k)
    private double[] uip_ = new double[2];
    private double[] vjp_ = new double[2];

    private int numParticles_;

    // use this to switch between current and last copies of fields. (hack)
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

    // global swap of fields (use with care). (hack)
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
     * compute the cells new status based on numParticles inside and the status of neighbors.
     */
    public void updateStatus( Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1 )
    {
        if ( numParticles_ < 0 ) {
            assert false : "num particles less than 0";
        } else if ( status_ == CellStatus.OBSTACLE ) {
            return;
        } else if ( numParticles_ == 0 ) {
            status_ = CellStatus.EMPTY;
        } else {
            if ( cXp1.getNumParticles() > 0 && cXm1.getNumParticles() > 0 &&
                    cYp1.getNumParticles() > 0 && cYm1.getNumParticles() > 0 )
                status_ = CellStatus.FULL;
            else if ( cXp1.getNumParticles() == 0 && cXm1.getNumParticles() == 0 &&
                    cYp1.getNumParticles() == 0 && cYm1.getNumParticles() == 0 )
                status_ = CellStatus.ISOLATED;
            else
                status_ = CellStatus.SURFACE;
        }
    }

    /**
     * compute velocity at next time step.
     *     OXX
     *     XMX
     *     XX0
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
        assert  (dt > 0.0000001) : "dt got too small";

        double dx2 = dx_ * dx_;
        double dy2 = dy_ * dy_;
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

        double xNume = (u_i * u_i - u_ip1 * u_ip1);
        double yNume = (u_ipjm * v_ipjm - u_ipjp * v_ipjp);
        double v1 = (cXp1.uip_[current_] - 2 * uip_[current_] + cXm1.uip_[current_]) / dx2;
        double v2 = (cYp1.uip_[current_] - 2 * uip_[current_] + cYm1.uip_[current_]) / dy2;
        double pf = xNume/ dx_ + yNume / dy_ +
                    forceX + (pressure_ + cXp1.getPressure()) / dx_ + viscosity * (v1 + v2);

        if ( cXp1.getStatus() != CellStatus.OBSTACLE ) {
            uip_[1 - current_] =  uip_[current_] + dt * ( pf );
        }


        // v
        // u(i-0.5, j+0.5) = 0.5*(u(i-0.5, j) + u(i-0.5, j+1))
        double u_imjp = (cXm1.uip_[current_] + cXm1Yp1.uip_[current_]) / 2.0;

        // v(i-0.5, j+0.5) = 0.5*(v(i, j+0.5) + v(i-1, j+0.5))
        double v_imjp = (vjp_[current_] + cYm1.vjp_[current_]) / 2.0;

        // v(i, j) = 0.5*(v(i, j-0.5) + v(i, j+0.5))
        double v_j = (cYm1.vjp_[current_] + vjp_[current_]) / 2.0;

        // v(i, j+1) = 0.5*(v(i, j+0.5) + v(i, j+1.5))
        double v_jp1 = (vjp_[current_] + cYp1.vjp_[current_]);

        xNume = (u_imjp * v_imjp - u_ipjp * v_ipjp);
        yNume = (v_j * v_j - v_jp1 * v_jp1);
        v1 =  (cXp1.vjp_[current_] - 2 * vjp_[current_] + cXm1.vjp_[current_]) / dx2;
        v2 =  (cYp1.vjp_[current_] - 2 * vjp_[current_] + cYm1.vjp_[current_]) / dy2;
        pf = xNume / dx_ + yNume / dy_ +
                    forceY + (pressure_ + cYp1.getPressure()) / dy_ + viscosity * (v1 + v2);

        if ( cYp1.getStatus() != CellStatus.OBSTACLE ) {
            vjp_[1 - current_] =  vjp_[current_] + dt * ( pf );
        }
        //String message = ("updateTilde: new vel is:"+v_jp[1-cur_]+", forceY="+dt*forceY);
        //JOptionPane.showMessageDialog(null,message);
        //System.err.println("updateTilde: new vel is:"+v_jp[1-cur_]+", forceY="+dt*forceY);
        if (uip_[1 - current_]!=0 || vjp_[1 - current_]!=0) {
            System.out.println("updateTilde: new vel is:"+uip_[1-current_]+", "+vjp_[1-current_]);
            System.out.println("updateTilde: u_i="+u_i+" u_ip1="+u_ip1+" u_ipjm="+u_ipjm+" v_ipjm="+v_ipjm+"u_ipjp="+u_ipjp+"v_ipjp="+v_ipjp);
            System.out.println("updateTilde: v_j="+v_j+" v_imjp="+u_imjp+" v_imjp="+v_imjp+" v_jp1="+v_jp1);
            System.out.println(this);
            System.out.println("");
        }
    }

    /**
     * update pressure and velocities to satisfy mass conservation.
     * What is b0?
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

        if ( cXp1.getStatus() != CellStatus.OBSTACLE )
            uip_[current_] += dpdx;

        if ( cXm1.getStatus() != CellStatus.OBSTACLE )
            cXm1.uip_[current_] -= dpdx;

        if ( cYp1.getStatus() != CellStatus.OBSTACLE )
            vjp_[current_] += dpdy;

        if ( cYm1.getStatus() != CellStatus.OBSTACLE )
            cYm1.vjp_[current_] -= dpdy;

        pressure_ += dp;
        return Math.abs( divergence );
    }

    /**
     * linearly interpolate the velocity of the particle based on its position
     * relative to 4 neighboring velocity vectors.
     */
    public Vector2d interpolateVelocity( Point2d particle,
                                     Cell cX, Cell cY,
                                     Cell cXm1, Cell cXm1y, // u
                                     Cell cYm1, Cell cXym1, // v
                                     Vector2d vel )
    {

        if ( status_ == CellStatus.OBSTACLE || status_ == CellStatus.EMPTY || numParticles_ <= 0 ) {  // hitting this
            System.out.println( "Error: interpVel cell status=" + status_ + " num particles = " + numParticles_ );
            return vel;
        }

        double x = particle.x - (int) particle.x;
        double y = particle.y - (int) particle.y;

        double xx = (x > 0.5) ? (1.5 - x) : (0.5 + x);
        double yy = (y > 0.5) ? (1.5 - y) : (0.5 + y);

        double x1 = (1.0 - x) * cXm1.uip_[current_] + x * uip_[current_];
        double x2 = (1.0 - x) * cXm1y.uip_[current_] + x * cY.uip_[current_];
        double pu =  x1 * yy + x2 * (1.0 - yy);

        double y1 = (1.0 - y) * cYm1.vjp_[current_] + y * vjp_[current_];
        double y2 = (1.0 - y) * cXym1.vjp_[current_] + y * cX.vjp_[current_];
        double pv = y1 * xx + y2 * (1.0 - xx);

        /*if (pu!=0 || pv!=0) {
            System.out.println(" nearby velocities:
                            ip="+u_ip[cur_]+", jp="+v_jp[cur_]+",
                            xm1_ip="+cXm1.u_ip[cur_]+", xm1_jp="+cXm1.v_jp[cur_]+",
                            xm1y_ip="+cXm1y.u_ip[cur_]+", xym1_jp="+cXym1.v_jp[cur_]);
            System.out.println(" interpolated velocity = "+pu+", "+pv);
        }*/
        vel.set( pu, pv );
        return vel;
    }

    /**
     * ensure that what comes in must also go out.
     */
    private void dissipateOverflow( int n, double overflow,
                                    Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1 )
    {
        //System.out.println("dissipating overflow at ("+x_[0]+","+x_[1]+") ="+overflow+" n= "+n);
        if ( cXp1.getStatus() == CellStatus.EMPTY ) {
            if ( n == 1 )
                uip_[current_] = -dx_ * overflow;
            else if ( (n == 3 && cXm1.getStatus() != CellStatus.EMPTY) || (n == 2) )
                uip_[current_] = cXm1.uip_[current_];
        }
        if ( cXm1.getStatus() == CellStatus.EMPTY ) {
            if ( n == 1 )
                cXm1.uip_[current_] = dx_ * overflow;
            else if ( (n == 3 && cXp1.getStatus() != CellStatus.EMPTY) || (n == 2) )
                cXm1.uip_[current_] = uip_[current_];
        }

        if ( cYp1.getStatus() == CellStatus.EMPTY ) {
            if ( n == 1 )
                vjp_[current_] = -dy_ * overflow;
            else if ( (n == 3 && cYm1.getStatus() != CellStatus.EMPTY) || (n == 2) )
                vjp_[current_] = cYm1.vjp_[current_];
        }
        if ( cYm1.getStatus() == CellStatus.EMPTY ) {
            if ( n == 1 )
                cYm1.vjp_[current_] = dy_ * overflow;
            else if ( (n == 3 && cYp1.getStatus() != CellStatus.EMPTY) || (n == 2) )
                cYm1.vjp_[current_] = vjp_[current_];
        }
    }

    /**
     * force no divergence in surface cells, by updating velocities directly.
     */
    public void updateSurfaceVelocities( Cell cXp1, Cell cXm1, Cell cYp1, Cell cYm1,
                                         double pressure0 )
    {
        if ( status_ != CellStatus.SURFACE )
            return;

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

        if ( count < NUM_CELL_FACES && Math.abs( overflow ) > 0.000 )
            dissipateOverflow( (NUM_CELL_FACES - count), overflow, cXp1, cXm1, cYp1, cYm1 );

        pressure_ = pressure0;
    }

    public void setVelocityP( double u, double v )
    {
        uip_[0] = u;
        uip_[1] = u;
        vjp_[0] = v;
        vjp_[1] = v;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Cell:");
        sb.append(status_);
        sb.append(" num particles="+numParticles_);
        sb.append(" pressure="+pressure_);
        sb.append(" velocity="+ this.uip_[current_] +", " + this.vjp_[current_]);
        return sb.toString();
    }
}