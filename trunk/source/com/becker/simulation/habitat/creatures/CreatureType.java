package com.becker.simulation.habitat.creatures;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Everything we need to know about a creature.
 * There are many different sorts of creatures.
 *
 * @author Barry Becker
 */
public enum CreatureType {
                                        // size,   mSpeed,  gest, starve, nutr
    GRASS("grass", new Color(40, 255, 20),    2.0,    0.0,     3,     0,     1),
    COW(  "cow",   new Color(70, 60, 100),   30.0,    0.01,   30,    40,    10),
    RAT(  "rat",   new Color(140, 105, 20),   2.0,    0.005,   5,    6,     2),
    CAT(  "cat",   new Color(10, 155, 200),   5.0,    0.02,   16,    19,     4),
    LION("lion",  new Color(230, 210, 10),   20.0,    0.04,   20,     23,    6);

    private String name;
    private Color color;
    private double size;
    private double maxSpeed;
    private int gestationPeriod; // when numDaysPregnant = this then reproduce
    private int starvationThreshold; // when hunger >= this then die.
    private int nutritionalValue;

    private static final Map<CreatureType, CreatureType[]> predatorMap_ = new HashMap<CreatureType, CreatureType[]>();
    private static final Map<CreatureType, CreatureType[]> preyMap_ = new HashMap<CreatureType, CreatureType[]>();


    static {
        predatorMap_.put(GRASS, new CreatureType[] {COW, RAT});
        predatorMap_.put(COW, new CreatureType[] {LION});
        predatorMap_.put(RAT, new CreatureType[] {CAT, LION});
        predatorMap_.put(CAT, new CreatureType[] {LION});
        predatorMap_.put(LION, new CreatureType[] {});

        for (CreatureType creature : values()) {
            List<CreatureType> preys = new ArrayList<CreatureType>();

            for (CreatureType potentialprey : values()) {
                List<CreatureType> preds = Arrays.asList(predatorMap_.get(potentialprey));
                if (preds.contains(creature)) {
                    preys.add(potentialprey);
                }
            }
            preyMap_.put(creature, preys.toArray(new CreatureType[preys.size()]));
        }
    }

    CreatureType(String name, Color color, double size,
                 double maxSpeed, int gestationPeriod, int starvationTheshold, int nutritionalValue)  {
        this.name = name;
        this.color = color;
        this.size = size;
        this.maxSpeed = maxSpeed;
        this.gestationPeriod = gestationPeriod;
        this.starvationThreshold = starvationTheshold;
        this.nutritionalValue = nutritionalValue;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public double getSize() {
        return size;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public int getGestationPeriod() {
        return gestationPeriod;
    }

    public int getStarvationThreshold() {
        return starvationThreshold;
    }

    public int getNutritionalValue() {
        return nutritionalValue;
    }


    public CreatureType[] getPredators() {
        return predatorMap_.get(this);
    }

    public CreatureType[] getPreys() {
        return preyMap_.get(this);
    }

    public String toString() {
        return getName();
    }

    /** for testing */
    public static void main(String[] args) {
        System.out.println("preyMap = " );
        for (CreatureType creature : values()) {
            System.out.println(creature + " eats  " + Arrays.toString(preyMap_.get(creature)));
        }
    }

}
