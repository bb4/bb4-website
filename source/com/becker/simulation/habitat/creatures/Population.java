package com.becker.simulation.habitat.creatures;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Everything we need to know about a population of creatures.
 *
 * @author Barry Becker
 */
public class Population {

    private CreatureType type;

    private List<Creature> creatures;


    public Population(CreatureType type) {
        this.type = type;
        creatures = new ArrayList<Creature>();
    }

    public void createInitialSet(int num) {
        for (int i=0; i<num; i++) {
            creatures.add(new Creature(type, new Point2d(Math.random(), Math.random())));
        }
    }

    /**
     * get a defensive copy.
     */
    public List<Creature> getCreatures() {
        return new ArrayList<Creature>(creatures);
    }

    public void nextDay() {
        List<Point2d> spawnLocations = new ArrayList<Point2d>();

        Iterator<Creature> creatureIt = creatures.iterator();
        int numRemoved = 0;
        while (creatureIt.hasNext())   {
            Creature creature = creatureIt.next();

            boolean spawn = creature.nextDay();

            if (!creature.isAlive())  {
                numRemoved++;
                creatureIt.remove();
            }
            else if (spawn) {
                Point2d loc = creature.getLocation();
                spawnLocations.add(new Point2d(loc.getX() + 0.1 * Math.random(), loc.getY() + 0.1 * Math.random()));
            }
        }


        // add new children
        System.out.println("adding " + spawnLocations.size() + " more " + getName() + " and removed " + numRemoved);
        for (Point2d newLocation : spawnLocations) {
            creatures.add(new Creature(type, newLocation));
        }
    }

    public CreatureType getType() {
        return type;
    }

    public int getSize() {
        return creatures.size();
    }

    public String getName() {
        return "Population of " + type.getName();
    }


}
