package com.becker.simulation.liquid;

import com.becker.common.Assert;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 *  A region of space containing particles, walls, or liquid.
 *  Adapted from work by Nick Foster
 *
 *  @author Barry Becker
 */
public class Cell
{

    // possible status of the cell. determined by what's in it
    public static final char EMPTY = '.';   // no liquid
    public static final char SURFACE = '*';   // has liquid and full cell is adjacent
    public static final char FULL = '#';   // liquid on all sides
    public static final char OBSTACLE = 'o';   // solid object (like a wall)
    public static final char ISOLATED = 'I';   // has liquid, but no full cells are adjacent

    // 4 if 2d 6 if 3d 12 if 4d
    public static final int NUM_CELL_FACES = 4;

    // type of cell
    private char status_;

    // size of a cell
    private double dx_;
    private double dy_;
    private double dxSq_;
    private double dySq_;

    // position i, j
    //private Point2D position;
    private int x_[] = new int[2];

    // pressure at the center of the cell
    private double pressure_;

    // velocities in x, y directions
    // defined at the center of each face
    // u_ip = u(i+0.5, j, k)
    // v_jp = v(i, j+0.5, k)
    private double[] u_ip = new double[2];
    private double[] v_jp = new double[2];

    private int numParticles_;

    // @@hack. use this to switch between current and last copies of fields
    private static int cur_;

    /**
     * constructor
     */
    public Cell( int i, int j )
    {
        x_[0] = i;	// x
        x_[1] = j;  // y

        pressure_ = 0;
        u_ip[0] = u_ip[1] = 0;
        v_jp[0] = v_jp[1] = 0;

        numParticles_ = 0;
        status_ = EMPTY;

        // cell dimensions
        dx_ = dy_ = 1.0;
        dxSq_ = dx_ * dx_;
        dySq_ = dy_ * dy_;
    }

    //@@ global swap of fields (use with care)
    public static void swap()
    {
        cur_ = 1 - cur_;
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
        u_ip[0] = u_ip[1] = u;
    }

    public void setVjp( double v )
    {
        v_jp[0] = v_jp[1] = v;
    }

    public double getUip()
    {
        return u_ip[cur_];
    }

