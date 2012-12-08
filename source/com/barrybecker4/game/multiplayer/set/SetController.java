/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;

import java.util.LinkedList;
import java.util.List;

/**
 * Defines everything the computer needs to know to play Set with multiple players.
 *
 * todo
 *  - selection does not always take
 *  - show sets panel does not scroll.
 *  - add hints
 *
 * @author Barry Becker
 */
public class SetController extends MultiGameController {

    /** the deck is like the board or model  */
    private List<Card> deck_;

    /** num cards on the board at the current moment.  */
    private int numCardsShown_;

    private static final int NO_PLAYER_SELECTED = -1;

    /** currently selected player. -1 if none selected        */
    private int currentPlayerIndex_ = NO_PLAYER_SELECTED;

    /** the maximum number of cards you can have and still not have a set (exceedingly rare). */
    private static final int MAX_CARDS_BEFORE_SET = 20;


    /**
     *  Construct the Set game controller
     */
    public SetController() {
         initializeData();
    }


    @Override
    protected Board createTable(int rowl, int cols) {
        return null;
    }

    /**
     * Return the game board back to its initial opening state
     */
    @Override
    public void reset() {
        initializeData();
    }

    @Override
    protected void initializeData() {
        deck_ = Card.newDeck();
        numCardsShown_ = ((SetOptions)getOptions()).getInitialNumCardsShown();

        initPlayers();
        gameChanged();
    }

    /**There is not a concept of turns in set.
     * @return the index of the next player
     */
    @Override
    protected int advanceToNextPlayerIndex() {
        assert false : "not used in set game";
        return 0;
    }

    /**
     *
     * @return the deck of cards (numCardsShown of which are shown face up on the board)
     */
    public List<Card> getDeck()  {
        return deck_;
    }

    @Override
    public GameOptions createOptions() {
        return new SetOptions();
    }

    @Override
    public boolean isOnlinePlayAvailable() {
        return false;
    }

    /**
     * @return the number of face up cards on the board.
     */
    public int getNumCardsShowing() {
        return numCardsShown_;
    }

    /**
     * @param num  the number of cards to turn face up on the board.
     */
    public void addCards(int num) {
        for (int i=0; i<num; i++) {
            if (hasCardsToAdd()) {
                numCardsShown_++;
            }
        }
        gameChanged();
    }

    /**
     * remove a card from the board and put it back in the deck.
     */
    public void removeCard() {
        if (canRemoveCards()) {
            numCardsShown_--;
        }
        gameChanged();
    }

    /**
     * @return true if legal to remove more cards from the board.
     */
    public boolean canRemoveCards() {
        return (numCardsShown_ > 3);
    }

    /**
     * @return true if not showing cards remain in deck, or we have not yet reached MAX_CARDS_BEFORE_SET visible cards.
     */
    public boolean hasCardsToAdd() {
        return (numCardsShown_ < deck_.size() && numCardsShown_ < MAX_CARDS_BEFORE_SET);
    }


    /**
     * @param cards to remove (usually a set that has been discovered by a player)
     */
    public void removeCards(List<Card> cards) {
        deck_.removeAll(cards);
        numCardsShown_ -= 3;
        gameChanged();
    }

    private List<Card> getCardsOnBoard() {
        List<Card> cardsOnBoard = new LinkedList<Card>();
        for (int i = 0; i<getNumCardsShowing(); i++ ) {
            cardsOnBoard.add(getDeck().get(i));
        }
        return cardsOnBoard;
    }

    public void setCurrentPlayer(Player player)  {
        if (player == null) {
            currentPlayerIndex_ = NO_PLAYER_SELECTED;
            return;
        }
        for (int i = 0; i < getPlayers().size(); i++) {
            if (player == getPlayers().get(i)) {
                currentPlayerIndex_ = i;
                return;
            }
        }
    }

    public List<Card> getSetsOnBoard()  {
       return Card.getSets(getCardsOnBoard());
    }

    /**
     * @return  the number of sets that are currently on the board and have not yet been discovered.
     */
    public int getNumSetsOnBoard() {
        return Card.numSets(getCardsOnBoard());
    }

     /**
      * By default we start with one human and one robot player.
      * After that, they can change manually to get different players.
      */
    @Override
    protected void initPlayers() {

        if (getPlayers() == null) {
            // create the default players. One human and one robot.
            PlayerList players = new PlayerList();
            players.add(SetPlayer.createSetPlayer("Player 1", SetPlayer.getNewPlayerColor(players), true));
            players.add(SetPlayer.createSetPlayer("Player 2", SetPlayer.getNewPlayerColor(players), false));
            players.get(1).setName(players.get(1).getName()+'('+((SetRobotPlayer)players.get(1)).getRobotType()+')');
            setPlayers(players);
        }
    }

    @Override
    public MultiGamePlayer getCurrentPlayer() {
        if (currentPlayerIndex_ != NO_PLAYER_SELECTED) {
            return (MultiGamePlayer)getPlayers().get(currentPlayerIndex_);
        }
        return null;
    }

    public void gameChanged() {
        if (getViewer() != null)  {
            ((GameBoardViewer) getViewer()).sendGameChangedEvent(null);
        }
    }

    @Override
    public void computerMovesFirst() {
        assert false : "No one moves first in set.";
    }


    /**
     * Game is over when there are no more sets to be found.
     *
     * @return true if the game is over.
     */
    public boolean isDone() {
        return !Card.hasSet(deck_);
    }

    /**
     *
     * @return the player with the most sets
     */
    @Override
    public SetPlayer determineWinner() {
        PlayerList players = getPlayers();
        SetPlayer winner;

        int first=0;

        winner = (SetPlayer)players.get(first);
        int mostSets = winner.getNumSetsFound();

        for (int i = first+1; i < players.size(); i++) {
            SetPlayer p = (SetPlayer)players.get(i);
            if (p.getNumSetsFound() > mostSets) {
                mostSets = p.getNumSetsFound();
                winner = p;
            }
        }

        return winner;
    }

    /**
     *  @return the player that goes first.
     *
    @Override
    public Player getFirstPlayer() {
        GameContext.log(0,"There is not actual first player in set");
        return null;
    }  */

}
