package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.SearchWindow;
import com.becker.game.twoplayer.common.search.transposition.Entry;
import com.becker.game.twoplayer.common.search.transposition.TranspositionTable;
import com.becker.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;

/**
 * Interface for all memory based SearchStrategies for 2 player games with perfect information.
 *
 * @author Barry Becker
 */
public interface MemorySearchStrategy extends SearchStrategy {

    TranspositionTable getTranspositionTable();
}
