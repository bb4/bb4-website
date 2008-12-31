package com.becker.simulation.liquid;

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
    private static final Color GRID_COLOR = new Color( 20, 20, 20, 15 );
    private static final Color PARTICLE_COLOR = new Color( 120, 0, 30, 80 );
    private static final Color VECTOR_COLOR = new Color( 205, 90, 25, 40 );
    private static final Color WALL_COLOR = new Color( 100, 210, 170, 150 );
    private static final Color TEXT_COLOR = new Color( 10, 10, 170, 200 );
    //private static final float PRESSURE_COL_OPACITY = 0.01f;
    // scales the size of everything
    private static final double DEFAULT_SCALE = 30;
    private static final double VELOCITY_VECTOR_SCALE = .5;

    private static final int OFFSET = 10;

    private static final ColorMap pressureColorMap_ = new PressureColorMap();

    // temp var used throughout for efficency. avoids creating objects
    private static final double[] a_ = new double[2]; // temp point var
    private static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 12 );

    private double scale_ = DEFAULT_SCALE;

    private float wallLineWidth_;
    private int particleSize_;

    private boolean showVelocities_ = false;
    private boolean showPressures_ = false;


    public EnvironmentRenderer() {
       setScale(DEFAULT_SCALE);
    }

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
   public void render(LiquidEnvironment env, Graphics2D g)
    {
        double time = System.currentTimeMillis();

        int i,j;
        int rightEdgePos = (int) (scale_ * env.getXDim());
        int bottomEdgePos = (int) (scale_ * env.getYDim());

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

        drawParticles(g, env);

        // draw text representing internal state for debug purposes.
        if ( LiquidEnvironment.LOG_LEVEL >= 2 ) {
            Cell[][] grid = env.getGrid();
            g.setColor( TEXT_COLOR );
            g.setFont( BASE_FONT );
            StringBuffer strBuf = new StringBuffer( "12" );
            for ( j = 0; j < env.getYDim(); j++ ) {
                for ( i = 0; i < env.getXDim(); i++ ) {
                    int x = (int) (scale_ * i) + OFFSET;
                    int y = (int) (scale_ * j) + OFFSET;
                    strBuf.setCharAt( 0, grid[i][j].getStatus().getSymbol() );
                    strBuf.setLength( 1 );
                    int nump = grid[i][j].getNumParticles();
                    if ( nump > 0 )
                        strBuf.append( String.valueOf( nump ) );
                    g.drawString( strBuf.toString(), x + 6, y + 18 );
                }
            }
        }

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

        double duration = (System.currentTimeMillis() - time) / 100.0;
        env.log( 1, "time to render:  (" + duration + ") " );
    }


    private void drawParticles(Graphics2D g, LiquidEnvironment env) {
        g.setColor( PARTICLE_COLOR );
        // draw the ---particles--- of liquid
        for (Particle p : env.getParticles()) {
            p.get(a_);
            //Cell c = p.getCell();
            //int[] pos = c.getPos();
            //if (pos[0] == 2  &&  pos[1] == 2)
            int comp = (int) (256.0 * p.getAge() / 10.0);
            comp = (comp > 255) ? 255 : comp;
            g.setColor(new Color(comp, 100, 255 - comp, 60));
            //System.out.println("pos = "+a_[0]+", "+a_[0]);
            g.fillOval((int) (scale_ * a_[0] + OFFSET), (int) (scale_ * a_[1] + OFFSET),
                              particleSize_, particleSize_);
        }
    }

    private void renderPressure(Graphics2D g, LiquidEnvironment env) {
        Cell[][] grid = env.getGrid();
        for (int j = 0; j < env.getYDim(); j++ ) {
            for (int i = 0; i < env.getXDim(); i++ ) {
                g.setColor( pressureColorMap_.getColorForValue( grid[i][j].getPressure() ) );
                g.fillRect( (int) (scale_ * (i)) + OFFSET, (int) (scale_ * (j)) + OFFSET,
                            (int) scale_, (int) scale_ );
            }
        }
    }

    private void drawVectors(Graphics2D g, LiquidEnvironment env) {
        g.setColor( VECTOR_COLOR );
        Cell[][] grid = env.getGrid();
        for ( int j = 0; j < env.getYDim(); j++ ) {
            for ( int i = 0; i < env.getXDim(); i++ ) {
                double u = grid[i][j].getUip();
                double v = grid[i][j].getVjp();
                int x = (int) (scale_ * i) + OFFSET;
                int xMid =  (int) (scale_ * (i + 0.5)) + OFFSET;
                int xLen = (int) (scale_ * i + VELOCITY_VECTOR_SCALE * u) + OFFSET;
                int y = (int) (scale_ * j) + OFFSET;
                int yMid =  (int) (scale_ * (j + 0.5)) + OFFSET;
                int yLen = (int) (scale_ * j + VELOCITY_VECTOR_SCALE * v) + OFFSET ;
                g.drawLine( xMid, y, xMid, yLen);
                g.drawLine( x, yMid, xLen, yMid );
            }
        }
    }
}
