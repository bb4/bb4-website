package com.becker.game.twoplayer.pente.test;

import com.becker.game.common.GameController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.test.TwoPlayerSearchableBaseTst;
import com.becker.game.twoplayer.pente.PenteController;
import junit.framework.TestSuite;
import junit.framework.Test;


/**
 * Verify that all the methods in GoBaord work as expected
 * @author Barry Becker
 */
public class TestPenteSearchable extends TwoPlayerSearchableBaseTst {


    /**
     * Create the controller containing the searchable to test.
     */
    protected  GameController createController() {
        return new PenteController(10, 10);
    }

    /**
     * Create the game options
     */
    protected TwoPlayerOptions createTwoPlayerGameOptions() {
        return new TwoPlayerOptions();
    }

    public static Test suite() {
        return new TestSuite(TestPenteSearchable.class);
    }
}
