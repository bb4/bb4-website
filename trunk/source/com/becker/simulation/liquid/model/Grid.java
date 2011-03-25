package com.becker.simulation.liquid.model;

import com.becker.common.ILog;
import com.becker.simulation.liquid.config.Conditions;
import com.becker.simulation.liquid.config.Region;
import com.becker.simulation.liquid.config.Source;

import javax.vecmath.Vector2d;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import static com.becker.simulation.common.PhysicsConstants.ATMOSPHERIC_PRESSURE;
import static com.becker.simulation.common.PhysicsConstants.GRAVITY;

/**
 *  This is the global space containing all the cells, walls, and particles
 *  Assumes an M*N grid of cells.
 *  X axis increases to the left
 *  Y axis increases downwards to be consistent with java graphics.
 *  adapted from work by Nick Foster.
 *  See
 *  http://physbam.stanford.edu/~fedkiw/papers/stanford2001-02.pdf
 *
 *  Improvements:
 *    - increase performance by only keeping track of particles near the surface.
 *    - allow configuring walls from file.
 *
 *  @author Barry Becker
 */
public class Grid {

    /** for debugging */
    public static final int LOG_LEVEL = 0;

    /** physical constants. cell width and height in mm. */
    private static final double CELL_SIZE = 10.0;

    /**
     * Viscosity of the liquid. Larger for molasses (.3), smaller for kerosene (.0001)
     * Water is about .001 Ns/m^2 or .01 g/s mm
     */
    private static final double VISCOSITY = 0.002; //0.001;

    private static final double B0 = 1.2;  // 1.7 // used in mass conservation (how?)

    // the dimensions of the space
    private int xDim_;
    private int yDim_;

    /** the grid of cells that make up the environment in x,y (col, row) order */
    private Cell[][] grid_ = null;

    private static final double EPSILON = 0.0000001;

    private ILog logger_;


    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public Grid(int xDim, int yDim, ILog logger) {

        xDim_ = xDim;
        yDim_ = yDim;
        grid_ = new Cell[xDim][yDim];

        for (int j = 0; j < yDim_; j++ ) {
            for (int i = 0; i < xDim; i++ ) {
                grid_[i][j] = new Cell();
            }
        }
        logger_ = logger;
    }

    public Cell getCell(int i, int j) {
        return grid_[i][j];
    }

    public CellNeighbors getNeighbors(int i, int j) {
        return new CellNeighbors(grid_[i + 1][j],   grid_[i - 1][j],
                                 grid_[i][j + 1],   grid_[i][j - 1] );
    }


    public int getXDimension() {
        return xDim_;
    }

    public int getYDimension() {
        return yDim_;
    }

    public void setVelocity(int i, int j, Vector2d velocity) {
        grid_[i][j].setUip( velocity.x );
        grid_[i][j].setVjp( velocity.y );
    }

    /**
     * Update the cell status for all the cells in the grid.
     */
    public void updateCellStatus() {
        int i, j;
        for ( j = 1; j < yDim_ - 1; j++ ) {
            for ( i = 1; i < xDim_ - 1; i++ ) {
                grid_[i][j].updateStatus(getNeighbors(i,j));
            }
        }
    }

    /**
     * setup the obstacles.
     */
    public void setBoundaries() {

        // right and left
        for ( int j = 0; j < yDim_; j++ ) {
            grid_[0][j].setStatus( CellStatus.OBSTACLE );
            grid_[xDim_ - 1][j].setStatus( CellStatus.OBSTACLE );
        }
        // top and bottom
        for ( int i = 0; i < xDim_; i++ ) {
            grid_[i][0].setStatus( CellStatus.OBSTACLE );
            grid_[i][yDim_ - 1].setStatus( CellStatus.OBSTACLE );
        }
    }

    /**
     * Set OBSTACLE condition of stationary objects, inflow/outflow.
     */
    public void setBoundaryConstraints() {

        // right and left
        for (int j = 0; j < yDim_; j++ ) {
            // left
            Cell n = grid_[1][j];
            grid_[0][j].setPressure( n.getPressure() );
            grid_[0][j].setVelocityP( 0, n.getVjp() );   // -n.getVjP ???
            // right
            n = grid_[xDim_ - 2][j];
            grid_[xDim_ - 1][j].setPressure( n.getPressure() );
            grid_[xDim_ - 1][j].setVelocityP( 0, n.getVjp() );  // -n.getVip()
            grid_[xDim_ - 2][j].setUip( 0 );
        }

        // top and bottom
        for (int i = 0; i < xDim_; i++ ) {
            // bottom
            Cell n = grid_[i][1];
            grid_[i][0].setPressure( n.getPressure() );
            grid_[i][0].setVelocityP( n.getUip(), 0 ); // -n.getUip() ???
            // top
            n = grid_[i][yDim_ - 2];
            grid_[i][yDim_ - 1].setPressure( n.getPressure() );
            grid_[i][yDim_ - 1].setVelocityP( n.getUip(), 0 );  // -n.getUip()
            grid_[i][yDim_ - 2].setVjp( 0 );
        }      
    }


