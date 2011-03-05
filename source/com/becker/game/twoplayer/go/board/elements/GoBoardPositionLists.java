package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.common.GameContext;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *  A list of GoBoardPositionList.
 *
 *  @author Barry Becker
 */
public class GoBoardPositionLists extends LinkedList<GoBoardPositionList> {

    /**
     * Default constructor.
     */
    public GoBoardPositionLists() {}

    /**
     * copy constructor.
     *
     * @param positionList list to initialize with
     */
    public GoBoardPositionLists(GoBoardPositionLists positionList) {
        super(positionList);
    }

    @Override
    public GoBoardPositionList get(int i) {
        return super.get(i);
    }



    /**
     * Set the visited flag back to false for a list of lists of stones
     */
    public void unvisitPositionsInLists() {
        for (GoBoardPositionList list : this) {
            list.unvisitPositions();
        }
    }
}