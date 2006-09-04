package com.becker.game.multiplayer.poker;

import com.becker.game.card.*;
import com.becker.game.common.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.multiplayer.poker.ui.*;
import com.becker.optimization.*;

import java.util.*;

/**
 * Defines everything the computer needs to know to play Poker.
 *
 * ToDo list
 * - use real faces for players
 *
 *  - options dialog
 *     - Texas holdem
 *     - N card stud
 *          - num cards for each
 *          - whether to use jokers
 *          - allow n exchanges
 *          - raise limit (eg $20)
 *   - summary dlg
 *      - show who gets pot
 *      - show the pot
 *      - give option to start another round with same players
 *      - unless really done then you can only exit
 *
 *  bugs
 *     - Raise amount not always matched! seems to happen in a multiplayer game when robots involved.
 *       this is because it should only inlcude the callAmount if the player has not aleady gone
 *     - reduce player radii
 *  possible bugs
 *    - ante getting subtracted twice
 *    - asking folded player to play  (fixed?)
 *
 * @author Barry Becker
 */
public class PokerController extends GameController
{

    private static final int DEFAULT_NUM_ROWS = 32;
    protected static final int DEFAULT_NUM_COLS = 32;

    private int currentPlayerIndex_;
    private int pot_;
    // there is a different starting player each round
    private int startingPlayerIndex_ = 0;
    // the ith play in a given round
    private int playIndex_ = 0;

    /**
     *  Construct the Poker game controller
     */
    public PokerController()
    {
        board_ = new PokerTable( DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS );
        initializeData();
    }

    /**
     *  Construct the Poker game controller given an initial board size
     */
    public PokerController(int nrows, int ncols )
    {
        board_ = new PokerTable( nrows, ncols);
        initializeData();
    }


    /**
     * Return the game board back to its initial openning state
     */
    public void reset()
    {
        super.reset();
        initializeData();
        anteUp();
    }

    protected void initializeData()
    {
        pot_ = 0;
        startingPlayerIndex_ = 0;
        playIndex_ = 0;
        currentPlayerIndex_ = 0;

        initPlayers();
        ((PokerTable)board_).initPlayers((PokerPlayer[])players_, this);
    }

    public GameOptions getOptions() {
        if (gameOptions_ == null) {
            gameOptions_ = new PokerOptions();
        }
        return gameOptions_;
    }

    /**
     * by default we start with one human and one robot player.
     */
    private void initPlayers()
    {
        // we just init the first time.
        // After that, they can change manually to get different players.
        if (players_ == null) {
            // create the default players. One human and one robot.
            players_ = new PokerPlayer[2];
            PokerPlayer[] gplayers = (PokerPlayer[])players_;
            //PokerHand hand = new PokerHand(null);
            players_[0] = PokerPlayer.createPokerPlayer("Player 1",
                                       100, MultiGamePlayer.getNewPlayerColor(gplayers), true);


            players_[1] = PokerPlayer.createPokerPlayer("Player 2",
                                       100, PokerPlayer.getNewPlayerColor(gplayers), false);
            players_[1].setName(players_[1].getName()+'('+((PokerRobotPlayer)players_[1]).getType()+')');
        }

        dealCardsToPlayers(5);
        currentPlayerIndex_ = 0;
    }

    /**
     * deat the casrds.
     * @param numCardsToDealToEachPlayer
     */
    private void dealCardsToPlayers(int numCardsToDealToEachPlayer) {
         // give the default players some cards.
        List deck = Card.newDeck();
        for (Player p : players_) {
            if (deck.size() < numCardsToDealToEachPlayer) {
                // ran out of cards. start a new shuffled deck.
                deck = Card.newDeck();
            }
            PokerPlayer player = ((PokerPlayer) p);
            player.setHand(new PokerHand(deck, numCardsToDealToEachPlayer));
            player.resetPlayerForNewRound();
        }
    }

