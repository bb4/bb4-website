package com.becker.maze;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 *  Program to automatically generate a Maze.
 *  Motivation: Get my son, Brian, to excel at Kumon by trying these mazes with a pencil.
 *  this is the global space containing all the cells, walls, and particles
 *  Assumes an M*N grid of cells
 *  X axis increases to the left
 *  Y axis increases downwards to be consistent with java graphics
 *
 * todo:
 *   - applet doesn't refresh when resizing in a browser window.
 *   - add a button for animating a solution to the maze.
 *
 *  @author Barry Becker
 */
public class MazeGenerator extends JComponent
{

    // the dimensions of the space
    protected int xDim_;
    protected int yDim_;

    // the grid of cells that make up the environment
    // in x,y (col, row) order
    private MazeCell[][] grid_ = null;

    // the default size of each square cell in millimeters
    private double cellSize_ = 40.0;
    private static Random RANDOM = new Random();

    // a list of 3 directions : FORWARD, LEFT,  and RIGHT
    private ArrayList directions_ = null;

    // the start and stop positions
    private Point startPosition_ = null;
    private Point stopPosition_ = null;
    // put the stop point at the maximum search depth
    private static int maxDepth_ = 0;

    // possible directions as we traverse
    private static final Integer FORWARD = new Integer( 1 );
    private static final Integer LEFT = new Integer( 2 );
    private static final Integer RIGHT = new Integer( 3 );

    // vary the probability that each direction occurs for interesting effects
    // the sum of these probabilities must sum to 1
    private static final double FORWARD_PROB = .6;
    private static final double LEFT_PROB = .39;
    private static final double RIGHT_PROB = .01;

    // default probs
    private double forwardProb_ = FORWARD_PROB;
    private double leftProb_ = LEFT_PROB;
    private double rightProb_ = RIGHT_PROB;

    // rending attributes
    //private static final Color GRID_COLOR = new Color(20,20,20);
    private static final Color WALL_COLOR = new Color( 80, 0, 150 );
    private static final Color TEXT_COLOR = new Color( 250, 0, 100 );
    private static final Color BG_COLOR = new Color( 225, 240, 250 );
    private static final Color VISITED_COLOR = new Color( 255, 255, 255 );

    private static final int WALL_LINE_WIDTH = 3;

    private Font textFont_ = null;
    //Log logger_ = null;


    //Constructor
    public MazeGenerator()
    {
        RANDOM.setSeed( 1 );
    }

    private void initGenerator( double w, double h )
    {
        int i, j;

        if ( w < 1.0 ) w = 1.0;
        if ( h < 1.0 ) h = 1.0;
        xDim_ = (int) w; // +2
        yDim_ = (int) h; // +2
        int numCells = xDim_ * yDim_;
        System.out.println( "initGenerator w=" + w + " h=" + h );
        grid_ = new MazeCell[xDim_ ][yDim_];
        stopPosition_ = new Point( xDim_, yDim_ );

        // further refine the cell size so it never exceeds borders
        Dimension d = this.getSize();
        System.out.println( "initGen(w=" + w + " h=" + h + ") allocation=(" + xDim_ + ", " + yDim_ + ") origSize=" + cellSize_ + "  xEst=" + d.getWidth() / xDim_ + " yEst=" + d.getHeight() / yDim_ );
        cellSize_ = Math.min( d.getWidth() / (xDim_), d.getHeight() / (yDim_) );

        textFont_ = new Font( "Serif", Font.BOLD, 6 + 5000 / (100 + numCells) );

        //logger_ = new Log(new OutputWindow("Log Output", null));
        //logger_.setDestination(Log.LOG_TO_WINDOW);

        for ( j = 0; j < yDim_; j++ ) {
            for ( i = 0; i < xDim_; i++ ) {
                grid_[i][j] = new MazeCell();
            }
        }

        directions_ = new ArrayList();

        directions_.add( FORWARD );
        directions_.add( LEFT );
        directions_.add( RIGHT );
        // Collections.shuffle(directions_);

        // a border around the whole maze
        setConstraints();

        // randomize these?
        startPosition_ = new Point( 2, 2 );
    }

    /**
     *  set OBSTACLEs, walls
     *  mark all the cells around the periphery as visited so there will be walls generated there
     */
    private void setConstraints()
    {
        int i, j;
        MazeCell c;

        // right and left
        for ( j = 0; j < yDim_; j++ ) {
            // left
            c = grid_[0][j];
            c.visited = true;
            // right
            c = grid_[xDim_ - 1][j];
            c.visited = true;
        }

        // top and bottom
        for ( i = 0; i < xDim_; i++ ) {
            // bottom
            c = grid_[i][0];
            c.visited = true;
            // top
            c = grid_[i][yDim_ - 1];
            c.visited = true;
        }
    }

