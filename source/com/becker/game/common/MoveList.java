package com.becker.game.common;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * A list of game moves.
 * What kind of performance difference is there if this is a LinkedList instead of ArrayList?
 *
 * @author Barry Becker
 */
public class MoveList extends ArrayList<Move> {

    /**
     * Construct set of players
     */
    public MoveList() {}

    /**
     * Copy constructor. Does not make a deep copy.
     * @param list
     */
    public MoveList(MoveList list) {
        super(list);
    }

    /**
     * Copyies the constituent moves as well.
     * @return a deep copy of the movelist.
     */
    public MoveList copy() {
        MoveList copiedList = new MoveList();
        for (Move m : this) {
            copiedList.add(m.copy());
        }
        return copiedList;
    }


    /**
     *  @return the player that goes first.
     */
    public Move getFirstMove() {
        return get(0);
    }

    public Move getLastMove() {
        if ( isEmpty() ) {
            return null;
        }
        return get(this.size()-1);
    }

    public Move removeLast() {
        return remove(this.size()-1);
    }

    /**
     * @return  number of active players.
     */
    public int getNumMoves() {
        return size();
    }

    @Override
    public MoveList subList(int first, int last) {
        MoveList subList = new MoveList();
        subList.addAll(super.subList(first, last));
        return subList;
    }

    /**
     * @return a random move from the list.
     */
    public Move getRandomMove() {

        return getRandomMove(size());
    }

    /**
     * Randomly get one of the top n moves and ignore the rest.
     * The moves are assumed ordered.
     * @param ofFirstN the first n to choose randomly from.
     * @return a random move from the list.
     */
    public Move getRandomMove(int ofFirstN) {

        int r = GameContext.random().nextInt(Math.min(ofFirstN, size()));
        return get( r );
    }

    /**
     * Randomly get one of the top n moves and ignore the rest.
     * The moves are assumed ordered.
     * @param percentLessThanBestThresh randomly get one of the moves whos score is
     * not more than this percent less that the first..
     * @return a random move from the list.
     */
    public Move getRandomMoveForThresh(int percentLessThanBestThresh) {

        // first find the index of the last move that is still above the thresh
        double thresh = getFirstMove().getValue() * (1.0 - (float)percentLessThanBestThresh/100.0);
        int ct = 1;
        Move currentMove = getFirstMove();
        int numMoves = size();
        while (currentMove.getValue() > thresh && ct < numMoves) {
            currentMove = get(ct++);
        }
        int r = GameContext.random().nextInt(ct);
        return get( r );
    }
}