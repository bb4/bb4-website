package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;
import com.becker.common.*;

import java.awt.*;

/**
 * Represents a Poker player in the viewer.
 * For the player we draw their picture or icon, their chips (or cash), various annotations and their cards.
 *
 * @see com.becker.game.multiplayer.poker.PokerTable
 * @author Barry Becker
 */
public class PokerPlayerMarker extends GamePiece
{

    private static final long serialVersionUID = 1;

    private PokerPlayer owner_;
    private Location location_;
    private boolean highlighted_;


    public PokerPlayerMarker(PokerPlayer owner)
    {
        setAnnotation(""+owner.getName());
        owner_ = owner;
    }

    public PokerPlayerMarker( PokerPlayer owner, Location pos)
    {
        setAnnotation(""+owner.getName());
        owner_ = owner;
        location_ = pos;
        assert(pos!=null);
    }


    public PokerPlayer getOwner()
    {
        return owner_;
    }


    public boolean isHighlighted() {
        return highlighted_;
    }

    public void setHighlighted(boolean highlighted) {
        highlighted_ = highlighted;
    }


    public Color getColor()
    {
        return getOwner().getColor();

    }


    public Location getLocation()
    {
        return location_;
    }

    /**
     * ordinarily this does not change
     */
    public void setLocation(Location loc)
    {
        location_ = loc;
    }

    /**
     * @return more detail that toString()
     */
    public String getDescription()
    {
        StringBuffer sb = new StringBuffer( this.toString() );

        sb.append("Player: "+owner_.getName());
        return sb.toString();
    }


    /**
      * get the textual representation of the poker marker.
      * @return string form
      */
     public String toString()
     {
         return getDescription();
     }

     /**
      * get the html representation of the group.
      * @return html form
      */
     public String toHtml()
     {
         return toString( "<br>" );
     }


    public String toString(String newLine)
    {
        StringBuffer sb = new StringBuffer("");

        if (getOwner()!=null)
            sb.append("Owner: "+this.getOwner().getName()+newLine);
        return sb.toString();
    }

}


