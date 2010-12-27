package com.becker.game.common;

/**
 * Keep track of timing info for different game searching aspects.
 * @author Barry Becker
 */
public class GameProfiler extends AbstractGameProfiler {

    private static GameProfiler instance;


    /**
     * @return singleton instance.
     */
    public static GameProfiler getInstance() {
        if (instance == null) {
            instance = new GameProfiler();
        }
        return instance;
    }

    /**
     * protected constructor.
     */
    protected GameProfiler() {
        add(GENERATE_MOVES);
            add(CALC_WORTH, GENERATE_MOVES);
        add(UNDO_MOVE);      
        add(MAKE_MOVE);      
    }
}
