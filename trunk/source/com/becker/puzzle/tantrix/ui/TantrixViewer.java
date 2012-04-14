// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui;

import com.becker.puzzle.common.ui.PuzzleViewer;
import com.becker.puzzle.tantrix.model.TantrixBoard;
import com.becker.puzzle.tantrix.model.TilePlacement;
import com.becker.puzzle.tantrix.ui.rendering.TantrixBoardRenderer;
import com.becker.sound.MusicMaker;

import java.awt.*;

/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public final class TantrixViewer extends PuzzleViewer<TantrixBoard, TilePlacement> {

    private TantrixBoardRenderer renderer_;

    /** play a sound effect when a piece goes into place.  */
    private MusicMaker musicMaker_ = new MusicMaker();

    /**
     * Constructor.
     */
    public TantrixViewer() {
        renderer_ = new TantrixBoardRenderer();
    }

    public TantrixBoard getBoard() {
        return board_;
    }

    /**
     *  This renders the current state of the PuzzlePanel to the screen.
     *  This method is part of the component interface.
     */
    @Override
    protected void paintComponent( Graphics g ) {

        super.paintComponent(g);
        renderer_.render(g, board_, getWidth(), getHeight());
    }


    @Override
    public void refresh(TantrixBoard board, long numTries) {
        if (numTries % 1 == 0) {
            status_ = createStatusMessage(numTries);
            simpleRefresh(board, numTries);
            //ThreadUtil.sleep(100);
        }
    }

    public void makeSound() {
        int note = Math.min(127, 20 + getBoard().getUnplacedTiles().size() * 12);
        musicMaker_.playNote(note * 20, 20, 640);
    }
}

