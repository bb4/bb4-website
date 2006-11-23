package com.becker.puzzle.sudoku;

import com.becker.common.*;
import com.becker.puzzle.sudoku.test.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Sudoku Puzzle UI.
 * This program solves a Sudoku puzzle.
 * Its difficult to solve by hand because of all the possible permutations.
 *
 * @author Barry becker
 */
public final class SudokuPuzzle extends JApplet implements ActionListener, ItemListener
{

    private PuzzlePanel puzzlePanel_;
    // buttons
    private JButton generateButton_;
    private JButton solveButton_;
    // size dropdown
    private Choice sizeChoice_;
    private String[] boardSizeMenuItems_ = {
        "4 cells on a side",
        "9 cells on a side",
        "16 cells on a side",
        "25 cells (prepare to wait)"
    };

    /**
     * Construct the application and set the look and feel.
     */
    public SudokuPuzzle() {
        GUIUtil.setCustomLookAndFeel();
    }

    /**
     * create and initialize the puzzle
     * (init required for applet)
     */
    public void init() {
        puzzlePanel_ = new PuzzlePanel(Data.SAMPLE1);

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(createButtonPanel(), BorderLayout.NORTH);
        panel.add(puzzlePanel_, BorderLayout.CENTER);
        getContentPane().add( panel);
    }

    /**
     * start solving the puzzle.
     * called by the browser after init(), if running as an applet
     */
    public void start() {
        puzzlePanel_.setSize(this.getSize());
        //puzzlePanel_.repaint();
    }

    /**
     * stop and cleanup.
     */
    public void stop() {}

    /**
     * solve and generate button at the top.
     */
    public JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        generateButton_ = new GradientButton("Generate");
        generateButton_.addActionListener(this);
        solveButton_ = new GradientButton("Solve");
        solveButton_.addActionListener(this);

        panel.add(generateButton_);
        panel.add(solveButton_);
        panel.add(createSizeDropdown());
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    private Choice createSizeDropdown() {
        sizeChoice_ = new Choice();
        sizeChoice_.addItemListener(this);
        for (final String item : boardSizeMenuItems_) {
            sizeChoice_.add(item);
        }
        sizeChoice_.select(1);
        return sizeChoice_;
    }


    public void actionPerformed(ActionEvent e) {

        // must execute long tasks in a separate thread,
        // otherwise you don't see the steps of the animation.
        Worker worker = null;
        Object src = e.getSource();

        if (src == generateButton_)  {
            worker = new Worker() {

                public Object construct() {
                    puzzlePanel_.generateNewPuzzle();
                    return null;
                }

                public void finished() {
                    puzzlePanel_.repaint();
                }
            };
            worker.start();
            solveButton_.setEnabled(true);
        }
        else if (src == solveButton_)  {

            worker = new Worker() {

                public Object construct() {
                    puzzlePanel_.startSolving();
                    return null;
                }

                public void finished() {
                    puzzlePanel_.repaint();
                }

            };
            worker.start();
            solveButton_.setEnabled(false);
        }

    }

    /**
     * size choice selected.
     * @param e  item event.
     */
    public void itemStateChanged(ItemEvent e) {

        int selected = sizeChoice_.getSelectedIndex();

        // this formula must change if the menu items change.
        int size = (selected + 2) ;
        System.out.println("selected = "+ selected+" size ="+size);
        PuzzleGenerator generator = new PuzzleGenerator(size);
        Board b = generator.generatePuzzleBoard(null);
        puzzlePanel_.setBoard(b);
        puzzlePanel_.repaint();
    }


    /**
     * use this to run as an application instead of an applet.
     */
    public static void main( String[] args )  {

        SudokuPuzzle applet = new SudokuPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet( applet, "Sudoku Puzzle Solver" );
    }
}
