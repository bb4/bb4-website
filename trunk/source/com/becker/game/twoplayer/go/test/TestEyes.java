package com.becker.game.twoplayer.go.test;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoBoard;
import com.becker.game.twoplayer.go.GoEye;
import com.becker.game.twoplayer.go.GoGroup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Set;


/**
 * @author Barry Becker
 */
public class TestEyes extends GoTestCase {


    // simple eye tests
    public void testEyes1() {
        EyeCounts blackEyes = new EyeCounts(0, 2, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_eyes1", blackEyes, whiteEyes);
    }

    public void testEyes2() {
        EyeCounts blackEyes = new EyeCounts(0, 2, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 1, 0);
        checkEyes("problem_eyes2", blackEyes, whiteEyes);
    }

    public void testFalseEye1() {
        EyeCounts blackEyes = new EyeCounts(1, 1, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_eyes3", blackEyes, whiteEyes);
    }

    public void testCornerLife() {
        EyeCounts blackEyes = new EyeCounts(0, 2, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 0);
        checkEyes("problem_eyes4", blackEyes, whiteEyes);
    }

     public void testEyes5() {
        EyeCounts blackEyes = new EyeCounts(0, 1, 0, 1);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 0);
        checkEyes("problem_eyes5", blackEyes, whiteEyes);
    }


    public void testFalsesOnEdge() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(3, 0, 0, 0);
        checkEyes("problem_falseeyes_on_edge", blackEyes, whiteEyes);
    }

    public void testStoneInEye1() {
        EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_stone_in_eye1", blackEyes, whiteEyes);
    }

