package com.becker.game.common;

/**
 * A box defined by 2 locations
 * @ Barry Becker
 */
public class Box {
    Location corner1_;
    Location corner2_;

    public Box(Location corner1, Location corner2) {
        corner1_ = corner1;
        corner2_ = corner2;
        verify();
    }


    public Box(int rowMin, int colMin, int rowMax, int colMax) {
        corner1_ = new Location(rowMin, colMin);
        corner2_ = new Location(rowMax, colMax);
        verify();
    }

    /**
     *  make sure corner 1 is the top left and corner 2 is the bottom right
     */
    private void verify() {
       if (corner1_.row > corner2_.row) {
            int temp = corner1_.row;
            corner1_.row = corner2_.row;
            corner2_.row = temp;
        }
        if (corner1_.col > corner2_.col) {
            int temp = corner1_.col;
            corner1_.col = corner2_.col;
            corner2_.col = temp;
        }
    }

    public int getWidth() {
        return Math.abs(corner2_.col - corner1_.col);
    }

    public int getHeight() {
        return Math.abs(corner2_.row - corner1_.row);
    }

    public Location getTopLeftCorner() {
       return corner1_;
    }

    public Location getBottomRightCorner() {
       return corner2_;
    }

    public int getMinRow() {
        return corner1_.row;
    }

    public int getMinCol() {
        return corner1_.col;
    }

    public int getMaxRow() {
        return corner2_.row;
    }

    public int getMaxCol() {
        return corner2_.col;
    }

    public int getArea()  {
        return getWidth() * getHeight();
    }

    public void expandBy(Location loc) {
        if (loc.row < corner1_.row) {
            corner1_.row = loc.row;
        }
        else if (loc.row > corner2_.row) {
            corner2_.row = loc.row;
        }
        if (loc.col < corner1_.col)  {
            corner1_.col = loc.col;
        }
        else if (loc.col > corner2_.col) {
            corner2_.col = loc.col;
        }
    }

    public void expandBy(Box box)  {
        expandBy(box.getTopLeftCorner());
        expandBy(box.getBottomRightCorner());
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("Box:");
        buf.append(corner1_);
        buf.append(" - ");
        buf.append(corner2_);
        return buf.toString();
    }
}