    /**
     * Compute tilde velocity of each cell
     */
    public void updateVelocity( double timeStep, double gravity ) {

        log( 1, "stepForward: about to update the velocity field (timeStep=" + timeStep + ')' );
        int i, j;
        double fx = 0;
        double fy = gravity;

        for ( j = 1; j < yDim_ - 1; j++ ) {
            for ( i = 1; i < xDim_ - 1; i++ ) {
                grid_[i][j].updateTildeVelocities( getNeighbors(i,j),
                        grid_[i + 1][j - 1], grid_[i - 1][j + 1],
                        timeStep, fx, fy, VISCOSITY
                );
            }
        }
        Cell.swap(); // @@ ugly hack
    }

    /**
     * perform pressure iteration to consider mass conservation.
     * repeat till all cells in the flow field have a divergence less than EPSILON.
     * When things go bad, this can take 50-70 or more iterations.
     * RISK: 6
     * @return the maximum divergence of any of the cells in the grid.
     */
    public double updatePressure( double timeStep ) {

        double maxDivergence;
        double divergence;
        int count = 0;

        do {
            // adjust tilde velocities to satisfy mass conservation
            maxDivergence = 0;
            for (int j = 1; j < yDim_ - 1; j++ ) {
                for (int i = 1; i < xDim_ - 1; i++ ) {
                    divergence =
                            grid_[i][j].updateMassConservation( B0, timeStep, getNeighbors(i,j));
                    if ( divergence > maxDivergence ) {
                        maxDivergence = divergence;
                    }
                }
            }
            count++;
            log( 2, " updatePress: maxDiv = " + maxDivergence );
        } while ( maxDivergence > 0.001 * EPSILON );

        if (count >20) {
            log( 0, " updatePress: converged to maxDiv = " + maxDivergence  + " after " + count +" iterations.");
        }
        return maxDivergence;
    }

    /**
     * compute velocity and pressure of SURFACE cells.
     */
    public void updateSurfaceVelocity() {

        for (int j = 1; j < yDim_ - 1; j++ ) {
            for (int i = 1; i < xDim_ - 1; i++ ) {
                // I think the last arg is atmospheric pressure
                grid_[i][j].updateSurfaceVelocities(
                                  getNeighbors(i, j),
                                  ATMOSPHERIC_PRESSURE );
            }
        }
    }

