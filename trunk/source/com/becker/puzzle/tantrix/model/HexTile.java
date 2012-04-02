// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import java.util.List;

/**
 * Represents a single tantrix tile.
 * Immutable.
 * @author Barry Becker
 */
public class HexTile {

    private byte tantrixNumber;
    private Rotation rotation;
    private List<Color> edgeColors;

    /**
     * Constructor.
     */
    public HexTile(byte tantrixNumber, Rotation rotation, List<Color> edgeColors) {
        this.tantrixNumber = tantrixNumber;
        this.rotation = rotation;
        this.edgeColors = edgeColors;
    }

    /** @return the number on the back of the tile */
    public byte getTantrixNumber() {
        return tantrixNumber;
    }

    /** @rturn the amount that the tile is rotated. */
    public Rotation getRotation() {
        return rotation;
    }

    /** @return the color of the tiles mani path */
    public List<Color> getEdgeColors() {
        return edgeColors;
    }

}
