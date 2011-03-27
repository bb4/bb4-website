package com.becker.simulation.liquid.model;

import javax.vecmath.Point2d;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *  A set of particles in a grid. These particles represent the fluid.
 *
 *  @author Barry Becker
 */
public class Particles extends HashSet<Particle> {



    /** ensure that the runs are the same  */
    private static final Random RANDOM = new Random(1);


    public void addRandomParticles( double x, double y, int numParticles, Grid grid)  {

        for ( int i = 0; i < numParticles; i++ ) {
            addParticle(x + RANDOM.nextDouble(), y + RANDOM.nextDouble(), grid);
        }
    }

    private void addParticle( double x, double y, Grid grid) {

        Cell cell = grid.getCell((int)x, (int)y);
        Particle p = new Particle( x, y, cell);
        this.add(p);
        cell.incParticles();
    }
}
