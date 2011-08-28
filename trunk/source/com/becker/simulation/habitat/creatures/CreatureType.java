package com.becker.simulation.habitat.creatures;

import org.igoweb.igoweb.client.gtp.A;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Everything we need to know about a creature.
 * There are many different sorts of creatures.
 *
 * Add more creatures like sheep, fox, chickens, ants, vulture,
 *
 * @author Barry Becker
 */
public enum CreatureType {
                                        // size,   mSpeed,  normpeed, gest, starve,  nutr
    GRASS("grass", new Color(40, 255, 20),    2.0,    0.0,      0.0,    11,    42,     1),
    COW(  "cow",   new Color(70, 60, 100),   15.0,    0.002,    0.0001,  25,    94,    10),
    RAT(  "rat",   new Color(140, 105, 20),   2.0,    0.005,    0.002,   8,    32,     1),
    CAT(  "cat",   new Color(0, 195, 220),    5.0,    0.01,     0.004,  14,    60,     4),
    LION("lion",   new Color(240, 200, 20),   9.0,    0.02,    0.008,  21,    90,     6);


    private String name;
    private Color color;
    private double size;
    private double normalSpeed;
    private double maxSpeed;

    /** when numDaysPregnant = this then reproduce */
    private int gestationPeriod;

    /** when hunger >= this then die. */
    private int starvationThreshold;

    private int nutritionalValue;

    private static final Map<CreatureType, List<CreatureType>> predatorMap_ = new HashMap<CreatureType, List<CreatureType>>();
    private static final Map<CreatureType, List<CreatureType>> preyMap_ = new HashMap<CreatureType, List<CreatureType>>();


    static {
        // eaten by relationship
        predatorMap_.put(GRASS, Arrays.asList(COW, RAT));
        predatorMap_.put(COW, Arrays.asList(LION));
        predatorMap_.put(RAT, Arrays.asList(CAT, LION));
        predatorMap_.put(CAT, Arrays.asList(LION));
        predatorMap_.put(LION, Collections.EMPTY_LIST);

        for (CreatureType creature : values()) {
            List<CreatureType> preys = new ArrayList<CreatureType>();

            for (CreatureType potentialprey : values()) {
                List<CreatureType> preds = predatorMap_.get(potentialprey);
                if (preds.contains(creature)) {
                    preys.add(potentialprey);
                }
            }
            preyMap_.put(creature, preys);
        }
    }

    CreatureType(String name, Color color, double size, double normlSpeed,
                 double maxSpeed, int gestationPeriod, int starvationTheshold, int nutritionalValue)  {
        this.name = name;
        this.color = color;
        this.size = size;
        this.normalSpeed = normlSpeed;
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

    public void setSize(double s) {
        size = s;
    }


    public double getNormalSpeed() {
        return normalSpeed;
    }

    public void setNormalSpeed(double s) {
        normalSpeed = s;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double s) {
        maxSpeed = s;
    }

    public int getGestationPeriod() {
        return gestationPeriod;
    }

    public void setGestationPeriod(int g) {
        gestationPeriod = g;
    }

    public int getStarvationThreshold() {
        return starvationThreshold;
    }

    public void setStarvationThreshold(int value) {
        starvationThreshold = value;
    }

    public int getNutritionalValue() {
        return nutritionalValue;
    }

    public void setNutritionalValue(int value) {
        nutritionalValue = value;
    }

    public List<CreatureType> getPredators() {
        return predatorMap_.get(this);
    }

    public List<CreatureType> getPreys() {
        return preyMap_.get(this);
    }

    public String toString() {
        return getName();
    }

    /** for testing */
    public static void main(String[] args) {
        System.out.println("preyMap = " + preyMap_);
    }

}
