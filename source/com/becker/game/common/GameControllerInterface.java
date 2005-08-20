package com.becker.game.common;


import java.util.List;

/**
 * This is the interface that all game controllers should implement.
 * Providing both an interface and an abstract implementation is a pattern
 * which maximizes flexibility in a framework. The interface defines the
 * public contract. The abstract class may be package private if we don't
 * want to expose it. Other classes may implement this interface without
 * extending the abstract base class.
 *   Another purpose of this interface is to limit the methods available to a
 * given client. We do not want to expose all the GameController methods to the UI client.
 *
 * @see com.becker.game.common.GameController for the abstract implementation of this interface
 * @see com.becker.game.common.ui.GameBoardViewer
 * @see Board
 *
 * @author Barry Becker
 */
public interface GameControllerInterface
{

    /**
     * @return the board representation object.
     */
    public Board getBoard();

    /**
     * @return the class which shows the current state of the game board. May be null.
     */
    public ViewerCallbackInterface getViewer();

    /**
     * retract the most recently played move
     * @return  the move which was undone (null returned if no prior move)
     */
    public Move undoLastMove();

    /**
     * this makes an arbitrary move (assumed valid) and
     * adds it to the move list.
     * For two player games, calling this does not keep track of weights or the search.
     * Its most common use is for browsing the game tree.
     *  @param m the move to play.
     */
    public void makeMove( Move m );

    
    /**
     * @return the list of moves made so far.
     */
    public List getMoveList();

    /**
     * @return  the number of moves currently played.
     */
    public int getNumMoves();


    /**
     * @return an array of the players playing the game
     */
    public Player[] getPlayers();


    /**
     *  @return the number of active players
     */
    public int getNumPlayers();

    /**
     *
     * @return the player whos turn it is now.
     */
    public Player getCurrentPlayer();

    /**
     *
     * @return true if there are only human players
     */
    public boolean allPlayersHuman();

    /**
     *
     * @return true if there are only coputer players
     */
    public boolean allPlayersComputer();

    /**
     * a coputer player makes the first move
     */
    public void computerMovesFirst();

    /**
     *
     * @return true if the game is over.
     */
    public boolean done();
}
