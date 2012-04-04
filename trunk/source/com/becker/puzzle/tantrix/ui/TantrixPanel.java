// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui;

import com.becker.puzzle.common.PuzzleViewer;
import com.becker.puzzle.tantrix.model.TantrixBoard;
import com.becker.puzzle.tantrix.model.HexTileList;
import com.becker.puzzle.tantrix.model.TilePlacement;
import com.becker.puzzle.tantrix.ui.rendering.TantrixBoardRenderer;

import java.awt.*;

/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public final class TantrixPanel extends PuzzleViewer<TantrixBoard, TilePlacement> {

    private TantrixBoardRenderer renderer_;

    /**
     * Constructor.
     */
    public TantrixPanel(TantrixBoard b) {
        renderer_ = new TantrixBoardRenderer(b);
    }

    public void setBoard(TantrixBoard b) {
        renderer_.setBoard(b);
    }


    private TantrixBoard getSolvedPuzzle()  {

        return renderer_.getBoard();
    }

    /**
     * reset to new puzzle with specified initial data.
     * @param initialTiles tiles to use when solving.
     */
    public void reset(HexTileList initialTiles) {
        renderer_.setBoard(new TantrixBoard(initialTiles));
        repaint();
    }


    private void showMessage(boolean solved) {
        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:");
                 //   + getBoard().getNumIterations() );
        else
            System.out.println("This puzzle is not solvable!");
    }

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
        renderer_.render(g, getWidth(), getHeight());
        // without this we do not get key events.
        requestFocus();
    }

    public void makeSound() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

