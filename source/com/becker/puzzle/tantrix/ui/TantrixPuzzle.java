// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui;

import com.becker.puzzle.common.*;
import com.becker.puzzle.tantrix.Algorithm;
import com.becker.puzzle.tantrix.TantrixController;
import com.becker.puzzle.tantrix.model.TantrixBoard;
import com.becker.puzzle.tantrix.model.HexTile;
import com.becker.puzzle.tantrix.model.HexTileList;
import com.becker.puzzle.tantrix.model.HexTiles;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;

/**
 * Tantrix Puzzle Application to show the solving of the puzzle.
 *
 * @author Barry becker
 */
public final class TantrixPuzzle extends PuzzleApplet<HexTileList, HexTile> {

    /**
     * Construct the application.
     */
    public TantrixPuzzle() {}

    @Override
    protected PuzzleViewer<HexTileList, HexTile> createViewer() {

        TantrixBoard board = new TantrixBoard(new HexTiles());
        return new TantrixPanel(board);
    }

    @Override
    protected PuzzleController<HexTileList, HexTile> createController(Refreshable<HexTileList, HexTile> viewer) {
        return new TantrixController(viewer);
    }
    
    @Override
    protected AlgorithmEnum<HexTileList, HexTile>[] getAlgorithmValues() {
        return Algorithm.values();
    }
    
    @Override
    protected JPanel createCustomControls() {

        return new JPanel();
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
