package com.becker.simulation.fluid.rendering;

import com.becker.common.ColorMap;
import com.becker.common.util.ImageUtil;
import com.becker.simulation.fluid.model.FluidEnvironment;
import com.becker.simulation.fluid.model.Grid;

import java.awt.*;

/**
 *  Renders a specified liquid environment.
 *
 *  @author Barry Becker
 */
public final class EnvironmentRenderer {
    // rendering attributes
    private static final Color GRID_COLOR = new Color( 30, 30, 30, 10 );
    private static final Color VECTOR_COLOR = new Color( 205, 90, 25, 40 );
    private static final Color WALL_COLOR = new Color( 100, 210, 170, 150 );
    private static final Color TEXT_COLOR = new Color( 10, 10, 170, 200 );
    
    /** scales the size of everything   */
    public static final double DEFAULT_SCALE = 4;
    private static final double  VECTOR_SCALE = 10.0;
    private static final int OFFSET = 10;
    
    public static final boolean USE_LINEAR_INTERPOLATION = false;

    private static final PressureColorMap PRESSURE_COLOR_MAP = new PressureColorMap();

    private static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 12 );

    private double scale_ = DEFAULT_SCALE;

    private boolean showVelocities_ = false;
    private boolean showPressures_ = true;


    public EnvironmentRenderer() {
       setScale(DEFAULT_SCALE);
    }
    
    public ColorMap getColorMap() {
        return PRESSURE_COLOR_MAP;
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
    public void render(FluidEnvironment env, Graphics2D g) {

        // draw the cells colored by ---pressure--- val
        if (showPressures_) {
            renderPressure(g, env);
        }

        // outer boundary
        g.drawRect( OFFSET, OFFSET, (int) (env.getXDim() * scale_), (int) (env.getYDim() * scale_) );

        // draw the ---velocity--- field (and status)
        if (showVelocities_) {
            drawVectors(g, env);
        }

        drawGrid(env, g);
    }

    private void drawGrid(FluidEnvironment env, Graphics2D g)    {
        g.setColor( GRID_COLOR );

        int rightEdgePos = (int) (scale_ * env.getXDim());
        int bottomEdgePos = (int) (scale_ * env.getYDim());

        for (int j = 0; j < env.getYDim(); j++ )   //  -----
        {
            int ypos = (int) (j * scale_);
            g.drawLine( OFFSET, ypos + OFFSET, rightEdgePos + OFFSET, ypos + OFFSET );
        }
        for (int i = 0; i < env.getXDim(); i++ )    //  ||||
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
            PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i, j)).getComponents(colorLL);
            PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i+1, j)).getComponents(colorLR);
            PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i, j+1)).getComponents(colorUL);
            PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i+1, j+1)).getComponents(colorUR);

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
            g.setColor( PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i, j)));
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
                        (int) (scale_ * (i + 0.5)) + OFFSET, (int) (scale_ * j + VECTOR_SCALE * v) + OFFSET );
                g.drawLine( x, (int) (scale_ * (j + 0.5)) + OFFSET,
                        (int) (scale_ * i + VECTOR_SCALE  * u) + OFFSET, (int) (scale_ * (j + 0.5)) + OFFSET );
            }
        }
    }
}
