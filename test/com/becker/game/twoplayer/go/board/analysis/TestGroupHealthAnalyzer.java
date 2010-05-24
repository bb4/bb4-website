package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.EyeType;
import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.GoGroup;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.common.util.Util;
import com.becker.game.twoplayer.go.GoTestCase;
import java.util.Set;
import junit.framework.Assert;

/**
 * Mostly test that the scoring of groups works correctly.
 * @author Barry Becker
 */
public class TestGroupHealthAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/grouphealth/";

    // test absolute health calculation, and the number of liberties for the main black and white groups.
    // testAbsHealth1* test configurations with 1 stone in each group.
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
        verifyHealthDifferences("groupHealth1", 4, 4, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }


    public void testAbsHealth1a() {
        controller_.reset();
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.3;
        double wPotential = 0.3;
        double blackHealth = 0.1;
        double whiteHealth = -0.1;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("groupHealth1a", 3, 3, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth1b() {
        controller_.reset();
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.34;
        double wPotential = 0.34;
        double blackHealth = 0.1;
        double whiteHealth = -0.1;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("groupHealth1b", 4, 4, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth1c() {
        controller_.reset();
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.49;
        double wPotential = 0.49;
        double blackHealth = 0.1;
        double whiteHealth = -0.1;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("groupHealth1c", 4, 4, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth1d() {
        controller_.reset();
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.24;
        double wPotential = 0.0;
        double blackHealth = 0.1;
        double whiteHealth = -0.02;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("groupHealth1d", 3, 2, blackEyes, whiteEyes,
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
        verifyHealthDifferences("groupHealth2", 19, 14, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth2a() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.42;
        double wPotential = 0.42;
        double blackHealth = 0.2;
        double whiteHealth = -0.2;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.4;
        double RelHealthDiff = 0.4;
        verifyHealthDifferences("groupHealth2a", 6, 6, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
   }
    public void testAbsHealth2b() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.73;
        double wPotential = 0.64;
        double blackHealth = 0.2;
        double whiteHealth = -0.2;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.4;
        double RelHealthDiff = 0.4;
        verifyHealthDifferences("groupHealth2b", 6, 6, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth2c() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.54;
        double wPotential = 0.38;
        double blackHealth = 0.2;
        double whiteHealth = -0.1;
        double AbsAbsHealthDiff = 0.1;
        double AbsHealthDiff = 0.3;
        double RelHealthDiff = 0.3;
        verifyHealthDifferences("groupHealth2c", 6, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
   }
    public void testAbsHealth2d() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.385;
        double wPotential = 0.384;
        double blackHealth = 0.1;
        double whiteHealth = -0.1;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("groupHealth2d", 5, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth2e() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.516;
        double wPotential = 0.344;
        double blackHealth = 0.1;
        double whiteHealth = -0.05;
        double AbsAbsHealthDiff = 0.1;
        double AbsHealthDiff = 0.15;
        double RelHealthDiff = 0.15;
        verifyHealthDifferences("groupHealth2e", 5, 4, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
   }
    public void testAbsHealth2f() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.596;
        double wPotential = 0.385;
        double blackHealth = 0.1;
        double whiteHealth = -0.1;
        double AbsAbsHealthDiff = 0.1;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("groupHealth2f", 5, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth2g() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.17;
        double wPotential = 0.544; //0.5439
        double blackHealth = -0.3;
        double whiteHealth = -0.05;
        double AbsAbsHealthDiff = 0.25;
        double AbsHealthDiff = -0.25;
        double RelHealthDiff = -0.3375;
        verifyHealthDifferences("groupHealth2g", 2, 4, blackEyes, whiteEyes,
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
        verifyHealthDifferences("groupHealth3", 20, 6, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth3a() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.877;
        double wPotential = 0.49;
        double blackHealth = 0.24;
        double whiteHealth = -0.24;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.48;
        double RelHealthDiff = 0.48;
        verifyHealthDifferences("groupHealth3a", 8, 8, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth3b() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.82;
        double wPotential = 0.51;
        double blackHealth = 0.10;
        double whiteHealth = -0.10;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.62;
        verifyHealthDifferences("groupHealth3b", 5, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth3c() {
        EyeType[] blackEyes = {EyeType.FALSE_EYE};
        EyeType[] whiteEyes = {};
        double bPotential = 0.344;
        double wPotential = 0.384;
        double blackHealth = 0.02;
        double whiteHealth = -0.1;
        double AbsAbsHealthDiff = -0.08;
        double AbsHealthDiff = 0.12;
        double RelHealthDiff = 0.12;
        verifyHealthDifferences("groupHealth3c", 3, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth4() {
        EyeType[] blackEyes = {};  // should really have a territorial eye here: EyeType.TERRITORIAL_EYE
        EyeType[] whiteEyes = {EyeType.TRUE_EYE};
        double bPotential = 1.9;
        double wPotential = 0.877;
        double blackHealth = 0.79;
        double whiteHealth = -0.405;
        double AbsAbsHealthDiff =0.388;
        double AbsHealthDiff = 1.11;
        double RelHealthDiff = 1.11;
        verifyHealthDifferences("groupHealth4", 33, 12, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth4a() {
        EyeType[] blackEyes = {};  // should really have a territorial eye here: EyeType.TERRITORIAL_EYE
        EyeType[] whiteEyes = {};
        double bPotential = 1.0;
        double wPotential = 0.54;
        double blackHealth = 0.36;
        double whiteHealth = -0.28;
        double AbsAbsHealthDiff =0.083;
        double AbsHealthDiff = 0.64;
        double RelHealthDiff = 0.64;
        verifyHealthDifferences("groupHealth4a", 10, 10, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth4b() {
        EyeType[] blackEyes = {};  // should really have a territorial eye here: EyeType.TERRITORIAL_EYE
        EyeType[] whiteEyes = {};
        double bPotential = 0.788;
        double wPotential = 0.91;
        double blackHealth = 0.26;
        double whiteHealth = -0.315;
        double AbsAbsHealthDiff = -0.054;
        double AbsHealthDiff = 0.58;
        double RelHealthDiff = 0.58;
        verifyHealthDifferences("groupHealth4b", 9, 12, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth5() {
        EyeType[] blackEyes = {EyeType.TRUE_EYE};
        EyeType[] whiteEyes = {};
        double bPotential = 0.67;
        double wPotential = 0.54;
        double blackHealth = -0.05;
        double whiteHealth = -0.24;
        double AbsAbsHealthDiff = -0.1;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("groupHealth5", 4, 8, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth5a() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.71;
        double wPotential = 1.09;
        double blackHealth = 0.28;
        double whiteHealth = -0.405;
        double AbsAbsHealthDiff = -0.125;
        double AbsHealthDiff = 0.685;
        double RelHealthDiff = 0.685;
        verifyHealthDifferences("groupHealth5a", 10, 12, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth5b() {
        EyeType[] blackEyes = {EyeType.BIG_EYE};
        EyeType[] whiteEyes = {};
        double bPotential = 0.877;
        double wPotential = 1.14;
        double blackHealth = 0.34;
        double whiteHealth = -0.42;
        double AbsAbsHealthDiff = -0.084;
        double AbsHealthDiff = 0.764;
        double RelHealthDiff = 0.764;
        verifyHealthDifferences("groupHealth5b", 9, 13, blackEyes, whiteEyes,
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
        verifyHealthDifferences("groupHealth6", 2, 2, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth6a() {
        EyeType[] blackEyes = {EyeType.TRUE_EYE, EyeType.TRUE_EYE};
        EyeType[] whiteEyes = {};
        double bPotential = 0.93;
        double wPotential = 1.31;
        double blackHealth = 0.9399999976158142;  // 1.0
        double whiteHealth = -0.5;
        double AbsAbsHealthDiff = 0.49;
        double AbsHealthDiff = 1.5;
        double RelHealthDiff = 1.5;
        verifyHealthDifferences("groupHealth6a", 8, 18, blackEyes, whiteEyes,
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
        double whiteHealth = -0.2;
        double AbsAbsHealthDiff = 0.74;
        double AbsHealthDiff = 1.14;
        double RelHealthDiff = 1.14;
        verifyHealthDifferences("groupHealth7", 19, 6, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth8() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 0.54;
        double wPotential = 0.89;
        double blackHealth = 0.315;
        double whiteHealth = -0.3;
        double AbsAbsHealthDiff = 0.017;
        double AbsHealthDiff = 0.613;
        double RelHealthDiff = 0.613;
        verifyHealthDifferences("groupHealth8", 12, 11, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }

    public void testAbsHealth9() {
        EyeType[] blackEyes = {};
        EyeType[] whiteEyes = {};
        double bPotential = 1.49;
        double wPotential = 0.77;
        double blackHealth = 0.44;
        double whiteHealth = -0.22;
        double AbsAbsHealthDiff = 0.22;
        double AbsHealthDiff = 0.663;
        double RelHealthDiff = 0.683;
        verifyHealthDifferences("groupHealth9", 14, 7, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }



    private static final double THRESH = 0.11;
    private static final double EPS = 0.01;

    /**
     * Verify the black - white goup health differences for
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
        restore(PREFIX + file);
        System.out.println("test=" + file);

        // find the biggest black and white groups
        GoGroup bg = getBiggestGroup(true);
        GoGroup wg = getBiggestGroup(false);

        GoBoard board = (GoBoard) controller_.getBoard();
        GroupHealthAnalyzer blackHealthAnalyzer = new GroupHealthAnalyzer(bg);
        GroupHealthAnalyzer whiteHealthAnalyzer = new GroupHealthAnalyzer(wg);

        GoProfiler p = new GoProfiler();

        double bah = blackHealthAnalyzer.calculateAbsoluteHealth( board, p );
        double wah = whiteHealthAnalyzer.calculateAbsoluteHealth( board, p );

         int numBlackLiberties = blackHealthAnalyzer.getNumLiberties();
         int numWhiteLiberties = whiteHealthAnalyzer.getNumLiberties();

         blackHealthAnalyzer.breakEyeCache();
         whiteHealthAnalyzer.breakEyeCache();

         // verify that we have the expected number and type of eyes for that biggest group
        verifyEyes(blackHealthAnalyzer.getEyes(board), blackEyes, true);
        verifyEyes(whiteHealthAnalyzer.getEyes(board), whiteEyes, false);

        // find a different way to do this. In separate EyePotential test.
        float bPotential = blackHealthAnalyzer.getEyePotential();
        float wPotential = whiteHealthAnalyzer.getEyePotential();

        double brh = blackHealthAnalyzer.calculateRelativeHealth( board, p );
        double wrh = whiteHealthAnalyzer.calculateRelativeHealth( board, p );

        double abah = Math.abs(bah);
        double awah = Math.abs(wah);

        double daah = abah - awah;
        double dah = bah - wah;
        double drh = brh - wrh;

        // if any of the assertions are going to fail, lets print all the results nicely so they can be copied over easily
        boolean libertiesOK = (expectedNumberOfBlackLiberties == numBlackLiberties) && (expectedNumberOfWhiteLiberties == numWhiteLiberties);
        boolean bEyePotentialOK = approximatelyEqual(expectedBlackEyePotential, bPotential, EPS);
        boolean wEyePotentialOK = approximatelyEqual(expectedWhiteEyePotential, wPotential, EPS);
        boolean bHealthOK = approximatelyEqual(expectedBlackHealth, bah, EPS);
        boolean wHealthOK = approximatelyEqual(expectedWhiteHealth, wah, EPS);
        boolean absAbsDifOK = approximatelyEqual(daah, expectedAbsAbsHealthDifference, THRESH);
        boolean absDifOK = approximatelyEqual(dah, expectedAbsHealthDifference, THRESH);
        boolean relDifOK = approximatelyEqual(drh, expectedRelHealthDifference, THRESH);

        if (!(libertiesOK && bEyePotentialOK && wEyePotentialOK && bHealthOK && wHealthOK && absAbsDifOK && absDifOK && relDifOK))  {
            System.out.println( file + "     \t exp \t got ");
            System.out.println("               \t\t-----\t-----");
            System.out.println("black liberties    \t "+ expectedNumberOfBlackLiberties +"\t " + numBlackLiberties + errorMarker(libertiesOK));
            System.out.println("white liberties    \t "+ expectedNumberOfWhiteLiberties +"\t " + numWhiteLiberties + errorMarker(libertiesOK) );
            System.out.println("black eye potential\t" + expectedBlackEyePotential + "\t " + Util.formatNumber(bPotential) + errorMarker(bEyePotentialOK));
            System.out.println("white eye potential\t" + expectedWhiteEyePotential + "\t " + Util.formatNumber(wPotential) + errorMarker(wEyePotentialOK));

            System.out.println("black health      \t" + expectedBlackHealth + "\t " + Util.formatNumber(bah) + errorMarker(bHealthOK));
            System.out.println("white health      \t" + expectedWhiteHealth + "\t " + Util.formatNumber(wah) + errorMarker(wHealthOK));

            System.out.println("absAbs health diff \t" + expectedAbsAbsHealthDifference + "\t " + Util.formatNumber(daah) + errorMarker(absAbsDifOK));
            System.out.println("absolute health diff\t" + expectedAbsHealthDifference + "\t " + Util.formatNumber(dah) + errorMarker(absDifOK));
            System.out.println("relative health diff \t" + expectedRelHealthDifference + "\t " + Util.formatNumber(drh) + errorMarker(relDifOK));

            System.out.println("black eyes: "+ blackHealthAnalyzer.getEyes(board));
            System.out.println("white eyes: "+ whiteHealthAnalyzer.getEyes(board));

            System.out.println("\n");
        }

        Assert.assertEquals(file + ". Expected num black liberties ="+ expectedNumberOfBlackLiberties + " but got "+numBlackLiberties,
                 expectedNumberOfBlackLiberties, numBlackLiberties);
        Assert.assertEquals(file + ". Expected num white liberties ="+ expectedNumberOfWhiteLiberties + " but got "+numWhiteLiberties,
                 expectedNumberOfWhiteLiberties, numWhiteLiberties);

        Assert.assertTrue(file + ".  Unexpected black eye potential = " + bPotential,  bEyePotentialOK);
        Assert.assertTrue(file + ".  Unexpected white eye potential = " + wPotential,  wEyePotentialOK);

        Assert.assertTrue(file + ".  Unexpected black health = " + bah,  bHealthOK);
        Assert.assertTrue(file + ".  Unexpected white health = " + wah,  wHealthOK);

        Assert.assertTrue(file + ". Expected (absAbs) abs(black AbsHealth) - abs(white AbsHealth) to be about "
                + expectedAbsAbsHealthDifference + "\n but instead got ("+abah+" -"+ awah+") = "+ daah, absAbsDifOK);
        Assert.assertTrue(file + ". Expected (black AbsHealth) - (white AbsHealth) to be about "
                + expectedAbsHealthDifference + "\n but instead got ("+bah+" -"+ wah+") = "+ dah, absDifOK);
        Assert.assertTrue(file + ". Expected (rel) (black RelativeHealth) - (white RelativeHealth) to be about "
                + expectedRelHealthDifference + "\n but instead got  ("+brh+" -"+ wrh+") = "+ drh, relDifOK);
    }

    private String errorMarker(boolean OK) {
        return (OK?"":"      *Error*");
    }

    private void verifyEyes(Set<GoEye> eyes, EyeType[] expectedEyes, boolean black)
    {
        String color = black? "black" : "white";
        Assert.assertEquals("unequal numbers of "+color+" eyes", expectedEyes.length, eyes.size());
        if (eyes.size() > 0)
        {
            int i = 0;
            for (GoEye eye : eyes) {
                EyeType eType = eye.getEyeType();
                Assert.assertTrue(color + "Eye " + i + " was not the type that we expected. " +
                        "It was " + eType +" , but we expected " + expectedEyes[i],
                         (expectedEyes[i] == eType));
                i++;
            }
        }
    }

}
