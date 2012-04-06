// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;
import static com.becker.puzzle.tantrix.model.TantrixBoard.HEX_SIDES;

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

    public PathColor getPathColor(byte i) {
        int index = (i - rotation.ordinal()) % HEX_SIDES;
        index = (index < 0) ? index + HEX_SIDES : index;
        return tile.getEdgeColor(index);
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
        Rotation newRotation = Rotation.values()[(rotation.ordinal() + turns) % HEX_SIDES];
        return new TilePlacement(tile, location, newRotation);
    }

    public TilePlacement rotate() {
        Rotation newRotation = Rotation.values()[(rotation.ordinal() + 1) % HEX_SIDES];
        return new TilePlacement(tile, location, newRotation);
    }

    public String toString() {
        return  "["  + tile +" at " + location + " " + rotation + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TilePlacement that = (TilePlacement) o;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (rotation != that.rotation) return false;
        if (tile != null ? !tile.equals(that.tile) : that.tile != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = location != null ? location.hashCode() : 0;
        result = 31 * result + (rotation != null ? rotation.hashCode() : 0);
        result = 31 * result + (tile != null ? tile.hashCode() : 0);
        return result;
    }
}
