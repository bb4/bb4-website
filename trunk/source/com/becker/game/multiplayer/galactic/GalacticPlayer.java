package com.becker.game.multiplayer.galactic;

import com.becker.game.common.*;
import com.becker.ui.GUIUtil;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.text.MessageFormat;

/**
 * Represents an Admiral commanding an intergalactic fleet of starships.
 *
 * @author Barry Becker
 */
public abstract class GalacticPlayer extends Player
{
    // this player's home planet. (like earth is for humans)
    private Planet homePlanet_;


    // a list of outstanding Orders
    List orders_;

    // ? have list of planets owned?

    private static final float SATURATION = .8f;
    private static final float BRIGHTNESS = .999f;

    public static final int DEFAULT_NUM_SHIPS = 100;

    protected GalacticPlayer(String name, Planet homePlanet, Color color, boolean isHuman)
    {
        super(name, color, isHuman);
        homePlanet_ = homePlanet;
        homePlanet_.setOwner(this);
        orders_ = new LinkedList();
    }

    /**
     * Factory method for creating Galactic players of the appropriate type.
     * @param name
     * @param homePlanet
     * @param color
     * @param isHuman
     * @return
     */
    public static GalacticPlayer createGalacticPlayer(String name, Planet homePlanet, Color color, boolean isHuman)
    {
       if (isHuman)
           return new GalacticHumanPlayer(name, homePlanet, color);
        else
           return GalacticRobotPlayer.getSequencedRobotPlayer(name, homePlanet, color);
    }

    /**
     *
     * @param i index of player
     * @return  the default name for player i
     */
    public String getDefaultName(int i)
    {
        Object[] args = {Integer.toString(i)};
        String dname = MessageFormat.format(GameContext.getLabel("GALACTIC_DEFAULT_NAME"), args );
        return dname;
    }

    public Planet getHomePlanet()
    {
        return homePlanet_;
    }

    public void setHomePlanet( Planet homePlanet )
    {
        this.homePlanet_ = homePlanet;
    }


    /**
     * try to give a unique color based on the name
     * and knowing what the current player colors are.
     */
    public static final Color getNewPlayerColor(GalacticPlayer[] players)
    {

        boolean uniqueEnough = false;
        float candidateHue;

        do {
            // keep trying hues until we find one that is not within tolerance distance from another
            candidateHue = (float)Math.random();
            uniqueEnough = isHueUniqueEnough(candidateHue, players);
        } while (!uniqueEnough);

        return Color.getHSBColor(candidateHue, SATURATION, BRIGHTNESS);
    }

    /**
     * @@ this method could use some improvment
     * @param hue to check for uniqueness compared to other players.
     * @param players
     * @return
     */
    private static boolean isHueUniqueEnough(float hue, GalacticPlayer[] players)
    {
        int ct=0;
        float tolerance = 1.0f/(1.0f+1.8f*players.length);
        while ( ct < players.length) {
            if (players[ct] == null)
                ct++;
            else if (Math.abs(GUIUtil.getColorHue(players[ct].getColor()) - hue) > tolerance)
                ct++;
            else break;
        }
        if (ct == players.length)
            return true;
        return false;
    }

    /**
     * @param orders set the current list of orders for the player
     */
    public void setOrders(List orders)
    {
        if (orders==null)
            return;
        orders_.clear();
        for (int i=0; i<orders.size(); i++) {
            orders_.add(orders.get(i));
        }
    }

    /**
     * @return get the current list of orders for the player
     */
    public List getOrders()
    {
        GameContext.log(1,  "orders_="+orders_ );
        return orders_;
    }

    /**
     * The total ships is computed by summing the number of ships
     * at each of the player owned planets plus the number of ships that
     * are in transit.
     * @return total num ships under this players command
     */
    public int getTotalNumShips()
    {
        int totalNumShips = 0;
        Iterator it = orders_.iterator();
        while (it.hasNext()) {
            Order order = (Order)it.next();
            totalNumShips += order.getFleetSize();
        }
        List ownedPlanets = Galaxy.getPlanets(this);
        Iterator pit = ownedPlanets.iterator();
        while (pit.hasNext()) {
            Planet p = (Planet)pit.next();
            totalNumShips += p.getNumShips();
        }
        return totalNumShips;
    }

    /**
     * @return  the total production capacity of all the planets owned by this player
     */
    public int getTotalProductionCapacity()
    {
         int totalCapacity = 0;
        List ownedPlanets = Galaxy.getPlanets(this);
        Iterator pit = ownedPlanets.iterator();
        while (pit.hasNext()) {
            Planet p = (Planet)pit.next();
            totalCapacity += p.getProductionCapacity();
        }
        return totalCapacity;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer( super.toString() );
        sb.append("Fleet size: "+getTotalNumShips());
        sb.append("Home planet: "+homePlanet_.getName());
        return sb.toString();
    }

}



