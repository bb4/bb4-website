/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.common.Move;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.WorthInfo;
import com.becker.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Responsible for evaluating groups and territory on a go board.
 *
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

    /**
     * @return the worth of the board from player1's perspective
     */
    public int worth(Move lastMove, ParameterArray weights) {
        return worthCalculator_.worth(lastMove, weights);
    }

    /** Used only for debugging to understand how the worth was calculated. */
    public WorthInfo getWorthInfo() {
        return worthCalculator_.getWorthInfo();
    }

    /** don't really want to expose this, but need for rendering. */
    public GroupAnalyzer getGroupAnalyzer(IGoGroup group) {
        return analyzerMap_.getAnalyzer(group);
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
    public float updateTerritoryAtEndOfGame() {
        return territoryAnalyzer_.updateTerritory(true);
    }
}
