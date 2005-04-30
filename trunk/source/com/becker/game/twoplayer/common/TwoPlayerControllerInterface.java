package com.becker.game.twoplayer.common;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.common.*;
import com.becker.optimization.Optimizee;

import java.util.LinkedList;

/**
 * This is the interface that all 2 palyer game controllers should implement.
 * Providing both an interface and an abstract implementation is a pattern
 * which maximizes flexibility in a framework. The interface defines the
 * public contract. The abstract class may be package private if we don't
 * want to expose it. Other classes may implement this interface without
 * extending the abstract base class.
 *   Another purpose of this interface is to limit the methods available to a
 * given client. We do not want to expose all the TwoPlayerController methods to the UI client.
 * The SearchStrategy (see com.becker.game.twoplayer.common.search package) classes call
 * Searchable methods to do their search.
 * The Optimizer (see com.becker.optimization package) calls Optimizee interface methods.
 *
 * @see com.becker.game.twoplayer.common.TwoPlayerController for the abstract implementation of this interface
 * @see com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer
 * @see com.becker.game.common.Board
 *
 * @author Barry Becker
 */
public interface TwoPlayerControllerInterface
         extends GameControllerInterface, Optimizee, Searchable
{


    public Player getPlayer1();
    public Player getPlayer2();

    /**
     * @return true if player ones turn.
     */
    public boolean isPlayer1sTurn();


    /**
     * @return the class which shows the current state of the game board. May be null.
     */
    public TwoPlayerViewerCallbackInterface get2PlayerViewer();

    /**
     * retract the most recently played move
     * @return  the move which was undone (null returned if no prior move)
     */
    public Move undoLastMove();

    /**
     * this makes an arbitrary move (assumed valid) and
     * adds it to the move list.
     * Calling this does not keep track of weights or the search.
     * Its most common use is for browsing the game tree.
     *  @param m the move to play.
     */
    public void makeMove( Move m );

    /**
     * @return the list of moves made so far.
     */
    public LinkedList getMoveList();

    /**
     * @return  the number of moves currently played.
     */
    public int getNumMoves();

    /**
     *  @return true if the viewer is currently processing (i.e. searching)
     */
    public boolean isProcessing();

    /**
     * pause computation
     */
    public void pause();


    /**
     *
     * @return true if currently paused.
     */
    public boolean isPaused();

    /**
     * When this method is called the game controller will asynchronously search for the next move
     * for the computer to make. It returns immediately (unless the computer is playing itself).
     * Usually returns false, but will return true if it is a computer vs computer game, and the
     * game is over.
     * @param isPlayer1
     * @return true if the game is over.
     * @throws java.lang.AssertionError thrown if something bad happened while searching.
     */
    public boolean requestComputerMove(final boolean isPlayer1) throws AssertionError;

}
