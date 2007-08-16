package com.becker.puzzle.common;

import java.util.List;

/**
 * A UI element that can be refreshed to show the current state.
 *
 * Created on July 28, 2007, 7:00 AM
 * @author becker
 */
public interface Refreshable <P, M> {
    
    /**
     * Call when you want the UI to update.
     * @param done if true then the puzzle simulation has completed.
     */
    void refresh(P pos, long numTries);
    
    /**
     *show the path to the solution at the end.
     */
    void finalRefresh(List<M> path, P pos, long numTries);
    
    /**
     *Make a sound of some sort
     */
    void makeSound();
}
