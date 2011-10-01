package com.becker.puzzle.sudoku.model;

import com.becker.puzzle.sudoku.model.update.IUpdater;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for updating a board given a list of updaters to apply.
 *
 * @author Barry Becker
 */
public class BoardUpdater {

    private List<Class> updaterClasses;

    /**
     * Constructor
     * @param updaterClasses the updater classes to use when updating the board during an interaction of the solver.
     */
    public BoardUpdater(List<Class> updaterClasses) {
        this.updaterClasses = updaterClasses;
    }


    public BoardUpdater(Class ... classes) {
        updaterClasses = Arrays.asList(classes);
    }

    /**
     * update candidate lists for all cells then set the unique values that are determined.
     * First create the updaters using reflection, then apply them.
     */
    public void updateAndSet(Board board) {

        List<IUpdater> updaters = createUpdaters(board);

        for (IUpdater updater : updaters) {
            updater.updateAndSet();
        }
    }

    /**
     * Creates the updater instances using reflection. Cool.
     * @param board
     * @return list of updaters to apply
     */
    private List<IUpdater> createUpdaters(Board board)   {

        List<IUpdater> updaters = new LinkedList<IUpdater>();

        for (Class clazz : updaterClasses) {
            Constructor ctor = null;
            try {
                ctor = clazz.getDeclaredConstructor(Board.class);
                ctor.setAccessible(true);
                try {
                    IUpdater updater = (IUpdater)ctor.newInstance(board);
                    updaters.add(updater);
                } catch (InstantiationException e) {
                    throw new IllegalStateException("Could not instantiate " + clazz.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not access constructor of " + clazz.getName(), e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Could not invoke constructor of " + clazz.getName(), e);
                }
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Could not find constructor for " + clazz.getName(), e);
            }
        }
        return updaters;
    }
}
