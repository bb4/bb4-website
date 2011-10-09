package com.becker.puzzle.sudoku.ui;

import com.becker.common.geometry.Location;
import com.becker.puzzle.sudoku.model.ValueConverter;
import com.becker.puzzle.sudoku.model.board.Board;
import com.becker.puzzle.sudoku.model.board.Cell;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 *  @author Barry Becker
 */
public final class UserInputListener implements MouseListener, KeyListener {

    private CellLocator locator;
    private Location currentCellLocation;

    private UserEnteredValues userEnteredValues;
    private List<RepaintListener> listeners;

    /**
     * Constructor. Pass in data for initial Sudoku problem.
     */
    UserInputListener(CellLocator locator) {
        this.locator = locator;
        listeners = new ArrayList<RepaintListener>();
        clear();
    }

    public void clear() {
        userEnteredValues = new UserEnteredValues();
    }

    public Location getCurrentCellLocation() {
        return currentCellLocation;
    }


    public UserEnteredValues getUserEnteredValues() {
        return userEnteredValues;
    }

    public void mouseClicked(MouseEvent e) {
        currentCellLocation = locator.getCellCoordinates(e.getPoint());
        notifyCellSelected(currentCellLocation);
    }


    public void keyPressed(KeyEvent event) {
        char key = event.getKeyChar();
        try {
            int value = ValueConverter.getValue(key, locator.getBoard().getEdgeLength());
            UserValue userValue = new UserValue(currentCellLocation, value);
            userEnteredValues.put(currentCellLocation, userValue);
            notifyValueEntered();
        }
        catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void validateValues(Board solvedPuzzle) {

        for (Location location : userEnteredValues.keySet())   {
            UserValue userValue = userEnteredValues.get(location);
            Cell cell = solvedPuzzle.getCell(location.getRow(), location.getCol());
            boolean valid = userValue.getValue() == cell.getValue();
            userValue.setValid(valid);
        }
    }

    public void addRepaintListener(RepaintListener listener) {
         listeners.add(listener);
    }

    public void removeRepaintListener(RepaintListener listener) {
        listeners.remove(listener);
    }

    private void notifyValueEntered() {
        for (RepaintListener listener : listeners) {
            listener.valueEntered();
        }
    }

    private void notifyCellSelected(Location location) {
        for (RepaintListener listener : listeners) {
            listener.cellSelected(location);
        }
    }

    public void keyTyped(KeyEvent event) {}
    public void keyReleased(KeyEvent e) {}

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

