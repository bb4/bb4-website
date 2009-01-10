package com.becker.simulation.liquid;

import com.becker.common.*;

import java.awt.*;
import javax.vecmath.Vector2d;

/**
 *  Renders a specified liquid environment.
 *
 *  @author Barry Becker
 */
public final class EnvironmentRenderer
{
    // rendering style attributes
    private static final Color GRID_COLOR = new Color( 20, 20, 20, 15 );

    private static final Color PARTICLE_COLOR = new Color( 120, 0, 30, 80 );
    private static final Color PARTICLE_VELOCITY_COLOR = new Color( 225, 0, 35, 20 );
    private static final Stroke PARTICLE_VELOCITY_STROKE  = new BasicStroke(0.2f);

    private static final Color FACE_VELOCITY_COLOR = new Color( 205, 90, 25, 110 );
    private static final Stroke FACE_VELOCITY_STROKE  = new BasicStroke(2.0f);
    private static final double VELOCITY_SCALE = .4;

    private static final Color WALL_COLOR = new Color( 100, 210, 170, 150 );
    private static final Color TEXT_COLOR = new Color( 10, 10, 170, 200 );
    //private static final float PRESSURE_COL_OPACITY = 0.01f;
    /** scales the size of everything */
    private static final double DEFAULT_SCALE = 30;

    /* grid offset  */
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

    LiquidEnvironment env_;

    public EnvironmentRenderer(LiquidEnvironment env) {
        env_ = env;
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
   public void render(Graphics2D g)
    {
        double time = System.currentTimeMillis();
        
        // make sure all the cell statuses are in a consistent state
        env_.updateCellStatus();
        
        drawGrid(g);

        // draw the cells colored by ---pressure--- val
        if (showPressures_) {
            renderPressure(g);
        }

        // draw the ---walls---
        drawWalls(g);

        drawParticles(g);

        if ( LiquidEnvironment.LOG_LEVEL >= 2 ) {
            drawCellSymbols(g);
        }
        
        // draw the ---velocity--- field (and status)
        if (showVelocities_)
            drawCellFaceVelocities(g);

        double duration = (System.currentTimeMillis() - time) / 100.0;
        env_.log( 1, "time to render:  (" + duration + ") " );
    }

   /**
    * draw the cells/grid_
    */
   private void drawGrid(Graphics2D g) {

        g.setColor( GRID_COLOR );
        int rightEdgePos = (int) (scale_ * env_.getXDim());
        int bottomEdgePos = (int) (scale_ * env_.getYDim());

        for (int  j = 0; j < env_.getYDim(); j++ )   //  -----
        {
            int ypos = (int) (j * scale_);
            g.drawLine( OFFSET, ypos + OFFSET, rightEdgePos + OFFSET, ypos + OFFSET );
        }
        for (int i = 0; i < env_.getXDim(); i++ )    //  ||||
        {
            int xpos = (int) (i * scale_);
            g.drawLine( xpos + OFFSET, OFFSET, xpos + OFFSET, bottomEdgePos + OFFSET );
        }
   }

    /**
     * Draw the particles in the liquid in the environment.
     */
    private void drawParticles(Graphics2D g) {
        g.setColor( PARTICLE_COLOR );

        // draw the ---particles--- of liquid
        for (Particle p : env_.getParticles()) {
            p.get(a_);
            Cell c = p.getCell();
            //int[] pos = c.getPos();
            //if (pos[0] == 2  &&  pos[1] == 2)
            int comp = (int) (256.0 * p.getAge() / 10.0);
            comp = (comp > 255) ? 255 : comp;
            g.setColor(new Color(comp, 100, 255 - comp, 60));
            //System.out.println("pos = "+a_[0]+", "+a_[0]);
            double offset = OFFSET - particleSize_/2.0;
            g.fillOval((int) (scale_ * a_[0] + offset), (int) (scale_ * a_[1] + offset),
                              particleSize_, particleSize_);
        }

        if (showVelocities_) {
            g.setStroke( PARTICLE_VELOCITY_STROKE);
            g.setColor( PARTICLE_VELOCITY_COLOR );
            for (Particle p : env_.getParticles()) {
                if (showVelocities_) {
                    Vector2d vel = env_.findInterpolatedGridVelocity(p);
                    p.get(a_);
                    double x = (scale_ * a_[0]) + OFFSET;

                    double xLen = x + VELOCITY_SCALE * vel.x;
                    double y = (scale_ * a_[1]) + OFFSET;
                    double yLen = y +  VELOCITY_SCALE * vel.y;
                    g.drawLine( (int)x, (int)y, (int)xLen, (int)yLen);
                }
            }
        }
    }

    /**
     * Color the squares according to the pressure in that discrete region.
     */
    private void renderPressure(Graphics2D g) {
        Cell[][] grid = env_.getGrid();
        for (int j = 0; j < env_.getYDim(); j++ ) {
            for (int i = 0; i < env_.getXDim(); i++ ) {
                g.setColor( pressureColorMap_.getColorForValue( grid[i][j].getPressure() ) );
                g.fillRect( (int) (scale_ * (i)) + OFFSET, (int) (scale_ * (j)) + OFFSET,
                            (int) scale_, (int) scale_ );
            }
        }
    }

    /**
     * Draw walls and boundary.
     */
    private void drawWalls(Graphics2D g) {

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
        g.drawRect( OFFSET, OFFSET, (int) (env_.getXDim() * scale_), (int) (env_.getYDim() * scale_) );
    }

    /**
     * draw text representing internal state for debug purposes.
     */
    private void drawCellSymbols(Graphics2D g) {

        Cell[][] grid = env_.getGrid();
        g.setColor( TEXT_COLOR );
        g.setFont( BASE_FONT );
        StringBuffer strBuf = new StringBuffer( "12" );
        for ( int j = 0; j < env_.getYDim(); j++ ) {
            for (int  i = 0; i < env_.getXDim(); i++ ) {
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

    /**
     * There is a velocity vector in the center of each cell face.
     */
    private void drawCellFaceVelocities(Graphics2D g) {
        g.setStroke( FACE_VELOCITY_STROKE);
        g.setColor( FACE_VELOCITY_COLOR );
        Cell[][] grid = env_.getGrid();
        for ( int j = 0; j < env_.getYDim(); j++ ) {
            for ( int i = 0; i < env_.getXDim(); i++ ) {
                double u = grid[i][j].getUip();
                double v = grid[i][j].getVjp();
                int x = (int) (scale_ * i) + OFFSET;
                int xMid =  (int) (scale_ * (i + 0.5)) + OFFSET;
                int xLen = (int) (scale_ * i + VELOCITY_SCALE * u) + OFFSET;
                int y = (int) (scale_ * j) + OFFSET;
                int yMid =  (int) (scale_ * (j + 0.5)) + OFFSET;
                int yLen = (int) (scale_ * j + VELOCITY_SCALE * v) + OFFSET ;
                g.drawLine( xMid, y, xMid, yLen);
                g.drawLine( x, yMid, xLen, yMid );
            }
        }
    }
}
