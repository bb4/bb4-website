package com.becker.game.multiplayer.galactic;

import com.becker.game.multiplayer.common.MultiGameOptions;

/**
 * Container for the different Galactic Game options.
 *
 * @author Barry Becker Date: Sep 2, 2006
 */
public class GalacticOptions extends MultiGameOptions {


    private static final int DEFAULT_PLANET_PRODUCTION_RATE = 2;
    private static final int DEFAULT_NEUTRAL_FLEET_SIZE = 10;
    private static final int DEFAULT_MAX_YEARS = 10;
    private static final boolean DEFAULT_NEUTRALS_BUILD = false;
    private static final int DEFAULT_NUM_PLANETS = 30;
    private static final int GALACTIC_PLAYER_LIMIT = 12;

    private int planetProductionRate_ = DEFAULT_PLANET_PRODUCTION_RATE;
    private int initialFleetSize_ = DEFAULT_NEUTRAL_FLEET_SIZE;
    private int maxYearsToPlay_ = DEFAULT_MAX_YEARS;
    private boolean neutralsBuild_ = DEFAULT_NEUTRALS_BUILD;
    private int numPlanets_ = DEFAULT_NUM_PLANETS;


    /**
     * this constructor uses all default values.
     */
    public GalacticOptions() {
        setMaxNumPlayers(GALACTIC_PLAYER_LIMIT);
    }

    /**
     * User specified valeus for options.
     */
    public GalacticOptions(int maxNumPlayers, int numRobotPlayers, int numPlanets, int planetProductionRate,
                           int maxYearsToPlay, int initialFleetSize, boolean neutralsBuild) {
        super(maxNumPlayers, numRobotPlayers);
        setNumPlanets(numPlanets);
        setPlanetProductionRate(planetProductionRate);
        setMaxYearsToPlay(maxYearsToPlay);
        setInitialFleetSize(initialFleetSize);
        setNeutralsBuild(neutralsBuild);
        setMaxNumPlayers(maxNumPlayers);
    }

    public int getNumPlanets() {
        return numPlanets_;
    }
    public void setNumPlanets(int numPlanets) {
        numPlanets_ = numPlanets;
    }


    public int getPlanetProductionRate() {
        return planetProductionRate_;
    }
    public void setPlanetProductionRate(int planetProductionRate) {
        planetProductionRate_ = planetProductionRate;
    }


    public int getMaxYearsToPlay() {
        return maxYearsToPlay_;
    }
    public void setMaxYearsToPlay(int maxYearsToPlay) {
        maxYearsToPlay_ = maxYearsToPlay;
    }


    public boolean doNeutralsBuild() {
        return neutralsBuild_;
    }
    public void setNeutralsBuild(boolean neutralsBuild) {
        neutralsBuild_ = neutralsBuild;
    }


    public int getInitialFleetSize() {
        return initialFleetSize_;
    }
    public void setInitialFleetSize(int initialFleetSize) {
        initialFleetSize_ = initialFleetSize;
    }
}
