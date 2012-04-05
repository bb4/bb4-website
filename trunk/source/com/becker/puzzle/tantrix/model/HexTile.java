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
    private List<PathColor> edgeColors;
    private PathColor primaryColor;

    /**
     * Constructor.
     */
    public HexTile(byte tantrixNumber, PathColor primaryColor, List<PathColor> edgeColors) {
        this.tantrixNumber = tantrixNumber;
        this.primaryColor = primaryColor;
        this.edgeColors = edgeColors;

    }

    /** @return the number on the back of the tile */
    public byte getTantrixNumber() {
        return tantrixNumber;
    }

    /** @return the color of the tiles mani path */
    public List<PathColor> getEdgeColors() {
        return edgeColors;
    }

    public PathColor getEdgeColor(int index) {
        return edgeColors.get(index);
    }

    /** The primary path color on the back of the tile */
    public PathColor getPrimaryColor() {
        return primaryColor;
    }

    public String toString() {
        return "tileNum=" + tantrixNumber + " colors: " + edgeColors;
    }
}
