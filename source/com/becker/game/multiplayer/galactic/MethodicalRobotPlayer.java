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
public class MethodicalRobotPlayer extends GalacticRobotPlayer
{


    public MethodicalRobotPlayer(String name, Planet homePlanet, Color color)
    {
        super(name, homePlanet, color);
    }


    /**
     * @return the current list of this Robot's orders.
     */
    public List makeOrders(Galaxy galaxy, int numYearsRemaining)
    {
        List newOrders = new ArrayList();

        List ownedPlanets = Galaxy.getPlanets(this);
        Iterator it = ownedPlanets.iterator();
        while (it.hasNext()) {
            Planet origin = (Planet)it.next();
            if (origin.getNumShips()>200)
                newOrders.addAll(getOrders(origin, 4, 50, numYearsRemaining));
            else if (origin.getNumShips()>100)
                newOrders.addAll(getOrders(origin, 3, 30, numYearsRemaining));
            else if (origin.getNumShips()>50)
                newOrders.addAll(getOrders(origin, 2, 20, numYearsRemaining));
            // else do nothing.
        }
        orders_.addAll(newOrders);

        return orders_;
    }

}



