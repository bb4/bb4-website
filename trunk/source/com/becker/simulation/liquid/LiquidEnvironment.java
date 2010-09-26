package com.becker.simulation.liquid;

import com.becker.common.ILog;
import com.becker.simulation.liquid.config.Conditions;
import com.becker.simulation.liquid.config.Region;
import com.becker.simulation.liquid.config.Source;
import com.becker.ui.Log;
import com.becker.ui.dialogs.OutputWindow;

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
public class LiquidEnvironment
{
    /** for debugging */
    public static final int LOG_LEVEL = 0;

    /** physical constants. cell width and height in mm. */
    private static final double CELL_SIZE = 10.0;

    /** density of the liquid in grams per mm^3.  water = 1000 kg/m^3 or .001 g/mm^2  */
    private static final double DENSITY = 0.001;

    /**
     *  viscosity of the liquid. Larger for molasses (.3), smaller for kerosine (.0001)
     * Water is about .001 Ns/m^2 or .01 g/s mm
     */
    private static final double VISCOSITY = 0.002; //0.001;
    //private static final double JITTERING = .1;
    private static final double B0 = 1.2;  // 1.7 // used in mass conservation (how?)
    private static final int NUM_RAND_PARTS = 1;

    // the dimensions of the space
    private int xDim_ = 30;
    private int yDim_ = 40;

    // the grid of cells that make up the environment
    // in x,y (col, row) order
    private Cell[][] grid_ = null;

    /** constraints and conditions from the configuation file. */
    private Conditions conditions_;

    // the set of particles in this simulation
    private Set<Particle> particles_;

    private static final double EPSILON = 0.0000001;

    // the time since the start of the simulation
    private double time_ = 0.0;

    // ensure that the runs are the same
    private static final Random RANDOM = new Random(1);

    private static ILog logger_ = null;


    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public LiquidEnvironment( String configFile )
    {
        initializeFromConfigFile(configFile);
    }

    private void initializeFromConfigFile(String configFile) {

        conditions_ = new Conditions(configFile);
        initEnvironment();
    }
    
    public void reset() {
        
        initEnvironment();
    }

