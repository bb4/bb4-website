package com.becker.game.twoplayer.pente.test.analysis;

import com.becker.game.common.GameWeights;
import com.becker.game.twoplayer.pente.PentePatterns;
import com.becker.game.twoplayer.pente.PenteWeights;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class PenteLineTest extends TestCase  {

    GameWeights weights;
    private static final boolean PLAYER1_PERSP = true;
    private static final boolean PLAYER2_PERSP = false;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        weights = new PenteWeights();
    }

    public void testEvalLinePlayer1Short() {
        checkLine("_X_XO", 1, PLAYER1_PERSP, 8, new String[] {"_X_X"});
        checkLine("_X_XO", 2, PLAYER1_PERSP, 8, new String[] {"_X_X"});
        checkLine("_X_XO", 3, PLAYER1_PERSP, 8, new String[] {"_X_X"});
        checkLine("_X_XO", 3, PLAYER2_PERSP, 0, new String[] {"_"});
        checkLine("_XX", 1, PLAYER1_PERSP, 0, new String[] {"_XX"});
        checkLine("_XX_", 2, PLAYER1_PERSP, 24, new String[] {"_XX_"});
        checkLine("_XX_", 2, PLAYER2_PERSP, 0, new String[] {""});
        checkLine("_XX_", 3, PLAYER1_PERSP, 24, new String[] {"_XX_"});
        checkLine("_XX_", 3, PLAYER2_PERSP, 0, new String[] {"_"});
    }

    public void testEvalLinePlayer1Long() {
        checkLine("_XX_XX__",  1, PLAYER1_PERSP, 168, new String[] {"_XX_XX_"});

        checkLine("_XX_XX__",  3, PLAYER1_PERSP, 168, new String[] {"_XX_XX_"});
        checkLine("_XX_XX__",  3, PLAYER2_PERSP, 0,   new String[] {"_"});
        checkLine("_XX_XX__",  4, PLAYER1_PERSP, 168, new String[] {"_XX_XX_"});
        checkLine("_XX_XX__",  6, PLAYER1_PERSP, 168, new String[] {"_XX_XX_"});
        checkLine("_XX_XX__",  1, PLAYER1_PERSP, 168, new String[] {"_XX_XX_"});
        checkLine("_XX_XOX_X__", 1, PLAYER1_PERSP, 24, new String[] {"_XX_X"});
        checkLine("_XX_XOX_X__", 1, PLAYER2_PERSP, 0,  new String[] {""});
        checkLine("_XX_XOX_X__", 5, PLAYER1_PERSP, 32, new String[] {"_XX_X", "X_X_"});
        checkLine("_XX_XOX_X__", 5, PLAYER2_PERSP, 0,  new String[] {"O"});
        checkLine("_OO_OXO_O__", 5, PLAYER1_PERSP, 0,  new String[] {"X"});
        checkLine("_OO_OXO_O__", 5, PLAYER2_PERSP, -32, new String[] {"_OO_O", "O_O_"});
    }

    private void checkLine(String linePattern, int position, boolean player1persp, int expectedWorth,
                           String[] expectedPatternsChecked) {
        LineRecorder line = createLine(linePattern);
        int worth = line.evalLine(player1persp, position, 0, linePattern.length()-1);
        //System.out.println("p1Persp=" + player1persp + " " + line +" pos="+ position);

        assertEquals("unexpected score for pattern "+ linePattern + " pos=" + position + " player1Persp="+ player1persp,
                expectedWorth, worth);

        List<String> checkedPats = line.getPatternsChecked();
        //System.out.println("pats="+ TstUtil.quoteStringList(checkedPats));
        assertEquals(expectedPatternsChecked.length, checkedPats.size());
        int i = 0;
        for (String p : checkedPats) {
            assertEquals(expectedPatternsChecked[i++], p);
        }
    }

    /**
     *
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private LineRecorder createLine(String linePattern) {
        return TstUtil.createLine(linePattern, new PentePatterns(), weights.getDefaultWeights());
    }


    public static Test suite() {
        return new TestSuite(PenteLineTest.class);
    }
}