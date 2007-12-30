package com.becker.puzzle.hiq;


import com.becker.common.util.Util;
import com.becker.puzzle.common.PuzzleViewer;
import com.becker.puzzle.common.Refreshable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.*;
import java.awt.*;
import java.text.*;
import java.util.List;

/**
 *  UI for drawing the current best solution to the puzzle.
 *
 */
final class PegBoardViewer extends PuzzleViewer<PegBoard, PegMove>
                                             implements ActionListener
{

    private static final int INC = 10;

    private static final int LEFT_MARGIN = 50;
    private static final int TOP_MARGIN = 55;

    private static final Color FILLED_HOLE_COLOR = new Color(120, 0, 190);
    private static final Color EMPTY_HOLE_COLOR = new Color(55, 55, 65, 150);
    private static final int FILLED_HOLE_RAD = 16;
    private static final int EMPTY_HOLE_RAD = 9;
 
    private List<PegMove> path_;
    private int currentStep_;
    
    private JButton backButton_;
    private JButton forwardButton_;
   
    private PegBoardCanvas canvas_;


    /**
     * Constructor.
     */
    PegBoardViewer(PegBoard board)
    {
        board_ = board;        
        initUI();
    }
    
    private void initUI() {

        setLayout(new BorderLayout());
        
        canvas_ = new PegBoardCanvas();        
        canvas_.setPreferredSize(new Dimension( board_.getSize() * INC, board_.getSize() * INC ));
        
        backButton_ = new JButton("Back");
        forwardButton_ = new JButton("Forward");
        backButton_.addActionListener(this);
        forwardButton_.addActionListener(this);
        backButton_.setEnabled(false);
        forwardButton_.setEnabled(false);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(backButton_, BorderLayout.WEST);
        buttonPanel.add(forwardButton_, BorderLayout.EAST);
        
        add(canvas_, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public List<PegMove> getPath() {
        return path_;
    }
    
    public void refresh(PegBoard board, long numTries) {         
        if (numTries % 6000 == 0) {
            status_ = createStatusMessage(numTries);
            refresh1(board, numTries);  
        }
    }
    
    public void finalRefresh(java.util.List<PegMove> path, PegBoard board, long numTries, long millis) {      
        super.finalRefresh(path, board, numTries, millis);
        showPath(path, board, numTries);                 
    }
   
    public void makeSound() {
        // add sound
    }

    public void showPath(java.util.List<PegMove> path, PegBoard board, long numTries) {
        java.util.List<PegMove> p = new LinkedList<PegMove>();
        p.addAll(path);
        
        path_ = path;
        board_ = board;
        System.out.println("path size="+ path.size());
        System.out.println("path="+ path);
        currentStep_ = path.size() - 1;
        backButton_.setEnabled(true);
        forwardButton_.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton_) {
            canvas_.moveInPath(-1);
            backButton_.setEnabled((currentStep_ > 0));
            forwardButton_.setEnabled(true);
        }
        else if (e.getSource() == forwardButton_) {
            canvas_.moveInPath(1);
            boolean enable = (currentStep_ < getPath().size()-1);
            forwardButton_.setEnabled(enable);
            backButton_.setEnabled(true);
        }
    }
    
    /**
     * Private inner class for rendering the peg board.
     */
    private class PegBoardCanvas extends JPanel
    {
        /**
         * switch from the current move in the sequence forwards or backwards stepSize.
         * @param stepSize num steps to move.
         */
        public void moveInPath(int stepSize) {
            if (stepSize == 0) return;
            int inc = stepSize > 0 ? 1 : -1;
            int toStep = currentStep_ + stepSize;
            do {
                board_ = board_.doMove((PegMove)path_.get(currentStep_), (inc < 0));
                currentStep_ += inc;
            } while (currentStep_ != toStep);
            repaint();
        }

        /**
         * This renders the current state of the puzzle to the screen.
         */
        protected void paintComponent( Graphics g )
        {
            int i, xpos, ypos;

            super.paintComponents( g );
            // erase what's there and redraw.

            g.clearRect( 0, 0, this.getWidth(), this.getHeight() );
            g.setColor( new Color( 235, 235, 230 ) );
            g.fillRect( 0, 0, this.getWidth(), this.getHeight() );

            int size = PegBoard.SIZE;
            int rightEdgePos = LEFT_MARGIN + 3 * INC * size;
            int bottomEdgePos = TOP_MARGIN + 3 * INC * size;

            g.setColor( Color.black );
            drawStatus(g,  LEFT_MARGIN, TOP_MARGIN - 36 );     

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

                        boolean empty = board_.isEmpty(row, col);
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
}

