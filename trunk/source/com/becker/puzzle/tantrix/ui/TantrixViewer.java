// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui;

import com.becker.puzzle.common.PuzzleViewer;
import com.becker.puzzle.tantrix.model.TantrixBoard;
import com.becker.puzzle.tantrix.model.TilePlacement;
import com.becker.puzzle.tantrix.ui.rendering.TantrixBoardRenderer;
import com.becker.sound.MusicMaker;

import java.awt.*;
import java.util.List;

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
    public TantrixViewer() {}

    public TantrixBoard getBoard() {
        return renderer_.getBoard();
    }

    /**
     *  This renders the current state of the PuzzlePanel to the screen.
     *  This method is part of the component interface.
     */
    @Override
    protected void paintComponent( Graphics g ) {

        super.paintComponents( g );
        if (board_ != null)  {
            renderer_ = new TantrixBoardRenderer(board_);
            renderer_.render(g, getWidth(), getHeight());
            // without this we do not get key events.
            requestFocus();
        }
    }


    @Override
    public void refresh(TantrixBoard board, long numTries) {
        if (numTries % 1 == 0) {
            status_ = createStatusMessage(numTries);
            makeASound(board.getUnplacedTiles().size());
            simpleRefresh(board, numTries);
        }
    }

    public void makeSound() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * make a little click noise when the piece fits into place.
     */
    private void makeASound(int num) {
        musicMaker_.playNote(num * 20, 20, 640);
    }
}

