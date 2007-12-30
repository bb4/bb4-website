package com.becker.game.multiplayer.poker.test;

import com.becker.common.util.FileUtil;
import com.becker.game.multiplayer.poker.PokerHand;
import com.becker.game.common.GameContext;
import com.becker.game.card.Card;
import com.becker.common.util.FileUtil;

import java.io.*;
import java.util.*;


/**
 * programming challenge to test which poker hands are better
 * see  http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
  * for details
 *
 * author Barry Becker
 */
public class PokerHandsChallenge {

    private static final String TEST_FILE = "multiplayer/poker/test/test_hands.data";

    private static final int MAX_LG = 255;


    /**
     *  for reading from stdin for the programmnig contests
     */
    static String readLine(InputStream stream)  // utility function to read from stdin
    {
        byte lin[] = new byte [MAX_LG];
        int lg = 0, car = -1;

        try {
            while (lg < MAX_LG) {
                car = stream.read();
                if ((car < 0) || (car == '\n')) break;
                lin [lg++] += car;
            }
        }
        catch (IOException e) {
            return (null);
        }

        if ((car < 0) && (lg == 0)) return (null);  // eof
        return (new String (lin, 0, lg));
    }


    private void evaluateLine(String line) {
         if (line == null || line.length() <2) {
            return;
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
            System.out.println("Black wins.");
        } else if (blackWin < 0)  {
            System.out.println("White wins.");
        } else {
            System.out.println("Tie.");
        }
    }


    public void evaluate(String file) throws IOException {


        BufferedReader breader = null;
        String fullPath = FileUtil.PROJECT_DIR + "source/" + GameContext.GAME_ROOT + file;
        try {
            FileReader reader = new FileReader(fullPath);
            breader = new BufferedReader(reader);

        } catch (FileNotFoundException e) {
            System.out.println("Could not find : "+fullPath);
        }

        String line;

        while ((line = breader.readLine()) != null)  {

            evaluateLine(line);

        }
        breader.close();
    }



     public void evaluate(InputStream stream) throws IOException {

        String line;

        while ((line = readLine(stream)) != null) {

            evaluateLine(line);

        }
    }


    public static void main(String args[])  {

        PokerHandsChallenge app = new PokerHandsChallenge();

        try {
            //app.evaluate(System.in);
           app.evaluate(TEST_FILE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
