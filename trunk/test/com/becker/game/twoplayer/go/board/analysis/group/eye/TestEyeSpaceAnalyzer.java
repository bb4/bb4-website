// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.go.board.analysis.group.eye;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.information.*;
import com.becker.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.becker.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.becker.game.twoplayer.go.board.elements.eye.IGoEye;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import junit.framework.Assert;

import java.util.Arrays;

/**
 * Verify that we come up with reasonable eye potential values (likelihood of making eyes in the group).
 *
 * @author Barry Becker
 */
public class TestEyeSpaceAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/group/eye/eyespace/";


    public void testEyeSpace_SingleIsolatedStone() {

        restoreGame("single_isolated_stone");

        verifyBlackEyes(createEyeSet());
        verifyWhiteEyes(createEyeSet());
    }

    public void testEyeSpace_SingleEye() {

        restoreGame("single_eye");

        IGoEye blackEye = new StubGoEye(true, EyeStatus.NAKADE, new E1Information(), 0, 0, false, 1);
        IGoEye whiteEye = new StubGoEye(false, EyeStatus.NAKADE, new E1Information(), 0, 1, false, 1);
        verifyBlackEyes(createEyeSet(blackEye));
        verifyWhiteEyes(createEyeSet(whiteEye));
    }

    /**
     * For black eye we have:
     *   C
     *   CEC
     * and for white we have
     *   CEC
     * where C's are counted as being edges as well as corner.
     */
    public void testEyeSpace_EyesInCorner() {

        restoreGame("eyes_in_corner");

        IGoEye blackEye = new StubGoEye(true, EyeStatus.ALIVE, EyeType.E6.getInformation("E222233"), 3, 4, false, 6);
        IGoEye whiteEye = new StubGoEye(false, EyeStatus.UNSETTLED, EyeType.E3.getInformation("E112"), 2, 3, false, 3);
        verifyBlackEyes(createEyeSet(blackEye));
        verifyWhiteEyes(createEyeSet(whiteEye));
    }

    public void testEyeSpace_EyesInCornerComplex() {

        restoreGame("eyes_in_corner_complex");

        IGoEye blackEye = new StubGoEye(true, EyeStatus.ALIVE, EyeType.E7.getInformation("E1122222"), 3, 6, false, 7);
        IGoEye whiteEye = new StubGoEye(false, EyeStatus.ALIVE, new TerritorialEyeInformation(), 3, 5, false, 8);
        verifyBlackEyes(createEyeSet(blackEye));
        verifyWhiteEyes(createEyeSet(whiteEye));
    }



    private void restoreGame(String file) {
        restore(PREFIX + file);
    }

    private void verifyBlackEyes(GoEyeSet expectedEyes) {
        verifyEyes(true, expectedEyes);
    }

    private void verifyWhiteEyes(GoEyeSet expectedEyes) {
        verifyEyes(false, expectedEyes);
    }

    
    private GoEyeSet createEyeSet(IGoEye... eyes) {
        GoEyeSet eyeList = new GoEyeSet();
        eyeList.addAll(Arrays.asList(eyes));
        return eyeList;
    }

    /**
     * Use EyeSpaceAnalyzer to find eyes and match against the expected set of eyes.
     */
    private void verifyEyes(boolean forBlackGroup, GoEyeSet expectedEyes) {

        IGoGroup group = getBiggestGroup(forBlackGroup);
   
        EyeSpaceAnalyzer analyzer = new EyeSpaceAnalyzer(group, new GroupAnalyzerMap());
        analyzer.setBoard((GoBoard) controller_.getBoard());
        GoEyeSet eyes = analyzer.determineEyes();

        boolean matched = compareEyeSets(expectedEyes, eyes);
        if (!matched) {                      
            System.err.println("we expected: \n" + expectedEyes);
            System.err.println("but got: \n" + eyes);
        }
        assertTrue("The actual eyes did not match the expected.", matched);
    }

    /**
     * @return true if both sets of eyes are the same
     */
    private boolean compareEyeSets(GoEyeSet expEyes, GoEyeSet actEyes) {
        
        Assert.assertEquals("Unexpected number of eyes in group.", expEyes.size(), actEyes.size());
        
        // assuming the number of eyes match, check for 1-1 correspondence
        boolean allFound = true;
        for (IGoEye actEye : actEyes) {
            boolean found = false;
            for (IGoEye eye : expEyes)  {
                StubGoEye expEye = (StubGoEye) eye;
                if (expEye.isMatch(actEye)) {
                    found = true;
                    break;
                }   
            }
            if (!found) {
                allFound = false;
                break;
            }
        }
        return allFound;
    }

}
