package com.becker.game.multiplayer.galactic;


import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Represents a Robot Admiral commanding an intergalactic fleet of starships.
 * These Robot Admirals have there own unique strategies for playing.
 * @@ for now there is only one type, but in the future this should be an abstract base class for other types.
 *
 * @author Barry Becker
 */
public class CrazyRobotPlayer extends GalacticRobotPlayer
{


    public CrazyRobotPlayer(String name, Planet homePlanet, Color color)
    {
        super(name, homePlanet, color);
    }


    /**
     * @return the current list of this Robot's orders.
     */
    public List makeOrders(Galaxy galaxy)
    {
        List newOrders = new ArrayList();

        List ownedPlanets = galaxy.getPlanets(this);
        Iterator it = ownedPlanets.iterator();
        while (it.hasNext()) {
            Planet origin = (Planet)it.next();
            if (origin.getNumShips()>200)
                newOrders.addAll(getOrders(origin, 6, 20));
            else if (origin.getNumShips()>100)
                newOrders.addAll(getOrders(origin, 5, 10));
            else if (origin.getNumShips()>50)
                newOrders.addAll(getOrders(origin, 3, 5));
            else if (origin.getNumShips()>20)
                newOrders.addAll(getOrders(origin, 1, 3));
            // else do nothing.
        }
        orders_.addAll(newOrders);

        return orders_;
    }


}



