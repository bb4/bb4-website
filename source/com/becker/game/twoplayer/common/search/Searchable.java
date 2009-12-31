package com.becker.game.twoplayer.common.search;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.game.twoplayer.common.*;

import java.util.*;


/**
 * This is the interface that all game controllers should implement if they want to allow searching.
 * One purpose of this interface is to limit the TwoPlayerController methods available to search.
 * The SearchStrategy classes call methods of this interface to do their search.
 * @see TwoPlayerController for the default implementation of this interface
 *
 * @author Barry Becker
 */
public interface Searchable
{

    /** 
     * @return the game specific two player options having to do with search parameters.
     */
    TwoPlayerOptions getOptions();

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
     * Given a move, determine whether the game is over.
     * If recordWin is true then the variables for player1/2HasWon can get set.
     * sometimes, like when we are looking ahead in search we do not want to set these.
     * @param m the move to check. If null then return true. This is typically the last move played
     * @param recordWin if true then the controller state will record wins
     * @return  true if the game is over.
     */
    boolean done( TwoPlayerMove m, boolean recordWin );

    /**
     * Generate a list of candidate next moves given the last move.
     * This function is a key function that must be created for each type of game added.
     *
     * @param lastMove  the last move made if there was one. (null if first move of the game)
     * @param weights  the polynomial weights to use in the polynomial evaluation function.
     * @param player1sPerspective if true assign worth values according to p1.
     * @return list of possible next moves.
     */
    List<? extends TwoPlayerMove> generateMoves(
                                    TwoPlayerMove lastMove,
                                    ParameterArray weights,
                                    boolean player1sPerspective );

    /**
     * generate those moves that are critically urgent
     * if you generate too many, then you run the risk of an explosion in the search tree
     * these moves should be sorted from most to least urgent
     *
     * @param lastMove  the last move made
     * @param weights  the polynomial weights to use in the polynomial evaluation function
     * @return a list of moves that the current player needs to urgently play or face imminent defeat.
     */
    List<? extends TwoPlayerMove> generateUrgentMoves(
                                     TwoPlayerMove lastMove, ParameterArray weights,
                                     boolean player1sPerspective );

    /**
     * returns true if the specified move caused one or more opponent pieces to become jeopardized
     * @return true if the move m is in jeopardy.
     */
    boolean inJeopardy( TwoPlayerMove m, ParameterArray weights, boolean player1sPerspective );

}
