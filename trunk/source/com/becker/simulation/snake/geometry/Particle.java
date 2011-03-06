package com.becker.simulation.snake.geometry;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 *  Particle is a point mass that approximates the mass of the snake.
 *  It is also a vertex composing the geometry.
 *  No getter/setter because we need speed.
 *
 *  @author Barry Becker
 */
public class Particle extends Point2d {

    /** the velocity vector of the particle in m/s */
    public Vector2d velocity = new Vector2d( 0.0, 0.0 );

    /** the acceleration vector of the particle in m/s^2 */
    public Vector2d acceleration = new Vector2d( 0.0, 0.0 );

    /** the force vector (sum of all forces acting on the particle) */
    public Vector2d force = new Vector2d( 0.0, 0.0 );

    /** the frictional force if used */
    public Vector2d frictionalForce = new Vector2d( 0.0, 0.0 );

    /** the mass of the particle in kg*/
    public double mass = 0;

    /**
     * Construct the particle
     * assumes that the initial velocity is 0.
     */
    public Particle( double x, double y, double m ) {
        super( x, y );
        mass = m;
    }

}
