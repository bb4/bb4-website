package com.becker.game.common;

import com.becker.game.common.online.IServerConnection;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents a player in a game (either human or computer).
 *
 * @author Barry Becker
 */
public class Player implements Serializable
{
    private static final long serialVersionUID = 1;

    private static final int HUMAN_PLAYER = 1;
    private static final int COMPUTER_PLAYER = 2;

    /** name of the pplayer. */
    protected String name_;

    /** each player has an associated color. */
    private Color color_;

    /** each player is either human or robot. */
    private int type_;

    /** Becomes true if this player has won the game. */
    private boolean hasWon_ = false;


    /**
     * Constructor.
     * @param name name of the player
     * @param color some color identifying th eplayer in the ui.
     * @param isHuman true if human rather than computer player
     */
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
    
    public boolean isSurrogate() {
        return false;
    }

    public Player createSurrogate(IServerConnection connection) {
        return new SurrogatePlayer(this, connection);
    }

    /**
     * Two players are considered equal if their name and type are the same.
     */
    @Override
    public boolean equals(Object p) {
        Player p1 = (Player) p;
        return (name_.equals(p1.getName()) && isHuman() == p1.isHuman());
    }

    @Override
    public int hashCode() {
        int hash = (isHuman() ? 100000000: 0);
        for (int i = 0; i<name_.length(); i++) {
            hash += 10*i + name_.charAt(i);
        }
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( 100 );

        sb.append("[ *").append(name_).append("* ");
        if (!isHuman())
            sb.append(" (computer)");
        sb.append(additionalInfo()).append(" ]");
        return sb.toString();
    }
    
    protected String additionalInfo() {
        return "";
    }
}



