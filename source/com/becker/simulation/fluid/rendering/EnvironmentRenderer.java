package com.becker.simulation.fluid.rendering;

import com.becker.common.ColorMap;
import com.becker.common.util.ImageUtil;
import com.becker.simulation.common.ColorRect;
import com.becker.simulation.common.ModelImage;
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
    private static final Color VECTOR_COLOR = new Color( 200, 60, 30, 50 );
    

    private static final double  VECTOR_SCALE = 30.0;
    private static final int OFFSET = 10;

    private static final PressureColorMap PRESSURE_COLOR_MAP = new PressureColorMap();

    private RenderingOptions options;
    Grid grid;
    private ModelImage modelImage;

    public EnvironmentRenderer(Grid grid, RenderingOptions options) {

        this.grid = grid;
        this.options = options;
        modelImage = new ModelImage(grid, PRESSURE_COLOR_MAP, (int)options.getScale());
    }
    
    public ColorMap getColorMap() {
        return PRESSURE_COLOR_MAP;
    }

    public RenderingOptions getOptions() {
        return options;
    }

    /**
     * Render the Environment on the screen.
     */
    public void render(Graphics2D g) {

        // draw the cells colored by ---pressure--- val
        if (options.getShowPressures()) {
            modelImage.setUseLinearInterpolation(options.getUseLinearInterpolation());
            modelImage.updateImage();
            //renderPressure(g);
            g.drawImage(modelImage.getImage(), OFFSET, OFFSET, null);
        }

        // outer boundary
        double scale = options.getScale();
        g.drawRect( OFFSET, OFFSET, (int) (grid.getWidth() * scale), (int) (grid.getHeight() * scale) );

        // draw the ---velocity--- field (and status)
        if (options.getShowVelocities()) {
            drawVectors(g);
        }

        if (options.getShowGrid())  {
            drawGrid(g);
        }
    }

    private void drawGrid(Graphics2D g)    {
        g.setColor( GRID_COLOR );
        double scale = options.getScale();

        int rightEdgePos = (int) (scale * grid.getWidth());
        int bottomEdgePos = (int) (scale * grid.getHeight());

        for (int j = 0; j < grid.getHeight(); j++ )   //  -----
        {
            int ypos = (int) (j * scale);
            g.drawLine( OFFSET, ypos + OFFSET, rightEdgePos + OFFSET, ypos + OFFSET );
        }
        for (int i = 0; i < grid.getWidth(); i++ )    //  ||||
        {
            int xpos = (int) (i * scale);
            g.drawLine( xpos + OFFSET, OFFSET, xpos + OFFSET, bottomEdgePos + OFFSET );
        }
    }

    /**
     * This optionally renders to an offscreen image for faster performance.
     *
    private void renderPressure(Graphics2D g) {

        for (int j = 0; j < grid.getHeight(); j++ ) {
            for (int i = 0; i < grid.getWidth(); i++ ) {
                drawPressureRectangle(i, j, g);
            }
        }
    } */


    /**
     * Determine the colors for a rectangular strip of pixels.
     * @return array of colors that will be used to define an image for quick rendering.
     *
    public ColorRect getColorRect(int minX, int maxX) {
        int ymax = grid.getHeight();
        int scale = (int)options.getScale();

        ColorRect colorRect = new ColorRect(maxX-minX, ymax);
        for (int i = minX; i < maxX; i++) {
            for (int j = 0; j < ymax; j++) {

                int xStart = scale * i;
                int yStart = scale * j;

                if (options.getUseLinearInterpolation()) {


                    float[] colorLL = new float[4];
                    float[] colorLR = new float[4];
                    float[] colorUL = new float[4];
                    float[] colorUR = new float[4];
                    PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i, j)).getComponents(colorLL);
                    PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i+1, j)).getComponents(colorLR);
                    PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i, j+1)).getComponents(colorUL);
                    PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i+1, j+1)).getComponents(colorUR);

                    for (int xx =0; xx < scale; xx++) {
                          for (int yy =0; yy < scale; yy++) {
                             double xrat = (double) xx / scale;
                             double yrat = (double) yy / scale;
                             Color c = ImageUtil.interpolate(xrat, yrat, colorLL, colorLR, colorUL, colorUR);

                             colorRect.setColor(xStart + xx, yStart + yy, c);
                         }
                    }
                }
                else {
                    colorRect.setColorRect(xStart-minX, yStart, scale, scale,
                                           PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i, j)));
                }

            }
        }
        return colorRect;
    } */

    /*
    public void renderPressureStrip(int minX, ColorRect colorRect, Graphics2D g2) {
        int scale = (int)options.getScale();
        Image img = colorRect.getAsImage();
        g2.drawImage(img, scale * minX + OFFSET, OFFSET, null);
    } */

    /*
    private void drawPressureRectangle(int i, int j, Graphics2D g) {

        double scale = options.getScale();

        int xStart =  (int) ((scale * i) + OFFSET);
        int yStart =  (int) ((scale * j) + OFFSET);
        
        // linear interpolation turns out to be too slow on java 2d (or at least my impl of it)
        if (options.getUseLinearInterpolation()) {
       
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
                     double xrat = (double) x / scale;
                     double yrat = (double) y / scale;
                     Color c = ImageUtil.interpolate(xrat, yrat, colorLL, colorLR, colorUL, colorUR);
                     g.setColor(c);
                     //g.drawLine(xStart + x, yStart + y, xStart + x, yStart + y);  
                     g.fillRect(xStart + x, yStart + y, 1, 1);     
                 }
            }  

        } else {
            g.setColor( PRESSURE_COLOR_MAP.getColorForValue( grid.getDensity(i, j)));
            g.fillRect(xStart, yStart, (int)scale, (int)scale);
        }
    } */


    private void drawVectors(Graphics2D g) {
        g.setColor( VECTOR_COLOR );
        double scale = options.getScale();

        for ( int j = 0; j < grid.getHeight(); j++ ) {
            for ( int i = 0; i < grid.getWidth(); i++ ) {
                double u = grid.getU(i, j);
                double v = grid.getV(i, j);
                int x = (int) (scale * i) + OFFSET;
                int y = (int) (scale * j) + OFFSET;

                g.drawLine( x, y,
                        (int) (scale * i + VECTOR_SCALE  * u) + OFFSET, (int) (scale * j + VECTOR_SCALE * v) + OFFSET );

                /*
                g.drawLine( (int) (scale * (i + 0.5)) + OFFSET, y,
                        (int) (scale * (i + 0.5)) + OFFSET, (int) (scale * j + VECTOR_SCALE * v) + OFFSET );
                g.drawLine( x, (int) (scale * (j + 0.5)) + OFFSET,
                        (int) (scale * i + VECTOR_SCALE  * u) + OFFSET, (int) (scale * (j + 0.5)) + OFFSET );
                */
            }
        }
    }
}
