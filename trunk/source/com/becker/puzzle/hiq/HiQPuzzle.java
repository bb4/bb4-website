package com.becker.puzzle.hiq;

import com.becker.puzzle.common.Refreshable;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * HiQ Puzzle.
 * This program solves a very difficult classic solitaire puzzle
 * where you jump pegs to remove them in a cross shaped peg-board.
 * The fewer pegs you have remaining at the end, the better.
 * A perfect solution is to have only one peg in the center square
 * at the end.
 *  Assuming an average of 7 different options on each move, there are
 *   7 ^ 32 = 10,000,000,000,000,000,000,000,000,000,000
 *  (10 decillion combinations)
 * Actually this calculation is not correct since many paths lead
 * to the same board positions.
 * There are actually only 23.4 million unique board positions
 * see http://www.durangobill.com/Peg33.html for analysis.
 *    A brute force solution will run for years on todays fastest
 * computers.
 * See http://homepage.sunrise.ch/homepage/pglaus/Solitaire/solitaire.htm
 * for a solution that uses a genetic algorithm to find a solution quickly.
 *
 * My initial approach was to apply a kind of tunnel method.
 * I tried to solve the problem from both ends.
 * First, I work backwards for 32-FORWARD_MOVE's and build a
 * hashmap of all the possible board positions - storing a path to the solution
 * at each hashmap location. Then I traverse forward from the initial position
 * for BACK_MOVE's until I reach one of these positions that I know
 * leads to the solution. Then I combined the 2 paths to see the sequence
 * that will lead to the solution.
 *   Finally, I found that it was enough to search entirely from the beginning
 * and just prune when I reach states I've encountered before.
 *When I first ran this successfully, it took about 1 hour to run on an AMD 64bit 3200.
 *After optimization it now run in about 3 minutes on a Core2Duo (189 seconds).
 *After parallelizing the algorithm using ConcurrrentPuzzleSolver it is down to 93 seconds on the CoreDuo.
 */
public final class HiQPuzzle extends JApplet implements ActionListener, Refreshable<PegBoard, PegMove>
{

    // create the pieces and add them to a list
    private HiQController controller_;
    private PegBoardViewer pegBoardViewer_;

    private JButton backButton_;
    private JButton forwardButton_;
 

    /**
     * Construct the application
     */
    public HiQPuzzle() {
        GUIUtil.setCustomLookAndFeel();
    }

    /**
     * create and initialize the puzzle
     * (init required for applet)
     */
    public void init() {

        controller_ = new HiQController(this);        
        
        pegBoardViewer_ = new PegBoardViewer(PegBoard.INITIAL_BOARD_POSITION);
        
        JLabel title = new JLabel("32 PegMove Solitaire (HiQ)");
        backButton_ = new JButton("Back");
        forwardButton_ = new JButton("Forward");
        backButton_.addActionListener(this);
        forwardButton_.addActionListener(this);
        backButton_.setEnabled(false);
        forwardButton_.setEnabled(false);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(backButton_, BorderLayout.WEST);
        buttonPanel.add(forwardButton_, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(pegBoardViewer_, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);

        this.setVisible(true);
    }


    /**
     * start solving the puzzle in a sepearate thread so the panel has a chance to refresh.
     * called by the browser after init(), if running as an applet
     */
    public void start() {
        controller_.startSolving();        
    }

    /**
     * stop and cleanup.
     */
    public void stop() {
    }
    

   private static void printPath(java.util.List<PegMove> path) {
       System.out.println("Path:");
       for (PegMove m : path) {
           System.out.println(m + ", ");
       }
       System.out.flush();
   }

    public void refresh(PegBoard board, long numTries) {   
        if (numTries % 6000 == 0)
        pegBoardViewer_.setNumTries(numTries);
        pegBoardViewer_.setBoard(board);
        pegBoardViewer_.repaint();
        //System.out.println("num pegs left ="+board.getNumPegsLeft() + " " + numTries );
    }
    
    public void finalRefresh(java.util.List<PegMove> path, PegBoard board, long numTries) { 
        refresh(board, numTries);
        showPath(path, board, numTries);                 
    }

    public void showPath(java.util.List<PegMove> path, PegBoard board, long numTries) {
        System.out.println();
        //printPath(path);        
        java.util.List<PegMove> p = new LinkedList<PegMove>();
        p.addAll(path);
        pegBoardViewer_.showPath(p, new PegBoard(board), numTries);
        backButton_.setEnabled(true);
        forwardButton_.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton_) {
            pegBoardViewer_.moveInPath(-1);
            backButton_.setEnabled((pegBoardViewer_.getCurrentStep() > 0));
            forwardButton_.setEnabled(true);
        }
        else if (e.getSource() == forwardButton_) {
            pegBoardViewer_.moveInPath(1);
            boolean enable = (pegBoardViewer_.getCurrentStep() < pegBoardViewer_.getPath().size()-1);
            forwardButton_.setEnabled(enable);
            backButton_.setEnabled(true);
        }
    }

    //------ Main method --------------------------------------------------------
    /**
     *use this to run as an application instead of an applet
     */
    public static void main(String[] args) {

        HiQPuzzle applet = new HiQPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet(applet, "HiQ Puzzle Solver");
    }
}

