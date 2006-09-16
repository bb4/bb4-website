package com.becker.game.multiplayer.poker.player;

/**
 * Available robot player types.
 *
 * @author Barry Becker Date: Sep 10, 2006
 */
public enum RobotType {


    CRAZY_ROBOT("Crazy"),
    METHODICAL_ROBOT("Methodical");

    
    private String name_;

    RobotType(String name) {
        name_ = name;
    }

    public String getName() {
        return name_;
    }
}
