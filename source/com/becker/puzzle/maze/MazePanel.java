package com.becker.puzzle.maze;

import javax.swing.*;
import java.awt.*;

/**
 * This panel is responsible for drawing the Maze (see MazeModel).
 * @author Barry Becker Date: Jul 29, 2006
 */
public class MazePanel extends JComponent {

    // represents the maze that we need to render.
    private MazeModel maze_;

    // the default size of each square cell in millimeters
    private double cellSize_ = 40.0;

    private int animationSpeed_;

    // rendering attributes
    private static final Color WALL_COLOR = new Color( 80, 0, 150 );
    private static final Color PATH_COLOR = new Color( 255, 230, 120);

    private static final Color TEXT_COLOR = new Color( 250, 0, 100 );
    private static final Color BG_COLOR = new Color( 225, 240, 250 );
    private static final Color VISITED_COLOR = new Color( 255, 255, 255 );

    private static final int WALL_LINE_WIDTH = 3;
    private static final int PATH_LINE_WIDTH = 14;

    private Font textFont_;

    public MazePanel() {

    }

    public MazeModel getMaze() {
        return maze_;
    }

    public void setAnimationSpeed(int animSpeed) {
        animationSpeed_ = animSpeed;
    }

    public int getAnimationSpeed() {
        return animationSpeed_;
    }

    public void setThickness(int thickness) {

        Dimension dim = getSize();
        if (dim.width <= 0 || dim.height < 0)
            return;

        cellSize_ = thickness;
        int w = dim.width / thickness;
        int h = dim.height / thickness;

        int fontSize = 2 + ((int) cellSize_ >> 1);
        textFont_ = new Font("Serif", Font.BOLD, fontSize);

        maze_ = new MazeModel(w, h);
    }


    public void generate(int thickness,
                         double forwardProb, double leftProb, double rightProb) {
        MazeGenerator generator = new MazeGenerator(this);
        generator.generate(forwardProb, leftProb, rightProb);
    }

    public void solve() {
        MazeSolver solver= new MazeSolver(this);
        solver.solve();
    }

    /**
     * paint the whole window right now!
     */
    public void paintAll()
    {
        Dimension d = this.getSize();
        this.paintImmediately( 0, 0, (int) d.getWidth(), (int) d.getHeight() );
    }

    /**
     * paint just the region around a single cell for performance.
     * @param pt
     */
    public void paintCell(Point pt) {
        int csized2 = (int)(cellSize_/2.0)+2;
        int xpos = (int)(pt.getX() * cellSize_);
        int ypos = (int)(pt.getY() * cellSize_);
        if (animationSpeed_ <= 10)  {
            // this paints just the cell immediately (sorta slow)
            this.paintImmediately( xpos-csized2, ypos-csized2, (int)(2.0*cellSize_), (int)(2.0*cellSize_));
        }
        else  {
            if (Math.random() < (10/(animationSpeed_ + 1)))  {
              paintAll();
            }
            else {
              this.repaint(xpos-csized2, ypos-csized2, (int)(2.0*cellSize_), (int)(2.0*cellSize_));
            }
        }
    }

    /**
     * Render the Environment on the screen
     */
    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );
        if (maze_ == null) return;

        Graphics2D g2 = (Graphics2D) g;

        int i,j;
        int cellSize = (int) cellSize_;
        int halfCellSize =  (int) (cellSize_/2.0);

        // background
        g2.setColor( BG_COLOR );
        int width = maze_.getWidth();
        int height = maze_.getHeight();
        g2.fillRect( 0, 0, cellSize * width, cellSize * height );

        int lineWidth = (int) (WALL_LINE_WIDTH * cellSize_ / 30.0);
        int pathWidth = (int) (PATH_LINE_WIDTH * cellSize_ / 30.0);

        Stroke wallStroke = new BasicStroke( lineWidth );
        Stroke pathStroke = new BasicStroke( pathWidth );

        g2.setFont( textFont_ );

        g2.setColor( VISITED_COLOR );
        for ( j = 0; j < height; j++ ) {
            for ( i = 0; i < width; i++ ) {
                MazeCell c = maze_.getCell(i, j);
                if ( c == null )
                    System.out.println( "Error1 pos i=" + i + " j=" + j +
                                        " is out of bounds. xDim=" + width + " yDim=" + height );
                int xpos = i * cellSize;
                int ypos = j * cellSize;

                if ( c!=null && c.visited ) {
                    g2.setColor( VISITED_COLOR );
                    g2.fillRect( xpos + 1, ypos + 1, cellSize, cellSize );
                    //g2.setColor(Color.black);
                }
            }
        }

        g2.setStroke( wallStroke );
        g2.setColor( WALL_COLOR );
        for ( j = 0; j < height; j++ ) {
            for ( i = 0; i < width; i++ ) {
                MazeCell c = maze_.getCell(i, j);
                if ( c == null )
                    System.out.println( "Error2 pos i=" + i + " j=" + j
                                        + " is out of bounds. xDim=" + width + " yDim=" + height );
                int xpos = i * cellSize;
                int ypos = j * cellSize;

                if ( c.eastWall ) {
                    g2.drawLine( xpos + cellSize, ypos, xpos + cellSize, ypos + cellSize );
                }
                if ( c.southWall ) {
                    g2.drawLine( xpos, ypos + cellSize, xpos + cellSize, ypos + cellSize );
                }
            }
        }

       g2.setStroke( pathStroke );
       g2.setColor( PATH_COLOR );
       for ( j = 0; j < height; j++ ) {
           for ( i = 0; i < width; i++ ) {
               MazeCell c = maze_.getCell(i, j);
               int xpos = i * cellSize;
               int ypos = j * cellSize;

               if (c==null) return;
               if ( c.eastPath )  {
                    g2.drawLine( xpos + halfCellSize, ypos + halfCellSize, xpos + cellSize, ypos + halfCellSize );
               }
               if ( c.westPath )  {
                    g2.drawLine( xpos, ypos + halfCellSize, xpos + halfCellSize, ypos + halfCellSize );
               }
               if ( c.northPath )  {
                    g2.drawLine( xpos + halfCellSize, ypos + halfCellSize, xpos + halfCellSize, ypos );
               }
               if ( c.southPath )  {
                    g2.drawLine( xpos + halfCellSize, ypos + cellSize, xpos + halfCellSize, ypos + halfCellSize );
               }
            }
        }

        g2.setColor( TEXT_COLOR );
        drawChar("S", maze_.getStartPosition(), cellSize, g2);
        drawChar("F", maze_.getStopPosition(), cellSize, g2);
    }

    private static void drawChar(String c, Point pos,  int cellSize, Graphics2D g2)
    {
        if (pos != null)
          g2.drawString( c, (int) ((pos.x + 0.32) * cellSize), (int) ((pos.y + 0.76) * cellSize) );
    }

}
