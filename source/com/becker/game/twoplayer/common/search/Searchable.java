package com.becker.game.twoplayer.common.search;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.ui.*;
import com.becker.optimization.*;

import java.util.*;


/**
 * This is the interface that all game controllers should implement if they want to allow searching.
 *
 *  One purpose of this interface is to limit the TwoPlayerController methods available to search.
 *  The SearchStrategy classes call methods of this interface to do their search.
 *
 * @see TwoPlayerController for the default implementation of this interface
 * @see TwoPlayerBoardViewer
 *
 * @author Barry Becker
 */
public interface Searchable
{

    /**
     * @return the amount of lookahead (number of plys) used by the search strategy
     */
    int getLookAhead();

    /**
     * @return true if alpha-beta pruning is being employed by the search strategy.
     */
    boolean getAlphaBeta();

    /**
     * @return whether or not the quiescent search option is being used by the search strategy
     */
    boolean getQuiescence();

    /**
     * @param m the move to play.
     */
    void makeInternalMove( TwoPlayerMove m );

    /**
     * takes back the most recent move.
     * @param m
     */
    void undoInternalMove( TwoPlayerMove m );

    /**
     * given a move determine whether the game is over.
     * If recordWin is true then the variables for player1/2HasWon can get set.
     * sometimes, like when we are looking ahead in search we do not want to set these.
     * @param m the move to check. If null then return true.
     * @param recordWin if true then the controller state will record wins
     */
    boolean done( TwoPlayerMove m, boolean recordWin );

    /**
     * Generate a list of candidate next moves given the last move
     * This function is a key function that must be created for each type of game added.
     *
     *  @param lastMove  the last move made
     *  @param weights  the polynomial weights to use in the polynomial evaluation function.
     *  @param player1sPerspective if true assign worth values according to p1.
     */
    List generateMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective );

    /**
     * generate those moves that are critically urgent
     * if you generate too many, then you run the risk of an explosion in the search tree
     * these moves should be sorted from most to least urgent
     *
     *  @param lastMove  the last move made
     *  @param weights  the polynomial weights to use in the polynomial evaluation function
     */
    List generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective );

    /**
     * returns true if the specified move caused one or more opponent pieces to become jeopardized
     */
    boolean inJeopardy( TwoPlayerMove m, ParameterArray weights, boolean player1sPerspective );

}
