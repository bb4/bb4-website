package com.becker.simulation.common;

import javax.vecmath.*;
import java.awt.*;

/**
 * Use this for Newtonian physics type simulations.
 *
 * @author Barry Becker Date: Sep 17, 2006
 */
public abstract class NewtonianSimulator extends Simulator {

    /**
     * @param name the name fo the simulator (eg Snake, Liquid, or Trebuchet)
     */
    public NewtonianSimulator(String name) {
        super(name);
    }

    public abstract void setShowVelocityVectors( boolean show );
    public abstract boolean getShowVelocityVectors();

    public abstract void setShowForceVectors( boolean show );
    public abstract boolean getShowForceVectors();

    public abstract void setDrawMesh( boolean use );
    public abstract boolean getDrawMesh();

    public abstract void setStaticFriction( double staticFriction );
    public abstract double getStaticFriction();

    public abstract void setDynamicFriction( double dynamicFriction );
    public abstract double getDynamicFriction();


    public static void drawGridBackground(Graphics2D g2, Color gridColor, double cellSize,
                                          int xDim, int yDim, Vector2d offset) {
        // draw the grid background
        g2.setColor( gridColor );
        int xMax = (int) (cellSize * xDim) - 1;
        int yMax = (int) (cellSize * yDim) - 1;
        int j;
        double pos = offset.y % cellSize;
        for ( j = 0; j <= yDim; j++ ) {
            int ht = (int) (pos + j * cellSize);
            g2.drawLine( 1, ht, xMax, ht );
        }
        pos = offset.x % cellSize;
        for ( j = 0; j <= xDim; j++ ) {
            int w = (int) (pos + j * cellSize);
            g2.drawLine( w, 1, w, yMax );
        }
    }

}
