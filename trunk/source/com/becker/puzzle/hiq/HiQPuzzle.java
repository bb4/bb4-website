package com.becker.puzzle.hiq;

import com.becker.ui.GUIUtil;

import javax.swing.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * HiQ Puzzle.
 * This program soles a very difficult classic solitaire puzzle
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
 */
public final class HiQPuzzle extends JApplet implements ActionListener
{
    private List<PegMove> path_;

    // create the pieces and add them to a list
    private PegBoard board_;
    private PegBoardViewer pegBoardViewer_;

    JButton backButton_;
    JButton forwardButton_;
    JLabel title_;

    Set visited_;

    private static JFrame baseFrame_ = null;

    // global counter;
    private static long numIterations_ = 0;

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

        board_ = new PegBoard();
        pegBoardViewer_ = new PegBoardViewer(board_);

        commonInit();

        title_ = new JLabel("32 PegMove Solitaire (HiQ)");
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
        mainPanel.add(title_, BorderLayout.NORTH);
        mainPanel.add(pegBoardViewer_, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);

        this.setVisible(true);
        pegBoardViewer_.repaint();
    }

    private void commonInit() {
        numIterations_ = 0;
        path_ = new LinkedList<PegMove>();
        visited_ = new HashSet<BoardHashKey>(10000);
    }

    /**
     * start solving the puzzle.
     * called by the browser after init(), if running as an applet
     */
    public void start() {

        board_.setToInitialState();
        path_.add(board_.getFirstMove());
        refresh();

        // this does all the heavy work of solving it.
        boolean solved = solvePuzzle(board_, path_);
        System.out.println("solved = "+solved);
    }

    /**
     * stop and cleanup.
     */
    public void stop() {
    }



   private static void printPath(List<PegMove> path) {
       for (PegMove m : path) {
           System.out.println(m + ", ");
       }
   }


   private boolean solvePuzzle(PegBoard board, List<PegMove> path) {
        List<PegMove> moves = board.generateMoves();

        if (board.isSolved()) {
            List<PegMove> finalPath = new LinkedList<PegMove>();
            finalPath.addAll(path);
            showPath(path, board);
            refresh();
            return true;
        }

        boolean solved = false;
        while (moves.size() > 0 && !solved) {
            PegMove move = moves.remove(0);
            path.add(move);
            board.makeMove(move);
            BoardHashKey key = board.hashKey();
            // if we arrived at the board state before (or one of its symmetries), prune the search.
            boolean visited = false;
            for (int i = 0; i < 8; i++) {
                if (visited_.contains(key.symmetry(i))) {
                    visited = true;
                    break;
                }
            }
            numIterations_++;

            if (!visited) {
                visited_.add(key);
                if (visited_.size() % 100000 == 0)
                    System.out.println("visited size = "+ visited_.size() + " path size = "+ path.size() + " num iterations = " + numIterations_);
                //pegBoardViewer_.setNumTries(numIterations_);
                solved = solvePuzzle(board, path);
            } else {
                //System.out.println("already visited " + key + " pruned at " + path.size() + " steps.");
                PegMove last = path.remove(path.size() - 1);
                board_.undoMove(last);
                pegBoardViewer_.setNumTries(numIterations_);
                refresh();
            }
        }
        // undo the last move and return
        PegMove last = (PegMove) path.remove(path.size() - 1);
        board_.undoMove(last);
        return solved;
    }

    private void refresh() {
        pegBoardViewer_.invalidate();
        pegBoardViewer_.revalidate();
        pegBoardViewer_.repaint();
    }

    public void showPath(List<PegMove> path, PegBoard board) {
        System.out.println();
        printPath(path);
        List<PegMove> p = new LinkedList<PegMove>();
        p.addAll(path);
        pegBoardViewer_.showPath(p, board.copy(), numIterations_);
        backButton_.setEnabled(true);
        forwardButton_.setEnabled(false);
    }

    private static void pause() {
        try {
            System.in.read(); // pause till keypressed
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        baseFrame_ = GUIUtil.showApplet(applet, "HiQ Puzzle Solver");
    }
}

