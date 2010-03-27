package com.becker.game.multiplayer.poker.test;

import com.becker.common.util.FileUtil;
import com.becker.game.multiplayer.poker.PokerHand;
import com.becker.game.common.GameContext;
import com.becker.game.card.Card;
import junit.framework.TestCase;

import java.io.*;
import java.util.*;


/**
 * programming challenge to test which poker hands are better
 * see  http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
 *
 * author Barry Becker
 */
public class PokerHandTest extends TestCase {

    /**
     * Expects input file to catains like   2H 3H 4H 5H 6H 3C 4C 5C 6C 7C
     * where the 5 black cards appear first, then the white cards.
      */
    private static final String TEST_FILE = "multiplayer/poker/test/test_hands.data";

    enum Result {WHITE_WIN, BLACK_WIN, TIE}
    private static final Result[] EXPECTED_RESULTS =
            {Result.WHITE_WIN, Result.BLACK_WIN, Result.BLACK_WIN, Result.TIE, Result.WHITE_WIN, Result.BLACK_WIN};


    public void testPokerHandComparison() throws IOException {

        List<Result> results = evaluate(TEST_FILE);

        assertEquals("Number of reuslts was not what was expected.",
                EXPECTED_RESULTS.length, results.size());

        for (int i=0; i<EXPECTED_RESULTS.length; i++) {
            assertEquals(i + ") ", EXPECTED_RESULTS[i], results.get(i));
        }
    }


    private Result evaluateLine(String line) {
        if (line == null || line.length() <2) {
            throw new IllegalArgumentException("Cannot evaluate line: " + line);
        }

        List<Card> blackCards = new ArrayList<Card>(5);
        List<Card> whiteCards = new ArrayList<Card>(5);

        StringTokenizer tokenizer = new StringTokenizer(line, " ");

        // the first five entries for for black the second five are for white
        int ct = 0;
        while (tokenizer.hasMoreElements()) {
            String cardToken = (String) tokenizer.nextElement();

            if (ct < 5)  {
                blackCards.add(new Card(cardToken));
            } else if (ct < 10)  {
                whiteCards.add(new Card(cardToken));
            }
            ct++;
        }

        PokerHand blackHand = new PokerHand(blackCards);
        PokerHand whiteHand = new PokerHand(whiteCards);

        int blackWin = blackHand.compareTo(whiteHand);
        if (blackWin > 0) {
            return Result.BLACK_WIN;
        } else if (blackWin < 0)  {
            return Result.WHITE_WIN;
        } else {
            return Result.TIE;
        }
    }


    public List<Result> evaluate(String file) throws IOException {

        List<Result> results = new LinkedList<Result>();
        BufferedReader breader;
        String fullPath = FileUtil.PROJECT_DIR + "source/" + GameContext.GAME_ROOT + file;
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

    /*
    public void evaluate(InputStream stream) throws IOException {
        String line;
        while ((line = readLine(stream)) != null) {
            evaluateLine(line);
        }
    }*/

    /*
    public static void main(String args[])  {

        PokerHandTest app = new PokerHandTest();

        try {
           //app.evaluate(System.in);
           app.evaluate(TEST_FILE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }   */
}