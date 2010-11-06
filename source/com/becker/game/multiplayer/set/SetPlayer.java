package com.becker.game.multiplayer.set;

import com.becker.game.common.GameContext;
import com.becker.game.common.PlayerAction;
import com.becker.game.multiplayer.common.MultiGameController;
import com.becker.game.multiplayer.common.MultiGamePlayer;

import java.awt.*;
import java.text.MessageFormat;

/**
 * Represents a Player in a Set game
 *
 * @author Barry Becker
 */
public class SetPlayer extends MultiGamePlayer
{

    private int numSetsFound_ = 0;

    public static final int DEFAULT_CASH = 100;


    SetPlayer(String name, Color color, boolean isHuman)
    {
        super(name, color, isHuman);
    }

    /**
     * Factory method for creating set players of the appropriate type.
     * @param name
     * @param color
     * @param isHuman
     * @return
     */
    public static SetPlayer createSetPlayer(String name, Color color, boolean isHuman)
    {
       if (isHuman)
           return new SetPlayer(name, color, true);
        else
           return SetRobotPlayer.getSequencedRobotPlayer(name, color);
    }

    /**
     * I see this as returning a set of 3 cards that the player has selected.
     * @return player action
     */
    @Override
    public PlayerAction getAction(MultiGameController controller) {
        assert false : "getAction not implemented yet";
        return null;
    }
    
    /**
     * @return player action
     */
    @Override
    public void setAction(PlayerAction action) {
        assert false : "setAction implemented yet";
    }
    
    /**
     *
     * @param i index of player
     * @return  the default name for player i
     */
    public String getDefaultName(int i)
    {
        Object[] args = {Integer.toString(i)};
        String dname = MessageFormat.format(GameContext.getLabel("SET_DEFAULT_NAME"), args );
        return dname;
    }


    public int getNumSetsFound() {
        return numSetsFound_;
    }

    public void setNumSetsFound(int numSetsFound) {
        this.numSetsFound_ = numSetsFound;
        if (numSetsFound_< 0) {
            numSetsFound_ = 0;
        }
    }

    public void incrementNumSetsFound() {
        numSetsFound_++;
    }

    public void decrementNumSetsFound() {
        // don;t go lower than 0.
        if (numSetsFound_> 0) {
            numSetsFound_--;
        }
    }

    /**
     * @param player
     * @return  true if the players name, isHuman and color attributes are the same as ours
     */
    public boolean equals(Object player) {
        SetPlayer p = (SetPlayer) player;
        return (p.getName().equals(getName()) && (p.isHuman() == isHuman()) && p.getColor().equals(getColor()));
    }

    /**
     * Players with the same name hash to the smae colation. Rare.
     * @return
     */
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String additionalInfo()
    {      
        return "Num Sets Found: "+numSetsFound_;
    }

}



