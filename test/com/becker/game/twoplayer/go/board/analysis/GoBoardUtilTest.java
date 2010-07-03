package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoStone;
import com.becker.game.twoplayer.go.board.IGoGroup;
import com.becker.game.twoplayer.go.board.analysis.group.StubGoGroup;

/**
 * @author Barry Becker
 */
public class GoBoardUtilTest extends GoTestCase {

    public void testDeadWhiteStoneInLiveBlackGroupIsMuchWeaker() {

        // a black group that is mostly alive
        IGoGroup group = new StubGoGroup(0.6f, true, 4);
        // a white stone that is mostly dead.
        GoStone stone = new GoStone(false, 0.4f);

        assertTrue(GoBoardUtil.isStoneMuchWeaker(group, stone));
    }

    public void testSemiDeadWhiteStoneInMostlyLiveBlackGroupIsMuchWeaker() {

        IGoGroup group = new StubGoGroup(0.5f, true, 4);
        GoStone stone = new GoStone(false, 0.2f);
        assertTrue(GoBoardUtil.isStoneMuchWeaker(group, stone));
    }

    public void testSemiLiveWhiteStoneInMostlyLiveBlackGroupIsNotMuchWeaker() {

        IGoGroup group = new StubGoGroup(0.5f, true, 4);
        GoStone stone = new GoStone(false, -0.2f);
        assertFalse(GoBoardUtil.isStoneMuchWeaker(group, stone));
    }


    public void testDeadBlackStoneInLiveWhiteGroupIsMuchWeaker() {

        IGoGroup group = new StubGoGroup(-0.6f, false, 4);
        GoStone stone = new GoStone(true, -0.4f);
        assertTrue(GoBoardUtil.isStoneMuchWeaker(group, stone));
    }

    public void testSemiDeadBlackStoneInMostlyLiveWhiteGroupIsMuchWeaker() {

        IGoGroup group = new StubGoGroup(-0.5f, false, 4);
        GoStone stone = new GoStone(true, -0.2f);
        assertTrue(GoBoardUtil.isStoneMuchWeaker(group, stone));
    }

    public void testSemiLiveBlackStoneInMostlyLiveWhiteGroupIsNotMuchWeaker() {

        IGoGroup group = new StubGoGroup(-0.5f, false, 4);
        GoStone stone = new GoStone(true, 0.2f);
        assertFalse(GoBoardUtil.isStoneMuchWeaker(group, stone));
    }
}
