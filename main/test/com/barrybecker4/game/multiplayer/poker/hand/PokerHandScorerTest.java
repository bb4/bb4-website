/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.hand;

import junit.framework.TestCase;
import static com.barrybecker4.game.multiplayer.poker.hand.PokerHandTstUtil.*;


/**
 * programming challenge to test which poker hands are better
 * see http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
 *
 * author Barry Becker
 */
public class PokerHandScorerTest extends TestCase {

    PokerHandScorer scorer = new PokerHandScorer();

    enum CompareType {BIGGER, SMALLER, SAME}

    public void testAceHighBeatsKingHighOrdered() {
        compareHands(createHand("2H 3D 5S 9C KD"), createHand("2C 3H 4S 8C AH"), CompareType.SMALLER);
    }

    public void testAceHighBeatsKingHighUnrdered() {
        compareHands(createHand("2H KD 3D 5S 9C "), createHand("2C 3H 4S AH 8C"), CompareType.SMALLER);
    }

    public void testThreeOfAKindBeatsTwoPair() {
        compareHands(createHand("2H 5D 5S 5C 3D"), createHand("KC AH 4S 4C AH"), CompareType.BIGGER);
    }

    public void testPairBeatsHighCard() {
        compareHands(createHand("2H 4S 4C 2D 4H"), createHand("2S 8S AS QS 3S"), CompareType.BIGGER);
    }

    public void testHighOfHeartsBeatsKingOfDiamonds() {
        compareHands(createHand("2H 3D 5S 9C KD"), createHand("2C 3H 4S 8C KH"), CompareType.SMALLER);
    }

    public void testKingOfDiamondsLessThanKingOfHeartsWithSecondaryCardsLower() {
        compareHands(createHand("6H QD 5S 9C KD"), createHand("2D 3H 5C 9S KH"), CompareType.SMALLER);
    }

    public void testPairOf7sLessThanPairOfJacks() {
        compareHands(createHand("7C 7H 5S 9C KD"), createHand("JC 3H JH 8C KH"), CompareType.SMALLER);
    }

    public void testPairOf9sBiggerThanKingHigh() {
        compareHands(createHand("9C 9H 5S 9C KD"), createHand("3C 4H 3H 8C KH"), CompareType.BIGGER);
    }

    public void testPairOf9sBiggerThanPairOf4s() {
        compareHands(createHand("9C 9H 5S 3C 2D"), createHand("4C 4H AH 8C KH"), CompareType.BIGGER);
    }

    public void testPairOf7sSmallerThanPairOf8s() {
        compareHands(createHand("7C 7H JS 9C KD"), createHand("8C 8H 3H 5C 4H"), CompareType.SMALLER);
    }

    public void testSamePairUsesHighSecondaryCardToResolve() {
        compareHands(createHand("9H AS AD QC 3D"), createHand("AC AH QS 4C JD"), CompareType.BIGGER);
    }

    public void testThreeInFullHouseUsedFirst() {
        compareHands(createHand("KH 3S KD 3C 3D"), createHand("4C JH JS 4D JD"), CompareType.SMALLER);
    }

    // impossible without wild cards
    public void testTwoInFullHouseUsedIfThreeTie() {
        compareHands(createHand("10H JS 10D 10C JD"), createHand("AC AH 10S 10C 10D"), CompareType.SMALLER);
    }

    public void testSecondPairUsedToBreakTie() {
        compareHands(createHand("10H JS 10D 3C JD"), createHand("AC AH 10S 10C AS"), CompareType.SMALLER);
    }

    public void testStraightHigherThanTriple() {
        compareHands(createHand("9H 10S JD QC KD"), createHand("AC AH 10S 9C AS"), CompareType.BIGGER);
    }

    /**
     * @param hand1 first hand
     * @param hand2 second hand
     * @param type expectation for hand1 compared to hand2
     */
    private void compareHands(PokerHand hand1, PokerHand hand2, CompareType type) {

        int result = hand1.compareTo(hand2);
        String h1 =  hand1 + " score=" + hand1.getScore() + " type="+ scorer.determineType(hand1);
        String h2 =  hand2 + " score=" + hand2.getScore() + " type="+ scorer.determineType(hand2);

        switch (type) {
         case BIGGER:
             assertTrue("Unexpected " + h1 + " unexpectedly less than or equals to " + h2, result > 0);
             break;
         case SMALLER:
             assertTrue("Unexpected " + h1 + " unexpectedly greater than or equal to " + h2,  result < 0);
             break;
        case SAME :
             assertTrue("Unexpected " + h1 + " unexpectedly not equal to " + h2, result == 0);
             break;
        }
    }
}