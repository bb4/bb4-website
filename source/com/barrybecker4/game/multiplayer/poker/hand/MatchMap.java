// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Card;
import com.barrybecker4.game.card.Rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A poker hand typically has 5 cards from a deck of normal playing cards.
 * @author Barry Becker
 */
class MatchMap extends LinkedHashMap<Rank, Integer> {

    /**
     * Constructor.
     * @param hand the initial sorted poker hand (not necessarily 5 cards)
     *             but it must be sorted by rank from high to low!
     */
    MatchMap(List<Card> hand) {
        init(hand);
    }

    /**
     * @return true if there is exactly N of a certain rank in the hand
     * (note: there is not 2 of a kind if there is 4 of a kind)
     */
    boolean hasNofaKind(int num) {

        Collection values = values();
        for (Object value : values) {
            if ((Integer) value == num)
                return true;
        }
        return false;
    }

    /**
     * Finds the ranks with specified num of a kind. Usually there will only be one rand returned in the list.
     * @param num num of a kinds to look for.
     * @return the ranks of the sets of cards that appear n times. Error thrown if it does not have n of a kind.
     * (note: the is not 2 of a kind if there is 4 of a kind)
     * (note: if there is more than 1 n of a kind, a list of ranks in descending order is returned. This can only
     * happen if the rank is 2 or 1)
     */
    List<Rank> getRankOfNofaKind(int num) {

        assert num > 0 && num < 6 : "Requested num="+ num + "is unreasonable.";
        Set<Map.Entry<Rank,Integer>> entries = entrySet();
        List<Rank> ranks = new ArrayList<Rank>();
        for (Map.Entry<Rank,Integer> entry : entries) {

            if ((entry.getValue()) == num) {
                ranks.add(entry.getKey());
            }
        }
        if (ranks.isEmpty()) {
            throw new IllegalStateException("There were not " + num + " of a kind among " + this);
        }
        // should not be needed if the hand is sorted
        //Collections.sort(ranks);
        //Collections.reverse(ranks);
        return ranks;
    }

    /**
     * (note: there are not 2 pairs if there is a full house)
     * @return true if there is exactly 2 pairs
     */
    boolean hasTwoPairs() {

        Collection values = values();
        int numPairs = 0;
        for (Object value : values) {
            if ((Integer) value == 2) {
                numPairs++;
            }
        }
        return (numPairs == 2);
    }

    Card getSecondaryHighCard(List<Card> hand) {
        Card highestRankedCard = null;
        for (Card c : hand) {
            if (get(c.rank()) == 1
                && (highestRankedCard == null || c.rank().ordinal() > highestRankedCard.rank().ordinal())) {
               highestRankedCard = c;
            }
        }
        return highestRankedCard;
    }

    /**
     * @return a map which has an entry for each card rank represented in the hand and its associated count.
     */
    private void init(List<Card> hand) {

        for (Card c : hand) {
            Integer num = get(c.rank());
            if (num != null)  {
                put(c.rank(), num + 1);
            }
            else {
                put(c.rank(), 1);
            }
        }
    }
}
