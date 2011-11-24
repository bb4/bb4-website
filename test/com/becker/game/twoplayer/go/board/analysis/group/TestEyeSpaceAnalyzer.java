// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.information.E1Information;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeStatus;
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

    private static final String PREFIX = "board/analysis/group/eyespace/";


    public void testEyePotential_SingleIsolatedStone() {

        restoreGame("single_isolated_stone");

        verifyBlackEyes(createEyeSet());
        verifyWhiteEyes(createEyeSet());
    }

    public void testEyePotential_SingleEye() {

        restoreGame("single_eye");

        IGoEye blackEye = new StubGoEye(true, EyeStatus.NAKADE, new E1Information(), "E1", 0, 0, false, 1);
        IGoEye whiteEye = new StubGoEye(false, EyeStatus.NAKADE, new E1Information(), "E1", 0, 1, false, 1);
        verifyBlackEyes(createEyeSet(blackEye));
        verifyWhiteEyes(createEyeSet(whiteEye));
    }


    private void restoreGame(String file) {
        restore(PREFIX + file);
    }

    private void verifyBlackEyes(GoEyeSet expectedEyes) {
        verifyEyePotential(true, expectedEyes);
    }

    private void verifyWhiteEyes(GoEyeSet expectedEyes) {
        verifyEyePotential(false, expectedEyes);
    }

    
    private GoEyeSet createEyeSet(IGoEye... eyes) {
        GoEyeSet eyeList = new GoEyeSet();
        eyeList.addAll(Arrays.asList(eyes));
        return eyeList;
    }

    /**
     * Use EyeSpaceAnalyzer to find eyes and match against the expected set of eyes.
     */
    private void verifyEyePotential(boolean forBlackGroup, GoEyeSet expectedEyes) {

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
