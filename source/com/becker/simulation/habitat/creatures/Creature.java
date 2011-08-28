package com.becker.simulation.habitat.creatures;

import com.becker.simulation.habitat.model.Cell;
import com.becker.simulation.habitat.model.HabitatGrid;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.List;

/**
 * Everything we need to know about a creature.
 * There are many different sorts of creatures, but they are all represented by instance of this class.
 *
 * @author Barry Becker
 */
public class Creature  {

    /** When this close we are considered on top ot the prey */
    private static final double THRESHOLD_TO_PREY = 0.001;

    /** only pursue prey that is this close to us */
    private static final double SMELL_PREY_DISTANCE = 0.05;

    private CreatureType type;

    private Point2d location;
    private Vector2d velocity;
    private int numDaysPregnant;

    /** if becomes too large, then starve */
    private int hunger;
    private boolean alive;
    /** set to true if pursued or pursuing. Use maxSpeed when running. */
    //private boolean isRunning;
    /** chasing prey */
    private boolean pursuing;

    /**
     * Constructor
     */
    public Creature(CreatureType type, Point2d location) {
        this.type = type;
        this.location = location;

        numDaysPregnant = (int) (Math.random() * type.getGestationPeriod());
        hunger = (int) (Math.random() * type.getStarvationThreshold()/2);


        this.velocity = randomVelocity();
        alive = true;
    }

    private Vector2d randomVelocity() {
        double theta = 2.0 * Math.PI * Math.random();
        return new Vector2d(Math.sin(theta) * type.getNormalSpeed(), Math.cos(theta) * type.getNormalSpeed());
    }

    /**
     * @return true if new child spawned
     */
    public boolean nextDay(HabitatGrid grid) {
        boolean spawn = false;
        numDaysPregnant++;
        hunger++;

        if (hunger >= type.getStarvationThreshold()) {
            alive = false;
        }

        if (numDaysPregnant >= type.getGestationPeriod()) {
            // if very hungary, abort the fetus
            if (hunger > type.getStarvationThreshold() / 2) {
                numDaysPregnant = 0;
            }
            else {
                // spawn new child.
                spawn = true;
                numDaysPregnant = 0;
            }
        }

        moveTowardPreyAndEatIfPossible(grid);
        // else move toward friends and swarm
        return spawn;
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    private void moveTowardPreyAndEatIfPossible(HabitatGrid grid) {

        // adjust velocity based on neighbors
        Cell oldCell = grid.getCellForPosition(location);

        Creature nearestPrey = findNearestPrey(grid);

        if (nearestPrey != null) {

            pursuing = true;
            double distance = nearestPrey.getLocation().distance(location);

            if (distance < THRESHOLD_TO_PREY) {
                eat(nearestPrey);
                //System.out.println(this +" eating "+ nearestPrey);
                velocity = randomVelocity();
            }
            else {
                velocity = new Vector2d(nearestPrey.getLocation().getX() - location.getX(),
                                        nearestPrey.getLocation().getY() - location.getY());
                if (type.getMaxSpeed() < distance) {
                    velocity.scale(type.getMaxSpeed()/distance);
                }
            }
        }
        location = computeNewPosition(velocity);

        Cell newCell = grid.getCellForPosition(location) ;
        if (newCell != oldCell) {
            newCell.addCreature(this);
            oldCell.removeCreature(this);
        }
    }

    private Creature findNearestPrey(HabitatGrid grid) {

        if (type.getPreys().size()== 0) {
            return null;
        }
        Creature nearestPrey = null;
        double nearestDistance = Double.MAX_VALUE;

        List<Cell> cells = grid.getNeighborCells(grid.getCellForPosition(this.getLocation()));

        for (Cell cell : cells) {
            for (Creature potentialPrey : cell.getCreatures()) {
                if (type.getPreys().contains(potentialPrey.type)) {
                    double dist = potentialPrey.getLocation().distance(getLocation());
                    if (dist < nearestDistance &&  dist < SMELL_PREY_DISTANCE) {
                        nearestDistance = dist;
                        nearestPrey = potentialPrey;
                    }
                }
            }
        }
        return nearestPrey;
    }


    /**
     * @param creature  the creature we will now eat.
     */
    public void eat(Creature creature) {
        hunger -= creature.type.getNutritionalValue();
        creature.kill();
        hunger = Math.max(0, hunger);
        pursuing = false;
    }

    public void kill() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isPursuing() {
        return pursuing;
    }

    public String getName() {
        return type.getName();
    }

    public Point2d getLocation() {
        return location;
    }

    private Point2d computeNewPosition(Vector2d vel) {
        return new Point2d( absMod(location.getX() + vel.getX()),  absMod(location.getY() + vel.getY()));
    }

    private double absMod(double value) {
        double newValue = value % 1.0;
        return newValue<0 ? 1- newValue :  newValue;
    }

    public double getSize() {
        return type.getSize();
    }

    public String toString() {
        return getName() + " hunger="  + hunger + " pregnant=" + numDaysPregnant + " alive="+ alive;
    }
}
