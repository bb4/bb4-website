// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import java.util.LinkedList;

/**
 * A list of tile placements
 *
 * @author Barry Becker
 */
public class TilePlacementList extends LinkedList<TilePlacement> {

    public TilePlacementList()  {}

    public TilePlacementList(Tantrix tantrix) {
        for (TilePlacement p : tantrix.values()) {
           this.add(p);
        }
    }

    /** copy constructor */
    public TilePlacementList(TilePlacementList list) {
        this.addAll(list);
    }
    /*
    public void addToStart(TilePlacement p) {
        this.addFirst(p);
    }  */

}


