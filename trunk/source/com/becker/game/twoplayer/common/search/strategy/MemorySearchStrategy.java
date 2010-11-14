package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.transposition.TranspositionTable;

/**
 * Interface for all memory based SearchStrategies for 2 player games with perfect information.
 *
 * @author Barry Becker
 */
public interface MemorySearchStrategy extends SearchStrategy {

    TranspositionTable getTranspositionTable();
}
