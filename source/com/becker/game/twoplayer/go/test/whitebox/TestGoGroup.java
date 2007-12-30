package com.becker.game.twoplayer.go.test.whitebox;

import com.becker.game.twoplayer.go.*;
import com.becker.game.twoplayer.go.test.GoTestCase;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;

/**
 * Mostly test that the scoring of groups works correctly.
 * @author Barry Becker
 */
public class TestGoGroup extends GoTestCase {    
    
    // test absolute health calculation, and the number of liberties for the main black and white groups.
    public void testAbsHealth1() {
        controller_.reset();
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.344;
        double wPotential = 0.49;
        double blackHealth = 0.1;
        double whiteHealth = -0.1;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("whitebox/groupHealth1", 4, 4, blackEyes, whiteEyes, 
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth, 
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    
    // test absolute health calculation, and the number of liberties for the main black and white groups.
    public void testAbsHealth1a() {
        controller_.reset();
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.3;
        double wPotential = 0.24;
        double blackHealth = 0.1;
        double whiteHealth = -0.1;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("whitebox/groupHealth1a", 3, 3, blackEyes, whiteEyes, 
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth, 
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }


    public void testAbsHealth2() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 1.127;
        double wPotential = 0.769;
        double blackHealth = 0.52;
        double whiteHealth = -0.348;
        double AbsAbsHealthDiff = 0.1;
        double AbsHealthDiff = 0.8;
        double RelHealthDiff = 0.872;
        verifyHealthDifferences("whitebox/groupHealth2", 19, 14, blackEyes, whiteEyes, 
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth, 
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
   }

    public void testAbsHealth3() {
        EyeType[] blackEyes = {EyeType.TERRITORIAL_EYE, EyeType.TERRITORIAL_EYE};
        EyeType[] whiteEyes = {};
        double bPotential = 1.58;
        double wPotential = 0.57;
        double blackHealth = 0.94;
        double whiteHealth = -0.2;
        double AbsAbsHealthDiff = 0.8;
        double AbsHealthDiff = 1.1;
        double RelHealthDiff = 1.1;
        verifyHealthDifferences("whitebox/groupHealth3", 20, 6, blackEyes, whiteEyes, 
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth, 
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    

    public void testAbsHealth4() {
        EyeType[] blackEyes = {};  // should really have a territorial eye here: EyeType.TERRITORIAL_EYE
        EyeType[] whiteEyes = {EyeType.TRUE_EYE};
        double bPotential = 1.9;
        double wPotential = 0.34;
        double blackHealth = 0.68; 
        double whiteHealth = -0.43;
        double AbsAbsHealthDiff =0.253;
        double AbsHealthDiff = 1.11;
        double RelHealthDiff = 1.11;
        verifyHealthDifferences("whitebox/groupHealth4", 33, 12, blackEyes, whiteEyes, 
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth, 
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth5() {
        EyeType[] blackEyes = {EyeType.TRUE_EYE};
        EyeType[] whiteEyes = {};
        double bPotential = 0.0;
        double wPotential = 0.52;
        double blackHealth = -0.05; 
        double whiteHealth = -0.24;
        double AbsAbsHealthDiff = -0.1;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("whitebox/groupHealth5", 4, 8, blackEyes, whiteEyes, 
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth, 
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth6() {
        EyeType[] blackEyes = {EyeType.TRUE_EYE};
        EyeType[] whiteEyes = {EyeType.TRUE_EYE};
        double bPotential = 0.67;
        double wPotential = 0.57;
        double blackHealth = -0.30;
        double whiteHealth = 0.30;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = -0.6;
        double RelHealthDiff = -0.6;
        verifyHealthDifferences("whitebox/groupHealth6", 2, 2, blackEyes, whiteEyes, 
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth, 
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

     public void testAbsHealth7() {
        EyeType[] blackEyes = {EyeType.TERRITORIAL_EYE, EyeType.BIG_EYE};
        EyeType[] whiteEyes = {};
        double bPotential = 1.55;
        double wPotential = 0.69;
        double blackHealth = 0.94;
        double whiteHealth = -0.20;
        double AbsAbsHealthDiff = 0.8;
        double AbsHealthDiff = 1.1;
        double RelHealthDiff = 1.1;
        verifyHealthDifferences("whitebox/groupHealth7", 19, 7, blackEyes, whiteEyes,  
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth, 
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    
   

    private static final double THRESH = 0.11;
    private static final double EPS = 0.01;

    /**
     * verify the black - white goup health differences for
     * the abs(absolute health), absolute health, and relative health.
     */
    private void verifyHealthDifferences(String file,
                                         int expectedNumberOfBlackLiberties, int expectedNumberOfWhiteLiberties,
                                         EyeType[] blackEyes,  EyeType[] whiteEyes,
                                         double expectedBlackEyePotential, double expectedWhiteEyePotential,
                                         double expectedBlackHealth,  double expectedWhiteHealth, 
                                         double expectedAbsAbsHealthDifference,
                                         double expectedAbsHealthDifference,
                                         double expectedRelHealthDifference) {
        restore(file);

        // find the biggest black and white groups
        GoGroup bg = getBiggestGroup(true);
        GoGroup wg = getBiggestGroup(false);
        
         int numBlackLiberties = bg.getLiberties((GoBoard) controller_.getBoard()).size();
         Assert.assertEquals(file + ". Expected num black liberties ="+ expectedNumberOfBlackLiberties + " but got "+numBlackLiberties, 
                 expectedNumberOfBlackLiberties, numBlackLiberties);
         
         int numWhiteLiberties = wg.getLiberties((GoBoard) controller_.getBoard()).size();
         Assert.assertEquals(file + ". Expected num white liberties ="+ expectedNumberOfWhiteLiberties + " but got "+numWhiteLiberties, 
                 expectedNumberOfBlackLiberties, numBlackLiberties);

         
        double bah = bg.getAbsoluteHealth();
        double wah = wg.getAbsoluteHealth();              
        //System.out.println(file + " b health="+bah + " white h="+wah);
         Assert.assertTrue(file + ".  Unexpected black health = " + bah,  approximatelyEqual(expectedBlackHealth, bah, EPS));
         Assert.assertTrue(file + ".  Unexpected white health = " + wah,  approximatelyEqual(expectedWhiteHealth, wah, EPS));
         
        double bPotential = bg.getEyePotential();
        double wPotential = wg.getEyePotential();
        Assert.assertTrue(file + ".  Unexpected black eye potential = " + bPotential,  approximatelyEqual(expectedBlackEyePotential, bPotential, EPS));
         Assert.assertTrue(file + ".  Unexpected white eye potential = " + wPotential,  approximatelyEqual(expectedWhiteEyePotential, wPotential, EPS));
        
        // verify that we have the expected number and type of eyes for that biggest group
        verifyEyes(bg.getEyes(), blackEyes, true);
        verifyEyes(wg.getEyes(), whiteEyes, false);      
        
        double abah = Math.abs(bah);
        double awah = Math.abs(wah);

        double brh = bg.getRelativeHealth();
        double wrh = wg.getRelativeHealth();

        double daah = abah - awah;
        double dah = bah - wah;
        double drh = brh - wrh;

        Assert.assertTrue(file + ". Expected abs(black AbsHealth) - abs(white AbsHealth) to be about "
                + expectedAbsAbsHealthDifference + "\n but instead got ("+abah+" -"+ awah+") = "+ daah,
                approximatelyEqual(daah, expectedAbsAbsHealthDifference, THRESH));
        Assert.assertTrue(file + ". Expected (black AbsHealth) - (white AbsHealth) to be about "
                + expectedAbsHealthDifference + "\n but instead got ("+bah+" -"+ wah+") = "+ dah,
                approximatelyEqual(dah, expectedAbsHealthDifference, THRESH));
        Assert.assertTrue(file + ". Expected (black RelativeHealth) - (white RelativeHealth) to be about "
                + expectedRelHealthDifference + "\n but instead got  ("+brh+" -"+ wrh+") = "+ drh,
                approximatelyEqual(drh, expectedRelHealthDifference, THRESH));
    }


    private final void verifyEyes(Set eyes, EyeType[] expectedEyes, boolean black)
    {
        String color = black? "black" : "white";
        Assert.assertEquals("unequal numbers of "+color+" eyes", expectedEyes.length, eyes.size());
        if (eyes.size() > 0)
        {
            int i = 0;
            for (Object o : eyes) {
                EyeType eType = ((GoEye) o).getEyeType();
                Assert.assertTrue(color + "Eye " + i + " was not the type that we expected. It was " + eType, (expectedEyes[i] == eType));  
                i++;
            }    
        }
    }
    
/*
    // test for unconditional life
    public void testUnconditionalLife1() {
        controller_.reset();
        verifyUnconditionalLife("whitebox/unconditionalLife1", true, 12, true);
    }

    public void testUnconditionalLife2() {
        verifyUnconditionalLife("whitebox/unconditionalLife2", true, 13, false);
    }

    public void testUnconditionalLife3() {
        verifyUnconditionalLife("whitebox/unconditionalLife3", true, 11, false);
    }


    public void testUnconditionalLife4() {
        verifyUnconditionalLife("whitebox/unconditionalLife4", true, 8, false);
    }

    public void testUnconditionalLife5() {
        verifyUnconditionalLife("whitebox/unconditionalLife5", true, 7, false);
    }


    public void testUnconditionalLife6() {
        verifyUnconditionalLife("whitebox/unconditionalLife6", true, 7, true);
    }

    public void testUnconditionalLife7() {
        verifyUnconditionalLife("whitebox/unconditionalLife7", false, 8, true);
    }


    public void testUnconditionalLife8() {
        verifyUnconditionalLife("whitebox/unconditionalLife8", true, 13, true);
    }

    public void testUnconditionalLife9() {
        verifyUnconditionalLife("whitebox/unconditionalLife9", false, 9, false);
    }


    public void testUnconditionalLife11() {

        verifyUnconditionalLife("whitebox/unconditionalLife11", true, 14, true);
    }

    public void testUnconditionalLife12() {
        verifyUnconditionalLife("whitebox/unconditionalLife12", true, 21, true);
    }

     public void testUnconditionalLife13() {
        verifyUnconditionalLife("whitebox/unconditionalLife13", true, 15, false);
    }

     public void testUnconditionalLife14() {
        verifyUnconditionalLife("whitebox/unconditionalLife14", true, 13, false);
    }
   
     public void testUnconditionalLife15() {
        verifyUnconditionalLife("whitebox/unconditionalLife15", true, 12, true);
    }
 */


    /**
     * Use Benson's algorithm for detecting unconditionally alive groups.
     */
    private void verifyUnconditionalLife(String file,
                                         boolean forBlackGroup, int expectedSizeOfGroup, boolean expectedUnconditionalyAlive) {
        restore(file);

        // find the biggest black and white groups
        GoGroup group = getBiggestGroup(forBlackGroup);

        int size = group.getNumStones();
        Assert.assertTrue("We expected the size of the test group to be "+ expectedSizeOfGroup
                +" but instead it was "+ size,
                size == expectedSizeOfGroup);

        boolean unconditionallyAlive = GoGroupUtil.isUnconditionallyAlive(group, (GoBoard) controller_.getBoard());

        if (expectedUnconditionalyAlive) {
            Assert.assertTrue("Expected this group be unconditionally alive, but its not. group=" + group,
                               unconditionallyAlive);
        } else {
            Assert.assertTrue("Did not expected this group be unconditionally alive, but it is. group=" + group,
                               !unconditionallyAlive);
        }
    }


}
