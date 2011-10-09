package com.becker.puzzle.sudoku.model.update;

import com.becker.puzzle.sudoku.model.update.updaters.*;

import java.util.Arrays;

/**
 * Default board updater applies all the standard updaters.
 *
 * @author Barry Becker
 */
public class DefaultBoardUpdater extends BoardUpdater {

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