    /**
     * generate the maze
     */
    public void generate( int thickness )
    {
        generate( thickness, FORWARD_PROB, LEFT_PROB, RIGHT_PROB );
    }

    /**
     * generate the maze
     */
    public void generate( int thickness, double forwardProb, double leftProb, double rightProb )
    {
        Dimension dim = this.getSize();
        System.out.println( "gen: dim.w=" + dim.getWidth() + " dim.ht=" + dim.getHeight() + " thickness=" + thickness );

        if ( thickness >= (dim.width / 4) || thickness >= (dim.height / 4) )
            thickness = Math.min( (dim.width / 5), (dim.height / 5) );

        // thickness must be divisible by 2;
        if ( thickness % 2 != 0 )
            thickness += 1;
        cellSize_ = thickness;

        double w = (double) dim.width / (double) thickness;
        double h = (double) dim.height / (double) thickness;
        //System.out.println("x="+w+" y="+h+ "  xDim="+(w*thickness)+" yDim="+(h*thickness));

        initGenerator( w, h );
        // the second argument is a dummy direction
        maxDepth_ = 0;
        forwardProb_ = forwardProb;
        leftProb_ = leftProb;
        rightProb_ = rightProb;

        search();
        //search( startPosition_, new Point(0,0), 0 );
        this.repaint();
    }

    /**
     * do a depth first search (without recursion) of the grid space to determine the graph.
     * I used to use a recursive algorithm but it was slower and would give stack overflow
     * exceptions even for moderately sized mazes.
     */
    public void search()
    {
        LinkedList stack = new LinkedList();

        Point currentPosition = startPosition_;
        //System.out.println( "currentPos="+currentPosition );
        //System.out.println( "grid dims ="+ (xDim_ )+" "+ (yDim_ ));
        MazeCell currentCell = grid_[currentPosition.x][currentPosition.y];
        //System.out.println( "current cell===="+currentCell );

        // push the initial moves
        pushMoves( currentPosition, new Point( 1, 0 ), 1, stack );
        Point dir = null;
        int depth = 1;
        Dimension d = this.getSize();

        while ( !stack.isEmpty() ) {
            boolean moved = false;

            do {
                GenState state = (GenState) stack.removeFirst();  // pop

                currentPosition = state.position;
                dir = state.direction;
                depth = state.depth;

                if ( depth > maxDepth_ ) {
                    maxDepth_ = depth;
                    stopPosition_ = currentPosition;
                }
                if ( depth > currentCell.depth )
                    currentCell.depth = depth;

                currentCell = grid_[currentPosition.x][currentPosition.y];
                if (currentCell==null) {
                    System.out.println( " currentPosition="+currentPosition);
                    System.out.println( " grid_["+currentPosition.x+"].length="+grid_[currentPosition.x].length);
                }
                currentCell.visited = true;

                Point nextPosition = (Point) currentPosition.clone();
                nextPosition.translate( dir.x, dir.y );
                MazeCell nextCell = grid_[nextPosition.x][nextPosition.y];

                if ( !nextCell.visited ) {
                    moved = true;
                    nextCell.visited = true;
                    currentPosition = nextPosition;
                }
                else {
                    // add a wall
                    if ( dir.x == 1 ) // east
                        currentCell.eastWall = true;
                    else if ( dir.y == 1 ) // south
                        currentCell.southWall = true;
                    else if ( dir.x == -1 )  // west
                        nextCell.eastWall = true;
                    else if ( dir.y == -1 )  // north
                        nextCell.southWall = true;
                }
            } while ( !moved && !stack.isEmpty() );

            // this can be really slow if you do a refresh everytime
            if ( RANDOM.nextDouble() < 10.0 / (xDim_ * yDim_) ) { // was 50.0
                this.paintImmediately( 0, 0, (int) d.getWidth(), (int) d.getHeight() );
            }

            // now at a new location
            if ( moved )
                pushMoves( currentPosition, dir, ++depth, stack );
        }
    }

    protected void pushMoves( Point currentPosition, Point fromDir, int depth, LinkedList stack )
    {
        // from this point try each direction in a random order
        // assigning probablilities to the order in which we check these directions can give interesting effects
        ArrayList directions = getShuffledDirections();

        // check all the directions except the one we came from
        for ( int i = 0; i < 3; i++ ) {
            Integer direction = (Integer) directions.get( i );
            Point dir = fromDir; // init with FORWARD
            if ( direction == LEFT ) {
                dir = leftOf( fromDir );
            }
            else if ( direction == RIGHT ) {
                dir = rightOf( fromDir );
            }
            stack.addFirst( new GenState( currentPosition, dir, depth ) );
        }
    }


