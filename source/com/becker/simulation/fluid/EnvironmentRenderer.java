package com.becker.simulation.fluid;

import com.becker.common.ColorMap;
import com.becker.common.util.ImageUtil;

import java.awt.*;

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
    public static final double DEFAULT_SCALE = 4;
    private static final int OFFSET = 10;
    
    public static final boolean USE_LINEAR_INTERPOLATION = false;

    private static final double pressureVals_[] = {0.0, 0.00001, 0.0001, 0.001, 0.005, 0.01, 0.02, 0.05, .1, 0.5, 2.0};
    private static final Color pressureColors_[] = {
        new Color( 110, 110, 140, 20 ),
        new Color( 205, 10, 255, 55 ),        
        new Color( 50, 50, 255, 80 ),
        new Color( 0, 0, 255, 100 ),
        new Color( 0, 200, 190, 130 ),
        new Color( 0, 255, 0, 140 ),
        new Color( 150, 255, 0, 190 ),
        new Color( 250, 230, 0, 230 ),        
        new Color( 255, 100, 0, 230 ),
        new Color( 205, 1, 255, 240 ),
        new Color( 250, 0, 0, 255 )
    };
    private static final ColorMap pressureColorMap_ =
            new ColorMap( pressureVals_, pressureColors_ );

    // temp var used throughout for efficency. avoids creating objects
    private static double[] a_ = new double[2]; // temp point var
    private static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 12 );

    private double scale_ = DEFAULT_SCALE;

    private boolean showVelocities_ = false;
    private boolean showPressures_ = true;


    public EnvironmentRenderer() {
       setScale(DEFAULT_SCALE);
    };
    
    public ColorMap getColorMap() {
        return pressureColorMap_;
    }

    public void setScale(double scale) {
        scale_ = DEFAULT_SCALE;
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


        // outer boundary
        g.drawRect( OFFSET, OFFSET, (int) (env.getXDim() * scale_), (int) (env.getYDim() * scale_) );

        // draw the ---velocity--- field (and status)
        if (showVelocities_)
            drawVectors(g, env);

        // draw the cells/grid_
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
        
        // linear interpolation turns out to be too slow on java 2d (or at least my impl of it)
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

    private static final double  VECTOR_SCALE = 10.0;

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
                        (int) (scale_ * (i + 0.5)) + OFFSET, (int) (scale_ * j + VECTOR_SCALE * v) + OFFSET );
                g.drawLine( x, (int) (scale_ * (j + 0.5)) + OFFSET,
                        (int) (scale_ * i + VECTOR_SCALE  * u) + OFFSET, (int) (scale_ * (j + 0.5)) + OFFSET );
            }
        }
    }
}
