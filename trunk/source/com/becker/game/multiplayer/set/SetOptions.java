package com.becker.game.multiplayer.set;

import com.becker.game.common.*;

/**
 * Set Game options
 * Some other possibilities to add
 *   - use only one color (need to specify which color).
 *   -
 *
 * @author Barry Becker Date: Sep 2, 2006
 */
public class SetOptions extends GameOptions {


    // initial number of cards shown face up on the board.
    private static final int INITIAL_NUM_CARDS_SHOWN = 12;

    private int initialNumCardsShown_ = INITIAL_NUM_CARDS_SHOWN;

    public SetOptions() {}
    
    public SetOptions(int initialNumCards) {
         setInitialNumCardsShown(initialNumCards);
    }


    public int getInitialNumCardsShown() {
        return initialNumCardsShown_;
    }

    public void setInitialNumCardsShown(int initialNumCardsShown) {
        this.initialNumCardsShown_ = initialNumCardsShown;
    }
}
