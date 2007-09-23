package com.becker.game.common;

import java.awt.*;
import java.io.*;

/**
 * Represents a player in a game (either human or computer).
 *
 * @author Barry Becker
 */
public class Player implements Serializable
{
    private static final long serialVersionUID = 1;

    public static final int HUMAN_PLAYER = 1;
    public static final int COMPUTER_PLAYER = 2;

    // name of the pplayer
    protected String name_;

    // each player has an associated color
    protected Color color_;

    // each player is either human or robot
    protected int type_;

    protected boolean hasWon_ = false;


    public Player(String name, Color color, boolean isHuman)
    {
        name_ = name;
        color_ = color;
        type_ = (isHuman) ? HUMAN_PLAYER : COMPUTER_PLAYER;
    }

    public String getName()
    {
        return name_;
    }


    public void setName( String name )
    {
        this.name_ = name;
    }


    public Color getColor()
    {
        return color_;
    }

    public void setColor( Color color )
    {
        this.color_ = color;
    }


    public boolean isHuman()
    {
        return (type_ == HUMAN_PLAYER);
    }

    public void setHuman( boolean human )
    {
        type_ =  (human) ? HUMAN_PLAYER : COMPUTER_PLAYER;
    }

    public boolean hasWon()
    {
        return hasWon_;
    }

    /**
     * once you have won you cannot return to the not-won state
     */
    public void setWon(boolean won)
    {
        hasWon_ = won;
    }

    /**
     * Two players are considered equal if their name and type are the same.
     */
    public boolean equals(Object p) {
        Player p1 = (Player) p;
        return (name_.equals(p1.getName()) && isHuman() == p1.isHuman());
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer( 100 );

        sb.append("Name : ").append(name_);
        if (!isHuman())
            sb.append(" (computer)");
        return sb.toString();
    }
}



