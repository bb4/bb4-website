/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.sudoku.ui;

import com.becker.common.geometry.Location;

import java.util.HashMap;

/**
 * A map of user entered values.
 * @author Barry Becker
 */
public class UserEnteredValues extends HashMap<Location, UserValue> {


    public UserEnteredValues() {
    }

    public UserValue get(int row, int col)  {
        return get(new Location(row, col));
    }
}
