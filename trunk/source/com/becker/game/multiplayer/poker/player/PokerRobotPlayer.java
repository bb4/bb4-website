package com.becker.game.multiplayer.poker.player;


import com.becker.game.multiplayer.poker.*;

import java.awt.*;

/**
 * Represents a Robot poker player.
 * These Robot Admirals have there own unique strategy for playing.
 * Abstract base class for other robot player types.
 *
 * @author Barry Becker
 */
public abstract class PokerRobotPlayer extends PokerPlayer
{
    private static final long serialVersionUID = 1;

    private RobotType robotType_;

    public PokerRobotPlayer(String name, int money, Color color, RobotType rType)
    {
        super(name, money, color, false);
        robotType_ = rType;
    }


    public String getType() {
        return  robotType_.getName();
    }

    /**
     *
     * @return an appropriate action based on the situation
     */
    //public abstract PokerAction getAction(PokerController pc);

    /**
     *
     * @return the amount that the robot raises if he raises.
     */
    protected abstract int getRaise(PokerController pc);

    /**
     *
     * @return a random robot player
     */
    public static PokerRobotPlayer getRandomRobotPlayer(String name, int money, Color color)
    {
        int r = (int)(RobotType.values().length * Math.random());
        return getRobotPlayer(RobotType.values()[r], name, money, color);
    }


    private static int seq_ = 0;
    /**
     *
     * @return  robot players in round robin order (not randomly)
     */
    public static PokerRobotPlayer getSequencedRobotPlayer(String name, int money, Color color)
    {
        int r = seq_++ % RobotType.values().length;
        return getRobotPlayer(RobotType.values()[r], name, money, color);
    }


    private static PokerRobotPlayer getRobotPlayer(RobotType type, String name, int money, Color color)
    {
         switch (type) {
            case CRAZY_ROBOT: return new CrazyRobotPlayer(name, money, color, RobotType.CRAZY_ROBOT);
            case METHODICAL_ROBOT: return new MethodicalRobotPlayer(name, money, color, RobotType.METHODICAL_ROBOT);
        }
        return null;
    }


    protected int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    protected boolean allOthersFolded(PokerController pc) {
        PokerPlayer[] players = (PokerPlayer[]) pc.getPlayers();
        for (final PokerPlayer newVar : players) {
            if (!newVar.hasFolded() && (newVar != this)) {
                return false;
            }
        }
        return true;
    }
}



