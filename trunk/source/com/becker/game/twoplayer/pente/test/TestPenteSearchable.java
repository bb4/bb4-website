package com.becker.game.twoplayer.pente.test;

import com.becker.game.common.GameController;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.test.TwoPlayerSearchableBaseTst;
import com.becker.game.twoplayer.pente.PenteController;
import junit.framework.TestSuite;
import junit.framework.Test;


/**
 * Verify that all the methods in PenteSearchable work as expected
 * @author Barry Becker
 */
public class TestPenteSearchable extends TwoPlayerSearchableBaseTst {


    /**
     * Create the controller containing the searchable to test.
     */
    @Override
    protected  GameController createController() {
        return new PenteController(10, 10);
    }

    @Override
    protected String getTestCaseDir() {
        return EXTERNAL_TEST_CASE_DIR + "pente/cases/searchable/";
    }

    /**
     * Create the game options
     */
    @Override
    protected TwoPlayerOptions createTwoPlayerGameOptions() {
        return new TwoPlayerOptions();
    }

    /**
     * @return an initial move by player one.
     */
    @Override
    protected  TwoPlayerMove createInitialMove() {
        return  TwoPlayerMove.createMove(5, 5,   0, new GamePiece(true));
    }

    public static Test suite() {
        return new TestSuite(TestPenteSearchable.class);
    }
}
