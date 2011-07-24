package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.common.board.Board;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoProfiler;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.elements.group.GoGroup;
import com.becker.game.twoplayer.go.board.elements.group.GoGroupSet;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

import java.util.Set;

/**
 * Finds the weakest group on the board relative to some other group.
 * @author Barry Becker
 */
class WeakestGroupFinder {

    private GoBoard board;
    private GroupAnalyzerMap analyzerMap;

    /**
     * Constructor
     */
    public WeakestGroupFinder(GroupAnalyzerMap analyzerMap, GoBoard board) {

        this.board = board;
        this.analyzerMap = analyzerMap;
    }

    /**
     * @param groupStones the stones to find the weakest bordering neighbor of.
     * @return the weakest bordering enemy group.
     */
    public GoGroup findWeakestGroup(GoBoardPositionSet groupStones) {

        boolean isPlayer1 = groupStones.getOneMember().getPiece().isOwnedByPlayer1();
        Set enemyNbrGroups = getEnemyGroupNeighbors(groupStones, isPlayer1);

        // we multiply by a +/- sign depending on the side
        float side = isPlayer1? 1.0f : -1.0f;

        // of these enemy groups which is the weakest?
        double weakestHealth = -side;
        GoGroup weakestGroup = null;
        for (Object egroup : enemyNbrGroups) {
            GoGroup enemyGroup = (GoGroup)egroup;
            double h = analyzerMap.getAnalyzer(enemyGroup).getAbsoluteHealth();
            if ((side * h) > (side * weakestHealth)) {
                weakestHealth = h;
                weakestGroup = enemyGroup;
            }
        }
        return weakestGroup;
    }

    /**
     * @@ may need to make this n^2 method more efficient.
     * note: has intentional side effect of marking stones with enemy group nbrs as visited (within groupStones).
     * @param groupStones the set of stones in the group to find enemies of.
     * @return a HashSet of the groups that are enemies of this group
     */
    private Set getEnemyGroupNeighbors(GoBoardPositionSet groupStones, boolean isPlayer1)  {
        GoProfiler.getInstance().startGetEnemyGroupNbrs();
        GoGroupSet enemyNbrs = new GoGroupSet();
        NeighborAnalyzer nbrAnalyzer =  new NeighborAnalyzer(board);
        
        // for every stone in the group.
        for (GoBoardPosition stone : groupStones) {
            GoBoardPositionSet nbrs = nbrAnalyzer.findGroupNeighbors(stone, false);
            addEnemyNeighborsForStone(enemyNbrs, stone, nbrs, isPlayer1);
        }
        GoProfiler.getInstance().stopGetEnemyGroupNbrs();
        return enemyNbrs;
    }

    /**
     * if the stone has any enemy nbrs then mark it visited.
     * later we will count how many got visited.
     * this is a bit of a hack to determine how surrounded the group is by enemy groups.
     * @param enemyNbrs the enemy neighbors to add.
     * @param stone stone that enyNbrs are enemy of.
     * @param nbrs set of stones to add enemyNbrs to.
     */
    private void addEnemyNeighborsForStone(GoGroupSet enemyNbrs, GoBoardPosition stone,
                                           GoBoardPositionSet nbrs, boolean isPlayer1) {
         
        for (GoBoardPosition possibleEnemy  : nbrs) {
            if (possibleEnemy.getPiece() != null
                    && possibleEnemy.getPiece().isOwnedByPlayer1() != isPlayer1
                    && !possibleEnemy.isInEye()) {
                // setting visited to true to indicate there is an enemy nbr within group distance.
                stone.setVisited(true);
                // if the group is already there, it does not get added again.
                assert (possibleEnemy.getGroup()!=null) : "Possible enemy, "
                        + possibleEnemy + ", had no group associated with it!";
                enemyNbrs.add(possibleEnemy.getGroup());
            }
        }
    }
}