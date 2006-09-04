package com.becker.simulation.liquid;

/**
 * @author Barry Becker Date: Aug 12, 2006
 */
public enum CellStatus {

    // possible status of the cell. determined by what's in it
    EMPTY('.'),      // no liquid
    SURFACE('*'),    // has liquid and full cell is adjacent
    FULL('#'),       // liquid on all sides
    OBSTACLE('o'),   // solid object (like a wall)
    ISOLATED('I');   // has liquid, but no full cells are adjacent


    private char symbol_;

    /**
     * constructor for cell type enum
     *
     * @param symbol character representation of the type.
     */
    private CellStatus(char symbol) {
       symbol_ = symbol;
    }

    public char getSymbol() {
        return symbol_;
    }
}
