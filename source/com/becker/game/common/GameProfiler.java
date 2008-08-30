package com.becker.game.common;

import com.becker.common.Profiler;

/**
 * User: Barry Becker
 * Date: Apr 16, 2005
 * Time: 6:20:14 AM
 */
public class GameProfiler extends Profiler {

    protected static final String GENERATE_MOVES = "generating moves";
    protected static final String UNDO_MOVE = "undoing move";
    protected static final String MAKE_MOVE = "making move";    
    protected static final String CALC_WORTH = "calculating worth";
    
    public GameProfiler() {
        add(GENERATE_MOVES);
            add(CALC_WORTH, GENERATE_MOVES);
        add(UNDO_MOVE);      
        add(MAKE_MOVE);      
    }

    public void initialize() {
        resetAll();
        setEnabled(GameContext.isProfiling());
        setLogger(GameContext.getLogger());
    }
    
    public void startGenerateMoves() {
        this.start(GENERATE_MOVES);
    }
    
    public void stopGenerateMoves() {
        this.stop(GENERATE_MOVES);
    }

    public void startUndoMove() {
        this.start(UNDO_MOVE);
    }

    public void stopUndoMove() {
        this.stop(UNDO_MOVE);
    }

    public void startMakeMove() {
        this.start(MAKE_MOVE);
    }

    public void stopMakeMove() {
        this.stop(MAKE_MOVE);
    }

    public void startCalcWorth() {
        this.start(CALC_WORTH);
    }
    
    public void stopCalcWorth() {
        this.stop(CALC_WORTH);
    }
    
}
