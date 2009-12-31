package com.becker.game.twoplayer.pente.test;

import com.becker.game.common.GameController;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.test.ISearchableHelper;
import com.becker.game.twoplayer.common.search.test.TwoPlayerSearchableBaseTst;
import com.becker.game.twoplayer.pente.PenteController;
import junit.framework.TestSuite;
import junit.framework.Test;


/**
 * Verify that all the methods in PenteSearchable work as expected
 * @author Barry Becker
 */
public class PenteSearchableTest extends TwoPlayerSearchableBaseTst {

    @Override
    protected ISearchableHelper createSearchableHelper() {
        return new PenteHelper();
    }

    /**
     * @return an initial move by player one.
     */
    @Override
    protected  TwoPlayerMove createInitialMove() {
        return  TwoPlayerMove.createMove(5, 5,   0, new GamePiece(true));
    }

    @Override
    public void testNotDoneMidGame() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void testDoneForMidGameWin() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void testDoneEndGame() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static Test suite() {
        return new TestSuite(PenteSearchableTest.class);
    }
}
