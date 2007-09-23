package com.becker.simulation.fluid;

import com.becker.common.*;

import java.awt.*;
import java.util.*;

/**
 *  Renders a specified liquid environment.
 *
 *  @author Barry Becker
 */
public final class EnvironmentRenderer
{
    // rendering attributes
    private static final Color GRID_COLOR = new Color( 30, 30, 30, 10 );
    private static final Color VECTOR_COLOR = new Color( 205, 90, 25, 40 );
    private static final Color WALL_COLOR = new Color( 100, 210, 170, 150 );
    private static final Color TEXT_COLOR = new Color( 10, 10, 170, 200 );
    //private static final float PRESSURE_COL_OPACITY = 0.01f;
    
    // scales the size of everything
    public static final double DEFAULT_SCALE = 8;
    private static final int OFFSET = 10;
    
    public static final boolean USE_LINEAR_INTERPOLATION = false;

    private static final double pressureVals_[] = {0.0, 0.1, 1.0, 4.0, 100.0};
    private static final Color pressureColors_[] = {
        new Color( 0, 0, 235, 90 ),
        new Color( 0, 200, 190, 150 ),
        new Color( 150, 255, 0, 190 ),
        new Color( 250, 190, 0, 220 ),
        new Color( 255, 0, 0, 255 )
    };
    private static final ColorMap pressureColorMap_ =
            new ColorMap( pressureVals_, pressureColors_ );

    // temp var used throughout for efficency. avoids creating objects
    private static double[] a_ = new double[2]; // temp point var
    private static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 12 );

    private double scale_ = DEFAULT_SCALE;
    private float wallLineWidth_;
    private int particleSize_;

    private boolean showVelocities_ = false;
    private boolean showPressures_ = false;


    public EnvironmentRenderer() {
       setScale(DEFAULT_SCALE);
    };

    public void setScale(double scale) {
        scale_ = DEFAULT_SCALE;
        wallLineWidth_ = (float) (scale / 5.0) + 1;
        particleSize_ = (int) (scale / 8.0) + 1;
    }

    public double getScale() {
        return scale_;
    }

    public void setShowVelocities(boolean show) {
        showVelocities_ = show;
    }

    public boolean getShowVelocities() {
        return showVelocities_;
    }

    public void setShowPressures(boolean show) {
        showPressures_ = show;
    }

    public boolean getShowPressures() {
        return showPressures_;
    }


    /**
     * Render the Environment on the screen.
     */
   public void render(FluidEnvironment env, Graphics2D g)
    {

        double time = System.currentTimeMillis();

        int i,j;
        int rightEdgePos = (int) (scale_ * env.getXDim());
        int bottomEdgePos = (int) (scale_ * env.getYDim());
        Grid grid = env.getGrid();

        // draw the cells colored by ---pressure--- val
        if (showPressures_)
            renderPressure(g, env);


        // draw the ---walls---
        Stroke oldStroke = g.getStroke();
        Stroke wallStroke = new BasicStroke( wallLineWidth_ );
        g.setStroke( wallStroke );

        g.setColor(WALL_COLOR);
        /*
        //Stroke stroke = new BasicStroke(wall.getThickness(), BasicStroke.CAP_BUTT,
        //                                BasicStroke.JOIN_ROUND, 10);
        for (i=0; i<walls_.size(); i++)  {
            Wall wall = (Wall)walls_.elementAt(i);
            //System.out.println("wall "+i+" = "+wall.getStartPoint().getX()+" "+wall.getStartPoint().getY());
            g.drawLine( (int)(wall.getStartPoint().getX()*rat+OFFSET),
                        (int)(wall.getStartPoint().getY()*rat+OFFSET),
                        (int)(wall.getStopPoint().getX()*rat+OFFSET),
                        (int)(wall.getStopPoint().getY()*rat+OFFSET) );
        }
        */

        // outer boundary
        g.drawRect( OFFSET, OFFSET, (int) (env.getXDim() * scale_), (int) (env.getYDim() * scale_) );

        // draw the ---velocity--- field (and status)
        if (showVelocities_)
            drawVectors(g, env);

        // draw the cells/grid_
        g.setStroke( oldStroke );
        g.setColor( GRID_COLOR );
        for ( j = 0; j < env.getYDim(); j++ )   //  -----
        {
            int ypos = (int) (j * scale_);
            g.drawLine( OFFSET, ypos + OFFSET, rightEdgePos + OFFSET, ypos + OFFSET );
        }
        for ( i = 0; i < env.getXDim(); i++ )    //  ||||
        {
            int xpos = (int) (i * scale_);
            g.drawLine( xpos + OFFSET, OFFSET, xpos + OFFSET, bottomEdgePos + OFFSET );
        }
    }


    private void renderPressure(Graphics2D g, FluidEnvironment env) {
        
        for (int j = 0; j < env.getYDim(); j++ ) {
            for (int i = 0; i < env.getXDim(); i++ ) {
                drawPressureRectangle(i, j, env, g);                
            }
        }
    }
    
    private void drawPressureRectangle(int i, int j, FluidEnvironment env, Graphics2D g) {
        Grid grid = env.getGrid();
        
         int xStart =  (int) ((scale_ * i) + OFFSET);
        int yStart =  (int) ((scale_ * j) + OFFSET);
        int scale = (int) scale_;
        
        if (USE_LINEAR_INTERPOLATION) {
       
            float[] colorLL = new float[4];
            float[] colorLR = new float[4];
            float[] colorUL = new float[4];
            float[] colorUR = new float[4]; 
            pressureColorMap_.getColorForValue( grid.getDensity(i, j)).getComponents(colorLL);
            pressureColorMap_.getColorForValue( grid.getDensity(i+1, j)).getComponents(colorLR);
            pressureColorMap_.getColorForValue( grid.getDensity(i, j+1)).getComponents(colorUL);
            pressureColorMap_.getColorForValue( grid.getDensity(i+1, j+1)).getComponents(colorUR);             

            for (int x =0; x < scale; x++) {
                  for (int y =0; y < scale; y++) {
                     double xrat = (double) x / scale_;
                     double yrat = (double) y / scale_;
                     Color c =  ImageUtil.interpolate(xrat, yrat, colorLL, colorLR, colorUL, colorUR);
                     g.setColor(c);
                     //g.drawLine(xStart + x, yStart + y, xStart + x, yStart + y);  
                     g.fillRect(xStart + x, yStart + y, 1, 1);     
                 }
            }  

        } else {
            g.setColor( pressureColorMap_.getColorForValue( grid.getDensity(i, j)));
            g.fillRect(xStart, yStart, scale, scale);
        }
    }


    private void drawVectors(Graphics2D g, FluidEnvironment env) {
        g.setColor( VECTOR_COLOR );
        Grid grid = env.getGrid();
        for ( int j = 0; j < env.getYDim(); j++ ) {
            for ( int i = 0; i < env.getXDim(); i++ ) {
                double u = grid.getU(i, j);
                double v = grid.getV(i, j);
                int x = (int) (scale_ * i) + OFFSET;
                int y = (int) (scale_ * j) + OFFSET;
                g.drawLine( (int) (scale_ * (i + 0.5)) + OFFSET, y,
                        (int) (scale_ * (i + 0.5)) + OFFSET, (int) (scale_ * j + 8.0 * v) + OFFSET );
                g.drawLine( x, (int) (scale_ * (j + 0.5)) + OFFSET,
                        (int) (scale_ * i + 8.0 * u) + OFFSET, (int) (scale_ * (j + 0.5)) + OFFSET );
            }
        }
    }
}
