package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.multiplayer.poker.ui.PokerGameViewer;
import com.becker.game.multiplayer.poker.ui.RoundOverDialog;
import com.becker.game.card.Card;
import com.becker.optimization.ParameterArray;

import java.util.*;

/**
 * Defines everything the computer needs to know to play Poker.
 *
 * ToDo list
 * - $number under chips
 * - use real faces for players
 *
 *  - options dialog
 *     - Texas holdem
 *     - n card stud
 *          - num cards for each
 *          - whether to use jokers
 *          - allow n exchanges
 *   - summary dlg
 *      - show who gets pot
 *      - show the pot
 *      - give option to start another round with same players
 *      - unless really done then you can only exit
 *
 * @author Barry Becker
 */
public class PokerController extends GameController
{

    private static final int DEFAULT_NUM_ROWS = 32;
    protected static final int DEFAULT_NUM_COLS = 32;

    private static final int DEFAULT_ANTE = 2;

    private int currentPlayerIndex_;
     private int ante_ = DEFAULT_ANTE;
    private int pot_;

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
        initPlayers();
        ((PokerTable)board_).initPlayers((PokerPlayer[])players_, this);
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
                                       100, PokerPlayer.getNewPlayerColor(gplayers), true);

            players_[1] = PokerPlayer.createPokerPlayer("Player 2",
                                       100, PokerPlayer.getNewPlayerColor(gplayers), false);

        }

        System.out.println(" init players dealcards");
        dealCardsToPlayers(5);
        currentPlayerIndex_ = 0;
    }

    /**
     * deat the casrds.
     * @param numCardsToDealToEachPlayer
     */
    private void dealCardsToPlayers(int numCardsToDealToEachPlayer) {
         // give the default players some cards.
        ArrayList<Card> deck = Card.newDeck();
        for (int i=0; i<players_.length; i++)  {
            if  (deck.size() < numCardsToDealToEachPlayer) {
                // ran out of cards. start a new shuffled deck.
                deck = Card.newDeck();
            }
            PokerPlayer player = ((PokerPlayer)players_[i]);
            player.setHand(new PokerHand(deck, numCardsToDealToEachPlayer));
        }
    }

    /**
     * collect the antes
     */
    public void anteUp() {
        // get players to ante up, if they have not already
        if (this.getPotValue() == 0) {
            for (int i=0; i<players_.length; i++)  {
                PokerPlayer player = ((PokerPlayer)players_[i]);
                player.contributeToPot(this, getAnte());
            }
        }
    }

    public void addToPot(int amount) {
        assert(amount > 0) : "You must add a positive amount";
        pot_ += amount;
    }

    /**
     *
     * @return the maximum contribution made by any player so far
     */
    public int getCurrentMaxContribution() {
       int max = Integer.MIN_VALUE;
        Player[] players = getPlayers();
        for (int i=0; i<players.length; i++) {
            PokerPlayer p = (PokerPlayer)players[i];
            if (p.getContribution() > max) {
                max = p.getContribution();
            }
        }
        return max;
    }

    /**
     *
     * @return the min number of chips of any player
     */
    public int getMaxRaiseAllowed() {
        // loop through the players and return the min number of chips of any player
        int min = Integer.MAX_VALUE;
        Player[] players = getPlayers();
        for (int i=0; i<players.length; i++) {
            PokerPlayer p = (PokerPlayer)players[i];
            if (p.getCash() < min) {
                min = p.getCash();
            }
        }
        return min;
    }

    public void setAnte(int amount) {
        ante_ = amount;
    }

    public int getAnte() {
        return ante_;
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
     * Game is over when everyone has called or folded.
     *
     * @return true if the game is over.
     */
    public boolean done()
    {
        if (getLastMove()==null)
            return false;
        // if one player has all the chips then the game is over
        int numPlayersWithChips = 0;
        Player[] players = getPlayers();
        for (int i=0; i<players.length; i++) {
            PokerPlayer p = (PokerPlayer)players[i];
            if (p.getCash() > 0) {
                numPlayersWithChips++;
            }
        }
        return (numPlayersWithChips == 1);
    }


    /**
     * advance to the next player turn in order.
     * @return the index of the next player to play.
     */
    public int advanceToNextPlayer()
    {
        PokerGameViewer pviewer  = (PokerGameViewer)this.getViewer();
        pviewer.refresh();

        // show message when done.
        System.out.println("done="+done());
        if (done()) {
            System.out.println( "advanceToNextPlayer done" );
            pviewer.sendGameChangedEvent(null);
            return 0;
        }

        int nextIndex = advanceToNextPlayerIndex();

        if (getCurrentPlayer() == getFirstPlayer()) {
            // we've made a complete round.
            // If every non-folded player has contributed the same amount,
            // then show the summary dialog and give the pot to someone.
            // Give the option to continue or exit.
            //    Otherwise we just continue for another round automatically
            // as players continue to raise.
            PokerRound round = pviewer.createMove(getLastMove());

            // records the result on the board.
            makeMove(round);
            pviewer.refresh();

            if (roundOver())  {
                showRoundOver();
            }
        }

        if (!getCurrentPlayer().isHuman()) {
            pviewer.doComputerMove(getCurrentPlayer());
        }

        // fire game changed event
        pviewer.sendGameChangedEvent(null);

        return nextIndex;
    }

    private boolean roundOver() {
        Player[] players = getPlayers();
        int contrib = this.getCurrentMaxContribution();
        for (int i=0; i<players.length; i++) {
            PokerPlayer p = (PokerPlayer)players[i];
            if (!p.hasFolded()) {
                if (p.getContribution() != contrib) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * show who won the round and dispurse the pot
     */
    private void showRoundOver() {
        PokerPlayer winner = determineWinner();
        int winnings = this.getPotValue();
        winner.claimPot(this);
        RoundOverDialog roundOverDlg = new RoundOverDialog(null, winner, winnings);
        roundOverDlg.setVisible(true);
    }

    /**
     *
     * @return the player with the best poker hand
     */
    private PokerPlayer determineWinner() {
        PokerPlayer[] players = (PokerPlayer[])getPlayers();
        PokerPlayer winner = players[0];
        PokerHand bestHand = winner.getHand();

        for (int i=1; i<players.length; i++) {
            PokerPlayer p = players[i];
            if (!p.hasFolded() && p.getHand().compareTo(bestHand) > 0) {
                bestHand = p.getHand();
                winner = p;
            }
        }
        return winner;
    }

    /**
     *
     * @param lastMove
     * @return
     */
    private PokerRound createMove(Move lastMove)
    {
        PokerRound gmove = PokerRound.createMove((lastMove==null)?0:lastMove.moveNumber+1);

        // for each player, apply it for one year
        // if there are battles, show them in the battle dialog and record the result in the move.
        Player[] players = this.getPlayers();

        /* do end of round processing here...
        for (int i=0; i< players.length; i++) {
            List orders = ((PokerPlayer)players[i]).getOrders();
            Iterator orderIt = orders.iterator();
            while (orderIt.hasNext()) {
                Order order = (Order)orderIt.next();
                // have we reached our destination?
                // if so show and record the battle, and then remove the order from the list.
                // If not adjust the distance remaining.
                order.incrementYear();
                if (order.hasArrived()) {
                    //  show battle dialog
                    Planet destPlanet = order.getDestination();

                    gmove.addSimulation(order, destPlanet);

                    //destPlanet.setOwner( gmove.getOwnerAfterAttack());
                    //destPlanet.setNumShips( gmove.getNumShipsAfterAttack() );

                    // remove this order as it has arrived.
                    orderIt.remove();
                }
            }
        }  */

        return gmove;
    }

    /**
     * make it the next players turn
     * @return the index of the next player
     */
    private int advanceToNextPlayerIndex()
    {
        currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.length;
        return currentPlayerIndex_;
    }

    /**
     *  @return the player that goes first.
     */
    public Player getFirstPlayer()
    {
        return players_[0];
    }

    /**
     * @return  the players currently playing the game
     */
    public Player[] getPlayers()
    {
        return players_;
    }

    /**
     * @param players  the players currently playing the game
     */
    public void setPlayers( Player[] players )
    {
        players_ = players;
        // deal cards to the players
        System.out.println("set player dealcards");
        dealCardsToPlayers(5);
    }


    /**
     * @return index of current player that is to give orders
     */
    public int getCurrentPlayerIndex()
    {
        return currentPlayerIndex_;
    }



    /**
     *  Statically evaluate the board position
     *  @return the lastMoves value modified by the value add of the new move.
     *   a large positive value means that the move is good from the specified players viewpoint
     */
    protected double worth( Move lastMove, ParameterArray weights )
    {
        return lastMove.value;
    }

    /*
     * generate all possible next moves.
     * impossible for this game.
     */
    public List generateMoves( Move lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        LinkedList moveList = new LinkedList();
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