    private void initEnvironment()
    {
        xDim_ = conditions_.getGridWidth() + 2;
        yDim_ = conditions_.getGridHeight() + 2;
        grid_ = new Cell[xDim_][yDim_];

        particles_ = new HashSet<Particle>();

        if (logger_ == null) {
            logger_ = new Log( new OutputWindow( "Log", null ) );
            logger_.setDestination( ILog.LOG_TO_WINDOW );
        }

        for (int j = 0; j < yDim_; j++ ) {
            for (int i = 0; i < xDim_; i++ ) {
                grid_[i][j] = new Cell();
            }
        }
        setInitialLiquid();
        setBoundaries();
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

    public Set<Particle> getParticles() {
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
        // Update cell status so we can track the surface.
        updateCellStatus();

        // Set up obstacle conditions for the free surface and obstacle cells
        setConstraints();

        // Compute velocities for all full cells.
        updateVelocity( timeStep );

        // Compute the pressure for all Full Cells.
        updatePressure( timeStep );

        // Re-calculate obstacle velocities for Surface cells.
        updateSurfaceVelocity();

        // Update the position of the surface and objects.
        double newTimeStep = updateParticlePosition( timeStep );

        time_ += newTimeStep;
        log( 1, " the Time= " + time_ );
        return newTimeStep;
    }

    /**
     * cell status
     *  EMPTY   : air
     *  FULL     : full of fluid.
     *  SURFACE : on the boundary between the liquid and surrounding medium
     *  ISOLATED: has liquid but no full cells are adjacent
     */
    public void updateCellStatus()
    {
        int i, j;
        for ( j = 1; j < yDim_ - 1; j++ ) {
            for ( i = 1; i < xDim_ - 1; i++ ) {
                grid_[i][j].updateStatus(
                                grid_[i + 1][j],     grid_[i - 1][j],
                                grid_[i][j + 1],     grid_[i][j - 1]  );
            }
        }
    }

    /**
     * setup the obstacles.
     */
    private void setBoundaries()
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

    private void setInitialLiquid() {
        for (Region region : conditions_.getInitialLiquidRegions()) {
            for (int i = region.getStart().getX(); i <= region.getStop().getX(); i++ ) {
                 for (int j = region.getStart().getY(); j <= region.getStop().getY(); j++ ) {
                     addRandomParticles(i, j, 4 * NUM_RAND_PARTS);
                 }
            }
        }
    }

    private void setConstraints() {
        setBoundaryConstraints();

        //addWalls();
        addSources();
        //addSinks();
    }

    private void addSources() {
        for (Source source : conditions_.getSources()) {
            addSource(source);
        }
    }


    private void addSource(Source source) {
        //add a spigot of liquid
        Vector2d velocity = source.getVelocity();

        if (source.isOn(time_)) {
            for (int i = source.getStart().getX(); i <= source.getStop().getX(); i++ ) {
                 for (int j = source.getStart().getY(); j <= source.getStop().getY(); j++ ) {
                     grid_[i][j].setUip( velocity.x );
                     grid_[i][j].setVjp( velocity.y );
                     addRandomParticles(i, j, NUM_RAND_PARTS);
                 }
            }
        }
    }


    /**
     *  set OBSTACLE condition of stationary objects, inflow/outflow.
     */
    private void setBoundaryConstraints()
    {
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
     * compute tilde velocity of each cell
     */
    private void updateVelocity( double timeStep )
    {
        log( 1, "stepForward: about to update the velocity field (timeStep=" + timeStep + ')' );
        int i, j;
        double fx = 0;
        double fy = GRAVITY;   // how do we need to scale gravity?

        for ( j = 1; j < yDim_ - 1; j++ ) {
            for ( i = 1; i < xDim_ - 1; i++ ) {
                grid_[i][j].updateTildeVelocities(
                        grid_[i + 1][j], grid_[i - 1][j],
                        grid_[i][j + 1], grid_[i][j - 1],
                        grid_[i + 1][j - 1], grid_[i - 1][j + 1], // grid_[i + 1][j - 1], grid_[i - 1][j + 1],
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
     */
    private double updatePressure( double timeStep )
    {
        double maxDivergence;
        double divergence = 0;
        int count = 0;

        do {
            // adjust tilde velocities to satisfy mass conservation
            maxDivergence = 0;
            for (int j = 1; j < yDim_ - 1; j++ ) {
                for (int i = 1; i < xDim_ - 1; i++ ) {
                    divergence =
                            grid_[i][j].updateMassConservation( B0, timeStep,
                                                             grid_[i + 1][j], grid_[i - 1][j],
                                                             grid_[i][j + 1], grid_[i][j - 1] );
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
    private void updateSurfaceVelocity()
    {
        int i, j;
        for ( j = 1; j < yDim_ - 1; j++ ) {
            for ( i = 1; i < xDim_ - 1; i++ ) {
                // I think the last arg is atmospheric pressure
                grid_[i][j].updateSurfaceVelocities(
                                  grid_[i + 1][j],   grid_[i - 1][j],
                                  grid_[i][j + 1],   grid_[i][j - 1],  
                                  ATMOSPHERIC_PRESSURE );
            }
        }
    }

    /**
     * move particles according to vector field.
     * updates the timeStep if the cfl condition is not met.
     * RISK: 3
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
                        logger_.print("b_vmag="+magnitude + " i="+i+" j="+j + "   " );
                        double factor =  .001 + maxDistance / dist;
                        vel.scale(factor);
                        logger_.println("a_vmag="+vel.length());
                }*/
                /*
                while (Math.abs(timeStep * vel.x) > 1.0 || Math.abs(timeStep * vel.y) > 1.0) {
                    timeStep /= 2.0;
                    System.out.println("vel.x="+vel.x+ ", vel.y="+vel.x +" new timeStep="+timeStep +"    invCellSize="+invCellSize);
                }*/

                double xChange = timeStep * vel.x;
                double yChange = timeStep * vel.y;
                particle.set( particle.x + xChange, particle.y + yChange );
                particle.incAge( timeStep );

                // ensure the liquid does not enter an OBSTACLE
                int ii = (int) Math.floor(particle.x);
                int jj = (int) Math.floor(particle.y);

                if (ii<0 || jj<0 || ii>=grid_.length || jj >= grid_[0].length) {
                    logger_.println( " i="+i+" j="+j +"    ii="+ii+ "  jj="+jj + " v.len="+vel.length() +" xChange="+xChange +" yChange=" + yChange +" timeStep="+timeStep);
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

         Vector2d vel =
             grid_[i][j].interpolateVelocity( particle,
                                              grid_[ii][j], grid_[i][jj],
                                              grid_[i - 1][j], grid_[i - 1][jj], // u
                                              grid_[i][j - 1], grid_[ii][j - 1]);  // v
         return vel;
    }

    private double adjustTimeStep(double timeStep, double maxLength) {

        // max distance to go in one step. Beyond this, we apply the governer.
        double maxDistance = CELL_SIZE/10.0;
        double minDistance = CELL_SIZE/10000.0;

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

    public int numParticles()
    {
        return particles_.size();
    }

    private void addParticle( double x, double y )
    {
        Particle p = new Particle( x, y, grid_[(int) x][(int) y] );
        particles_.add( p );
        grid_[(int) x][(int) y].incParticles();
    }

    private void addRandomParticles( double x, double y, int numParticles )
    {
        for ( int i = 0; i < numParticles; i++ ) {
            addParticle( x + RANDOM.nextDouble(), y + RANDOM.nextDouble());
        }
    }


    public void log(int level, String msg) {
        logger_.println(level, LOG_LEVEL, msg);
    }

}
