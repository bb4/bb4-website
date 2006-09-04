package com.becker.game.multiplayer.galactic;

import com.becker.common.*;

import javax.vecmath.*;
import java.awt.geom.*;



/**
 * An order describes how a fleet of ships should be deployed.
 * ie where coming from, where going to, and how many ships.
 *
 * @author Barry Becker
 */
public class Order
{

    private Planet origin_;
    private Planet destination_;
    private int fleetSize_;
    private Point2D currentLocation_;
    private GalacticPlayer owner_;
    private boolean hasArrived_ = false;

    private static final int NORMAL_SPEED = 2;
    private static final double INTERSECT_TOLERANCE = 0.2;

    public Order(Planet origin, Planet target, int fleetSize, Point2D loc)
    {
        commonInit(origin, target, fleetSize);
        currentLocation_ = loc;
    }

    public Order(Planet origin, Planet target, int fleetSize)
    {
       commonInit(origin, target, fleetSize);
       currentLocation_ = new Point2D.Double(origin.getLocation().getCol(), origin.getLocation().getRow());
    }

    private void commonInit(Planet origin, Planet target, int fleetSize)
    {
        origin_ = origin;
        assert(origin!=null);
        destination_ = target;
        fleetSize_ = fleetSize;

        // we need to store who the current owner is, because the owner of the planet of origin may change.
        owner_ = origin.getOwner();
        assert(owner_!=null);
    }


    public Planet getOrigin()
    {
        return origin_;
    }

    public void setOrigin( Planet origin)
    {
        this.origin_ = origin;
    }

    public Planet getDestination()
    {
        return destination_;
    }

    public void setDestination( Planet destination )
    {
        this.destination_ = destination;
    }

    public int getFleetSize()
    {
        return fleetSize_;
    }

    public void setFleetSize( int fleetSize )
    {
        this.fleetSize_ = fleetSize;
    }

    /**
     * @return the player who issued this order
     */
    public GalacticPlayer getOwner()
    {
        assert(owner_!=null);
        return owner_;
    }

    public double getDistanceRemaining()
    {
        return destination_.getDistanceFrom( getCurrentLocation() );
    }

    public double getTimeRemaining()
    {
        return getDistanceRemaining() / NORMAL_SPEED;
    }

    public boolean hasArrived()
    {
        return hasArrived_;
    }

    /**
     * adjust the current location so it represents the position of the fleet a year later.
     */
    public void incrementYear()
    {
        Point2D oldLocation = new Point2D.Double(currentLocation_.getX(), currentLocation_.getY());

        Vector2d v = getUnitDirection();
        v.scale(NORMAL_SPEED);

        currentLocation_.setLocation( currentLocation_.getX() + v.x,  currentLocation_.getY() +v.y);

        // if the destination planet lies on the line from where we were to where we are now,
        // then we overshot. set the currentLocation to the dest planet location.
        Line2D.Double line = new Line2D.Double(oldLocation, currentLocation_);
        Location dLoc = destination_.getLocation();
        if (line.intersects(dLoc.getCol(),  dLoc.getRow(), INTERSECT_TOLERANCE, INTERSECT_TOLERANCE)) {
            currentLocation_.setLocation(dLoc.getCol(), dLoc.getRow());
            // the order never leaves once it has arrived. The order will be destroyed.
            hasArrived_ = true;
        }
    }

    public int getTimeNeeded() {
        return (int)(getDistanceRemaining() / NORMAL_SPEED + 1);
    }

    /**
     * @return a unit vector pointing in the current direction of movement.
     */
    private Vector2d getUnitDirection()
    {
        Location dLoc = destination_.getLocation();
        Vector2d unitVec = new Vector2d(dLoc.getCol() - currentLocation_.getX(), dLoc.getRow() - currentLocation_.getY());
        unitVec.normalize();
        return unitVec;
    }

    public Point2D getCurrentLocation() {
        return currentLocation_;
    }


    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Target: "+destination_+'\n');
        sb.append("Fleet size: "+fleetSize_+'\n');
        sb.append("Location: "+currentLocation_+'\n');
        sb.append("Owner: "+owner_+'\n');
        return sb.toString();
    }
}