    public void testStoneInEye2() {
        EyeCounts blackEyes = new EyeCounts(0, 1, 0, 1);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 0);
        checkEyes("problem_stone_in_eye2", blackEyes, whiteEyes);
    }

    public void testStoneInEye3() {
          EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
          EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 0);
          checkEyes("problem_stone_in_eye3", blackEyes, whiteEyes);
    }

    /*
    public void testStoneInEye4() {
          EyeCounts blackEyes = new EyeCounts(0, 1, 0, 1);
          EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 0);
          checkEyes("problem_stone_in_eye4", blackEyes, whiteEyes);
   } */



    ////////////////// test the different big eye shapes /////////////////
    /**
     * ***
     */
    public void testBigEye1() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 0);
        checkEyes("problem_bigeye1", blackEyes, whiteEyes);
    }

    /**
     *   *
     *   **
     */
    public void testBigEye2() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_bigeye2", blackEyes, whiteEyes);
    }

    /**
     *  **
     *  **
     */
    public void testBigEye3() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_bigeye3", blackEyes, whiteEyes);
    }

    /**
     *   ***
     *    **
     */
    public void testBigEye4() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_bigeye4", blackEyes, whiteEyes);
    }

    /**
     *   *
     *  ***
     *   **
     */
    public void testBigEye5() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_bigeye5", blackEyes, whiteEyes);
    }

    /**
     *  **
     *  ***
     *   **
     */
    public void testBigEye6() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_bigeye6", blackEyes, whiteEyes);
    }

    /**
     *  ***
     *   *
     */
    public void testBigEye7() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_bigeye7", blackEyes, whiteEyes);
    }

    /**
     *   *
     *  ***
     *   *
     */
    public void testBigEye8() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_bigeye8", blackEyes, whiteEyes);
    }

    ////////////////// test the different big eye shapes in the corner /////////////////
    /**
     * ***
     */
    public void testCornerBigEye1() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_cornerbigeye1", blackEyes, whiteEyes);
    }

    /**
     *   *
     *   **
     */
    public void testCornerBigEye2() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_cornerbigeye2", blackEyes, whiteEyes);
    }

    /**
     *  **
     *  **
     */
    public void testCornerBigEye3() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_cornerbigeye3", blackEyes, whiteEyes);
    }

    /**
     *   ***
     *    **
     */
    public void testCornerBigEye4() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_cornerbigeye4", blackEyes, whiteEyes);
    }

    /**
     *   *
     *  ***
     *   **
     */
    public void testCornerBigEye5() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_cornerbigeye5", blackEyes, whiteEyes);
    }

    /**
     *  **
     *  ***
     *   **
     */
    public void testCornerBigEye6() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_cornerbigeye6", blackEyes, whiteEyes);
    }

    /**
     *  ***
     *   *
     */
    public void testCornerBigEye7() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_cornerbigeye7", blackEyes, whiteEyes);
    }

    /**
     *   *
     *  ***
     *   *
     */
    public void testCornerBigEye8() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 1, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_cornerbigeye8", blackEyes, whiteEyes);
    }

    ///////////////// check for territorial eyes

    /**
      *  ****
      *  ****
      *  ****
      *  ****
      */
     public void testTerritoryEye() {
         EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
         EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 1);
         checkEyes("problem_terreye5", blackEyes, whiteEyes);
     }


    /**
     *   ****
     */
    public void testTerritoryEye1() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 0, 1);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_terreye1", blackEyes, whiteEyes);
    }

    /**
     *  ***
     *  *
     */
    public void testTerritoryEye2() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 0, 1);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_terreye2", blackEyes, whiteEyes);
    }

    /**
     *  ***
     *  ***
     */
    public void testTerritoryEye3() {
        EyeCounts blackEyes = new EyeCounts(0, 0, 0, 1);
        EyeCounts whiteEyes = new EyeCounts(0, 1, 0, 0);
        checkEyes("problem_terreye3", blackEyes, whiteEyes);
    }

    /**
     *  ***
     *  ***
     *  ***
     */
    public void testTerritoryEye4() {
        EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 1);
        checkEyes("problem_terreye4", blackEyes, whiteEyes);
    }

    /**
     *  ****
     *  ****
     *  ****
     *  ****
     */
    public void testTerritoryEye5() {
        EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 1);
        checkEyes("problem_terreye5", blackEyes, whiteEyes);
    }

    ///////////////// check for territorial eyes in the corner

    /**
     *   ****
     */
    public void testCornerTerritoryEye1() {
        EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 1);
        checkEyes("problem_cornerterreye1", blackEyes, whiteEyes);
    }

    /**
     *  ***
     *  *
     */
    public void testCornerTerritoryEye2() {
        EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 1);
        checkEyes("problem_cornerterreye2", blackEyes, whiteEyes);
    }

    /**
     *  ***
     *  ***
     */
    public void testCornerTerritoryEye3() {
        EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 1);
        checkEyes("problem_cornerterreye3", blackEyes, whiteEyes);
    }

    /**
     *  ***
     *  ***
     *  ***
     */
    public void testCornerTerritoryEye4() {
        EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
        EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 1);
        checkEyes("problem_cornerterreye4", blackEyes, whiteEyes);
    }

    /**
       *  ****
       *  ****
       *  ****
       *  ****
       */
      public void testCornerTerritoryEye5() {
          EyeCounts blackEyes = new EyeCounts(0, 1, 0, 0);
          EyeCounts whiteEyes = new EyeCounts(0, 0, 0, 1);
          checkEyes("problem_cornerterreye5", blackEyes, whiteEyes);
      }



  //-----------------------------------------------------------------------------------
    /**
     *
     * @param eyesProblemFile
     * @param expectedBlackEyes number of black eyes in the group.
     * @param expectedWhiteEyes number of white eyes in the group.
     */
    private void checkEyes(String eyesProblemFile,
                           EyeCounts expectedBlackEyes, EyeCounts expectedWhiteEyes) {

        GameContext.log(0, "finding eyes for "+eyesProblemFile+" ...");
        restore(eyesProblemFile);

        //Set groups = ((GoBoard) controller_.getBoard()).getGroups();

        // consider the 2 biggest groups
        //Assert.assertTrue("There were not two groups. Instead there were :"+groups.size(), groups.size() == 2);

        GoGroup biggestBlackGroup =getBiggestGroup(true);
        GoGroup biggestWhiteGroup = getBiggestGroup(false);      

        EyeCounts eyeCounts = getEyeCounts(biggestBlackGroup.getEyes());
        Assert.assertTrue("Actual Black Eye counts were \n"+eyeCounts+" but was expecting \n"+ expectedBlackEyes,
                              eyeCounts.equals(expectedBlackEyes));
        eyeCounts = getEyeCounts(biggestWhiteGroup.getEyes());
        Assert.assertTrue("Actual White Eye counts were \n"+eyeCounts+" but was expecting \n"+ expectedWhiteEyes,
                              eyeCounts.equals(expectedWhiteEyes));
    }


    public static Test suite() {
        return new TestSuite(TestEyes.class);
    }

    private EyeCounts getEyeCounts(Set eyes)  {
        EyeCounts counts = new EyeCounts();

        // int numFalseEyes = 0;
        for (Object e : eyes) {

            GoEye eye = (GoEye) e;
            switch (eye.getEyeType()) {
                case FALSE_EYE:
                    counts.numFalseEyes++;
                    break;
                case TRUE_EYE:
                    counts.numTrueEyes++;
                    break;
                case BIG_EYE:
                    counts.numBigEyes++;
                    break;
                case TERRITORIAL_EYE:
                    counts.numTerritorialEyes ++;
                    break;
                default:
                    assert false: "bad eye type:" + eye.getEyeType() ;
            }
        }
        return counts;
    }


    private class EyeCounts {
        protected int numFalseEyes;
        protected int numTrueEyes;
        protected int numBigEyes;
        protected int numTerritorialEyes;

        public EyeCounts() {}

        public EyeCounts(int numFalse, int numTrue, int numBig, int numTerritorial) {
            numFalseEyes = numFalse;
            numTrueEyes = numTrue;
            numBigEyes = numBig;
            numTerritorialEyes = numTerritorial;
        }

        public boolean equals(Object ocounts) {
            EyeCounts counts = (EyeCounts)ocounts;
            return (counts.numFalseEyes == numFalseEyes
                    && counts.numTrueEyes == numTrueEyes
                    && counts.numBigEyes == numBigEyes
                    && counts.numTerritorialEyes == numTerritorialEyes);
        }

        public String toString() {
            StringBuffer buf = new StringBuffer('\n');
            buf.append("False Eyes: "+numFalseEyes+'\n');
            buf.append("True Eyes: "+numTrueEyes+'\n');
            buf.append("Big Eyes  : "+numBigEyes+'\n');
            buf.append("Territorial: "+numTerritorialEyes+'\n');
            return buf.toString();
        }
    }
}