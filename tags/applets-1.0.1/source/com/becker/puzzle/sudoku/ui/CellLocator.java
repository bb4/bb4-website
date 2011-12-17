/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.sudoku.ui;

import com.becker.common.geometry.Location;
import com.becker.puzzle.sudoku.model.board.Board;

import java.awt.*;

/**
 * Locates cell coordinates given a point location on the screen.
 * @author Barry Becker
 */
public interface CellLocator {

    Board getBoard();

    Location getCellCoordinates(Point point);
}
