package com.becker.game.multiplayer.common;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.game.common.*;
import com.becker.game.common.online.*;
import com.becker.game.multiplayer.common.online.SurrogatePlayer;
import com.becker.game.multiplayer.common.ui.MultiGameViewer;

import java.util.*;
import java.util.List;

/**
 * Abstract base class for multi player game controllers.
 * 
 * Online play should work like this:
 *  Case One: no robot players, all players are humans on client computers.
 *    - One of the human players creates a table with certain parameters that define 
 *      the game to be played.
 *   - Each client and the server create an instance of the game controller.
 *     On each client there is a human player representing the player on that client,
 *     and surrogate player objects representing all the other human players.
 *   - The server will have surrogates for all the human players.
 *   - When it is a given players turn, they specify their action.
 *     That action is sent in a message to the server. The server then broadcasts
 *     the response (in this case the players action) to all OnlineChangeListeners.
 *     Since all the surrogates for that player (one on the server and one on each client 
 *     except the one representing that acutal player) are OnlineChangeListeners,
 *     they will get the event and know to set the action so that it can be retrieved by 
 *     that controller.
 *   - When a surrogate is asked for its action, it blocks until it actually recieved the action
 *     in that response message from the server.
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
    protected IServerConnection createServerConnection() {

        IServerConnection sc =  new ServerConnection(getServerPort());
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
     * Apply the action from the client for the associated player.
     * @param action
     *
    public void handlePlayerAction(PlayerAction action) {
        // find the player and set his action            
        for (Player p : players_) {            
            if (p.getName().equals(action.getPlayerName())) {
                ((MultiGamePlayer)p).setAction(action);
            }
        }
    }*/

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

        if (!isDone()) {
            if (getCurrentPlayer().isSurrogate()) {
                System.out.println("about to do surrogate move for " + getCurrentPlayer() + " in controller="+this + " in thread="+Thread.currentThread().getName());
                pviewer.doSurrogateMove((SurrogatePlayer)getCurrentPlayer());
            }         
            else if (!getCurrentPlayer().isHuman()) {
                pviewer.doComputerMove(getCurrentPlayer());
            }                          
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
