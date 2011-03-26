package com.becker.simulation.liquid.model;

import com.becker.common.ILog;
import com.becker.simulation.liquid.Logger;

import javax.vecmath.Vector2d;
import java.util.Iterator;
import java.util.Set;

import static com.becker.simulation.common.PhysicsConstants.ATMOSPHERIC_PRESSURE;

/**
 *  Updates the grid physics.
 *  adapted from work by Nick Foster.
 *  See http://physbam.stanford.edu/~fedkiw/papers/stanford2001-02.pdf
 *
 *  Improvements:
 *    - increase performance by only keeping track of particles near the surface.
 *    - allow configuring walls from file.
 *
 *  @author Barry Becker
 */
public class GridUpdater {

    /**
     * Viscosity of the liquid. Larger for molasses (.3), smaller for kerosene (.0001)
     * Water is about .001 Ns/m^2 or .01 g/s mm
     */
    private static final double VISCOSITY = 0.002; //0.001;

    /**  used in mass conservation (how?) */
    private double b0;

    /** the grid of cells that make up the environment */
    private Grid grid ;

    private static final double EPSILON = 0.0000001;


    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public GridUpdater(Grid grid, double b0) {

        this.grid = grid;
        this.b0 = b0;
    }

    /**
     * Compute tilde velocity of each cell
     */
    public void updateVelocity( double timeStep, double gravity ) {

        Logger.log( 1, "stepForward: about to update the velocity field (timeStep=" + timeStep + ')' );
        int i, j;
        Vector2d force = new Vector2d(0, gravity);
        VelocityUpdater velocityUpdater = new VelocityUpdater();

        for ( j = 1; j < grid.getYDimension() - 1; j++ ) {
            for ( i = 1; i < grid.getYDimension() - 1; i++ ) {
                velocityUpdater.updateTildeVelocities(grid.getCell(i, j), grid.getNeighbors(i,j),
                                               grid.getCell(i - 1, j + 1), grid.getCell(i + 1, j - 1),
                                               timeStep, force, VISCOSITY );
            }
        }
        for ( j = 1; j < grid.getYDimension() - 1; j++ ) {
            for ( i = 1; i < grid.getXDimension() - 1; i++ ) {
                grid.getCell(i, j).swap();
            }
        }
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
        MassConserver conserver = new MassConserver(b0, timeStep);

        do {
            // adjust tilde velocities to satisfy mass conservation
            maxDivergence = 0;
            for (int j = 1; j < grid.getYDimension() - 1; j++ ) {
                for (int i = 1; i < grid.getXDimension() - 1; i++ ) {
                    divergence = conserver.updateMassConservation(grid.getCell(i, j), grid.getNeighbors(i, j));
                    if ( divergence > maxDivergence ) {
                        maxDivergence = divergence;
                    }
                }
            }
            count++;
            Logger.log( 2, " updatePress: maxDiv = " + maxDivergence );
        } while ( maxDivergence > 0.001 * EPSILON );

        if (count > 20) {
            Logger.log(0, " updatePress: converged to maxDiv = " + maxDivergence + " after " + count + " iterations.");
        }
        return maxDivergence;
    }

    /**
     * compute velocity and pressure of SURFACE cells.
     */
    public void updateSurfaceVelocity() {

        SurfaceVelocityUpdater surfaceUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE);

        for (int j = 1; j < grid.getYDimension() - 1; j++ ) {
            for (int i = 1; i < grid.getXDimension() - 1; i++ ) {
                surfaceUpdater.updateSurfaceVelocities(grid.getCell(i, j), grid.getNeighbors(i, j));
            }
        }
    }

    /**
     * move particles according to vector field.
     * updates the timeStep if the cfl condition is not met.
     * RISK: 3
     * @return the current timeStep (it was possible adjusted)
     */
    public double updateParticlePosition( double timeStep, Set<Particle> particles) {

        Iterator it = particles.iterator();
        // keep track of the biggest velocity magnitude so we can adjust the timestep appropriately.
        double maxLength = Double.MIN_VALUE;
        double invCellSize = 1.0 / CellDimensions.CELL_SIZE;
        VelocityInterpolator interpolator = new VelocityInterpolator(grid);
        
        while ( it.hasNext() ) {
            Particle particle = (Particle) it.next();
            // velocity of a particle : determined using area weighting interpolation

            int i = (int) Math.floor(particle.x);
            int j = (int) Math.floor(particle.y);
            CellStatus status = grid.getCell(i, j).getStatus();

            if ( status == CellStatus.FULL || status == CellStatus.SURFACE || status == CellStatus.ISOLATED ) {

                Vector2d vel = interpolator.findVelocity(particle);
               
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

                if (ii<0 || jj<0 || ii>=grid.getXDimension() || jj >= grid.getYDimension()) {
                    Logger.log(0, " i=" + i + " j=" + j + "    ii=" + ii + "  jj=" + jj +
                            " v.len=" + vel.length() + " xChange=" + xChange + " yChange=" + yChange + " timeStep=" + timeStep);
                    if (ii < 0) {
                        particle.x = 0.0;
                        ii = 0;
                    }
                    if (jj < 0) {
                        particle.y = 0.0;
                        jj = 0;
                    }
                    if (ii >= grid.getXDimension()) {
                        ii = grid.getXDimension() - 1;
                        particle.x = ii;

                    }
                    if (jj >= grid.getYDimension()) {
                        jj = grid.getYDimension() - 1;
                        particle.y = jj;
                    }
                }

                // move outside the obstacle if we find ourselves in one
                if ( grid.getCell(ii, jj).getStatus() == CellStatus.OBSTACLE ) {
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

                assert ( particle.x >= 1 && particle.y >= 1
                        && particle.x < grid.getXDimension() - 1
                        && particle.y < grid.getYDimension() - 1) :
                        "particle.x=" + particle.x + "particle.y=" + particle.y ;

                // adjust # particles as they cross cell boundaries
                grid.getCell(ii, jj).incParticles(); // increment new cell
                grid.getCell(i, j).decParticles();  // decrement last cell
                particle.setCell( grid.getCell(ii, jj) );

                assert ( grid.getCell(i, j).getNumParticles() >= 0): // hitting this
                        "The number of particles in grid[" + i + "][" + j + "] is " + grid.getCell(i, j).getNumParticles();
                assert ( grid.getCell(ii, jj).getNumParticles() >= 0 );
            }
        }

        adjustTimeStep(timeStep, maxLength);
       
        return timeStep;
    }


    private double adjustTimeStep(double timeStep, double maxLength) {

        // max distance to go in one step. Beyond this, we apply the governor.
        double maxDistance = CellDimensions.CELL_SIZE / 10.0;
        double minDistance = CellDimensions.CELL_SIZE / 10000.0;

        // adjust the timestep if needed.
        double increment = (timeStep * maxLength);
        double newTimeStep = timeStep;
        if (increment > maxDistance) {
            newTimeStep /= 2.0;
            Logger.log(0, "updateParticlePosition: HALVED dt=" + timeStep +" increment="+increment );
        }
        else if (increment < minDistance) {
            newTimeStep *= 2.0;
            Logger.log(0, "updateParticlePosition: DOUBLED dt=" + timeStep +" increment="+increment );
        }
        return newTimeStep;
    }
}
