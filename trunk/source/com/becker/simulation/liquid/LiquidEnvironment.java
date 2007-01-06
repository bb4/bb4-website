package com.becker.simulation.liquid;

import com.becker.ui.*;

import javax.vecmath.*;
import java.util.*;

import static com.becker.simulation.common.PhysicsConstants.*;

/**
 *  this is the global space containing all the cells, walls, and particles
 *  Assumes an M*N grid of cells.
 *  X axis increases to the left
 *  Y axis increases downwards to be consistent with java graphics
 *  adapted from work by Nick Foster.
 *
 *  Improvements:
 *    - increase performance by only keeping trakc of particles near the surface.
 *    - allow configuring walls from file
 *
 *  @author Barry Becker
 */
public class LiquidEnvironment
{

    // the dimensions of the space
    private int xDim_ = 30;
    private int yDim_ = 40;

    // the grid of cells that make up the environment
    // in x,y (col, row) order
    private Cell[][] grid_ = null;

    // the set of particles in this simulation
    private Set particles_ = new HashSet();
    // keep the walls globally because we need to draw them each frame
    private List walls_ = new ArrayList();

    private static final double EPSILON = 0.0000001;
    private static final double MAX_INC = 0.1;
    private static final double MIN_INC = 0.001;

    // the time since the start of the simulation
    private double time_ = 0.0;


    // physical constants.
    // cell width and height in mm.
    private static final double CELL_SIZE = 10.0;

    // density of the liquid in grams per mm^3
    // water = 1000 kg/m^3 or .001 g/mm^2
    private static final double DENSITY = 0.001;
    // viscosity of the liquid. Larger for molasses (.3), smaller for kerosine (.0001)
    // Water is about .001 Ns/m^2 or .01 g/s mm
    private static final double VISCOSITY = 0.001; //0.001;
    //private static final double JITTERING = .1;
    private static final double B0 = 1.7;  // used in mass conservation (how?)
    private static final double SPIGOT_VELOCITY = 30.0;
    private static final int NUM_RAND_PARTS = 5;


    // temp var used throughout for efficency. avoids creating objects
    private static Vector2d v_ = new Vector2d( 0, 0 );

    private static Log logger_ = null;
    public static final int LOG_LEVEL = 0;

    //Constructor
    public LiquidEnvironment( String configFile )
    {
        //readConfigFile(configFile, walls_);
        initEnvironment( walls_ );
    }

    //Constructor
    public LiquidEnvironment( int w, int h )
    {
        xDim_ = w + 2;
        yDim_ = h + 2;
        grid_ = new Cell[xDim_][yDim_];
        initEnvironment( walls_ );
    }

    private void initEnvironment( List walls )
    {
        int i, j;
        logger_ = new Log( new OutputWindow( "Log", null ) );
        logger_.setDestination( Log.LOG_TO_WINDOW );

        for ( j = 0; j < yDim_; j++ ) {
            for ( i = 0; i < xDim_; i++ ) {
                grid_[i][j] = new Cell();
            }
        }
        //pressureColorMap_.setOpacity(PRESSURE_COL_OPACITY);
        setInitialConditions();
        setConstraints();
    }

    public int getWidth() {
        return ((xDim_ + 2 ) );
    }

    public int getHeight() {
        return ((yDim_ + 2) );
    }

    public int getXDim() {
        return xDim_;
    }
    public int getYDim()  {
        return yDim_;
    }

    public Cell[][] getGrid() {
        return grid_;
    }

    public Set getParticles() {
        return particles_;
    }

    /**
     * Steps the simulation forward in time.
     * If the timestep is too big, inaccuracy and instability will result.
     * To prevent the instability we halve the timestep until the
     * Courant-Friedrichs-Levy condition is met.
     * In other words a particle should not be able to move more than a single cell
     * length in a given timestep.
     */
    public double stepForward( double timeStep )
    {
        // Determine cell contents depending on the method used to track the surface
        updateCellStatus();

        // Set up OBSTACLE conditions for the free surface and obstacle cells
        setConstraints();

        // Compute u, v for all full cells
        updateVelocity( timeStep );

        // Compute the pressure for all Full Cells
        updatePressure( timeStep );

        // Re-calculate OBSTACLE velocities for Surface cells
        updateSurfaceVelocity();

        // Update the position of the surface and objects
        double newTimeStep = updateParticlePosition( timeStep );

        time_ += newTimeStep;
        log( 1, " the Time= " + time_ );
        return newTimeStep;
    }

