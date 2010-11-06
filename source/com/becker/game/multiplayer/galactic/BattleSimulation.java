package com.becker.game.multiplayer.galactic;

import com.becker.game.common.Player;
import com.becker.game.multiplayer.galactic.player.GalacticPlayer;

import java.util.LinkedList;
import java.util.List;

/**
 *  Captures the delta state change of everything that happened during one turn of the game.
 *
 *  @see Galaxy
 *  @author Barry Becker
 */
public class BattleSimulation
{

    private List<Player> hits_;
    private int numShipsAfterAttack_ = 0;
    private GalacticPlayer ownerAfterAttack_;
    //private GalacticPlayer ownerBeforeAttack_;

    private Order order_;
    private Planet destPlanet_;


    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    public BattleSimulation(Order order, Planet destPlanet)
    {
        order_ = order;
        destPlanet_ = destPlanet;
        createSimulation(order, destPlanet);
    }

    /*
    public GalacticPlayer getOwnerBeforeAttack()
    {
        return ownerBeforeAttack_;
    }*/

    public GalacticPlayer getOwnerAfterAttack()
    {
        return ownerAfterAttack_;
    }

    public int getNumShipsAfterAttack()
    {
        return numShipsAfterAttack_;
    }

    public List getHitSequence()
    {
        return hits_;
    }

    /**
     * @return the planet on which the battle is occurring.
     */
    public Planet getPlanet()
    {
        return destPlanet_;
    }

    /**
     * @return the order that started it all.
     */
    public Order getOrder()
    {
        return order_;
    }


    /**
     * given an order and destPlanet create a battle sequence that can be played back in the ui
     * @param order
     * @param destPlanet
     */
    void createSimulation(Order order, Planet destPlanet)
    {
        hits_ = new LinkedList<Player>();

        int numAttackShips = order.getFleetSize();
        int numDefendShips = destPlanet.getNumShips();
        Player attacker = order.getOwner();
        Player defender = destPlanet.getOwner();

        //String sDefender = (destPlanet.getOwner()==null)? "Neutral" : destPlanet.getOwner().getName();

        if (order.getOwner()==destPlanet.getOwner()) {
            // reinforcements have arrived.
            numShipsAfterAttack_ = order.getFleetSize() + destPlanet.getNumShips();
            ownerAfterAttack_ = destPlanet.getOwner();
        }
        else {
            // create hit sequence
            while (numAttackShips>0 && numDefendShips>0) {
                // int total = numAttackShips + numDefendShips;

                double ratio = (0.5+ (double)numDefendShips/((double)(numAttackShips + numDefendShips)))/2.0;
                if (Math.random() > ratio) {
                    numAttackShips--;
                    hits_.add(attacker);
                }
                else {
                    numDefendShips--;
                    hits_.add(defender);
                }
            }
            if (numAttackShips == 0) {
                // defenders won
                ownerAfterAttack_ = destPlanet.getOwner();
                numShipsAfterAttack_ = numDefendShips;
            }
            else {
                // attacker won
                assert (numDefendShips == 0);
                ownerAfterAttack_ = order.getOwner();
                numShipsAfterAttack_ = numAttackShips;
            }
        }
    }


}



