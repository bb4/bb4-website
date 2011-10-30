/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.trivial.player;

import com.becker.game.common.GameContext;
import com.becker.game.common.player.PlayerAction;
import com.becker.game.multiplayer.common.MultiGameController;
import com.becker.game.multiplayer.common.MultiGamePlayer;
import com.becker.game.multiplayer.common.MultiPlayerMarker;
import com.becker.game.multiplayer.trivial.TrivialAction;

import java.awt.*;
import java.text.MessageFormat;

/**
 * Represents a Player in a game
 *
 * @author Barry Becker
 */
public abstract class TrivialPlayer extends MultiGamePlayer
{
    private static final long serialVersionUID = 1;

    private MultiPlayerMarker piece_;

    private int value;

    private TrivialAction action_;
    
    private static final int MAX_VALUE = 1000;
    
    /** only becomes true when the player decides to reveal his value */
    private boolean revealed_ = false;
    

    /**
     * 
     */
    TrivialPlayer(String name, Color color, boolean isHuman)
    {
        super(name, color, isHuman);
        
        value = (int) ((MAX_VALUE + 0.9999) * Math.random());
    
        piece_ = new MultiPlayerMarker(this);
        revealed_ = false;
    }

    /**

     * @return a number between 0 and 1000.
     */
    public int getValue() {
        return value;
    }
    
    @Override
    public PlayerAction getAction(MultiGameController pc) {
        return action_;
    }
    
    @Override
    public void setAction(PlayerAction action) {
        action_ = (TrivialAction) action;        
    }

    /**
     * You can only reveal your value, never hide it once revealed.
     */
    public void revealValue() {
        revealed_ = true;
    }
    
    public boolean isRevealed() {
        return revealed_;
    }
    
    @Override
    public MultiPlayerMarker getPiece() {
        return piece_;
    }
    
    /**
     * Factory method for creating players of the appropriate type.
     * @param name
     * @param color
     * @param isHuman
     * @return
     */
    public static TrivialPlayer createTrivialPlayer(String name, Color color, boolean isHuman)
    {
       if (isHuman)
           return new TrivialHumanPlayer(name,  color);
        else
           return new TrivialRobotPlayer(name, color);
    }
    
    /**
     *
     * @param i index of player
     * @return  the default name for player i
     */
    public String getDefaultName(int i)
    {
        Object[] args = {Integer.toString(i)};
        return MessageFormat.format(GameContext.getLabel("TRIVIAL_DEFAULT_NAME"), args );
    }

    public void setPiece(MultiPlayerMarker piece) {
        piece_ = piece;
    }

}



