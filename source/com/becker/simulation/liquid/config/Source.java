package com.becker.simulation.liquid.config;

import com.becker.common.Location;
import javax.vecmath.Vector2d;

/**
 * Represents a source of liquid in the simulation.
 * Like a spigot.
 *
 * @author becker
 */
public class Source extends Region {

    /** The direction that all the cells in the source region are flowing. */
    Vector2d velocity;

    public Source(Location start, Location stop, Vector2d velocity) {
        super(start, stop);
        this.velocity = velocity;
    }

    public Source(Location start,  Vector2d velocity) {
        this(start, null, velocity);
    }

    public Vector2d getVelocity() {
        return velocity;
    }
}
