package com.becker.game.twoplayer.blockade.test;

import junit.framework.*;
import com.becker.game.twoplayer.blockade.*;
import com.becker.game.common.*;

/**
 * @author Barry Becker Date: Mar 3, 2007
 */
public class TestBlockadeBoard extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }


    public void testPositionStates() {

        BlockadeBoardPosition p = new BlockadeBoardPosition(1, 1, null, null, null, false, false);

        Assert.assertTrue("no piece or walls", p.getStateIndex() == 0);
        p.setPiece(new GamePiece(true));

        Assert.assertTrue("p1 piece and no walls", p.getStateIndex() == 1);
        p.setEastWall(new BlockadeWall(true));

        System.out.println(p.getStateIndex());
        Assert.assertTrue("p1 piece and east wall", p.getStateIndex() == 3);

        p.setSouthWall(new BlockadeWall(false));
        Assert.assertTrue("p1 piece and both walls", p.getStateIndex() == 7);

        p.setPiece(new GamePiece(false));
        Assert.assertTrue("p2 piece and both wals", p.getStateIndex() == 11);
    }
    

}
