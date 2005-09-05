package com.becker.liquid;

import com.becker.common.ColorMap;

import java.awt.*;
import java.util.Iterator;

/**
 *  Renders a specified liquid environment
 *  @author Barry Becker
 */
public final class EnvironmentRenderer
{

    // rendering attributes
    private static final Color GRID_COLOR = new Color( 20, 20, 20, 15 );
    private static final Color PARTICLE_COLOR = new Color( 120, 0, 30, 80 );
    private static final Color VECTOR_COLOR = new Color( 205, 90, 25, 40 );
    //private static final Color WALL_COLOR = new Color( 100, 210, 170, 150 );
    private static final Color TEXT_COLOR = new Color( 10, 10, 170, 200 );
    //private static final float PRESSURE_COL_OPACITY = 0.01f;
    private static final double RENDER_RAT = 20;
    private static final int WALL_LINE_WIDTH = (int) (RENDER_RAT / 5.0) + 1;
    private static final int OFFSET = 10;
    private static final int PARTICLE_SIZE = (int) (RENDER_RAT / 8.0) + 1;
    private static final double pressureVals_[] = {-100.0, -5.0, 0.0, 5.0, 100.0};
    private static final Color pressureColors_[] = {
        new Color( 0, 0, 255, 20 ), new Color( 70, 120, 240, 20 ), new Color( 250, 250, 250, 20 ), new Color( 240, 120, 57, 20 ), new Color( 255, 0, 0, 20 )
    };
    private static final ColorMap pressureColorMap_ =
            new ColorMap( pressureVals_, pressureColors_ );

    // temp var used throughout for efficency. avoids creating objects
    private static double[] a_ = new double[2]; // temp point var

   private static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 12 );

   private EnvironmentRenderer() {};

    /**
     * Render the Environment on the screen.
     */
   public static void render(LiquidEnvironment env, Graphics2D g)
    {
        double rat = RENDER_RAT;
        //double ratD2 = rat/2.0;
        //double scale = rat / cellSize_;
        //double scaleD2 = scale/2.0;
        double time = System.currentTimeMillis();

        int i,j;
        int rightEdgePos = (int) (rat * env.getXDim());
        int bottomEdgePos = (int) (rat * env.getYDim());
        Cell[][] grid = env.getGrid();

        // draw the cells colored by ---pressure--- val
        for ( j = 0; j < env.getYDim(); j++ ) {
            for ( i = 0; i < env.getXDim(); i++ ) {
                g.setColor( pressureColorMap_.getColorForValue( grid[i][j].getPressure() ) );
                g.fillRect( (int) (rat * (i)) + OFFSET, (int) (rat * (j)) + OFFSET, (int) (rat), (int) (rat) );
            }
        }

        // draw the ---walls---
        Stroke oldStroke = g.getStroke();
        Stroke wallStroke = new BasicStroke( WALL_LINE_WIDTH );
        g.setStroke( wallStroke );
        /*
        g.setColor(WALL_COLOR);
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
        g.drawRect( OFFSET, OFFSET, (int) (env.getXDim() * rat), (int) (env.getYDim() * rat) );

        drawParticles(env, rat, g);


        // draw text representing internal state for debug purposes.
        if ( LiquidEnvironment.LOG_LEVEL >= 2 ) {
            g.setColor( TEXT_COLOR );
            g.setFont( BASE_FONT );
            StringBuffer strBuf = new StringBuffer( "12" );
            for ( j = 0; j < env.getYDim(); j++ ) {
                for ( i = 0; i < env.getXDim(); i++ ) {
                    int x = (int) (rat * i) + OFFSET;
                    int y = (int) (rat * j) + OFFSET;
                    strBuf.setCharAt( 0, grid[i][j].getStatus() );
                    strBuf.setLength( 1 );
                    int nump = grid[i][j].getNumParticles();
                    if ( nump > 0 )
                        strBuf.append( String.valueOf( nump ) );
                    g.drawString( strBuf.toString(), x + 6, y + 18 );
                }
            }
        }

        // draw the ---velocity--- field (and status)
        g.setColor( VECTOR_COLOR );
        drawVectors(env, g, rat );

        // draw the cells/grid_
        g.setStroke( oldStroke );
        g.setColor( GRID_COLOR );
        for ( j = 0; j < env.getYDim(); j++ )   //  -----
        {
            int ypos = (int) (j * rat);
            g.drawLine( OFFSET, ypos + OFFSET, rightEdgePos + OFFSET, ypos + OFFSET );
        }
        for ( i = 0; i < env.getXDim(); i++ )    //  ||||
        {
            int xpos = (int) (i * rat);
            g.drawLine( xpos + OFFSET, OFFSET, xpos + OFFSET, bottomEdgePos + OFFSET );
        }

        double duration = (System.currentTimeMillis() - time) / 100.0;
        env.log( 1, "time to render:  (" + duration + ") " );
    }


    private static void drawParticles(LiquidEnvironment env, double rat, Graphics2D g) {
        g.setColor( PARTICLE_COLOR );
        // draw the ---particles--- of liquid
        Iterator it = env.getParticles().iterator();
        while ( it.hasNext() ) {
            Particle p = (Particle) it.next();
            p.get( a_ );
            //Cell c = p.getCell();
            //int[] pos = c.getPos();
            //if (pos[0] == 2  &&  pos[1] == 2)
            int comp = (int) (256.0 * p.getAge() / 10.0);
            comp = (comp > 255) ? 255 : comp;
            g.setColor( new Color( comp, 100, 255 - comp, 60 ) );
            //System.out.println("pos = "+a_[0]+", "+a_[0]);
            g.fillOval( (int) (rat * a_[0] + OFFSET), (int) (rat * a_[1] + OFFSET), PARTICLE_SIZE, PARTICLE_SIZE );
            g.setColor( PARTICLE_COLOR );
        }
    }

    private static void drawVectors(LiquidEnvironment env, Graphics2D g, double rat )
    {
        Cell[][] grid = env.getGrid();
        for ( int j = 0; j < env.getYDim(); j++ ) {
            for ( int i = 0; i < env.getXDim(); i++ ) {
                double u = grid[i][j].getUip();
                double v = grid[i][j].getVjp();
                int x = (int) (rat * i) + OFFSET;
                int y = (int) (rat * j) + OFFSET;
                g.drawLine( (int) (rat * (i + 0.5)) + OFFSET, y,
                        (int) (rat * (i + 0.5)) + OFFSET, (int) (rat * j + 8.0 * v) + OFFSET );
                g.drawLine( x, (int) (rat * (j + 0.5)) + OFFSET,
                        (int) (rat * i + 8.0 * u) + OFFSET, (int) (rat * (j + 0.5)) + OFFSET );
            }
        }
    }
}
