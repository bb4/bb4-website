package com.becker.simulation.habitat.creatures;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 * Everything we need to know about a creature.
 * There are many different sorts of creatures.
 *
 * @author Barry Becker
 */
public class Creature  {

    private CreatureType type;

    private Point2d location;
    private Vector2d velocity;
    private int numDaysPregnant;

    /** if becomes too large, then starve */
    private int hunger;
    private boolean alive;

    /**
     * Constructor
     */
    public Creature(CreatureType type, Point2d location) {
        this.type = type;
        this.location = location;

        numDaysPregnant = (int) (Math.random() * type.getGestationPeriod());
        hunger = (int) (Math.random() * type.getStarvationThreshold()/2);

        double theta = 2.0 * Math.PI * Math.random();
        this.velocity = new Vector2d(Math.sin(theta) * type.getMaxSpeed(), Math.cos(theta) * type.getMaxSpeed());
        alive = true;
    }

    /**
     * @return true if new child spawned
     */
    public boolean nextDay() {
        boolean spawn = false;
        numDaysPregnant++;
        hunger++;

        if (hunger >= type.getStarvationThreshold()) {
            alive = false;
        }

        if (numDaysPregnant >= type.getGestationPeriod()) {
            // spawn new child.
            spawn = true;
            numDaysPregnant = 0;
        }

        // adjust velocity based on neighbors
        location = computeNewPosition(velocity);

        return spawn;
    }

    public void eat(CreatureType type) {
        hunger -= type.getNutritionalValue();
        hunger = Math.max(0, hunger);
    }

    public boolean isAlive() {
        return alive;
    }

    public String getName() {
        return type.getName();
    }

    public Point2d getLocation() {
        return location;
    }

    private Point2d computeNewPosition(Vector2d vel) {
        return new Point2d((location.getX() + vel.getX()) % 1.0, (location.getY() + vel.getY()) % 1.0);
    }

    public double getSize() {
        return type.getSize();
    }
}
