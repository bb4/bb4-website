/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.game.common.GameContext;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * programming challenge to test which poker hands are better
 * see  http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
 *
 * author Barry Becker
 */
public class PokerHandTestOld extends TestCase {

    /**
     * Expects input file to contain something like   2H 3H 4H 5H 6H 3C 4C 5C 6C 7C
     * where the 5 player1 cards appear first, then the player2 cards.
     */
    private static final String TEST_FILE = "multiplayer/poker/hand/test_hands.data";

    enum Result {PLAYER1_WIN, PLAYER2_WIN, TIE}

    /** These are the expected winning hands - used to verify */
    private static final Result[] EXPECTED_RESULTS = {
        Result.PLAYER2_WIN,
        Result.PLAYER1_WIN,
        Result.PLAYER2_WIN,
        Result.PLAYER2_WIN, //Result.TIE,   used to be tie, but then I changed the scorer
        Result.PLAYER2_WIN,
        Result.PLAYER1_WIN,
        Result.PLAYER1_WIN,
        Result.PLAYER2_WIN
    };


    public void testPokerHandComparisons() throws IOException {

        List<Result> results = evaluate(TEST_FILE);

        assertEquals("Number of results was not what was expected.",
                EXPECTED_RESULTS.length, results.size());

        for (int i = 0; i < EXPECTED_RESULTS.length; i++) {
            System.out.println(i + ") " +  results.get(i));
            assertEquals(i + ") ", EXPECTED_RESULTS[i], results.get(i));
        }
    }

    public List<Result> evaluate(String file) throws IOException {

        List<Result> results = new LinkedList<Result>();
        BufferedReader breader;
        String fullPath = FileUtil.PROJECT_HOME + "game/test/" + GameContext.GAME_ROOT + file;
        try {
            FileReader reader = new FileReader(fullPath);
            breader = new BufferedReader(reader);

            String line;
            while ((line = breader.readLine()) != null)  {
                results.add(evaluateLine(line));
            }
            breader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find : "+fullPath);
        }
        return results;
    }

    private Result evaluateLine(String line) {
        if (line == null || line.length() <2) {
            throw new IllegalArgumentException("Cannot evaluate line: " + line);
        }

        String[] sublines = line.split(",");
        PokerHand blackHand = PokerHandTstUtil.createHand(sublines[0]);
        PokerHand whiteHand = PokerHandTstUtil.createHand(sublines[1]);

        int blackWin = blackHand.compareTo(whiteHand);

        if (blackWin > 0) {
            return Result.PLAYER1_WIN;
        } else if (blackWin < 0)  {
            return Result.PLAYER2_WIN;
        } else {
            return Result.TIE;
        }
    }


}