    /**
     * return a shuffled list of directions
     * they are ordered given the potentially skewed probablilities at the top
     */
    protected ArrayList getShuffledDirections()
    {
        double rnd = RANDOM.nextDouble(); //Math.random();
        ArrayList directions = new ArrayList();
        ArrayList originalDirections = (ArrayList) directions_.clone();
        if ( rnd < forwardProb_ ) {
            directions.add( originalDirections.remove( 0 ) );
            directions.add( getSecondDir( originalDirections, leftProb_, rightProb_ ) );
        }
        else if ( rnd >= forwardProb_ && rnd < (forwardProb_ + leftProb_) ) {
            directions.add( originalDirections.remove( 1 ) );
            directions.add( getSecondDir( originalDirections, forwardProb_, rightProb_ ) );
        }
        else {
            directions.add( originalDirections.remove( 2 ) );
            directions.add( getSecondDir( originalDirections, forwardProb_, leftProb_ ) );
        }
        // the third direction is whatever remains
        directions.add( originalDirections.remove( 0 ) );
        return directions;
    }

    protected Integer getSecondDir( ArrayList twoDirections, double p1, double p2 )
    {
        double rnd = RANDOM.nextDouble(); //Math.random();
        //double prob1 = p1 / (p1+p2);
        if ( rnd < p1 )
            return (Integer) twoDirections.remove( 0 );
        else
            return (Integer) twoDirections.remove( 1 );
    }

    /* find the direction which is counterclockwise 90 to the left of the specified dir.
     */
    protected Point leftOf( Point dir )
    {
        Point newDir = null;
        if ( dir.x == 0 ) {
            if ( dir.y > 0 )
                newDir = new Point( -1, 0 );
            else
                newDir = new Point( 1, 0 );
        }
        else {  // assumed dir.y == 0
            if ( dir.x > 0 )
                newDir = new Point( 0, 1 );
            else
                newDir = new Point( 0, -1 );
        }
        return newDir;
    }

    /* find the direction which is counterclockwise 90 to the left of the specified dir.
    */
    protected Point rightOf( Point dir )
    {
        Point newDir = null;
        if ( dir.x == 0 ) {
            if ( dir.y > 0 )
                newDir = new Point( 1, 0 );
            else
                newDir = new Point( -1, 0 );
        }
        else {  // assumed dir.y == 0
            if ( dir.x > 0 )
                newDir = new Point( 0, -1 );
            else
                newDir = new Point( 0, 1 );
        }
        return newDir;
    }

    /**
     * Render the Environment on the screen
     */
    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );
        Graphics2D g2 = (Graphics2D) g;

        int i,j;
        int cellSize = (int) cellSize_;

        // background
        g2.setColor( BG_COLOR );
        g2.fillRect( 0, 0, cellSize * xDim_, cellSize * yDim_ );

        // draw the walls
        //Stroke oldStroke = g2.getStroke();

        int lineWidth = WALL_LINE_WIDTH;
        if ( cellSize_ < 20 )
            lineWidth = 2;
        if ( cellSize_ < 8 )
            lineWidth = 1;
        Stroke wallStroke = new BasicStroke( lineWidth );
        g2.setStroke( wallStroke );
        g2.setFont( textFont_ );

        g2.setColor( VISITED_COLOR );
        for ( j = 0; j < yDim_; j++ ) {
            for ( i = 0; i < xDim_; i++ ) {
                //g.drawLine(OFFSET, ypos+OFFSET, rightEdgePos+OFFSET, ypos+OFFSET);
                MazeCell c = grid_[i][j];
                if ( c == null )
                    System.out.println( "Error1 pos i=" + i + " j=" + j + " is out of bounds. xDim=" + xDim_ + " yDim=" + yDim_ );
                int xpos = i * cellSize;
                int ypos = j * cellSize;

                if ( c.visited ) {
                    g2.setColor( VISITED_COLOR );
                    g2.fillRect( xpos + 1, ypos + 1, cellSize, cellSize );
                    //g2.setColor(Color.black);
                }
            }
        }

        g2.setColor( WALL_COLOR );
        for ( j = 0; j < yDim_; j++ ) {
            for ( i = 0; i < xDim_; i++ ) {
                //g.drawLine(OFFSET, ypos+OFFSET, rightEdgePos+OFFSET, ypos+OFFSET);
                MazeCell c = grid_[i][j];
                if ( c == null )
                    System.out.println( "Error2 pos i=" + i + " j=" + j + " is out of bounds. xDim=" + xDim_ + " yDim=" + yDim_ );
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

        g2.setColor( TEXT_COLOR );
        g2.drawString( "S", (int) ((startPosition_.x + .32) * cellSize), (int) ((startPosition_.y + .76) * cellSize) );
        if ( stopPosition_ != null )
            g2.drawString( "F", (int) ((stopPosition_.x + .32) * cellSize), (int) ((stopPosition_.y + .76) * cellSize) );
    }

}
