package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;
import com.becker.ui.GUIUtil;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.text.MessageFormat;

/**
 * Represents a Player in a poker game
 *
 * @author Barry Becker
 */
public abstract class PokerPlayer extends Player
{
    // this player's home planet. (like earth is for humans)
    private PokerHand hand_;
    private PokerPlayerMarker piece_;

    // in dollars
    private int cash_;
    private boolean hasFolded_;
    // the maount that this player has contributed to the pot
    private int contribution_;

    public enum Action { FOLD, CALL, RAISE };

    public static final int DEFAULT_CASH = 100;

    private static final float SATURATION = .8f;
    private static final float BRIGHTNESS = .999f;

    protected PokerPlayer(String name,  int money, Color color, boolean isHuman)
    {
        super(name, color, isHuman);
        cash_ = money;
        contribution_ = 0;
        hasFolded_ = false;
        piece_ = new PokerPlayerMarker(this);
    }

    /**
     * Factory method for creating poker players of the appropriate type.
     * @param name
     * @param color
     * @param isHuman
     * @return
     */
    public static PokerPlayer createPokerPlayer(String name, int money, Color color, boolean isHuman)
    {
       if (isHuman)
           return new PokerHumanPlayer(name, money, color);
        else
           return PokerRobotPlayer.getSequencedRobotPlayer(name, money, color);
    }

    /**
     *
     * @param i index of player
     * @return  the default name for player i
     */
    public String getDefaultName(int i)
    {
        Object[] args = {Integer.toString(i)};
        String dname = MessageFormat.format(GameContext.getLabel("POKER_DEFAULT_NAME"), args );
        return dname;
    }

    public PokerHand getHand()
    {
        return hand_;
    }

    public void setHand( PokerHand hand )
    {
        this.hand_ = hand;
    }

    public int getCash()
    {
        return cash_;
    }

    public void setFold(boolean folded) {
        hasFolded_ = folded;
    }
    
    public boolean hasFolded() {
        return hasFolded_;
    }

    public PokerPlayerMarker getPiece() {
        return piece_;
    }

    public void setPiece(PokerPlayerMarker piece) {
        piece_ = piece;
    }


    /**
     * have this player contribute some amount to the pot
     * of course the amount must be less than they have altogether.
     * @param amount
     */
    public void contributeToPot(PokerController controller, int amount) {
        assert(amount <= cash_) : "You cannot add more to the pot than you have.";

        if (amount >0) {
            cash_ -= amount;
            contribution_ += amount;
            controller.addToPot(amount);
        }
    }

    /**
     * @return  the amount this player has currently put in the pot this round.
     */
    public int getContribution() {
        return contribution_;
    }

    /**
     * the pot goes to this player
     */
    public void claimPot(PokerController controller)  {
        cash_ += controller.getPotValue();
        controller.setPotValue(0);
    }

    /**
     * try to give a unique color based on the name
     * and knowing what the current player colors are.
     */
    public static final Color getNewPlayerColor(PokerPlayer[] players)
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
    private static boolean isHueUniqueEnough(float hue, PokerPlayer[] players)
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


    public String toString()
    {
        StringBuffer sb = new StringBuffer( super.toString() );
        sb.append("Hand: "+getHand());
        sb.append("Money: "+cash_);
        return sb.toString();
    }

}



