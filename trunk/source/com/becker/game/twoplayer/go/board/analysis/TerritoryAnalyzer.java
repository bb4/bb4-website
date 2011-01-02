package com.becker.game.twoplayer.go.board.analysis;

import com.becker.common.Box;
import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoBoardPositionList;
import com.becker.game.twoplayer.go.board.elements.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.elements.GoGroup;

import java.util.LinkedList;
import java.util.List;

import static com.becker.game.twoplayer.go.GoControllerConstants.USE_RELATIVE_GROUP_SCORING;

/**
 *
 * @author Barry Becker
 */
public class TerritoryAnalyzer {

    private GoBoard board_;
    
    /**
     * The difference between the 2 player's territory.
     * It is computed as black-white = sum(health of stone i) 
     */
    private float territoryDelta_ = 0;
    
    /**
     * When the ratio of actual moves to expected moves exceeds this, then
     * take the analysis all the way to the edge of the board.
     */
    private static final float EMPTY_REGION_EDGE_THRESH = 0.24f;

    private NeighborAnalyzer nbrAnalyzer_;

    /**
     * Constructor
     * @param board board to analyze
     */
    public TerritoryAnalyzer(GoBoard board)  {
        board_ = board;
        nbrAnalyzer_ = new NeighborAnalyzer(board);
    }
    
    public float getTerritoryDelta() {
        return territoryDelta_;
    }
  
            
    /**
     * Get an estimate of the territory for the specified player.
     * This estimate is computed by summing all spaces in eyes with dead opponent stones that are still on the board.
     * Empty spaces are weighted by how likely they are to eventually be territory of one side or the other.
     * At the end of the game this + the number of pieces captured so far should give the true score.
     * @param isEndOfGame use 0 or 1 instead of pos.scoreContribution if true.
     * @return estimate of territory for forPlayer1
     */
    public int getTerritoryEstimate( boolean forPlayer1, boolean isEndOfGame)
    {
        float territoryEstimate = 0;

        // we should be able to just sum all the position scores now.
        for ( int i = 1; i <= board_.getNumRows(); i++ )  {
           for ( int j = 1; j <= board_.getNumCols(); j++ ) {
               GoBoardPosition pos =  (GoBoardPosition) board_.getPosition(i, j);
               territoryEstimate += getTerritoryEstimateForPosition(pos, forPlayer1, isEndOfGame);
           }
        }
        return (int)territoryEstimate;
    }

    /**
     * @return  the estimate for a single position.
     */
    private float getTerritoryEstimateForPosition(GoBoardPosition pos, boolean forPlayer1, boolean isEndOfGame) {
        double val = isEndOfGame?  (forPlayer1? 1.0 : -1.0) : pos.getScoreContribution() ;
        float territoryEstimate = 0;

        // the territory estimate will always be positive for both sides.
        if (pos.isUnoccupied()) {
            if (forPlayer1 && pos.getScoreContribution() > 0) {
                territoryEstimate += val;
            }
            else if (!forPlayer1 && pos.getScoreContribution() < 0)  {
                territoryEstimate -= val;  // will be positive
            }
        }
        else { // occupied
            GamePiece piece = pos.getPiece();
            GoGroup group = pos.getGroup();
            assert(piece != null);
            if (group != null) {
                // add credit for probable captured stones.
                if (forPlayer1 && !piece.isOwnedByPlayer1() && group.getRelativeHealth(board_, isEndOfGame) >= 0) {
                    territoryEstimate += val;
                }
                else if (!forPlayer1 && piece.isOwnedByPlayer1() && group.getRelativeHealth(board_, isEndOfGame) <= 0)  {
                    territoryEstimate -= val;
                }
            }
        }
        return territoryEstimate;
    }

    /**
     * Loops through the groups to determine the territorial difference between the players.
     * Then it loops through and determines a score for positions that are not part of groups.
     * If a position is part of an area that borders only a living group, then it is considered
     * territory for that group's side. If, however, the position borders living groups from
     * both sides, then the score is weighted according to the proportion of the perimeter
     * that borders each living group and how alive those bordering groups are.
     * This is the primary factor in evaluating the board position for purposes of search.
     * This method and the methods it calls are the crux of this go playing program.
     *
     * @return the estimated difference in territory between the 2 sides.
     *  A large positive number indicates black is winning, while a negative number indicates that white has the edge.
     */
    public float updateTerritory(boolean isEndOfGame) {

        GoProfiler prof = GoProfiler.getInstance();
        prof.startUpdateTerritory();

        float delta = calcAbsoluteHealth();
        delta = calcRelativeHealth(prof, delta);

        prof.startUpdateEmpty();
        delta += updateEmptyRegions(isEndOfGame);
        prof.stopUpdateEmpty();

        prof.stopUpdateTerritory();
        territoryDelta_ = delta;
        return delta;
    }

