package com.becker.game.multiplayer.common;

import com.becker.game.common.*;
import com.becker.game.multiplayer.trivial.player.*;
import com.becker.common.*;

import java.awt.*;

/**
 * Represents a trivial player in the viewer.
 * For the player we draw their value.
 *
 * @author Barry Becker
 */
public class MultiPlayerMarker extends GamePiece
{

    private static final long serialVersionUID = 1;

    protected MultiGamePlayer owner_;
    protected Location location_;
    protected boolean highlighted_;


    public MultiPlayerMarker(MultiGamePlayer owner)
    {
        setAnnotation(""+owner.getName());
        owner_ = owner;
    }

    public MultiPlayerMarker( MultiGamePlayer owner, Location pos)
    {
        this(owner);    
        location_ = pos;
        assert(pos!=null);
    }


    public MultiGamePlayer getOwner()
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
      * get the textual representation of thel marker.
      * @return string form
      */
     public String toString()
     {
         return toString("\n");
     }

     /**
      * get the html representation of the marker.
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


