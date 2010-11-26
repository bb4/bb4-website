package com.becker.game.common;


import java.util.LinkedList;
import java.util.Random;

/**
 * A list of game moves.
 *
 * @author Barry Becker
 */
public class MoveList extends LinkedList<Move> {

    /** Make sure that the program runs in a reproducible way by always starting from the same random seed. */
    private static final Random RANDOM = new Random(1);

    /**
     * Construct set of players
     */
    public MoveList() {}

    /**
     * Copy constructor
     * @param list
     */
    public MoveList(MoveList list) {
        super(list);
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
        return getLast();
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
     * @param ofFirstN randomly get one of the top n moves and ignore the rest.
     * @return a random move from the list.
     */
    public Move getRandomMove(int ofFirstN) {

        int r = RANDOM.nextInt(Math.min(ofFirstN, size()));
        return get( r );
    }
}