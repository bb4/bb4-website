// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker;

import com.barrybecker4.game.card.Deck;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.poker.hand.PokerHand;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.game.multiplayer.poker.player.PokerRobotPlayer;
import com.barrybecker4.game.multiplayer.poker.ui.PokerGameViewer;

/**
 * Poker dealer deals the cards to the players from a new shuffled deck.
 *
 * @author Barry Becker
 */
public class Dealer  {

    /**
     *  Construct the Poker game controller
     */
    public Dealer() {
    }


    /**
     * Deal the cards. Give the default players some cards.
     * @param players the players to deal to.
     * @param numCardsToDealToEachPlayer
     */
    public void dealCardsToPlayers(PlayerList players, int numCardsToDealToEachPlayer) {

        Deck deck = new Deck();
        assert (players != null) : "No players! (players_ is null)";
        for (Player p : players) {
            if (deck.size() < numCardsToDealToEachPlayer) {
                // ran out of cards. start a new shuffled deck.
                deck = new Deck();
            }
            PokerPlayer player = (PokerPlayer) p.getActualPlayer();
            player.setHand(new PokerHand(deck, numCardsToDealToEachPlayer));
            player.resetPlayerForNewRound();
        }
    }

}