    public double getVjp()
    {
        return v_jp[cur_];
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

    public char getStatus()
    {
        return status_;
    }

    public void setStatus( char status )
    {
        status_ = status;
    }

    /**
     * compute the cells new status based on numParticles inside and the status of neighbors
     */
    public void updateStatus( Cell c_xp1, Cell c_xm1, Cell c_yp1, Cell c_ym1 )
    {
        if ( numParticles_ < 0 )
            Assert.exception( "num particles less than 0" );
        else if ( status_ == OBSTACLE )
            return;
        else if ( numParticles_ == 0 )
            status_ = EMPTY;
        else {
            if ( c_xp1.getNumParticles() > 0 && c_xm1.getNumParticles() > 0 &&
                    c_yp1.getNumParticles() > 0 && c_ym1.getNumParticles() > 0 )
                status_ = FULL;
            else if ( c_xp1.getNumParticles() == 0 && c_xm1.getNumParticles() == 0 &&
                    c_yp1.getNumParticles() == 0 && c_ym1.getNumParticles() == 0 )
                status_ = ISOLATED;
            else
                status_ = SURFACE;
        }
    }

    /**
     * compute velocity at next time step
     *     OXX
     *     XMX
     *     XX0
     */
    public void updateTildeVelocities( Cell c_xp1, Cell c_xm1, Cell c_yp1, Cell c_ym1,
                                       Cell c_xp1ym1, Cell c_xm1yp1,
                                       double dt, double fx, double fy, double viscosity )
    {
        // only for FULL cells.
        if ( status_ != FULL ) {
            if ( status_ == ISOLATED ) {
                u_ip[1 - cur_] = u_ip[cur_] + dt * fx;
                v_jp[1 - cur_] = v_jp[cur_] + dt * fy;
            }
            else {
                u_ip[1 - cur_] = u_ip[cur_] + dt * fx;
                v_jp[1 - cur_] = v_jp[cur_] + dt * fy;
            }
            return;
        }
        Assert.isTrue( dt > .0000001, "dt got too small" );

        // u
        // u(i, j) = 0.5*(u(i+0.5, j) + u(i-0.5, j))
        double u_i = (u_ip[cur_] + c_xm1.u_ip[cur_]) / 2.0;

        // u(i+1, j) = 0.5*(u(i+1.5, j) + u(i+0.5, j))
        double u_ip1 = (c_xp1.u_ip[cur_] + u_ip[cur_]) / 2.0;

        // u(i+0.5, j-0.5) = 0.5*(u(i+0.5, j) + u(i+0.5, j-1))
        double u_ipjm = (u_ip[cur_] + c_ym1.u_ip[cur_]) / 2.0;

        // v(i+0.5, j-0.5) = 0.5*(v(i, j-0.5) + v(i+1, j-0.5))
        double v_ipjm = (c_ym1.v_jp[cur_] + c_xp1ym1.v_jp[cur_]) / 2.0;

        // u(i+0.5, j+0.5) = 0.5*(u(i+0.5, j) + u(i+0.5, j+1))
        double u_ipjp = (u_ip[cur_] + c_yp1.u_ip[cur_]) / 2.0;

        // v(i+0.5, j+0.5) = 0.5*(v(i, j+0.5) + v(i+1, j+0.5))
        double v_ipjp = (v_jp[cur_] + c_xp1.v_jp[cur_]) / 2.0;

        if ( c_xp1.getStatus() != OBSTACLE ) {
            u_ip[1 - cur_] =
                    u_ip[cur_] +
                    dt * (
                    (u_i * u_i - u_ip1 * u_ip1) / dx_ +
                    (u_ipjm * v_ipjm - u_ipjp * v_ipjp) / dy_ +
                    fx +
                    (pressure_ + c_xp1.getPressure()) / dx_ +
                    viscosity * (c_xp1.u_ip[cur_] - 2 * u_ip[cur_] + c_xm1.u_ip[cur_]) / (dx_ * dx_) +
                    viscosity * (c_yp1.u_ip[cur_] - 2 * u_ip[cur_] + c_ym1.u_ip[cur_]) / (dy_ * dy_)
                    );
        }

        // v
        // u(i-0.5, j+0.5) = 0.5*(u(i-0.5, j) + u(i-0.5, j+1))
        double u_imjp = (c_xm1.u_ip[cur_] + c_xm1yp1.u_ip[cur_]) / 2.0;

        // v(i-0.5, j+0.5) = 0.5*(v(i, j+0.5) + v(i-1, j+0.5))
        double v_imjp = (v_jp[cur_] + c_ym1.v_jp[cur_]) / 2.0;

        // v(i, j) = 0.5*(v(i, j-0.5) + v(i, j+0.5))
        double v_j = (c_ym1.v_jp[cur_] + v_jp[cur_]) / 2.0;

        // v(i, j+1) = 0.5*(v(i, j+0.5) + v(i, j+1.5))
        double v_jp1 = (v_jp[cur_] + c_yp1.v_jp[cur_]);

        if ( c_yp1.getStatus() != OBSTACLE ) {
            v_jp[1 - cur_] =
                    v_jp[cur_] +
                    dt * (
                    (u_imjp * v_imjp - u_ipjp * v_ipjp) / dx_ +
                    (v_j * v_j - v_jp1 * v_jp1) / dy_ +
                    fy +
                    (pressure_ - c_yp1.getPressure()) / dy_ +
                    viscosity * (c_xp1.v_jp[cur_] - 2 * v_jp[cur_] + c_xm1.v_jp[cur_]) / (dx_ * dx_) +
                    viscosity * (c_yp1.v_jp[cur_] - 2 * v_jp[cur_] + c_ym1.v_jp[cur_]) / (dy_ * dy_)
                    );
        }
        //String message = ("updateTilde: new vel is:"+v_jp[1-cur_]+", fy="+dt*fy);
        //JOptionPane.showMessageDialog(null,message);
        //System.err.println("updateTilde: new vel is:"+v_jp[1-cur_]+", fy="+dt*fy);
        /*if (u_ip[1-cur_]!=0 || v_jp[1-cur_]!=0) {
            System.out.println("updateTilde: new vel is:"+u_ip[1-cur_]+", "+v_jp[1-cur_]);
            System.out.println("updateTilde: u_i="+u_i+" u_ip1="+u_ip1+" u_ipjm="+u_ipjm+" v_ipjm="+v_ipjm+"u_ipjp="+u_ipjp+"v_ipjp="+v_ipjp);
            System.out.println("updateTilde: v_j="+v_j+" v_imjp="+u_imjp+" v_imjp="+v_imjp+" v_jp1="+v_jp1);
        }*/
    }

    /**
     * update pressure and velocities to satisfy mass conservation.
     * What is b0?
     */
    public double updateMassConservation( double b0, double dt,
                                          Cell c_xp1, Cell c_xm1, Cell c_yp1, Cell c_ym1 )
    {
        if ( status_ != FULL ) {
            return 0;
        }

        // divergence of fluid within the cell
        double divergence = (c_xm1.u_ip[cur_] - u_ip[cur_]) / dx_ +
                (c_ym1.v_jp[cur_] - v_jp[cur_]) / dy_;

        double b = b0 / (dt * (2.0 / dxSq_ + 2.0 / dySq_));

        // the change in pressure for a cell.
        double dp = b * divergence;
        double dpdx = (dt / dx_) * dp;
        double dpdy = (dt / dy_) * dp;

        if ( c_xp1.getStatus() != OBSTACLE )
            u_ip[cur_] += dpdx;

        if ( c_xm1.getStatus() != OBSTACLE )
            c_xm1.u_ip[cur_] -= dpdx;

        if ( c_yp1.getStatus() != OBSTACLE )
            v_jp[cur_] += dpdy;

        if ( c_ym1.getStatus() != OBSTACLE )
            c_ym1.v_jp[cur_] -= dpdy;

        pressure_ += dp;
        return Math.abs( divergence );
    }

    /**
     * linearly interpolate the velocity of the particle based on its position
     * relative to 4 neighboring velocity vectors
     */
    public Vector2d interpolateVelocity( Point2d particle,
                                     Cell c_x, Cell c_y,
                                     Cell c_xm1, Cell c_xm1y, // u
                                     Cell c_ym1, Cell c_xym1, // v
                                     Vector2d vel )
    {
        double pu = 0;
        double pv = 0;
        if ( status_ == OBSTACLE || status_ == EMPTY || numParticles_ <= 0 ) {  // hitting this
            System.out.println( "Error: interpVel cell status=" + status_ + " num particles = " + numParticles_ );
            return vel;
        }

        double x = particle.x - (int) particle.x;
        double y = particle.y - (int) particle.y;

        double xx = (x > 0.5) ? (1.5 - x) : (0.5 + x);
        double yy = (y > 0.5) ? (1.5 - y) : (0.5 + y);

        pu = ((1.0 - x) * c_xm1.u_ip[cur_] + x * u_ip[cur_]) * yy +
                ((1.0 - x) * c_xm1y.u_ip[cur_] + x * c_y.u_ip[cur_]) * (1.0 - yy);

        pv = ((1.0 - y) * c_ym1.v_jp[cur_] + y * v_jp[cur_]) * xx +
                ((1.0 - y) * c_xym1.v_jp[cur_] + y * c_x.v_jp[cur_]) * (1.0 - xx);

        /*if (pu!=0 || pv!=0) {
            System.out.println(" nearby velocities: ip="+u_ip[cur_]+", jp="+v_jp[cur_]+", xm1_ip="+c_xm1.u_ip[cur_]+", xm1_jp="+c_xm1.v_jp[cur_]+", xm1y_ip="+c_xm1y.u_ip[cur_]+", xym1_jp="+c_xym1.v_jp[cur_]);
            System.out.println(" interpolated velocity = "+pu+", "+pv);
        }*/
        vel.set( pu, pv );
        return vel;
    }

    /**
     * ensure that what comes in must also go out
     */
    private void dissipateOverflow( int n, double overflow,
                                    Cell c_xp1, Cell c_xm1, Cell c_yp1, Cell c_ym1 )
    {
        //System.out.println("dissipating overflow at ("+x_[0]+","+x_[1]+") ="+overflow+" n= "+n);
        if ( c_xp1.getStatus() == EMPTY ) {
            if ( n == 1 )
                u_ip[cur_] = -dx_ * overflow;
            else if ( (n == 3 && c_xm1.getStatus() != EMPTY) || (n == 2) )
                u_ip[cur_] = c_xm1.u_ip[cur_];
        }
        if ( c_xm1.getStatus() == EMPTY ) {
            if ( n == 1 )
                c_xm1.u_ip[cur_] = dx_ * overflow;
            else if ( (n == 3 && c_xp1.getStatus() != EMPTY) || (n == 2) )
                c_xm1.u_ip[cur_] = u_ip[cur_];
        }

        if ( c_yp1.getStatus() == EMPTY ) {
            if ( n == 1 )
                v_jp[cur_] = -dy_ * overflow;
            else if ( (n == 3 && c_ym1.getStatus() != EMPTY) || (n == 2) )
                v_jp[cur_] = c_ym1.v_jp[cur_];
        }
        if ( c_ym1.getStatus() == EMPTY ) {
            if ( n == 1 )
                c_ym1.v_jp[cur_] = dy_ * overflow;
            else if ( (n == 3 && c_yp1.getStatus() != EMPTY) || (n == 2) )
                c_ym1.v_jp[cur_] = v_jp[cur_];
        }
    }

    /**
     * force no divergence in surface cells, by updating velocities directly.
     */
    public void updateSurfaceVelocities( Cell c_xp1, Cell c_xm1, Cell c_yp1, Cell c_ym1,
                                         double pressure0 )
    {
        if ( status_ != SURFACE )
            return;

        int count = 0;
        double overflow = 0;

        if ( c_xp1.getStatus() != EMPTY ) {
            count++;
            overflow += u_ip[cur_] / dx_;
        }
        if ( c_xm1.getStatus() != EMPTY ) {
            count++;
            overflow -= c_xm1.u_ip[cur_] / dx_;
        }
        if ( c_yp1.getStatus() != EMPTY ) {
            count++;
            overflow += v_jp[cur_] / dy_;
        }
        if ( c_ym1.getStatus() != EMPTY ) {
            count++;
            overflow -= c_ym1.v_jp[cur_] / dy_;
        }

        if ( count < NUM_CELL_FACES && Math.abs( overflow ) > 0.000 )
            dissipateOverflow( (NUM_CELL_FACES - count), overflow, c_xp1, c_xm1, c_yp1, c_ym1 );

        pressure_ = pressure0;
    }

    public void setVelocity_p( double a, double b )
    {
        u_ip[0] = a;
        v_jp[0] = b;
        u_ip[1] = a;
        v_jp[1] = b;
    }
}