    /**
     * cell status
     *  EMPTY   : air
     *  FULL    : full of fluid.
     *  SURFACE : on the boundary between the liquid and surrounding medium
     *  ISOLATED: has liquid but no full cells are adjacent
     */
    private void updateCellStatus()
    {
        int i, j;
        for ( j = 1; j < yDim_ - 1; j++ ) {
            for ( i = 1; i < xDim_ - 1; i++ ) {
                grid_[i][j].updateStatus(
                        grid_[i + 1][j], grid_[i - 1][j], grid_[i][j + 1], grid_[i][j - 1] );
            }
        }
    }

    /**
     *  set OBSTACLE condition of stationary objects, inflow/outflow.
     */
    private void setConstraints()
    {
        int i, j;
        Cell n;

        // right and left
        for ( j = 0; j < yDim_; j++ ) {
            // left
            n = grid_[1][j];
            grid_[0][j].setPressure( n.getPressure() );
            grid_[0][j].setVelocityP( 0, -n.getVjp() );
            // right
            n = grid_[xDim_ - 2][j];
            grid_[xDim_ - 1][j].setPressure( n.getPressure() );
            grid_[xDim_ - 1][j].setVelocityP( 0, -n.getVjp() );
            grid_[xDim_ - 2][j].setUip( 0 );
        }

        // top and bottom
        for ( i = 0; i < xDim_; i++ ) {
            // bottom
            n = grid_[i][1];
            grid_[i][0].setPressure( n.getPressure() );
            grid_[i][0].setVelocityP( -n.getUip(), 0 );
            // top
            n = grid_[i][yDim_ - 2];
            grid_[i][yDim_ - 1].setPressure( n.getPressure() );
            grid_[i][yDim_ - 1].setVelocityP( -n.getUip(), 0 );
            grid_[i][yDim_ - 2].setVjp( 0 );
        }

        //add a spigot of liquid
        for ( i = 2; i < 6; i++ ) {
            grid_[2][i].setUip( SPIGOT_VELOCITY );
            grid_[3][i].setUip( SPIGOT_VELOCITY );
            grid_[4][i].setUip( SPIGOT_VELOCITY );
        }

        addRandomParticles( 3, 2 );
        addRandomParticles( 3, 3 );
        addRandomParticles( 3, 4 );
        addRandomParticles( 3, 5 );
    }

