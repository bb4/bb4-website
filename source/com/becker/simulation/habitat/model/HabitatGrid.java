package com.becker.simulation.habitat.model;

import ca.dj.jigo.sgf.tokens.SourceToken;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class HabitatGrid {

    int xDim;
    int yDim;

    Cell[][] cells;


    public HabitatGrid(int xDim, int yDim)  {
        this.xDim = xDim;
        this.yDim = yDim;

        cells = new Cell[xDim + 1][yDim + 1];
        for (int i=0; i<=xDim; i++) {
            for (int j=0; j<=yDim; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }


    public Cell getCellForPosition(Point2d position) {

        int x = (int) (position.getX() * xDim);
        int y = (int) (position.getY() * yDim);

        return cells[x][y];
    }

    public List<Cell> getNeighborCells(Cell cell) {
         List<Cell> nbrCells = new ArrayList<Cell>(8);

        nbrCells.add(cells[getSafeX(cell.xIndex -1)][getSafeY(cell.yIndex -1)]);
        nbrCells.add(cells[getSafeX(cell.xIndex -1)][cell.yIndex]);
        nbrCells.add(cells[getSafeX(cell.xIndex -1)][getSafeY(cell.yIndex +1)]);
        nbrCells.add(cells[cell.xIndex][getSafeY(cell.yIndex -1)]);
        nbrCells.add(cells[cell.xIndex][getSafeY(cell.yIndex +1)]);
        nbrCells.add(cells[getSafeX(cell.xIndex +1)][getSafeY(cell.yIndex -1)]);
        nbrCells.add(cells[getSafeX(cell.xIndex +1)][cell.yIndex]);
        nbrCells.add(cells[getSafeX(cell.xIndex +1)][getSafeY(cell.yIndex +1)]);

        return nbrCells;
    }

    private int getSafeX(int xIndex) {
        return Math.abs(xIndex % xDim);
    }

    private int getSafeY(int yIndex) {
        return Math.abs(yIndex % yDim);
    }
}
