// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import java.util.ArrayList;
import java.util.Collections;

import static com.becker.puzzle.tantrix.model.PathColor.*;

/**
 * The complete set of hexagonal tantrix tiles
 *
 * @author Barry Becker
 */
public class HexTiles extends HexTileList {

    public HexTiles()  {
        super();
        byte i = 1;
        add(new HexTile(i++, YELLOW, new PathColors(RED, BLUE, RED, BLUE, YELLOW, YELLOW)));
      /*  add(new HexTile(i++, YELLOW, new PathColors(BLUE, YELLOW, YELLOW, BLUE, RED, RED)));
        add(new HexTile(i++, YELLOW, new PathColors(BLUE, BLUE, RED, RED, YELLOW, YELLOW)));
        add(new HexTile(i++, YELLOW, new PathColors(BLUE, YELLOW, RED, BLUE, RED, YELLOW)));
        add(new HexTile(i++, YELLOW, new PathColors(RED, BLUE, BLUE, RED, YELLOW, YELLOW)));
        add(new HexTile(i++, YELLOW, new PathColors(YELLOW, RED, BLUE, YELLOW, BLUE, RED)));
        add(new HexTile(i++, YELLOW, new PathColors(RED, YELLOW, RED, YELLOW, BLUE, BLUE))); // 7
        add(new HexTile(i++, YELLOW, new PathColors(YELLOW, RED, YELLOW, RED, BLUE, BLUE)));
        add(new HexTile(i++, YELLOW, new PathColors(RED, YELLOW, BLUE, RED, BLUE, YELLOW)));
        add(new HexTile(i++, YELLOW, new PathColors(RED, YELLOW, YELLOW, BLUE, RED, BLUE))); // 10  */
    }

    /**
     *
     * @param numTiles  the number of tiles to draw from the master list starting with 1.
     * @return randome collection of tantrix tiles.
     */
    public HexTileList createRandomList(int numTiles) {
        HexTileList tiles = new HexTileList();
        for (int i=0; i<numTiles; i++) {
            tiles.add(this.get(i));
        }
        Collections.shuffle(tiles);
        return tiles;
    }



}

  
