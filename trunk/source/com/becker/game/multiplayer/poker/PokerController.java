package com.becker.game.multiplayer.poker;

import com.becker.game.card.*;
import com.becker.game.common.*;
import com.becker.game.common.online.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.multiplayer.poker.player.*;
import com.becker.game.multiplayer.poker.ui.*;
import com.becker.optimization.*;

import java.util.*;
import java.util.List;

/**
 * Defines everything the computer needs to know to play Poker.
 *
 * ToDo list
 * - change GameController.setPlayer and getPlayers to use Lists rather than Arrays (refactor)
 * - Make PokerHumanPlayer return a PokerAction
 * - SurrogatePokerPlayer should wait (block) on an Action from the client or server.
 *
 * - add host and port to game options
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
 *      - unless really done, onmly then can you exit.
 *
 *  bugs
 *     - at end of game the winning players winnings are not added to his cash.
 *     - robot player keeps adding last raise amount even though competitor is calling.
 *     - Raise amount not always matched! seems to happen in a multiplayer game when robots involved.
 *       this is because it should only inlcude the callAmount if the player has not aleady gone.
 *     - reduce player radii
 *  possible bugs
 *    - ante getting subtracted twice
 *
 * @author Barry Becker
 */
public class PokerController extends GameController
{

    private static final int DEFAULT_NUM_ROWS = 32;
    private static final int DEFAULT_NUM_COLS = 32;

    private static final int POKER_SERVER_PORT = 4443;

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
        ((PokerTable)board_).initPlayers((List<PokerPlayer>)players_, this);
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
            players_ = new ArrayList<PokerPlayer>(2);
            List<PokerPlayer> pplayers = (List<PokerPlayer>)players_;

            pplayers.add(PokerPlayer.createPokerPlayer("Player 1",
                                       100, MultiGamePlayer.getNewPlayerColor(pplayers), true));


            pplayers.add(PokerPlayer.createPokerPlayer("Player 2",
                                       100, PokerPlayer.getNewPlayerColor(pplayers), false));
            players_.get(1).setName(pplayers.get(1).getName()+'('+((PokerRobotPlayer)players_.get(1)).getType()+')');
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
        assert (players_ != null) : "No players! (players_ is null)";
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
        List<PokerPlayer> players = (List<PokerPlayer>)getPlayers();
        for (final PokerPlayer p : players) {
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
        List<PokerPlayer> players = (List<PokerPlayer>)getPlayers();
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
        return players_.get(currentPlayerIndex_);
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
     * @return the server connection.
     */
    protected ServerConnection createServerConnection() {

        ServerConnection sc =  new ServerConnection(getServerPort());
        sc.addOnlineChangeListener(this);
        return sc;
    }

    public boolean isOnlinePlayAvailable() {
        return getServerConnection().isConnected();
    }

    public int getServerPort() {
        return POKER_SERVER_PORT;
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

        List<PokerPlayer> players = (List<PokerPlayer>)getPlayers();
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

        PokerGameViewer pviewer = (PokerGameViewer) getViewer();
        pviewer.refresh();

        int nextIndex = advanceToNextPlayerIndex();

        if (roundOver()) {
            // every player left in the game has called.
            PokerRound round = pviewer.createMove(getBoard().getLastMove());
            // records the result on the board.
            makeMove(round);
            pviewer.refresh();

            doRoundOverBookKeeping(pviewer);
        }

        // show message when done.
        // moved from above.
        if (isDone()) {
            pviewer.sendGameChangedEvent(null);
            return 0;
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
        //List<PokerPlayer> players = (List<PokerPlayer>)getPlayers();

        if (allButOneFolded())  {
            return true;
        }

        // special case of no one raising
        int contrib = this.getCurrentMaxContribution();
        System.out.println("in roundover check max contrib="+contrib);

        for (Player pp : getPlayers()) {
            PokerPlayer p = (PokerPlayer)pp;
            if (!p.hasFolded()) {
                assert(p.getContribution() <= contrib) :
                       "contrib was supposed to be the max, but " + p + " contradicats that.";
                if (p.getContribution() != contrib) {
                    return false;
                }
            }
        }

        return ((playIndex_ >= getNumNonFoldedPlayers()) );
    }

    /**
      * @return  number of active players.
      */
     public int getNumNonFoldedPlayers()
     {
        // a player is not counted as active if he is "out of the game".
        List<PokerPlayer> players = (List<PokerPlayer>)getPlayers();
        int count = 0;
        for (final PokerPlayer p : players) {
            if (!p.isOutOfGame())
                count++;
        }
        return count;
     }


    /**
     * take care of distributing the pot, dealing, anteing.
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
            while (getPlayer(startingPlayerIndex_).isOutOfGame());

            currentPlayerIndex_ = startingPlayerIndex_;
            playIndex_ = 0;
        }
    }

    private boolean allButOneFolded() {
        List<PokerPlayer> players = (List<PokerPlayer>)getPlayers();

        int numNotFolded = 0;
        for (final PokerPlayer p : players) {
            if (!p.hasFolded()) {
                numNotFolded++;
            }
        }
        return (numNotFolded == 1);
    }

    /**
     *
     * @return the player with the best poker hand
     */
    private PokerPlayer determineWinner() {
        List<PokerPlayer> players = (List<PokerPlayer>)getPlayers();
        PokerPlayer winner;
        PokerHand bestHand;
        int first=0;
        
        while (players.get(first).hasFolded() && first < players.size()) {
            first++;
        }
        if (players.get(first).hasFolded())
            GameContext.log(0, "All players folded. That was dumb. The winner will be random.");

        winner = players.get(first);
        bestHand = winner.getHand();

        for (int i = first+1; i < players.size(); i++) {
            PokerPlayer p = players.get(i);
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
        currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.size();
        while (getPlayer(currentPlayerIndex_).hasFolded())
            currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.size();

        return currentPlayerIndex_;
    }


    private PokerPlayer getPlayer(int index) {
        return (PokerPlayer) getPlayers().get(index);
    }

    /**
     *  @return the player that goes first.
     */
    public Player getFirstPlayer()
    {
        return players_.get(startingPlayerIndex_);
    }

    /**
     * @param players  the players currently playing the game
     */
    public void setPlayers( List<? extends Player> players )
    {
        super.setPlayers(players);
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
        return new LinkedList();
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
