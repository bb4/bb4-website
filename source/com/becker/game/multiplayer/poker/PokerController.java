package com.becker.game.multiplayer.poker;

import com.becker.game.card.*;
import com.becker.game.common.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.becker.game.multiplayer.poker.player.*;
import com.becker.game.multiplayer.poker.ui.*;

import java.util.*;
import java.util.List;

/**
 * Defines everything the computer needs to know to play Poker.
 * 
 *
 * ToDo list
 *  - for chat, you should only chat with those at your table if you are in t a game, else chat only with those not in a game.
 * - something screwed up adding players out of order
 * - fix TrivialMarker not showing number.
 * - move most of what is in the trivial game up to multiplayer common.
 * - Make PokerHumanPlayer return a PokerAction
 * - SurrogatePokerPlayer should wait (block) on an Action from the client or server.
 * - All players should have an action that they perform (for all games. This action is like a move in a 2 player game.)
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
 *      - unless really done, only then can you exit.
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
public class PokerController extends MultiGameController
{

    private static final int DEFAULT_NUM_ROWS = 32;
    private static final int DEFAULT_NUM_COLS = 32;

    private static final int POKER_SERVER_PORT = 4443;

    private int pot_;

    /**
     *  Construct the Poker game controller
     */
    public PokerController()
    {
        super( DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS );   
    }

    /**
     * Return the game board back to its initial openning state
     */
    @Override
    public void reset()
    {
        super.reset();
        initializeData();
        pot_ = 0;
        anteUp();
    }

    public GameOptions createOptions() {
        return new PokerOptions();
    }
    
    /**
     *  Construct the game controller given an initial board size
     */
    protected Board createTable(int nrows, int ncols )
    {
        return new PokerTable(nrows, ncols);
    }


    /**
     * by default we start with one human and one robot player.
     */
    protected void initPlayers()
    {
        // we just init the first time.
        // After that, they can change manually to get different players.
        if (players_ == null) {
            // create the default players. One human and one robot.
            players_ = new ArrayList<PokerPlayer>(2);
            @SuppressWarnings("unchecked")
            List<PokerPlayer> pplayers = (List<PokerPlayer>)players_;

            pplayers.add(PokerPlayer.createPokerPlayer("Player 1",
                                       100, MultiGamePlayer.getNewPlayerColor(pplayers), true));


            pplayers.add(PokerPlayer.createPokerPlayer("Player 2",
                                       100, PokerPlayer.getNewPlayerColor(pplayers), false));
            players_.get(1).setName(pplayers.get(1).getName()+'('+((PokerRobotPlayer)players_.get(1)).getType()+')');
        }

        dealCardsToPlayers(5);
        currentPlayerIndex_ = 0;
        
        ((PokerTable)board_).initPlayers(players_, this);
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
            PokerPlayer player = null;
            if (p.isSurrogate()) {
                player = (PokerPlayer) ((SurrogateMultiPlayer)p).getPlayer();
                
            }
            else {
                player = (PokerPlayer) p;
            }
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


    public int getPotValue() {
        return pot_;
    }

    public void setPotValue(int potValue) {
        pot_ = potValue;
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
        PokerPlayer winner = (PokerPlayer)determineWinner();
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
            while (((PokerPlayer)getPlayer(startingPlayerIndex_)).isOutOfGame());

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
    public MultiGamePlayer determineWinner() {
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
    protected int advanceToNextPlayerIndex()
    {
        playIndex_++;
        currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.size();
        while (((PokerPlayer)getPlayer(currentPlayerIndex_)).hasFolded())
            currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.size();

        return currentPlayerIndex_;
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

}
