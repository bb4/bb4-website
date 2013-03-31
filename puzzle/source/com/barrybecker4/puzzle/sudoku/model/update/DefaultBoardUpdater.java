/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.sudoku.model.update;

import com.barrybecker4.puzzle.sudoku.model.update.updaters.BigCellScoutUpdater;
import com.barrybecker4.puzzle.sudoku.model.update.updaters.LoneRangerUpdater;
import com.barrybecker4.puzzle.sudoku.model.update.updaters.NakedSubsetUpdater;
import com.barrybecker4.puzzle.sudoku.model.update.updaters.StandardCRBUpdater;

import java.util.Arrays;

/**
 * Default board updater applies all the standard updaters.
 *
 * @author Barry Becker
 */
public class DefaultBoardUpdater extends ReflectiveBoardUpdater {

    private static final Class[] UPDATERS =  {
         StandardCRBUpdater.class,
         LoneRangerUpdater.class,
         BigCellScoutUpdater.class,
         NakedSubsetUpdater.class
    };

    /** Constructor */
    public DefaultBoardUpdater() {

        super(Arrays.asList(UPDATERS));
    }
}
