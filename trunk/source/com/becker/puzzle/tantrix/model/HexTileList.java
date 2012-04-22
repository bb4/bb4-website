// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The complete set of hexagonal tantrix tiles
 *
 * @author Barry Becker
 */
public class HexTileList extends ArrayList<HexTile> {

   public HexTileList()  {}

   public HexTileList(TilePlacementList tiles) {
       for (TilePlacement placement : tiles) {
           this.add(placement.getTile());
       }
   }

}

  
