package com.becker.game.twoplayer.common.search;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.transposition.HashKey;
import com.becker.optimization.parameter.ParameterArray;


/**
 * This is the interface that all game controllers should implement if they want to allow searching.
 * One purpose of this interface is to limit the TwoPlayerController methods available to search.
 * The SearchStrategy classes call methods of this interface to do their search.
 * @see TwoPlayerController for the default implementation of this interface
 *
 * @author Barry Becker
 */
public interface Searchable {

    /** 
     * @return the search options having to do with search parameters.
     */
    SearchOptions getSearchOptions();

    /**
     * @param m the move to play.
     */
    void makeInternalMove( TwoPlayerMove m );

    /**
     * takes back the most recent move.
     * @param m move to undo.
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
      *  Statically evaluate a boards state to compute the value of the last move
      *  from player1's perspective.
      *  This function is a key function that must be created for each type of game added.
      *  If evaluating from player 1's perspective, then good moves for p1 are given a positive score.
      *
      *  @param lastMove  the last move made
      *  @param weights  the polynomial weights to use in the polynomial evaluation function
      *  @return the worth of the board from the specified players point of view
      */
    int worth( Move lastMove, ParameterArray weights);

    /**
     * Generate a list of good evaluated next moves given the last move.
     * This function is a key function that must be created for each type of game added.
     *
     * @param lastMove  the last move made if there was one. (null if first move of the game)
     * @param weights  the polynomial weights to use in the polynomial evaluation function.
     * @return list of possible next moves.
     */
    MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights);

    /**
     * generate those moves that are critically urgent
     * If you generate too many, then you run the risk of an explosion in the search tree.
     * These moves should be sorted from most to least urgent
     *
     * @param lastMove  the last move made
     * @param weights  the polynomial weights to use in the polynomial evaluation function
     * @return a list of moves that the current player needs to urgently play or face imminent defeat.
     */
    MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights);

    /**
     * returns true if the specified move caused one or more opponent pieces to become jeopardized
     * @return true if the move m is in jeopardy.
     */
    boolean inJeopardy( TwoPlayerMove m, ParameterArray weights);

    /** The current board state. */
    TwoPlayerBoard getBoard();

    MoveList getMoveList();
    
    /**
     * @return num moves played so far
     */
    int getNumMoves();
    
    /**
     * @return a copy of our current state so we can make moves and not worry about undoing them.
     */
    Searchable copy();

    /**
     *
     * @return  the Zobrist hash for the currently searched position
     */
    HashKey getHashKey();
}
