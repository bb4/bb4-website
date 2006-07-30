package com.becker.game.common;

import com.becker.common.*;

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
       if (corner1_.getRow() > corner2_.getRow()) {
            int temp = corner1_.getRow();
            corner1_.setRow(corner2_.getRow());
            corner2_.setRow(temp);
        }
        if (corner1_.getCol() > corner2_.getCol()) {
            int temp = corner1_.getCol();
            corner1_.setCol(corner2_.getCol());
            corner2_.setCol(temp);
        }
    }

    public int getWidth() {
        return Math.abs(corner2_.getCol() - corner1_.getCol());
    }

    public int getHeight() {
        return Math.abs(corner2_.getRow() - corner1_.getRow());
    }

    public Location getTopLeftCorner() {
       return corner1_;
    }

    public Location getBottomRightCorner() {
       return corner2_;
    }

    public int getMinRow() {
        return corner1_.getRow();
    }

    public int getMinCol() {
        return corner1_.getCol();
    }

    public int getMaxRow() {
        return corner2_.getRow();
    }

    public int getMaxCol() {
        return corner2_.getCol();
    }

    public int getArea()  {
        return getWidth() * getHeight();
    }

    public void expandBy(Location loc) {
        if (loc.getRow() < corner1_.getRow()) {
            corner1_.setRow(loc.getRow());
        }
        else if (loc.getRow() > corner2_.getRow()) {
            corner2_.setRow(loc.getRow());
        }
        if (loc.getCol() < corner1_.getCol())  {
            corner1_.setCol(loc.getCol());
        }
        else if (loc.getCol() > corner2_.getCol()) {
            corner2_.setCol(loc.getCol());
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