    /**
     * collect the antes
     */
    public void anteUp() {
        // get players to ante up, if they have not already
        if (this.getPotValue() == 0) {
            for (final Player p : players_) {
                PokerPlayer player = ((PokerPlayer) p);
                // if a player does not have enough money to ante up, he is out of the game
                player.contributeToPot(this, ((PokerOptions)getOptions()).getAnte());

            }
        }
    }

    public void addToPot(int amount) {
        assert(amount > 0) : "You must add a positive amount";
        pot_ += amount;
    }

    /**
     * @return the maximum contribution made by any player so far
     */
    public int getCurrentMaxContribution() {
       int max = Integer.MIN_VALUE;
        Player[] players = getPlayers();
        for (final Player p : players) {
            PokerPlayer player = (PokerPlayer) p;
            if (player.getContribution() > max) {
                max = player.getContribution();
            }
        }
        return max;
    }


    /**
     *
     * @return the min number of chips of any player
     */
    public int getAllInAmount() {
        // loop through the players and return the min number of chips of any player
        int min = Integer.MAX_VALUE;
        Player[] players = getPlayers();
        for (final Player p : players) {
            PokerPlayer player = (PokerPlayer) p;
            if (!player.hasFolded() && ((player.getCash() + player.getContribution()) < min)) {
                min = player.getCash() + player.getContribution();
            }
        }
        return min;
    }

    /**
     *
     * @return the player whos turn it is now.
     */
    public Player getCurrentPlayer()
    {
        return players_[currentPlayerIndex_];
    }

    public void computerMovesFirst()
    {
        PokerGameViewer gviewer  = (PokerGameViewer)this.getViewer();
        gviewer.doComputerMove(getCurrentPlayer());
    }


    public int getPotValue() {
        return pot_;
    }

    public void setPotValue(int potValue) {
        pot_ = potValue;
    }

    /**
     * Game is over when only one player has enough money left to play
     *
     * @return true if the game is over.
     */
    public boolean isDone()
    {
        if (getBoard().getLastMove() == null)
            return false;

        Player[] players = getPlayers();
        int numPlayersStillPlaying = 0;
        for (Player p : players) {
            PokerPlayer player = (PokerPlayer) p;
            if (!player.isOutOfGame())
                numPlayersStillPlaying++;
        }
        return (numPlayersStillPlaying == 1);
    }


    /**
     * advance to the next player turn in order.
     * @return the index of the next player to play.
     */
    public int advanceToNextPlayer()
    {
        PokerGameViewer pviewer  = (PokerGameViewer) getViewer();
        pviewer.refresh();

        // show message when done.
        if (isDone()) {
            pviewer.sendGameChangedEvent(null);
            return 0;
        }


        int nextIndex = advanceToNextPlayerIndex();

        if (roundOver()) {
            // every player left in the game has called.
            PokerRound round = pviewer.createMove(getBoard().getLastMove());
            // records the result on the board.
            makeMove(round);
            pviewer.refresh();

            doRoundOverBookKeeping(pviewer);
        }

        if (!getCurrentPlayer().isHuman() && !isDone()) {
            pviewer.doComputerMove(getCurrentPlayer());
        }

        // fire game changed event
        pviewer.sendGameChangedEvent(null);

        return nextIndex;
    }


    /**
     * the round is over if there is only one player left who has not folded, or
     * everyone has had a chance to call.
     * @return true of the round is over
     */
    private boolean roundOver() {
        PokerPlayer[] players = (PokerPlayer[])getPlayers();

        if (allButOneFolded())  {
            return true;
        }

        // special case of no one raising
        int contrib = this.getCurrentMaxContribution();

        for (PokerPlayer p : players) {
            if (!p.hasFolded()) {
                if (p.getContribution() != contrib) {
                    return false;
                }
            }
        }

        if ((playIndex_ < getNumNonFoldedPlayers()) ) {
            return false;
        }
        return true;
    }

