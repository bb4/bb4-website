package com.becker.game.multiplayer.galactic;


import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Represents a Robot Admiral commanding an intergalactic fleet of starships.
 * These Robot Admirals have there own unique strategies for playing.
 * Abstract base class for other robot player types.
 *
 * @author Barry Becker
 */
public abstract class GalacticRobotPlayer extends GalacticPlayer
{

    private static final int CRAZY_ROBOT = 0;
    private static final int METHODICAL_ROBOT = 1;
    private static int NUM_ROBOT_TYPES = 2;


    public GalacticRobotPlayer(String name, Planet homePlanet, Color color)
    {
        super(name, homePlanet, color, false);
    }


    /**
     * @return the current list of this Robot's orders.
     */
    public abstract List makeOrders(Galaxy galaxy, int numYearsRemaining);


    /**
     * send atacks to numAttacks closest planets not owned by this robot player.
     * @param origin planet from which the attack fleet will originate
     * @param numAttacks
     * @return list of orders
     */
    protected List getOrders(Planet origin, int numAttacks, int numShipsToLeaveBehind, int numYearsRemaining)
    {
        List orders = new ArrayList();

        int numShipsToSend = origin.getNumShips() - numShipsToLeaveBehind;

        List planets = Galaxy.getPlanets();
        // we must set a comparator to sort all the planets relative to.
        Planet.comparatorPlanet = origin;
        Collections.sort(planets);

        // find the numAttack closest planets
        List closestEnemies = new ArrayList();
        Iterator it = planets.iterator();
        int ct = 0;
        while (it.hasNext() && ct<numAttacks) {
            Planet p = (Planet)it.next();

            if (p.getOwner() != origin.getOwner()) {
                closestEnemies.add(p);
                ct++;
            }
        }
        // now create the orders that will send numShipsToSend/numAttacks ships to each of these planets
        int attackFleetSize = numShipsToSend/numAttacks;
        it = closestEnemies.iterator();
        while (it.hasNext()) {
            Planet target = (Planet)it.next();
            Order order = new Order(origin, target, attackFleetSize);

            // only add the order if there is enough time remaining to reach that planet.
            if (order.getTimeNeeded() < numYearsRemaining)  {
                origin.deductShips(attackFleetSize);
                orders.add(order);
            }
        }
        return orders;
    }


    /**
     *
     * @return a random robot player
     */
    public static GalacticRobotPlayer getRandomRobotPlayer(String name, Planet homePlanet, Color color)
    {
        int r = (int)(NUM_ROBOT_TYPES * Math.random());
        return getRobotPlayer(r, name, homePlanet, color);
    }


    private static int seq_ = 0;
    /**
     *
     * @return  robot players in round robin order (not randomly)
     */
    public static GalacticRobotPlayer getSequencedRobotPlayer(String name, Planet homePlanet, Color color)
    {

        int r = seq_++ % NUM_ROBOT_TYPES;
        return getRobotPlayer(r, name, homePlanet, color);
    }


    private static GalacticRobotPlayer getRobotPlayer(int type, String name, Planet homePlanet, Color color)
    {
         switch (type) {
            case CRAZY_ROBOT: return new CrazyRobotPlayer(name, homePlanet, color);
            case METHODICAL_ROBOT: return new MethodicalRobotPlayer(name, homePlanet, color);
        }
        assert (false):"bad type="+type;
        return null;
    }
}



