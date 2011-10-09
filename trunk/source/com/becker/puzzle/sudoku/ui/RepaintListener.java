package com.becker.puzzle.sudoku.ui;

import com.becker.common.geometry.Location;
import com.becker.puzzle.sudoku.model.board.Board;

import java.awt.*;

/**
 * Called when the user enters a value.
 * @author Barry Becker
 */
public interface RepaintListener {


    void valueEntered();

    void cellSelected(Location location);
}