    /**
     * First calculate the absolute health of the groups so that measure can
     * be used in the more accurate relative health computation.
     * @return total health of all stones in all groups in absolute terms.
     */
    private float calcAbsoluteHealth() {
        float delta = 0;
        GoProfiler prof = GoProfiler.getInstance();
        prof.startAbsoluteTerritory();

        for (GoGroup g : board_.getGroups()) {

            float health = g.calculateAbsoluteHealth(board_);

            if (!USE_RELATIVE_GROUP_SCORING) {
                g.updateTerritory(health);
                delta += health * g.getNumStones();
            }
        }
        prof.stopAbsoluteTerritory();
        return delta;
    }

    /**
     *
     * @param initDelta  initial value.
     * @return total health of all stones in all groups in relative terms.
     */
    private float calcRelativeHealth(GoProfiler prof, float initDelta) {
        float delta = initDelta;
        if (USE_RELATIVE_GROUP_SCORING) {
            prof.startRelativeTerritory();
            for (GoGroup g : board_.getGroups()) {
                float health = g.calculateRelativeHealth(board_);
                g.updateTerritory(health);
                delta += health * g.getNumStones();
            }

            prof.stopRelativeTerritory();
        }
        return delta;
    }

    /**
     * Need to loop over the board and determine for each space if it is territory for the specified player.
     * We will first mark visited all the stones that are "controlled" by the specified player.
     * The unoccupied "controlled" positions will be territory.
     * @return the change in score after updating the empty regions.
     */
    private float updateEmptyRegions(boolean isEndOfGame) {
        float diffScore = 0;
        //only do this when the mid-game starts, since early on there is always only one connected empty region.
        int edgeOffset = 1;

        if (board_.getMoveList().getNumMoves() <= 2 * board_.getNumRows()) {
            return diffScore;
        }
        // later in the game we can take the analysis all the way to the edge.
        float ratio = (float)board_.getMoveList().getNumMoves() / board_.getTypicalNumMoves();
        if ((ratio > EMPTY_REGION_EDGE_THRESH) || isEndOfGame) {
            edgeOffset = 0;
        }
        int min = 1+edgeOffset;
        int rMax = board_.getNumRows() - edgeOffset;
        int cMax = board_.getNumCols() - edgeOffset;
        Box box = new Box(min, min, rMax, cMax);

        List<GoBoardPositionList> emptyLists = new LinkedList<GoBoardPositionList>();

        for ( int i = min; i <= rMax; i++ )  {
           for ( int j = min; j <= cMax; j++ ) {
               GoBoardPosition pos = (GoBoardPosition)board_.getPosition(i, j);
               diffScore += updateEmptyRegionFromSeed(box, emptyLists, pos);
           }
        }

        GoBoardUtil.unvisitPositionsInLists(emptyLists);
        return diffScore;
    }

    /**
     * Update diff?Score for the who string connected to pos and mark it visited.
     * If pos is in an eye, update the score contribution for that eye space.
     * @return diffScore value.
     */
    private float updateEmptyRegionFromSeed(Box box, List<GoBoardPositionList> emptyLists,
                                            GoBoardPosition pos) {
        float diffScore = 0;
        if (pos.getString() == null && !pos.isInEye()) {
            assert pos.isUnoccupied();
            if (!pos.isVisited()) {

                // don't go all the way to the borders (until the end of the game),
                // since otherwise we will likely get only one big empty region.
                GoBoardPositionList empties =
                        nbrAnalyzer_.findStringFromInitialPosition(pos, false, false, NeighborType.UNOCCUPIED, box);
                emptyLists.add(empties);

                GoBoardPositionSet nbrs = nbrAnalyzer_.findOccupiedNobiNeighbors(empties);
                float avg = calcAverageScore(nbrs);

                float score = avg * (float)nbrs.size() / Math.max(1, Math.max(nbrs.size(), empties.size()));
                assert (score <= 1.0 && score >= -1.0): "score="+score+" avg="+avg;

                for (GoBoardPosition space : empties) {
                    space.setScoreContribution(score);
                    diffScore += score;
                }
            }
        }
        else if (pos.isInEye()) {
            pos.setScoreContribution(pos.getGroup().isOwnedByPlayer1()? 0.1 : -0.1);
        }
        return diffScore;
    }

    /**
     * @param stones actually the positions containing the stones.
     * @return the average scores of the stones in the list.
     */
    private float calcAverageScore(GoBoardPositionSet stones)
    {
        float totalScore = 0;

        for (GoBoardPosition stone : stones) {           
            GoGroup group = stone.getString().getGroup();
            totalScore += group.getRelativeHealth(board_, false);
        }
        return totalScore/stones.size();
    } 
            
}