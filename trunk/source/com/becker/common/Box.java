package com.becker.common;


/**
 * A box defined by 2 locations.
 * @author Barry Becker
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Box {
    
    private final Location topLeftCorner_;
    private final Location bottomRightCorner_;

    /**
     * Constructor
     *@param topLeftCorner top left corner coordinates.
     *@param bottomRightCorner bottom right corner coordinates.
     */
    public Box(Location topLeftCorner, Location bottomRightCorner) {
        topLeftCorner_ = topLeftCorner;
        bottomRightCorner_ = bottomRightCorner;
        verify();
    }

    public Box(int rowMin, int colMin, int rowMax, int colMax) {
        topLeftCorner_ = new Location(rowMin, colMin);
        bottomRightCorner_ = new Location(rowMax, colMax);
        verify();
    }

    /**
     *  make sure corner 1 is the top left and corner 2 is the bottom right
     */
    private void verify() {
       if (topLeftCorner_.getRow() > bottomRightCorner_.getRow()) {
            byte temp = topLeftCorner_.getRow();
            topLeftCorner_.setRow(bottomRightCorner_.getRow());
            bottomRightCorner_.setRow(temp);
        }
        if (topLeftCorner_.getCol() > bottomRightCorner_.getCol()) {
            byte temp = topLeftCorner_.getCol();
            topLeftCorner_.setCol(bottomRightCorner_.getCol());
            bottomRightCorner_.setCol(temp);
        }
    }

    public int getWidth() {
        return Math.abs(bottomRightCorner_.getCol() - topLeftCorner_.getCol());
    }

    public int getHeight() {
        return Math.abs(bottomRightCorner_.getRow() - topLeftCorner_.getRow());
    }

    public Location getTopLeftCorner() {
       return topLeftCorner_;
    }

    public Location getBottomRightCorner() {
       return bottomRightCorner_;
    }

    public int getMinRow() {
        return topLeftCorner_.getRow();
    }

    public int getMinCol() {
        return topLeftCorner_.getCol();
    }

    public int getMaxRow() {
        return bottomRightCorner_.getRow();
    }

    public int getMaxCol() {
        return bottomRightCorner_.getCol();
    }

    public void expandBy(Location loc) {
        if (loc.getRow() < topLeftCorner_.getRow()) {
            topLeftCorner_.setRow(loc.getRow());
        }
        else if (loc.getRow() > bottomRightCorner_.getRow()) {
            bottomRightCorner_.setRow(loc.getRow());
        }
        if (loc.getCol() < topLeftCorner_.getCol())  {
            topLeftCorner_.setCol(loc.getCol());
        }
        else if (loc.getCol() > bottomRightCorner_.getCol()) {
            bottomRightCorner_.setCol(loc.getCol());
        }
    }

    /**
     * @param amount amount to expand all borders of the box by.
     * @param maxRow don't go further than this though.
     * @param maxCol don't go further than this though.
     */
    public void expandGloballyBy(int amount, int maxRow, int maxCol) {
      
        topLeftCorner_.setRow((byte)Math.max(topLeftCorner_.getRow() - amount, 1));
        topLeftCorner_.setCol((byte)Math.max(topLeftCorner_.getCol() - amount, 1));
        
        bottomRightCorner_.setRow((byte)Math.min(bottomRightCorner_.getRow() + amount, maxRow));
        bottomRightCorner_.setCol((byte)Math.min(bottomRightCorner_.getCol() + amount, maxCol));
    }
    
    /**
     * @param threshold if withing this distance to the edge, extend the box all the way to that edge.
     * @param maxRow don't go further than this though.
     * @param maxCol don't go further than this though.
     */
    public void expandBordersToEdge(int threshold, int maxRow, int maxCol) {
        if (topLeftCorner_.getRow() <= threshold + 1) {
            topLeftCorner_.setRow((byte)1);
        }
        if (topLeftCorner_.getCol() <= threshold + 1) {
            topLeftCorner_.setCol(1);
        }
        if (maxRow - bottomRightCorner_.getRow() <= threshold) {
            bottomRightCorner_.setRow(maxRow);
        }
        if (maxCol - bottomRightCorner_.getCol() <= threshold) {
            bottomRightCorner_.setCol(maxCol);
        }
    }
    

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("Box:");
        buf.append(topLeftCorner_);
        buf.append(" - ");
        buf.append(bottomRightCorner_);
        return buf.toString();
    }
}
