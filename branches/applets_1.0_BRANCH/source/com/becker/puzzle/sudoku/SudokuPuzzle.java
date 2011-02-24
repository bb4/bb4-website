package com.becker.puzzle.sudoku;

import com.becker.common.concurrency.Worker;
import com.becker.ui.GUIUtil;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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

    private SizeSelector sizeSelector_;
    private SpeedSelector speedSelector_;


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
        getContentPane().add(panel);
    }

    /**
     * start solving the puzzle.
     * called by the browser after init(), if running as an applet
     */
    @Override
    public void start() {
        System.out.println("in Sudoku start");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getContentPane().repaint();
            }
        });
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
        sizeSelector_ = new SizeSelector();
        speedSelector_ = new SpeedSelector();
        sizeSelector_.addItemListener(this);
        speedSelector_.addItemListener(this);
        panel.add(sizeSelector_);
        panel.add(speedSelector_);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    /**
     * Must execute long tasks in a separate thread,
     * otherwise you don't see the steps of the animation.
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        Object src = e.getSource();

        if (src == generateButton_)  {
            generatePuzzle(speedSelector_.getSelectedDelay());
        }
        else if (src == solveButton_)  {
            solvePuzzle();
        }
    }

    private void generatePuzzle(final int delay) {
        Worker worker = new Worker() {

            public Object construct() {
                puzzlePanel_.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                puzzlePanel_.setDelay(delay);
                puzzlePanel_.generateNewPuzzle(sizeSelector_.getSelectedSize());
                return null;
            }

            public void finished() {
                puzzlePanel_.repaint();
                puzzlePanel_.setCursor(Cursor.getDefaultCursor());
            }
        };
        worker.start();
        solveButton_.setEnabled(true);
    }

    private void solvePuzzle() {
        Worker worker = new Worker() {

            public Object construct() {
                puzzlePanel_.setDelay(speedSelector_.getSelectedDelay());
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


    /**
     * size choice selected.
     * @param e  item event.
     */
    public void itemStateChanged(ItemEvent e) {

        if (e.getSource() == sizeSelector_)  {
            generatePuzzle(10);
        }
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
