package com.becker.puzzle.sudoku;

import com.becker.ui.components.GradientButton;
import com.becker.common.Worker;
import com.becker.puzzle.sudoku.test.*;
import com.becker.ui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Sudoku Puzzle UI.
 * This program solves a Sudoku puzzle.
 * Its difficult to solve by hand because of all the possible permutations.
 *
 * @author Barry becker
 */
public final class SudokuPuzzle extends JApplet
                                implements ActionListener, ItemListener
{

    private SudokuPanel puzzlePanel_;
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
    @Override
    public void init() {
        puzzlePanel_ = new SudokuPanel(Data.SAMPLE1);

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(createButtonPanel(), BorderLayout.NORTH);
        panel.add(puzzlePanel_, BorderLayout.CENTER);
        getContentPane().add( panel);
    }

    /**
     * start solving the puzzle.
     * called by the browser after init(), if running as an applet
     */
    @Override
    public void start() {
        puzzlePanel_.setSize(this.getSize());
    }

    /**
     * stop and cleanup.
     */
    @Override
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

                @Override
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

                @Override
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
        SudokuGenerator generator = new SudokuGenerator(size);
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
