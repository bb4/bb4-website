package com.becker.game.common;

import com.becker.common.Util;

import java.util.*;

/**
 * This is an abstract base class for a Game Controller.
 * It contains the key logic for n player games.
 * It is a much more general form of the TwoPlayerController subclass.
 *
 * Instance of this class process requests from the GameViewer.
 *
 *  @author Barry Becker
 */
public abstract class GameController
           implements GameControllerInterface
{

    // these are the default game constants
    // they may be modified through the ui (see GameOptionsDialog)

    // the board has the layout of the pieces
    protected Board board_ = null;

    // sometimes we want to draw directly to the ui while thinking (for debugging purposes)
    protected ViewerCallbackInterface viewer_ = null;

    // We keep a list of the moves that have been made.
    // We can navigate forward or backward in time using this
    protected final LinkedList moveList_ = new LinkedList();

    // the list of players actively playing the game, in the order that they move.
    protected Player[] players_;


    /**
     * Construct the game controller
     */
    public GameController()
    {
        GameContext.log( 2, " mem=" + Runtime.getRuntime().freeMemory() );
    }

    /**
     * optionally set a viewer for the controller.
     * @param viewer
     */
    public void setViewer(ViewerCallbackInterface viewer)
    {
       viewer_ = viewer;
    }

    /**
     * Return the game board back to its initial openning state
     */
    public void reset()
    {
        board_.reset();
        moveList_.clear();
    }

    /**
     * @return the class which shows the current state of the game board.
     * May be null if the viewer was never set.
     */
    public ViewerCallbackInterface getViewer()
    {
        return viewer_;
    }

    /**
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    public double getStrengthOfWin()
    {
        return 0.0;
    }

    public abstract Player getCurrentPlayer();


    /**
     *  @return the player that goes first.
     */
    public Player getFirstPlayer()
    {
        return getPlayers()[0];
    }


    /**
     * @return the most recent move played on the board. Returns null if there isn't one.
     */
    public final Move getLastMove()
    {
        if ( moveList_ == null || moveList_.isEmpty() )
            return null;
        return (Move) moveList_.getLast();
    }

    /**
     * @return the board representation object.
     */
    public final Board getBoard()
    {
        return board_;
    }

    /**
     * Setup the initial game state.
     */
    protected abstract void initializeData();

    /**
     * retract the most recently played move
     * @return  the move which was undone (null returned if no prior move)
     */
    public Move undoLastMove()
    {
        if ( !moveList_.isEmpty() ) {
            Move m = (Move) moveList_.removeLast();
            board_.undoMove( m );
            return m;
        }
        return null;
    }


    protected boolean checkMove(Move m)
    {
        // confirm that this moves number is one more that the last move
        if (getLastMove()==null) {
            assert (m.moveNumber==1) : "m.moveNumber ="+m.moveNumber;
            return false;
        }
        int lmn = getLastMove().moveNumber;
        if (m.moveNumber == (lmn+1))
            return true;
        else {
            GameContext.log(0, "Error: m.moveNumber ="+m.moveNumber +" lastmove="+lmn);
            return false;
        }
    }


    /**
     * this makes an arbitrary move (assumed valid) and adds it to the move list.
     * Calling this does not keep track of weights or the search.
     * Its most common use is for browsing the game tree.
     *  @param m the move to play.
     */
    public void makeMove( Move m )
    {
        board_.makeMove( m );
        moveList_.add( m );
    }


    /**
     * @return the list of moves made so far
     */
    public final LinkedList getMoveSequence()
    {
        return moveList_;
    }

    /**
     * @return  the number of moves currently played.
     */
    public final int getNumMoves()
    {
        if ( moveList_ == null || moveList_.isEmpty() )
            return 0; // no moves yet
        Move m = (Move) moveList_.getLast();
        assert (m.moveNumber == moveList_.size()): " moveList_.size()="+moveList_.size()+" "+Util.stringify(moveList_);
        return m.moveNumber;
    }

    /**
     * clean things up to avoid memory leaks.
     */
    public final void dispose()
    {
        board_.dispose();
    }

    /**
     * save the current state of the game to a file
     * Use this version when an error occurred and you want to dump the state.
     * There is no default implementation (other than to say it is not implemented).
     * You must override if you want it to work.
     * @param fileName the file to save the state to
     * @param rte exception that occurred upon failure
     */
    public void saveToFile( String fileName, AssertionError rte )
    {
        GameContext.log(0,  "Error: saveToFile(name, rte) not implemented yet" );
    }

    /**
     *
     * @return an array of the players playing the game (in the order that they move).
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
    }

    /**
     * @return  number of active players.
     */
    public int getNumPlayers()
    {
        return players_.length;
    }

    /**
     * @return true if there are only human players
     */
    public boolean allPlayersHuman()
    {
       for (int i=0; i<getNumPlayers(); i++)  {
           if (!players_[i].isHuman()) return false;
       }
       return true;
    }

    /**
     * @return true if there are only computer players
     */
    public boolean allPlayersComputer()
    {
       for (int i=0; i<getNumPlayers(); i++)  {
           if (players_[i].isHuman()) return false;
       }
       return true;
    }

    /**
     * save the current state of the game to a file.
     * You must override if you want it to work.
     */
    public static void saveToFile( String fileName )
    {
        GameContext.log(0,  "Error: saveToFile("+fileName+") not implemented yet" );
    }
}