    /**
      * @return  number of active players.
      */
     public int getNumNonFoldedPlayers()
     {
        // a player is not counted as active if he is "out of the game".
        PokerPlayer[] players = (PokerPlayer[])getPlayers();
        int count = 0;
        for (final PokerPlayer p : players) {
            if (!p.isOutOfGame())
                count++;
        }
        return count;
     }


    /**
     * take care of distrbuting the pot, dealing, anteing.
     * @param pviewer
     */
    private void doRoundOverBookKeeping(PokerGameViewer pviewer) {
        PokerPlayer winner = determineWinner();
        int winnings = this.getPotValue();
        winner.claimPot(this);
        pviewer.showRoundOver(winner, winnings);
        // start a new round deal new cards and ante

        // if the game is over, we don't want to continue.
        if (!isDone()) {
            dealCardsToPlayers(5);
            anteUp();
            // the player to start the betting in the next round is the next player who still has some money left.
            do {
               startingPlayerIndex_ = (++startingPlayerIndex_) % this.getNumPlayers();
            }
            while (this.getPlayer(startingPlayerIndex_).isOutOfGame());

            currentPlayerIndex_ = startingPlayerIndex_;
            playIndex_ = 0;
        }
    }

    private boolean allButOneFolded() {
        PokerPlayer[] players = (PokerPlayer[])getPlayers();

        int numNotFolded = 0;
        for (final PokerPlayer p : players) {
            if (!p.hasFolded()) {
                numNotFolded++;
            }
        }
        if (numNotFolded == 1) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return the player with the best poker hand
     */
    private PokerPlayer determineWinner() {
        PokerPlayer[] players = (PokerPlayer[])getPlayers();
        PokerPlayer winner;
        PokerHand bestHand;
        int first=0;
        //
        while (players[first].hasFolded() && first < players.length) {
            first++;
        }
        if (players[first].hasFolded())
            GameContext.log(0, "All players folded. That was dumb. The winner will be random.");

        winner = players[first];
        bestHand = winner.getHand();

        for (int i=first+1; i<players.length; i++) {
            PokerPlayer p = players[i];
            if (!p.hasFolded() && p.getHand().compareTo(bestHand) > 0) {
                bestHand = p.getHand();
                winner = p;
            }
        }
        return winner;
    }

    /**
     * make it the next players turn
     * @return the index of the next player
     */
    private int advanceToNextPlayerIndex()
    {
        playIndex_++;
        currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.length;
        while (getPlayer(currentPlayerIndex_).hasFolded())
            currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.length;

        return currentPlayerIndex_;
    }


    private PokerPlayer getPlayer(int index) {
        return (PokerPlayer) getPlayers()[index];
    }

    /**
     *  @return the player that goes first.
     */
    public Player getFirstPlayer()
    {
        return players_[startingPlayerIndex_];
    }

    /**
     * @param players  the players currently playing the game
     */
    public void setPlayers( Player[] players )
    {
        players_ = players;
        // deal cards to the players
        dealCardsToPlayers(5);
    }

    /**
     *  Statically evaluate the board position
     *  @return the lastMoves value modified by the value add of the new move.
     *   a large positive value means that the move is good from the specified players viewpoint
     */
    protected double worth( Move lastMove, ParameterArray weights )
    {
        return lastMove.getValue();
    }

    /*
     * generate all possible next moves.
     * impossible for this game.
     */
    public List generateMoves( Move lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        List moveList = new LinkedList();
        return moveList;
    }

    /**
     * return any moves that result in a win
     */
    public List generateUrgentMoves( Move lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        return null;
    }

    /**
     * @param m
     * @param weights
     * @param player1sPerspective
     * @return true if the last move created a big change in the score
     */
    public boolean inJeopardy( Move m, ParameterArray weights, boolean player1sPerspective )
    {
        return false;
    }

}
