package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.WinProbabilityCaclulator;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *  Implementation of Upper Confidence Tree (UCT) search strategy.
 *  This method uses a monte carlo (stochastic) method and is fundamentally different than minimax and its derivatives.
 *  It's subclasses define the key search algorithms for 2 player zero sum games with perfect information.
 *
 *    - add option to use concurrency. Need lock on uctNodes
 *
 *  @author Barry Becker
 */
public class UctStrategy extends AbstractSearchStrategy {

    private static final double WIN_THRESH = (float)WINNING_VALUE / 6.0;


    /** ratio of exploration to exploitation (of known good moves) while searching.  */
    private double exploreExploitRatio;

    /** Number of moves to play in a random game from the starting move state */
    private int numRandomLookAhead;

    /** When selecting a random move for a random game, select from only this many of the top moves. */
    private int percentLessThanBestThresh;


    /**
     * Constructor - do not call directly.
     * @param searchable the thing to be searched that has options and can make/undo moves.
     * @param weights coefficients for the evaluation polynomial that indirectly determines the best move.
     */
    UctStrategy( Searchable searchable, ParameterArray weights ) {
        super(searchable, weights);
        exploreExploitRatio = getOptions().getMonteCarloSearchOptions().getExploreExploitRatio();
        numRandomLookAhead = getOptions().getMonteCarloSearchOptions().getRandomLookAhead();
        percentLessThanBestThresh = getOptions().getBestMovesSearchOptions().getPercentLessThanBestThresh();
    }

    @Override
    public SearchOptions getOptions() {
        return searchable_.getSearchOptions();
    }

    /**
     * {@inheritDoc}
     */
    public TwoPlayerMove search(TwoPlayerMove lastMove, SearchTreeNode parent) {

        int numSimulations = 0;
        int maxSimulations = getOptions().getMonteCarloSearchOptions().getMaxSimulations();
        UctNode root = new UctNode(lastMove);

        while (numSimulations < maxSimulations ) {
            playSimulation(root, parent);
            numSimulations++;
            percentDone_ = (100 *  numSimulations) / maxSimulations;
        }
        //root.printTree();
        return root.findBestChildMove();
    }

    /**
     * This recursive method ultimately expands the in memory game try by one node and updates that nodes parents.
     * @return true if player1 wins when running a simulation from this board position.
     */
    public float playSimulation(UctNode lastMoveNode, SearchTreeNode parent) {

        float p1Score;
        if (lastMoveNode.getNumVisits() == 0) {
            p1Score = playRandomGame(lastMoveNode.move);
            movesConsidered_++;
        }
        else {
            UctNode nextNode = null;

            if (!searchable_.done(lastMoveNode.move, false))  {
               if (!lastMoveNode.hasChildren()) {
                   int added = lastMoveNode.addChildren(searchable_.generateMoves(lastMoveNode.move, weights_, true));
                   if (added == 0) {
                       System.out.println("no moves added for " + lastMoveNode);
                   }
                   addNodesToTree(parent, lastMoveNode.getChildren());
               }
               nextNode = uctSelect(lastMoveNode);
            }

            // may be null if there are no move valid moves or lastMoveNode won the game.
            if (nextNode != null) {
                SearchTreeNode nextParent = parent!=null ? parent.findChild(nextNode.move) : null;
                searchable_.makeInternalMove(nextNode.move);
                p1Score = playSimulation(nextNode, nextParent);
                searchable_.undoInternalMove(nextNode.move);
            } else {
                p1Score = WinProbabilityCaclulator.getChanceOfPlayer1Winning(lastMoveNode.move.getValue());
            }
        }

        lastMoveNode.update(p1Score);
        if (parent != null)
            parent.attributes = lastMoveNode.getAttributes();
        return p1Score;
    }

    /**
     * Selects the best child of parentNode.
     * @return the best child of parentNode. May be null if there are no next moves.
     */
    private UctNode uctSelect(UctNode parentNode) {
        double bestUct = -1.0;
        UctNode selected = null;

        for (UctNode child : parentNode.getChildren()) {
            double uctValue = child.calculateUctValue(exploreExploitRatio, parentNode.getNumVisits());
            if (uctValue > bestUct) {
                bestUct = uctValue;
                selected = child;
            }
        }
        return selected;
    }

    /**
     * Plays a semi-random game from the current node position.
     * Its semi random in the sense that we try to avoid obviously bad moves.
     * @return a score (0 = p1 lost; 0.5 = tie; or 1= p1 won) indication p1 advantage.
     */
    private float playRandomGame(TwoPlayerMove move) {

        Searchable s = searchable_.copy();
        return playRandomMove(move, s, s.getNumMoves());
    }

    /**
     * Plays a semi-random game from the current node position.
     * Its semi-random in the sense that we try to avoid obviously bad moves.
     * @return a score (0 = p1 lost; 0.5 = tie; or 1= p1 won) indication p1 advantage.
     */
    private float playRandomMove(TwoPlayerMove move, Searchable searchable, int startNumMoves) {

        int numRandMoves = searchable.getNumMoves() - startNumMoves;
        if (numRandMoves >= numRandomLookAhead || searchable.done(move, false)) {
            // GoGameExporter exporter = new GoGameExporter((GoBoard)searchable.getBoard());
            // exporter.saveToFile( FileUtil.PROJECT_HOME + "temp/tmp/file_" + startNumMoves + "_" + move.hashCode(), null);
            int score = searchable.worth( move, weights_, true );
            move.setValue(score);
            return WinProbabilityCaclulator.getChanceOfPlayer1Winning(score);
        }
        MoveList moves = searchable.generateMoves(move, weights_, true);
        if (moves.size() == 0) {
            return WinProbabilityCaclulator.getChanceOfPlayer1Winning(move.getValue());
        }
        TwoPlayerMove randomMove = (TwoPlayerMove) moves.getRandomMoveForThresh(percentLessThanBestThresh);

        searchable.makeInternalMove(randomMove);
        return playRandomMove(randomMove, searchable, startNumMoves);
    }

    /**
     * add a move to the visual game tree (if parent not null).
     * If the new node is already in the tree, do not add it, but maybe update values.
     */
    protected void addNodesToTree(SearchTreeNode parent, List<UctNode> childUctNodes) {

        if (parent == null) return;

        for (UctNode child : childUctNodes)  {
           addNodeToTree(parent, child.move, child.getAttributes());
        }
    }
}