package com.becker.game.multiplayer.galactic;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.sound.MusicMaker;

import java.util.*;

/**
 *  Captures the delta state change of everything that happened during one turn (year) of the game.
 *
 *  @see Galaxy
 *  @author Barry Becker
 */
public class GalacticMove extends Move
{

    // a list of battle simulations
    private List battles_;


    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    private GalacticMove( )
    {

    }

    /**
     *  factory method for getting new moves.
     *  used to use recycled objects, but did not increase performance, so I removed it.
     */
    public static GalacticMove createMove(int moveNum)
    {
        GalacticMove m = new GalacticMove();

        m.moveNumber = moveNum;
        return m;
    }


    public List getBattleSimulations()
    {
        return battles_;
    }

    /**
     * given an order and destPlanet create a battle sequence that can be played back in the ui.
     * @param order
     * @param destPlanet
     */
    public void addSimulation(Order order, Planet destPlanet)
    {
        BattleSimulation battle = new BattleSimulation(order, destPlanet);
        addSimulation(battle);
    }


    /**
     * given an order and destPlanet create a battle sequence that can be played back in the ui.
     * @param battle
     */
    public void addSimulation(BattleSimulation battle)
    {
        if (battles_==null)
            battles_ = new ArrayList();
        battles_.add(battle);
    }


    /**
     * private class representing a single melee round result
     */
    private class Hit
    {
        int numShipsDestroyed;
        Player playerHit;

        public Hit(Player p, int numDestroyed)
        {
            playerHit = p;
            numShipsDestroyed = numDestroyed;
        }
    }

}



