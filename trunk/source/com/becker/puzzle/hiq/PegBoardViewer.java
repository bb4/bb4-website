package com.becker.puzzle.hiq;


import javax.swing.*;
import java.awt.*;
import java.text.*;
import java.util.List;

/**
 *  UI for drawing the current best solution to the puzzle.
 *
 */
final class PegBoardViewer extends JPanel
{

    private static final int INC = 10;

    private static final int LEFT_MARGIN = 40;
    private static final int TOP_MARGIN = 50;

    private static final Color FILLED_HOLE_COLOR = new Color(120, 0, 190);
    private static final Color EMPTY_HOLE_COLOR = new Color(55, 55, 65, 150);
    private static final int FILLED_HOLE_RAD = 16;
    private static final int EMPTY_HOLE_RAD = 9;

    private final NumberFormat formatter_;

    private PegBoard pegBoard_;
    private List<PegMove> path_;
    private int currentStep_;
    private long numTries_;


    // Constructor.
    PegBoardViewer(PegBoard board)
    {
        pegBoard_ = board;
        setPreferredSize(new Dimension( board.getSize() * INC, board.getSize() * INC ));
        formatter_ = new DecimalFormat();
        formatter_.setGroupingUsed(true);
        formatter_.setMaximumFractionDigits(0);
    }

    public void setBoard(PegBoard board) {
        pegBoard_ = board;
    }
    
    public PegBoard getBoard() {
        return pegBoard_;
    }

    public void showPath(List<PegMove> path, PegBoard board, long numTries) {
        path_ = path;
        pegBoard_ = board;
        System.out.println("path size="+ path.size());
        System.out.println("path="+ path);
        currentStep_ = path.size() - 1;
        numTries_ = numTries;
    }

    public List<PegMove> getPath() {
        return path_;
    }

    public int getCurrentStep() {
        return currentStep_;
    }

    public void setNumTries(long numTries) {
        numTries_ = numTries;
    }

    /**
     * switch from the current move in the sequence forwards or backwards stepSize.
     * @param stepSize num steps to move.
     */
    public void moveInPath(int stepSize) {
        if (stepSize == 0) return;
        int inc = stepSize > 0 ? 1 : -1;
        int toStep = currentStep_ + stepSize;
        do {
            pegBoard_ = pegBoard_.doMove((PegMove)path_.get(currentStep_), (inc < 0));
            currentStep_ += inc;
        } while (currentStep_ != toStep);
        repaint();
    }

    /**
     * This renders the current state of the puzzle to the screen
     */
    protected void paintComponent( Graphics g )
    {
        int i, xpos, ypos;

        super.paintComponents( g );
        // erase what's there and redraw.

        g.clearRect( 0, 0, this.getWidth(), this.getHeight() );
        g.setColor( new Color( 235, 235, 230 ) );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );

        int size = pegBoard_.getSize();
        int rightEdgePos = LEFT_MARGIN + 3 * INC * size;
        int bottomEdgePos = TOP_MARGIN + 3 * INC * size;


        g.setColor( Color.black );
        g.drawString( "Number of tries: " +  formatter_.format(numTries_) ,
                LEFT_MARGIN, TOP_MARGIN - 24 );

        // draw the hatches which deliniate the cells
        g.setColor( Color.darkGray );
        for ( i = 0; i <= size; i++ )  //   -----
        {
            ypos = TOP_MARGIN + i * 3 * INC;
            g.drawLine( LEFT_MARGIN, ypos, rightEdgePos, ypos );
        }
        for ( i = 0; i <= size; i++ )  //   ||||
        {
            xpos = LEFT_MARGIN + i * 3 * INC;
            g.drawLine( xpos, TOP_MARGIN, xpos, bottomEdgePos );
        }

        // now draw the pieces that we have so far
        for (byte row = 0; row < size; row++) {
            for (byte col = 0; col < size; col++) {

                if (PegBoard.isValidPosition(row, col)) {

                    xpos = LEFT_MARGIN + col * 3 * INC + INC / 3;
                    ypos = TOP_MARGIN + row * 3 * INC + 2 * INC / 3;

                    boolean empty = pegBoard_.isEmpty(row, col);
                    Color c = empty ?  EMPTY_HOLE_COLOR : FILLED_HOLE_COLOR;
                    int r = empty ? EMPTY_HOLE_RAD : FILLED_HOLE_RAD;
                    g.setColor(c);
                    int rr = r >> 1;

                    g.fillOval(xpos + INC - rr, ypos + INC - rr, r, r);
                }
            }
        }
    }
}

