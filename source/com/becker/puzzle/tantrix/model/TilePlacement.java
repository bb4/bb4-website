// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;

import java.util.List;

/**
 * Represents the positioning of a tantrix tile on the board.
 * Immutable.
 * @author Barry Becker
 */
public class TilePlacement {

    private Location location;
    private Rotation rotation;
    private HexTile tile;

    /**
     * Constructor.
     */
    public TilePlacement(HexTile tile, Location location, Rotation rotation) {
        this.tile = tile;
        this.location = location;
        this.rotation = rotation;
    }

    /**
     * Constructor for empty location.
     */
    public TilePlacement(Location location) {
        this(null, location, Rotation.ANGLE_0);
    }

    /**
     * Constructor.
     */
    public HexTile getTile() {
        return tile;
    }

    public boolean hasTile() {
        return tile != null;
    }

    public Location getLocation() {
        return location;
    }

    /** @rturn the amount that the tile is rotated. */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Turn the tile.
     * @param turns positive (c-clockwise) or negative (clockwise) number of turns.
     * @return new immutable TilePlacement instance.
     */
    public TilePlacement rotate(byte turns) {
        Rotation newRotation = Rotation.values()[(rotation.ordinal() + turns) % rotation.values().length];
        return new TilePlacement(tile, location, newRotation);
    }

    public TilePlacement rotate() {
        Rotation newRotation = Rotation.values()[rotation.ordinal() + 1];
        return new TilePlacement(tile, location, newRotation);
    }
}
