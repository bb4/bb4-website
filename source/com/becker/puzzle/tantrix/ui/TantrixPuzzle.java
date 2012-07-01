// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui;

import com.becker.puzzle.common.AlgorithmEnum;
import com.becker.puzzle.common.PuzzleController;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.common.ui.PuzzleApplet;
import com.becker.puzzle.common.ui.PuzzleViewer;
import com.becker.puzzle.tantrix.TantrixController;
import com.becker.puzzle.tantrix.model.HexTiles;
import com.becker.puzzle.tantrix.model.TantrixBoard;
import com.becker.puzzle.tantrix.model.TilePlacement;
import com.becker.puzzle.tantrix.solver.Algorithm;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Tantrix Puzzle Application to show the solving of the puzzle.
 *
 * @author Barry becker
 */
public final class TantrixPuzzle extends PuzzleApplet<TantrixBoard, TilePlacement>
                                 implements ChangeListener {
    JSpinner spinner;
    private static final int DEFAULT_NUM_TILES = 7;

    /**
     * Construct the application.
     */
    public TantrixPuzzle() {}

    @Override
    protected PuzzleViewer<TantrixBoard, TilePlacement> createViewer() {

        //TantrixBoard board = new TantrixBoard(new HexTiles());
        return new TantrixViewer();
    }

    @Override
    protected PuzzleController<TantrixBoard, TilePlacement>
                createController(Refreshable<TantrixBoard, TilePlacement> viewer) {
        TantrixController controller = new TantrixController(viewer);
        controller.setNumTiles(DEFAULT_NUM_TILES);
        return controller;
    }

    @Override
    protected AlgorithmEnum<TantrixBoard, TilePlacement>[] getAlgorithmValues() {
        return Algorithm.values();
    }

    @Override
    protected JPanel createCustomControls() {
        JLabel label = new JLabel("Number of Tiles");
        SpinnerModel model = new SpinnerNumberModel(DEFAULT_NUM_TILES, 3, 30, 1);
        spinner = new JSpinner(model);
        spinner.addChangeListener(this);

        JPanel numTilesSelector = new JPanel();
        numTilesSelector.add(label);
        numTilesSelector.add(spinner);

        return numTilesSelector;
    }

    public void stateChanged(ChangeEvent e) {

        getController().setNumTiles((Integer)spinner.getValue());
    }

    private TantrixController getController() {
        return ((TantrixController)controller_);
    }

    /**
     * use this to run as an application instead of an applet.
     */
    public static void main( String[] args )  {

        PuzzleApplet applet = new TantrixPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet(applet, "Tantrix Puzzle Solver");
    }
}