    /**
     * move particles according to vector field.
     * updates the timeStep if the cfl condition is not met.
     * RISK: 3
     */
    public double updateParticlePosition( double timeStep, Set<Particle> particles) {

        Iterator it = particles.iterator();
        // keep track of the biggest velocity magnitude so we can adjust the timestep appropriately.
        double maxLength = Double.MIN_VALUE;
        double invCellSize = 1.0/ CELL_SIZE;
        
        while ( it.hasNext() ) {
            Particle particle = (Particle) it.next();
            // velocity of a particle : determined using area weighting interpolation

            int i = (int) Math.floor(particle.x);
            int j = (int) Math.floor(particle.y);
            CellStatus status = grid_[i][j].getStatus();

            if ( status == CellStatus.FULL || status == CellStatus.SURFACE || status == CellStatus.ISOLATED ) {

                Vector2d vel = findInterpolatedGridVelocity(particle);
               
                // scale the velocity by the cell size so we can assume the cells have unit dims
                vel.scale(invCellSize);

                double magnitude = vel.length();
                if (magnitude > maxLength) {
                    maxLength = magnitude;
                }
                /*
                double dist = magnitude * timeStep;
                if (dist > maxDistance) {
                        log(0, "b_vmag="+magnitude + " i="+i+" j="+j + "   " );
                        double factor =  .001 + maxDistance / dist;
                        vel.scale(factor);
                        log(0, "a_vmag="+vel.length());
                }*/
                /*
                while (Math.abs(timeStep * vel.x) > 1.0 || Math.abs(timeStep * vel.y) > 1.0) {
                    timeStep /= 2.0;
                    log(0, "vel.x="+vel.x+ ", vel.y="+vel.x +" new timeStep="+timeStep +"    invCellSize="+invCellSize);
                }*/

                double xChange = timeStep * vel.x;
                double yChange = timeStep * vel.y;
                particle.set( particle.x + xChange, particle.y + yChange );
                particle.incAge( timeStep );

                // ensure the liquid does not enter an OBSTACLE
                int ii = (int) Math.floor(particle.x);
                int jj = (int) Math.floor(particle.y);

                if (ii<0 || jj<0 || ii>=grid_.length || jj >= grid_[0].length) {
                    log(0, " i=" + i + " j=" + j + "    ii=" + ii + "  jj=" + jj + " v.len=" + vel.length() + " xChange=" + xChange + " yChange=" + yChange + " timeStep=" + timeStep);
                    if (ii < 0) {
                        particle.x = 0.0;
                        ii = 0;
                    }
                    if (jj < 0) {
                        particle.y = 0.0;
                        jj = 0;
                    }
                    if (ii >= grid_.length) {
                        ii = grid_.length - 1;
                        particle.x = ii;

                    }
                    if (jj >= grid_[0].length) {
                        jj = grid_[0].length - 1;
                        particle.y = jj;
                    }
                }

                // move outside the obstacle if we find ourselves in one
                if ( grid_[ii][jj].getStatus() == CellStatus.OBSTACLE ) {
                    if ( i < ii ) {
                        particle.set( ii - EPSILON, particle.y );
                    }
                    else if (i > ii ) {
                        particle.set( ii + 1.0 + EPSILON, particle.y );
                    }
                    if ( j < jj ) {
                        particle.set( particle.x, jj - EPSILON );
                    }
                    else if ( j > jj ) {
                        particle.set( particle.x, jj + 1.0 + EPSILON );
                    }
                }

                ii = (int) particle.x;
                jj = (int) particle.y;

                assert ( particle.x >= 1 && particle.y >= 1 && particle.x < xDim_ - 1 && particle.y < yDim_ - 1) :
                        "particle.x=" + particle.x + "particle.y=" + particle.y ;

                // adjust # particles as they cross cell boundaries
                grid_[ii][jj].incParticles(); // increment new cell
                grid_[i][j].decParticles();  // decrement last cell
                particle.setCell( grid_[ii][jj] );

                assert ( grid_[i][j].getNumParticles() >= 0): // hitting this
                        "The number of particles in grid[" + i + "][" + j + "] is " + grid_[i][j].getNumParticles();
                assert ( grid_[ii][jj].getNumParticles() >= 0 );
            }
        }

        adjustTimeStep(timeStep, maxLength);
       
        return timeStep;
    }

    /**
     * @param particle partical to find velocity for
     * @return the interpolated (weighted) velocity vector for the particle
     */
    public Vector2d findInterpolatedGridVelocity(Particle particle) {
         int i = (int) particle.x;
         int j = (int) particle.y;
         int ii = ((particle.x - i) > 0.5) ? (i + 1) : (i - 1);
         int jj = ((particle.y - j) > 0.5) ? (j + 1) : (j - 1);

         return grid_[i][j].interpolateVelocity( particle,
                                          grid_[ii][j],    grid_[i][jj],
                                          grid_[i - 1][j], grid_[i - 1][jj], // u
                                          grid_[i][j - 1], grid_[ii][j - 1]);  // v
    }

    private double adjustTimeStep(double timeStep, double maxLength) {

        // max distance to go in one step. Beyond this, we apply the governor.
        double maxDistance = CELL_SIZE / 10.0;
        double minDistance = CELL_SIZE / 10000.0;

        // adjust the timestep if needed.
        double increment = (timeStep * maxLength);
        double newTimeStep = timeStep;
        if (increment > maxDistance) {
            newTimeStep /= 2.0;
            log(0, "updateParticlePosition: HALVED dt=" + timeStep +" increment="+increment );
        }
        else if (increment < minDistance) {
            newTimeStep *= 2.0;
            log(0, "updateParticlePosition: DOUBLED dt=" + timeStep +" increment="+increment );
        }
        return newTimeStep;
    }

    private void log(int level, String msg) {
        logger_.println(level, LOG_LEVEL, msg);
    }

}
