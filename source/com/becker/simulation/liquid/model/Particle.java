package com.becker.simulation.liquid.model;

import javax.vecmath.Point2d;

/**
 *  the Particle is the base entity in the liquid simulation.
 *  Ordinarily we would have setter and getter methods for all the data variables
 *  but since this is the lowest level primitive, so performance is tantamount.
 *
 *  @author Barry Becker
 */
public class Particle extends Point2d
{

    /** the cell that the particle belongs to */
    private Cell cell_;

    /**
     * the velocity vector of the particle in mm/s
     *public Vector2d velocity;
     * the mass of the particle in grams.
     */
    private double age_;

    /** radius of influence of this particle */
    //private double radius_;

    /**
     * we may want to mix different liquids and uyse this for coloration
     * what about mixing oil and water?
     */
    //private int materialType_;


    /**
     * Construct the particle
     *  assumes that the initial velocity is 0.
     */
    public Particle( double x, double y, Cell cell )
    {
        super( x, y );
        cell_ = cell;
        age_ = 0.0;
    }

    public void setCell( Cell cell )
    {
        cell_ = cell;
    }

    public Cell getCell()
    {
        return cell_;
    }

    /**
     * increment the age of the particle
     */
    public void incAge( double timeStep )
    {
        age_ += timeStep;
    }

    public double getAge()
    {
        return age_;
    }

}
