package com.becker.simulation.predprey.creatures;

/**
 * Everything we need to know about a population of creatures.
 *
 * @author Barry Becker
 */
public abstract class Population {

    public int population;
    public double birthRate;
    public double deathRate;

    public Population() {
        reset();
    }

    public abstract String getName();

    public void reset() {
        population = getInitialPopulation();
        birthRate = getInitialBirthRate();
        deathRate = getInitialDeathRate();
    }

    public abstract int getInitialPopulation();
    public abstract double getInitialBirthRate();
    public abstract double getInitialDeathRate();


    public double getMaxBirthRate() {
        return 3.0;
    }

    public double getMaxDeathRate() {
        return 20.0;
    }

}
