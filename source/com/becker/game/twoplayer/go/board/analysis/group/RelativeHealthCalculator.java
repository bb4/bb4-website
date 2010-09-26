package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.elements.GoGroup;
import com.becker.game.twoplayer.go.board.elements.GoGroupSet;

import java.util.Set;

/**
 * Determine the absolute health of a group independent of the health of neighboring groups.
 * @author Barry Becker
 */
public class RelativeHealthCalculator {

    /** The group of go stones that we are analyzing. */
    private GoGroup group_;

    /**
     * Constructor
     * @param group the group to analyze
     */
    public RelativeHealthCalculator(GoGroup group) {
        group_ = group;
    }

    /**
     * Calculate the relative health of a group.
     * This method must be called only after calculateAbsoluteHealth has be done for all groups.
     * Good health is positive for a black group.
     * This measure of the group's health should be much more accurate than the absolute health
     * because it takes into account the relative health of neighboring groups.
     * If the health of an opponent bordering group is in worse shape
     * than our own then we get a boost since we can probably kill that group first.
     *
     * @return the overall health of the group.
     */
    public float calculateRelativeHealth(GoBoard board,float absoluteHealth )
    {
        return boostRelativeHealthBasedOnWeakNbr(board, absoluteHealth);
    }

    /**
     * If there is a weakest group, then boost ourselves relative to it.
     * it may be a positive or negative boost to our health depending on its relative strength.
     */
    private float boostRelativeHealthBasedOnWeakNbr(GoBoard board, float absoluteHealth) {

        // the default if there is no weakest group.
        float relativeHealth = absoluteHealth;
        GoBoardPositionSet groupStones = group_.getStones();
        GoGroup weakestGroup = findWeakestGroup(board, groupStones);

        if (weakestGroup != null)  {
            double proportionWithEnemyNbrs = findProportionWithEnemyNbrs(groupStones);

            double diff = absoluteHealth + weakestGroup.getAbsoluteHealth();
            // @@ should use a weight to help determine how much to give a boost.

            // must be bounded by -1 and 1
            relativeHealth =
                    (float) (Math.min(1.0, Math.max(-1.0, absoluteHealth + diff * proportionWithEnemyNbrs)));
        }
        GoBoardUtil.unvisitPositions(groupStones);
        return relativeHealth;
    }

    /**
     * @return the weakest bordering enemy group.
     */
    private GoGroup findWeakestGroup(GoBoard board, GoBoardPositionSet groupStones) {
        GoProfiler.getInstance().start(GoProfiler.GET_ENEMY_GROUPS_NBRS);
        Set cachedEnemyNbrGroups = getEnemyGroupNeighbors(board, groupStones);
        GoProfiler.getInstance().stop(GoProfiler.GET_ENEMY_GROUPS_NBRS);

        // we multiply by a +/- sign depending on the side
        float side = group_.isOwnedByPlayer1()? 1.0f : -1.0f;

        // of these enemy groups which is the weakest?
        double weakestHealth = -side;
        GoGroup weakestGroup = null;
        for (Object egroup : cachedEnemyNbrGroups) {
            GoGroup enemyGroup = (GoGroup)egroup;
            double h = enemyGroup.getAbsoluteHealth();
            if ((side * h) > (side * weakestHealth)) {
                weakestHealth = h;
                weakestGroup = enemyGroup;
            }
        }
        return weakestGroup;
    }

    /**
     * What proportion of the groups stones are close to enemy groups?
     * this gives us an indication of how surrounded we are.
     * If we are very surrounded then we give a big boost for being stronger or weaker than a nbr.
     * If we are not very surrounded then we don't give much of a boost because there are other
     * ways to make life (i.e. run out/away).
     * @return proportion of our group stones with enemy neighbors.
     */
    private double findProportionWithEnemyNbrs(GoBoardPositionSet groupStones) {

        int numWithEnemyNbrs = 0;
        for (Object p : groupStones) {
            GoBoardPosition stone = (GoBoardPosition)p;
            if (stone.isVisited()) {
                numWithEnemyNbrs++;
                stone.setVisited(false); // clear the visited state.
            }
        }
        return(double)numWithEnemyNbrs / ((double)groupStones.size() + 2);
    }

    /**
     * @@ may need to make this n^2 method more efficient.
     * note: has intentional side effect of marking stones with enemy group nbrs as visited (within groupStones).
     * @param board
     * @param groupStones the set of stones in the group to find enemies of.
     * @return a HashSet of the groups that are enemies of this group
     */
    private Set getEnemyGroupNeighbors(GoBoard board, GoBoardPositionSet groupStones)
    {
        GoGroupSet enemyNbrs = new GoGroupSet();
        NeighborAnalyzer nbrAnalyzer =  new NeighborAnalyzer(board);
        
        // for every stone in the group.
        for (GoBoardPosition stone : groupStones) {
            Set nbrs = nbrAnalyzer.findGroupNeighbors(stone, false);

            // if the stone has any enemy nbrs then mark it visited.
            // later we will count how many got visited.
            // this is a bit of a hack to determine how surrounded the group is by enemy groups
            for (Object peNbr : nbrs) {
                GoBoardPosition possibleEnemy = (GoBoardPosition)peNbr;
                if (possibleEnemy.getPiece().isOwnedByPlayer1() != group_.isOwnedByPlayer1()
                        && !possibleEnemy.isInEye()) {
                    // setting visited to true to indicate there is an enemy nbr within group distance.
                    stone.setVisited(true);
                    // if the group is already there, it does not get added again.
                    assert (possibleEnemy.getGroup()!=null);
                    enemyNbrs.add(possibleEnemy.getGroup());
                }
            }
        }
        return enemyNbrs;
    }
}