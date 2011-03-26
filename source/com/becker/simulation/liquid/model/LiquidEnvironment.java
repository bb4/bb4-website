package com.becker.simulation.liquid.model;

import com.becker.simulation.liquid.Logger;
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

    /**
     * Viscosity of the liquid. Larger for molasses (.3), smaller for kerosene (.0001)
     * Water is about .001 Ns/m^2 or .01 g/s mm
     */
    public static final double DEFAULT_VISCOSITY = 0.002; //0.001;

    /**   used in mass conservation (how?) */
    public static final double DEFAULT_B0 = 1.2;  // 1.7
    private double b0 = DEFAULT_B0;

    private static final int NUM_RAND_PARTS = 1;

    /** the grid of cells that make up the environment */
    private Grid grid;


    /** constraints and conditions from the configuration file. */
    private Conditions conditions;

    // the set of particles in this simulation
    private Set<Particle> particles;

    /** the time since the start of the simulation  */
    private double time = 0.0;

    /** High viscosity becomes like molasses, low like kerosene */
    private double viscosity = DEFAULT_VISCOSITY;

    /** ensure that the runs are the same  */
    private static final Random RANDOM = new Random(1);


    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public LiquidEnvironment( String configFile ) {

        initializeFromConfigFile(configFile);
    }

    private void initializeFromConfigFile(String configFile) {

        conditions = new Conditions(configFile);
        initEnvironment();
    }
    
    public void reset() {
        
        initEnvironment();
    }

    private void initEnvironment() {

        int xDim = conditions.getGridWidth() + 2;
        int yDim = conditions.getGridHeight() + 2;

        grid = new Grid(xDim, yDim);
        particles = new HashSet<Particle>();

        setInitialLiquid();
        grid.setBoundaries();
        setConstraints();
    }

    public int getWidth() {
        return ((grid.getXDimension() + 2 ) );
    }

    public int getHeight() {
        return ((grid.getYDimension() + 2) );
    }

    public Grid getGrid() {
        return grid;
    }

    public Set<Particle> getParticles() {
        return particles;
    }

    public void setViscosity(double v) {
        this.viscosity = v;
    }

    public void setB0(double b0) {
        this.b0 = b0;
    }

    public double getB0() {
        return b0;
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

        GridUpdater gridUpdater = new GridUpdater(grid, b0);

        // Update cell status so we can track the surface.
        grid.updateCellStatus();

        // Set up obstacle conditions for the free surface and obstacle cells
        setConstraints();

        // Compute velocities for all full cells.
        gridUpdater.updateVelocity(timeStep, conditions.getGravity());

        // Compute the pressure for all Full Cells.
        gridUpdater.updatePressure(timeStep);

        // Re-calculate obstacle velocities for Surface cells.
        gridUpdater.updateSurfaceVelocity();

        // Update the position of the surface and objects.
        double newTimeStep = gridUpdater.updateParticlePosition(timeStep, particles);

        time += newTimeStep;
        Logger.log(1, " the Time= " + time);
        return newTimeStep;
    }

    private void setInitialLiquid() {
        for (Region region : conditions.getInitialLiquidRegions()) {
            for (int i = region.getStart().getX(); i <= region.getStop().getX(); i++ ) {
                 for (int j = region.getStart().getY(); j <= region.getStop().getY(); j++ ) {
                     addRandomParticles(i, j, 4 * NUM_RAND_PARTS);
                 }
            }
        }
    }

    private void setConstraints() {
        grid.setBoundaryConstraints();

        //addWalls();
        addSources();
        //addSinks();
    }

    public void addSources() {
        for (Source source : conditions.getSources()) {
            addSource(source);
        }
    }

    private void addSource(Source source) {
        //add a spigot of liquid
        Vector2d velocity = source.getVelocity();

        if (source.isOn(time)) {
            for (int i = source.getStart().getX(); i <= source.getStop().getX(); i++ ) {
                 for (int j = source.getStart().getY(); j <= source.getStop().getY(); j++ ) {
                     grid.setVelocity(i, j, velocity);
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

        Cell cell = grid.getCell((int)x, (int)y);
        Particle p = new Particle( x, y, cell);
        particles.add( p );
        cell.incParticles();
    }
}
