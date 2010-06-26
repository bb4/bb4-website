package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.GoTestCase;

import com.becker.game.twoplayer.go.board.*;

import java.util.*;


/**
 * @author Barry Becker
 */
public class TestEyeNeighborMap extends GoTestCase {

    private EyeNeighborMap nbrMap;

    private static final Location[] RABBITY_SIX =  new Location[] {
            new Location(4, 2), new Location(3, 2), new Location(2, 3),
            new Location(3, 3), new Location(4, 3), new Location(3, 4)
    };

    public void testSingleEyeSpace() {

        IGoString eye = new StubGoEye(createPositionList(new Location[] {new Location(2, 2)}));
        nbrMap = new EyeNeighborMap(eye);

        assertEquals("unexpected size", 1, nbrMap.keySet().size());
    }

    public void testTwoSpaceEye() {

        Location[] positions = new Location[] {new Location(2, 2), new Location(2, 3)};
        IGoString eye = new StubGoEye(createPositionList(positions));
        nbrMap = new EyeNeighborMap(eye);

        assertEquals("unexpected size", 2, nbrMap.keySet().size());
    }

    /**
     * Negative case
     */
    public void testDisjointEye() {

        Location[] positions = new Location[] {new Location(2, 2), new Location(3, 3)};
        IGoString eye = new StubGoEye(createPositionList(positions));
        try{
            nbrMap = new EyeNeighborMap(eye);
            fail();
        }
        catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testThreeSpaceEye() {

        Location[] positions = new Location[] {new Location(2, 2), new Location(2, 3), new Location(3, 3)};
        List<GoBoardPosition> spaces = createPositionList(positions);
        IGoString eye = new StubGoEye(spaces);
        nbrMap = new EyeNeighborMap(eye);

        assertEquals("unexpected size", 3, nbrMap.keySet().size());
        verifyNumEyeNbrs( new int[]{1, 2, 1}, spaces);
    }


    public void testComplexEye() {

        List<GoBoardPosition> spaces = createPositionList(RABBITY_SIX);
        IGoString eye = new StubGoEye(spaces);
        nbrMap = new EyeNeighborMap(eye);

        assertEquals("unexpected size", 6, nbrMap.keySet().size());

        verifyNumEyeNbrs( new int[]{2, 2, 1, 4, 2, 1}, spaces); 
    }


    public void testComplexEye1() {

        List<GoBoardPosition> spaces = createPositionList(RABBITY_SIX);
        IGoString eye = new StubGoEye(spaces);
        nbrMap = new EyeNeighborMap(eye);

        // we expect 3,3 and 4,2 to be special points.
        float[] specialPoints = new float[] {4.06f, 2.04f};

        assertTrue(nbrMap.isSpecialPoint(spaces.get(0), specialPoints));
        assertFalse(nbrMap.isSpecialPoint(spaces.get(1), specialPoints));
        assertFalse(nbrMap.isSpecialPoint(spaces.get(2), specialPoints));
        assertTrue(nbrMap.isSpecialPoint(spaces.get(3), specialPoints));

        verifyNumEyeNbrs( new int[]{2, 2, 1, 4, 2, 1}, spaces);
    }


    private void verifyNumEyeNbrs(int[] expectedNumNbrs, List<GoBoardPosition> spaces) {

        for (int i=0; i<expectedNumNbrs.length; i++) {
            assertEquals("Unexpected number of neighbors for position " + i,
                expectedNumNbrs[i], nbrMap.getNumEyeNeighbors(spaces.get(i)));
        }
    }

    private List<GoBoardPosition> createPositionList(Location[] positions) {

        List<GoBoardPosition> spaces = new ArrayList<GoBoardPosition>();
        for (Location pos : positions) {
            spaces.add(new GoBoardPosition(pos.getRow(), pos.getCol(), null, null));
        }
        return spaces;
    }

}
