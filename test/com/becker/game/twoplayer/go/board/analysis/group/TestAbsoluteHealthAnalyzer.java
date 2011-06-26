package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.common.format.FormatUtil;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.information.E1Information;
import com.becker.game.twoplayer.go.board.analysis.eye.information.E2Information;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.becker.game.twoplayer.go.board.analysis.eye.information.FalseEyeInformation;
import com.becker.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.becker.game.twoplayer.go.board.elements.eye.IGoEye;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import junit.framework.Assert;

import static com.becker.game.twoplayer.go.board.analysis.eye.information.E4Information.Eye4Type.E2222;
import static com.becker.game.twoplayer.go.board.analysis.eye.information.E5Information.Eye5Type.E12223;
import static com.becker.game.twoplayer.go.board.analysis.eye.information.E6Information.Eye6Type.E222233;
import static com.becker.game.twoplayer.go.board.analysis.eye.information.EyeType.*;


/**
* Mostly test that the scoring of groups works correctly.
* @author Barry Becker
*/
public class TestAbsoluteHealthAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/grouphealth/";

    // test absolute health calculation, and the number of liberties for the main black and white groups.
    // testAbsHealth1* test configurations with 1 stone in each group.
    public void testAbsHealth1() {
        controller_.reset();
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
        double bPotential = 0.49;
        double wPotential = 0.5958;
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
        double bPotential = 1.18;
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
        double bPotential = 0.17;
        double wPotential = 0.807; //0.5439
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
        EyeInformation[] blackEyes = {E6.getInformation(E222233.toString()), E6.getInformation(E222233.toString())};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
        double bPotential = 0.877;
        double wPotential = 0.596;
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
        double bPotential = 0.6661734; // 0.82;
        double wPotential = 0.5958436; // 0.51;
        double blackHealth = 0.10;
        double whiteHealth = -0.10;
        double AbsAbsHealthDiff = 0.0;
        double AbsHealthDiff = 0.2;
        double RelHealthDiff = 0.2;
        verifyHealthDifferences("groupHealth3b", 5, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff);
    }
    public void testAbsHealth3c() {
        EyeInformation[] blackEyes = {new FalseEyeInformation()};
        EyeInformation[] whiteEyes = {};
        double bPotential = 0.59584;
        double wPotential = 0.51602;
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
        EyeInformation[] blackEyes = {};  // should really have a territorial eye here: EyeInformation.TERRITORIAL_EYE
        EyeInformation[] whiteEyes = {new E1Information()};
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
        EyeInformation[] blackEyes = {};  // should really have a territorial eye here: EyeInformation.TERRITORIAL_EYE
        EyeInformation[] whiteEyes = {};
        double bPotential = 1.06;
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
        EyeInformation[] blackEyes = {};  // should really have a territorial eye here: EyeInformation.TERRITORIAL_EYE
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {new E2Information()};
        EyeInformation[] whiteEyes = {};
        double bPotential = 0.7497;
        double wPotential = 0.6436;
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {E4.getInformation(E2222.toString())};
        EyeInformation[] whiteEyes = {};
        double bPotential = 1.003;
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
        EyeInformation[] blackEyes = {new E1Information()};
        EyeInformation[] whiteEyes = {new E1Information()};
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
        EyeInformation[] blackEyes = {new E1Information(), new E1Information()};
        EyeInformation[] whiteEyes = {};
        double bPotential = 0.9881;
        double wPotential = 1.397;
        double blackHealth = 1.0; //0.9399999976158142;  
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
        EyeInformation[] blackEyes = {E6.getInformation(E222233.toString()), E5.getInformation(E12223.toString())};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
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
        EyeInformation[] blackEyes = {};
        EyeInformation[] whiteEyes = {};
        double bPotential = 1.49;
        double wPotential = 0.91;
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
                                         EyeInformation[] blackEyes,  EyeInformation[] whiteEyes,
                                         double expectedBlackEyePotential, double expectedWhiteEyePotential,
                                         double expectedBlackHealth,  double expectedWhiteHealth,
                                         double expectedAbsAbsHealthDifference,
                                         double expectedAbsHealthDifference,
                                         double expectedRelHealthDifference) {
        restore(PREFIX + file);

        // find the biggest black and white groups
        IGoGroup bg = getBiggestGroup(true);
        IGoGroup wg = getBiggestGroup(false);

        GoBoard board = (GoBoard) controller_.getBoard();

        GroupAnalyzerMap analyzerMap = new GroupAnalyzerMap();
        GroupAnalyzer blackHealthAnalyzer = new GroupAnalyzer(bg, analyzerMap);
        GroupAnalyzer whiteHealthAnalyzer = new GroupAnalyzer(wg, analyzerMap);

        double bah = blackHealthAnalyzer.calculateAbsoluteHealth(board);
        double wah = whiteHealthAnalyzer.calculateAbsoluteHealth(board);

        int numBlackLiberties = blackHealthAnalyzer.getNumLiberties(null);
        int numWhiteLiberties = whiteHealthAnalyzer.getNumLiberties(null);

        blackHealthAnalyzer.invalidate();
        whiteHealthAnalyzer.invalidate();

         // verify that we have the expected number and type of eyes for that biggest group
        verifyEyes(blackHealthAnalyzer.getEyes(board), blackEyes, true);
        verifyEyes(whiteHealthAnalyzer.getEyes(board), whiteEyes, false);

        // find a different way to do this. In separate EyePotential test.
        float bPotential = blackHealthAnalyzer.getEyePotential();
        float wPotential = whiteHealthAnalyzer.getEyePotential();

        double brh = blackHealthAnalyzer.calculateRelativeHealth(board);
        double wrh = whiteHealthAnalyzer.calculateRelativeHealth(board);

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
            System.out.println("black eye potential\t" + expectedBlackEyePotential + "\t " + FormatUtil.formatNumber(bPotential) + errorMarker(bEyePotentialOK));
            System.out.println("white eye potential\t" + expectedWhiteEyePotential + "\t " + FormatUtil.formatNumber(wPotential) + errorMarker(wEyePotentialOK));

            System.out.println("black health      \t" + expectedBlackHealth + "\t " + FormatUtil.formatNumber(bah) + errorMarker(bHealthOK));
            System.out.println("white health      \t" + expectedWhiteHealth + "\t " + FormatUtil.formatNumber(wah) + errorMarker(wHealthOK));

            System.out.println("absAbs health diff \t" + expectedAbsAbsHealthDifference + "\t " + FormatUtil.formatNumber(daah) + errorMarker(absAbsDifOK));
            System.out.println("absolute health diff\t" + expectedAbsHealthDifference + "\t " + FormatUtil.formatNumber(dah) + errorMarker(absDifOK));
            System.out.println("relative health diff \t" + expectedRelHealthDifference + "\t " + FormatUtil.formatNumber(drh) + errorMarker(relDifOK));

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

    private void verifyEyes(GoEyeSet eyes, EyeInformation[] expectedEyes, boolean black)
    {
        String color = black? "black" : "white";
        Assert.assertEquals("unequal numbers of " + color + " eyes", expectedEyes.length, eyes.size());
        if (eyes.size() > 0)
        {
            int i = 0;
            for (IGoEye eye : eyes) {
                EyeInformation eyeInformation = eye.getInformation();
                Assert.assertEquals(color + "Eye " + i + " was not the type that we expected. ",
                         expectedEyes[i], eyeInformation);
                i++;
            }
        }
    }

}
