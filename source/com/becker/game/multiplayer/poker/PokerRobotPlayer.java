package com.becker.game.multiplayer.poker;


import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Represents a Robot poker player.
 * These Robot Admirals have there own unique strategies for playing.
 * Abstract base class for other robot player types.
 *
 * @author Barry Becker
 */
public abstract class PokerRobotPlayer extends PokerPlayer
{

    private static final int CRAZY_ROBOT = 0;
    private static final int METHODICAL_ROBOT = 1;
    private static int NUM_ROBOT_TYPES = 2;


    public PokerRobotPlayer(String name, int money, Color color)
    {
        super(name, money, color, false);
    }


    /**
     *
     * @return an appropriate action based on the situation
     */
    public abstract Action getAction(PokerController pc);

    /**
     *
     * @return the amount that the robot raises if he raises.
     */
    public abstract int getRaise();

    /**
     *
     * @return a random robot player
     */
    public static PokerRobotPlayer getRandomRobotPlayer(String name, int money, Color color)
    {
        int r = (int)(NUM_ROBOT_TYPES * Math.random());
        return getRobotPlayer(r, name, money, color);
    }


    private static int seq_ = 0;
    /**
     *
     * @return  robot players in round robin order (not randomly)
     */
    public static PokerRobotPlayer getSequencedRobotPlayer(String name, int money, Color color)
    {
        int r = seq_++ % NUM_ROBOT_TYPES;
        return getRobotPlayer(r, name, money, color);
    }


    private static PokerRobotPlayer getRobotPlayer(int type, String name, int money, Color color)
    {

         switch (type) {
            case CRAZY_ROBOT: return new CrazyRobotPlayer(name, money, color);
            case METHODICAL_ROBOT: return new MethodicalRobotPlayer(name, money, color);
        }
        assert (false):"bad type="+type;

        return null;

    }

    protected boolean allOthersFolded(PokerController pc) {
        PokerPlayer[] players = (PokerPlayer[]) pc.getPlayers();
        for (int i=0; i<players.length; i++) {
            if (!players[i].hasFolded() && (players[i] != this))  {
                return false;
            }
        }
        return true;
    }
}



