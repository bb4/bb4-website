/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.hiq;

import com.becker.puzzle.common.PuzzleViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *  UI for drawing the current best solution to the puzzle.
 *  @author Barry Becker
 */
final class PegBoardViewer extends PuzzleViewer<PegBoard, PegMove>
                                             implements ActionListener
{
    private static final long serialVersionUID = 1L;

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
        int size =  board_.getSize() * PegBoardRenderer. INC;
        canvas_.setPreferredSize(new Dimension(size, size));
        
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
    
    @Override
    public void refresh(PegBoard board, long numTries) {         
        if (numTries % 6000 == 0) {
            status_ = createStatusMessage(numTries);
            refresh1(board, numTries);  
        }
    }
    
    @Override
    public void finalRefresh(List<PegMove> path, PegBoard board, long numTries, long millis) {
        super.finalRefresh(path, board, numTries, millis);
        showPath(path, board);
    }
   
    public void makeSound() {
        // add sound
    }

    public void showPath(List<PegMove> path, PegBoard board) {
        
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
        private static final long serialVersionUID = 1L;

        private PegBoardRenderer renderer_ =  new PegBoardRenderer();
        /**
         * switch from the current move in the sequence forwards or backwards stepSize.
         * @param stepSize num steps to move.
         */
        public void moveInPath(int stepSize) {
            if (stepSize == 0) return;
            int inc = stepSize > 0 ? 1 : -1;
            int toStep = currentStep_ + stepSize;
            do {
                board_ = board_.doMove(path_.get(currentStep_), (inc < 0));
                currentStep_ += inc;
            } while (currentStep_ != toStep);
            repaint();
        }

        /**
         * This renders the current state of the puzzle to the screen.
         */
        @Override
        protected void paintComponent( Graphics g )
        {
            super.paintComponents( g );

            renderer_.render(g, board_, status_, getWidth(), getHeight());
        }
    }
}

