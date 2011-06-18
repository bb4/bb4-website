package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.common.Move;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.WorthInfo;
import com.becker.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.optimization.parameter.ParameterArray;


/**
 * Representation of a Go Game Board
 * There are a lot of data structures to organize the state of the pieces.
 * For example, we update strings, and groups (and eventually armies) after each move.
 * After updating we can use these structures to estimate territory for each side.
 *
 * Could move many methods to StringFinder and GroupFinder classes.
 * @author Barry Becker
 */
public final class BoardEvaluator {

    private WorthCalculator worthCalculator_;

    private TerritoryAnalyzer territoryAnalyzer_;

    private GroupAnalyzerMap analyzerMap_;


    /**
     *  Constructor.
     */
    public BoardEvaluator(GoBoard board) {

        analyzerMap_ = new GroupAnalyzerMap();
        territoryAnalyzer_ = new TerritoryAnalyzer(board, analyzerMap_);
        worthCalculator_ = new WorthCalculator(board, territoryAnalyzer_);
    }

    public int worth(Move lastMove, ParameterArray weights) {
        return worthCalculator_.worth(lastMove, weights);
    }

    public WorthInfo getWorthInfo() {
        return worthCalculator_.getWorthInfo();
    }

    /** don't really want to expose this */
    public GroupAnalyzer getGroupAnalyzer(IGoGroup group) {
        return analyzerMap_.getAnalyzer(group);
    }

    /**
     * @return change in territorial score
     */
    public float getTerritoryDelta() {
        return territoryAnalyzer_.getTerritoryDelta();
    }

    /**
     * Get estimate of territory for specified player.
     * @param forPlayer1 the player to get the estimate for
     * @param isEndOfGame then we need the estimate to be more accurate.
     * @return estimate of size of territory for specified player.
     */
    public int getTerritoryEstimate(boolean forPlayer1, boolean isEndOfGame) {
        return territoryAnalyzer_.getTerritoryEstimate(forPlayer1, isEndOfGame);
    }

    /**
     * @return the estimated difference in territory between the 2 sides.
     */
    public float updateTerritory(boolean isEndOfGame) {
        return territoryAnalyzer_.updateTerritory(isEndOfGame);
    }
}
