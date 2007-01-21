package com.becker.game.multiplayer.galactic;

import com.becker.game.common.*;
import com.becker.game.multiplayer.galactic.player.*;
import com.becker.common.*;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 *  A Planet object describes the physical marker for a planet at a location on the Galaxy.
 *  Implements comparable so lists of planets can be sorted.
 *
 * @see Galaxy
 * @author Barry Becker
 */
public class Planet extends GamePiece
{

    private GalacticPlayer owner_;
    private int numShips_;
    private int productionCapacity_;
    // the planets never move
    private Location location_;
    private boolean underAttack_;
    private boolean highlighted_;

    public static final char OCCUPIED_PLANET= 'O';
    public static final char UNOCCUPIED_PLANET= 'U';

    public static final Color NEUTRAL_COLOR = Color.LIGHT_GRAY;



    public Planet( char name, int initialNumShips, int productionCapacity, Location pos)
    {
        type_ = name;
        setAnnotation(""+name);
        numShips_ = initialNumShips;
        productionCapacity_ = productionCapacity;
        location_ = pos;
        assert(pos!=null);
    }


    public char getName()
    {
        return type_;
    }

    public GalacticPlayer getOwner()
    {
        return owner_;
    }

    public void setOwner( GalacticPlayer owner )
    {
        owner_ = owner;
    }

    public int getNumShips()
    {
        return numShips_;
    }

    /**
     * prefer using deductShips.
     * @param numShips
     */
    public void setNumShips( int numShips )
    {
        numShips_ = numShips;
    }

    public void setUnderAttack(boolean underAttack) {
        underAttack_ = underAttack;
    }

    public boolean isUnderAttack() {
        return underAttack_;
    }

    public boolean isHighlighted() {
        return highlighted_;
    }

    public void setHighlighted(boolean highlighted) {
        highlighted_ = highlighted;
    }

    /**
     * @param numShips to deduct from those currently at the planet.
     */
    public void deductShips( int numShips)
    {
        // for propper bookkeeping, we need to subtract the fleetsize from the planet of origin.
        // do this for new orders only. It was already done for old orders.
        assert (numShips_ >= numShips) :
                    "Trying to send "+numShips+" when you have only "+numShips_;
        //System.out.println( "subracting "+numShips+ " from "+numShips_ );
        setNumShips(numShips_ - numShips);
    }

    public int getProductionCapacity()
    {
        return productionCapacity_;
    }

    public void setProductionCapacity( int productionCapacity )
    {
        productionCapacity_= productionCapacity;
    }

    public Color getColor()
    {
        // if we have an owner, then use his color, otherwise use the default.
        if (getOwner()!=null)  {
            return getOwner().getColor();
        }
        else {
            return NEUTRAL_COLOR;
        }
    }

    public void incrementYear()
    {
        if (this.getOwner()!=null) { // or if neutrals build
            numShips_ += productionCapacity_;
        }
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

        sb.append("Planet: "+type_+" production:"+getProductionCapacity());
        return sb.toString();
    }


    public double getDistanceFrom(Planet p)
    {
        return getDistanceFrom(p.getLocation());
    }

    private double getDistanceFrom(Location loc)
    {
        return location_.getDistanceFrom(loc);
    }

    public double getDistanceFrom(Point2D loc)
    {
        return location_.getDistanceFrom(loc);
    }

    /**
     * if the production capacity is proportional to the volume, then the radius
     * should be proportional to the cube root of the production capacity.
     * Normallize by some average production.
     * @return planet radius.
     */
    public double getRadius() {
        return 0.85 * Math.pow(getProductionCapacity(), 0.333)/2.1;
    }

    /**
      * get the textual representation of the group.
      * @return string form
      */
     public String toString()
     {
         return toString( "\n" );
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
        StringBuffer sb = new StringBuffer("Planet: "+type_+newLine);

        if (getOwner()!=null)
            sb.append("Owner: "+this.getOwner().getName()+newLine);
        sb.append("production:"+getProductionCapacity()+newLine);
        sb.append("Num ships: "+getNumShips());
        return sb.toString();
    }

}



