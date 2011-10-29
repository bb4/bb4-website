package com.becker.game.multiplayer.galactic.player;


import com.becker.game.multiplayer.galactic.Galaxy;
import com.becker.game.multiplayer.galactic.Order;
import com.becker.game.multiplayer.galactic.Planet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a Robot Admiral commanding an intergalactic fleet of starships.
 * These Robot Admirals have there own unique strategy for playing.
 * @@ for now there is only one type, but in the future this should be an abstract base class for other types.
 *
 * @author Barry Becker
 */
public class CrazyRobotPlayer extends GalacticRobotPlayer {

    private static final int NUM_ICONS = 6;
    private static int iconIndexCounter_ = 0;


    public CrazyRobotPlayer(String name, Planet homePlanet, Color color, ImageIcon icon) {
        super(name, homePlanet, color);
        if (icon != null) {
            icon_ = icon;
        } else {
            iconBaseName_ = "crazy_robot_icon";
            iconIndex_ = iconIndexCounter_++ % NUM_ICONS;
        }
    }


    /**
     * @return the current list of this Robot's orders.
     */
    @Override
    public List<Order> makeOrders(Galaxy galaxy, int numYearsRemaining) {
        List<Order> newOrders = new ArrayList<Order>();

        List<Planet> ownedPlanets = Galaxy.getPlanets(this);
        for (Planet origin : ownedPlanets) {
            if (origin.getNumShips() > 200)
                newOrders.addAll(getOrders(origin, 6, 20, numYearsRemaining));
            else if (origin.getNumShips() > 100)
                newOrders.addAll(getOrders(origin, 5, 10, numYearsRemaining));
            else if (origin.getNumShips() > 50)
                newOrders.addAll(getOrders(origin, 3, 5, numYearsRemaining));
            else if (origin.getNumShips() > 20)
                newOrders.addAll(getOrders(origin, 1, 3, numYearsRemaining));
            // else do nothing.
        }
        orders_.addAll(newOrders);

        return orders_;
    }

}



