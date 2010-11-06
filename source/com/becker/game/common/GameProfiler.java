package com.becker.game.common;

import com.becker.common.profile.Profiler;
import com.becker.common.util.Util;

/**
 * Keep krack of timing info for different game searching aspects.
 * @author Barry Becker
 */
public class GameProfiler extends Profiler {

    protected static final String GENERATE_MOVES = "generating moves";
    protected static final String UNDO_MOVE = "undoing move";
    protected static final String MAKE_MOVE = "making move";    
    protected static final String CALC_WORTH = "calculating worth";

    private static GameProfiler instance;

    private long searchTime;

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

    /**
     * Start profiling the game search.
     */
    public void startProfiling()  {

        searchTime = 0;
        if ( GameContext.isProfiling() ) {
            searchTime = System.currentTimeMillis();
            initialize();
        }
    }

    /**
     * Stop profiling and report the stats.
     * @param numMovesConsidered the number of moves considered duering search.
     */
    public void stopProfiling(long numMovesConsidered) {
        if ( GameContext.isProfiling() ) {
            long totalTime = System.currentTimeMillis() - searchTime;
            showProfileStats(totalTime, numMovesConsidered);
        }
    }


    /**
     * Export some usefule performance profile statistics in the log.
     * @param totalTime total elapsed time.
     * @param numMovesConsidered number of moves inspected during search.
     */
    void showProfileStats( long totalTime, long numMovesConsidered ) {
        GameContext.log( 0, "----------------------------------------------------------------------------------" );
        GameContext.log( 0, "There were " + numMovesConsidered + " moves considered." );
        GameContext.log( 0, "The total time for the computer to move was : " +
                Util.formatNumber((float)totalTime/1000) + " seconds." );
        print();
    }

    void initialize() {
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