    /**
     * compute tilde velocity of each cell
     */
    private void updateVelocity( double timeStep )
    {
        log( 1, "stepForward: about to update the velocity field (timeStep=" + timeStep + ')' );
        int i, j;
        double fx = 0;
        double fy = GRAVITY / 1.0;   // how do we need to scale gravity?

        for ( j = 1; j < yDim_ - 1; j++ ) {
            for ( i = 1; i < xDim_ - 1; i++ ) {
                grid_[i][j].updateTildeVelocities(
                        grid_[i + 1][j], grid_[i - 1][j],
                        grid_[i][j + 1], grid_[i][j - 1],
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
     */
    private double updatePressure( double timeStep )
    {
        int i, j;
        double maxDivergence, divergence;

        do {
            // adjust tilde velocities to satisfy mass conservation
            maxDivergence = 0;
            for ( j = 1; j < yDim_ - 1; j++ ) {
                for ( i = 1; i < xDim_ - 1; i++ ) {
                    divergence =
                            grid_[i][j].updateMassConservation( B0, timeStep,
                                    grid_[i + 1][j], grid_[i - 1][j],
                                    grid_[i][j + 1], grid_[i][j - 1] );
                    if ( divergence > maxDivergence )
                        maxDivergence = divergence;
                }
            }
            log( 2, " updatePress: maxDiv = " + maxDivergence );
        } while ( maxDivergence > EPSILON );
        log( 1, " updatePress: converged to maxDiv = " + maxDivergence );
        return maxDivergence;
    }

    /**
     * compute velocity and pressure of SURFACE cells.
     */
    private void updateSurfaceVelocity()
    {
        int i, j;
        for ( j = 1; j < yDim_ - 1; j++ ) {
            for ( i = 1; i < xDim_ - 1; i++ ) {
                // I think the last arg is atmospheric pressure
                grid_[i][j].updateSurfaceVelocities(
                        grid_[i + 1][j], grid_[i - 1][j], grid_[i][j + 1], grid_[i][j - 1], ATMOSPHERIC_PRESSURE );
            }
        }
    }

    /**
     * move particles according to vector field
     * updates the timeStep if the cfl condition is not met.
     */
    private double updateParticlePosition( double timeStep )
    {
        Iterator it = particles_.iterator();
        // keep track of the biggest velocity magnitude so we can adjust the timestep appropriately.
        double maxLength = Double.MIN_VALUE;
        double invCellSize = 1.0/CELL_SIZE;

        while ( it.hasNext() ) {
            Particle particle = (Particle) it.next();
            // velocity of a particle : determined using area weighting interpolation
            int i = (int) particle.x;
            int j = (int) particle.y;
            CellStatus status = grid_[i][j].getStatus();

            if ( status == CellStatus.FULL || status == CellStatus.SURFACE || status == CellStatus.ISOLATED ) {
                int ii = ((particle.x - i) > 0.5) ? (i + 1) : (i - 1);
                int jj = ((particle.y - j) > 0.5) ? (j + 1) : (j - 1);

                v_ = grid_[i][j].interpolateVelocity( particle,
                        grid_[ii][j], grid_[i][jj],
                        grid_[i - 1][j], grid_[i - 1][jj], // u
                        grid_[i][j - 1], grid_[ii][j - 1], // v
                        v_);

                // scale the velocity by the cell size so we can assume the cells have unit dims
                v_.scale(invCellSize);

                double magnitude = v_.length();
                if (magnitude > maxLength)
                    maxLength = magnitude;

                particle.set( particle.x + timeStep * v_.x, particle.y + timeStep * v_.y );
                particle.incAge( timeStep );

                // to ensure the liquid does not enter an OBSTACLE
                ii = (int) particle.x;
                jj = (int) particle.y;

                // move outside the obstacle if we find ouselves in one
                if ( grid_[ii][jj].getStatus() == CellStatus.OBSTACLE ) {
                    if ( ii > i )
                        particle.set( ii - EPSILON, particle.y );
                    else if ( ii < i )
                        particle.set( ii + 1.0 + EPSILON, particle.y );
                    if ( jj > j )
                        particle.set( particle.x, jj - EPSILON );
                    else if ( jj < j )
                        particle.set( particle.x, jj + 1.0 + EPSILON );
                }
                ii = (int) particle.x;
                jj = (int) particle.y;

                assert ( particle.x >= 1 && particle.y >= 1 && particle.x < xDim_ - 1 && particle.y < yDim_ - 1) :
                        "particle.x=" + particle.x + "particle.y=" + particle.y ;

                // adjust # particles as they cross cell boundaries
                grid_[ii][jj].incParticles(); // increment new cell
                grid_[i][j].decParticles();   // decrement last cell
                particle.setCell( grid_[ii][jj] );

                assert ( grid_[i][j].getNumParticles() >= 0): // hitting this
                        "The number of particles in grid[" + i + "][" + j + "] is " + grid_[i][j].getNumParticles();
                assert ( grid_[ii][jj].getNumParticles() >= 0 );
            }
        }


        double increment = (timeStep * maxLength);
        double newTimeStep = timeStep;
        if (increment > MAX_INC) {
            newTimeStep /= 2.0;
            log(0, "updateParticlePosition: HALVED dt=" + timeStep +" increment="+increment );
        }
        else if (increment < MIN_INC) {
            newTimeStep *= 2.0;
            log(0, "updateParticlePosition: DOUBLED dt=" + timeStep +" increment="+increment );
        }


        return newTimeStep;
    }

    public int numParticles()
    {
        return particles_.size();
    }

    /**
     * config() and addParticles()
     */
    private void setInitialConditions()
    {
        int i, j;
        // right and left
        for ( j = 0; j < yDim_; j++ ) {
            grid_[0][j].setStatus( CellStatus.OBSTACLE );
            grid_[xDim_ - 1][j].setStatus( CellStatus.OBSTACLE );
        }
        // top and bottom
        for ( i = 0; i < xDim_; i++ ) {
            grid_[i][0].setStatus( CellStatus.OBSTACLE );
            grid_[i][yDim_ - 1].setStatus( CellStatus.OBSTACLE );
        }
    }

    private void addParticle( double x, double y )
    {
        Point2d p = new Particle( x, y, grid_[(int) x][(int) y] );
        particles_.add( p );
        grid_[(int) x][(int) y].incParticles();
    }

    private void addRandomParticles( double x, double y )
    {
        for ( int i = 0; i < NUM_RAND_PARTS; i++ ) {
            Particle p = new Particle( x + Math.random(), y + Math.random(), grid_[(int) x][(int) y] );
            particles_.add( p );
            grid_[(int) x][(int) y].incParticles();
        }
    }


    public void log(int level, String msg) {
        logger_.println(level, LOG_LEVEL, msg);
    }

}
