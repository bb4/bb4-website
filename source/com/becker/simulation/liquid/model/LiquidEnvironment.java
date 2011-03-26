package com.becker.simulation.liquid.model;

import com.becker.common.ILog;
import com.becker.simulation.liquid.config.Conditions;
import com.becker.simulation.liquid.config.Region;
import com.becker.simulation.liquid.config.Source;

import javax.vecmath.Vector2d;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


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
public class LiquidEnvironment {

    /** for debugging */
    public static final int LOG_LEVEL = 0;

    /**
     * Viscosity of the liquid. Larger for molasses (.3), smaller for kerosene (.0001)
     * Water is about .001 Ns/m^2 or .01 g/s mm
     */
    public static final double DEFAULT_VISCOSITY = 0.002; //0.001;

    private static final int NUM_RAND_PARTS = 1;

    /** the grid of cells that make up the environment */
    private Grid grid_ = null;

    /** constraints and conditions from the configuration file. */
    private Conditions conditions_;

    // the set of particles in this simulation
    private Set<Particle> particles_;

    /** the time since the start of the simulation  */
    private double time_ = 0.0;

    /** High viscosity becomes like molasses, low like kerosene */
    private double viscosity = DEFAULT_VISCOSITY;

    /** ensure that the runs are the same  */
    private static final Random RANDOM = new Random(1);

    private ILog logger_;


    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public LiquidEnvironment( String configFile, ILog logger ) {

        logger_ = logger;
        initializeFromConfigFile(configFile);
    }

    private void initializeFromConfigFile(String configFile) {

        conditions_ = new Conditions(configFile);
        initEnvironment();
    }
    
    public void reset() {
        
        initEnvironment();
    }

    private void initEnvironment() {

        int xDim = conditions_.getGridWidth() + 2;
        int yDim = conditions_.getGridHeight() + 2;

        grid_ = new Grid(xDim, yDim, logger_);
        particles_ = new HashSet<Particle>();

        setInitialLiquid();
        grid_.setBoundaries();
        setConstraints();
    }

    public int getWidth() {
        return ((grid_.getXDimension() + 2 ) );
    }

    public int getHeight() {
        return ((grid_.getYDimension() + 2) );
    }

    public Grid getGrid() {
        return grid_;
    }

    public Set<Particle> getParticles() {
        return particles_;
    }

    public void setViscosity(double v) {
        viscosity = v;
    }

    public double getViscosity() {
        return viscosity;
    }

    /**
     * Steps the simulation forward in time.
     * If the timestep is too big, inaccuracy and instability will result.
     * To prevent the instability we halve the timestep until the
     * Courant-Friedrichs-Levy condition is met.
     * In other words a particle should not be able to move more than a single cell
     * length in a given timestep.
     * @return new new timeStep to use.
     */
    public double stepForward( double timeStep ) {

        // Update cell status so we can track the surface.
        grid_.updateCellStatus();

        // Set up obstacle conditions for the free surface and obstacle cells
        setConstraints();

        // Compute velocities for all full cells.
        grid_.updateVelocity(timeStep, conditions_.getGravity());

        // Compute the pressure for all Full Cells.
        grid_.updatePressure(timeStep);

        // Re-calculate obstacle velocities for Surface cells.
        grid_.updateSurfaceVelocity();

        // Update the position of the surface and objects.
        double newTimeStep = grid_.updateParticlePosition(timeStep, particles_);

        time_ += newTimeStep;
        log( 1, " the Time= " + time_ );
        return newTimeStep;
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
        grid_.setBoundaryConstraints();

        //addWalls();
        addSources();
        //addSinks();
    }

    public void addSources() {
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
                     grid_.setVelocity(i, j, velocity);
                     addRandomParticles(i, j, NUM_RAND_PARTS);
                 }
            }
        }
    }

    private void addRandomParticles( double x, double y, int numParticles )  {

        for ( int i = 0; i < numParticles; i++ ) {
            addParticle( x + RANDOM.nextDouble(), y + RANDOM.nextDouble());
        }
    }

    private void addParticle( double x, double y ) {

        Cell cell = grid_.getCell((int)x, (int)y);
        Particle p = new Particle( x, y, cell);
        particles_.add( p );
        cell.incParticles();
    }

    public void log(int level, String msg) {
        logger_.println(level, LOG_LEVEL, msg);
    }

}
