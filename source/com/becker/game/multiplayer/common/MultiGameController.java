package com.becker.game.multiplayer.common;

import com.becker.game.card.*;
import com.becker.game.common.*;
import com.becker.game.common.online.*;
import com.becker.game.multiplayer.common.ui.MultiGameViewer;
import com.becker.game.multiplayer.trivial.player.*;
import com.becker.game.multiplayer.trivial.ui.*;
import com.becker.optimization.*;

import java.util.*;
import java.util.List;

/**
 * Abstract base class for multi player game controllers.
 *
 * @author Barry Becker
 */
public abstract class MultiGameController extends GameController
{

    protected int currentPlayerIndex_;
 
    // there is a different starting player each round
    protected int startingPlayerIndex_ = 0;
    
    // the ith play in a given round
    protected int playIndex_ = 0;
        
    protected  MultiGameController()
    {
        this(0, 0);
    }
    
    /**
     *  Construct the game controller given an initial board size
     */
    protected MultiGameController(int nrows, int ncols )
    {
        board_ = createTable( nrows, ncols);
        initializeData();
    }
    
    protected abstract Board createTable(int nrows, int ncols);


    /**
     * Return the game board back to its initial openning state
     */
    public void reset()
    {
        super.reset();
        initializeData();
    }

    protected void initializeData()
    {
        startingPlayerIndex_ = 0;
        playIndex_ = 0;
        currentPlayerIndex_ = 0;

        initPlayers();        
    }

    public GameOptions getOptions() {
        if (gameOptions_ == null) {
            gameOptions_ = createOptions();
        }
        return gameOptions_;
    }
    
    protected abstract GameOptions createOptions();

    /**
     * by default we start with one human and one robot player.
     */
    protected abstract void initPlayers();
 

    /**
     *
     * @return the player whos turn it is now.
     */
    public MultiGamePlayer getCurrentPlayer()
    {
        return (MultiGamePlayer)players_.get(currentPlayerIndex_);
    }

    public void computerMovesFirst()
    {
        MultiGameViewer gviewer  = (MultiGameViewer) this.getViewer();
        gviewer.doComputerMove(getCurrentPlayer());
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

    public int getServerPort()
    {
        assert false : "online game play not supported for " + this.getClass().getName();
        return 0;
    }


    /**
     * advance to the next player turn in order.
     * @return the index of the next player to play.
     */
    public int advanceToNextPlayer()
    {

        MultiGameViewer pviewer = (MultiGameViewer) getViewer();
        pviewer.refresh();

        // show message when done.
        if (isDone()) {
            pviewer.sendGameChangedEvent(null);
            return 0;
        }
        
        int nextIndex = advanceToNextPlayerIndex();        

        if (!getCurrentPlayer().isHuman() && !isDone()) {
            pviewer.doComputerMove(getCurrentPlayer());
        }

        // fire game changed event
        pviewer.sendGameChangedEvent(null);

        return nextIndex;
    }

    /**
     * @return the player with the best Trivial hand
     */
    public abstract MultiGamePlayer determineWinner();

    /**
     * make it the next players turn
     * @return the index of the next player
     */
    protected abstract int advanceToNextPlayerIndex();
   

    /**
     *  @return the player that goes first.
     */
    public Player getFirstPlayer()
    {
        return players_.get(startingPlayerIndex_);
    }

    protected MultiGamePlayer getPlayer(int index) {
        return (MultiGamePlayer) getPlayers().get(index);
    }

    
    /**
     * @param players  the players currently playing the game
     */
    public void setPlayers( List<? extends Player> players )
    {
        super.setPlayers(players);
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
