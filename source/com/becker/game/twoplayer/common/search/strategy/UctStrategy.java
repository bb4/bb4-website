package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

/**
 *  Implementation of Upper Confidedence Tree (UCT) search strategy.
 *  This method uses a monte carlo (stochastic) method and is fundamentally different than minimax and its derivatives.
 *  It's sublcasses define the key search algorithms for 2 player zero sum games with perfect information.
 *
 *  @author Barry Becker
 */
public class UctStrategy extends AbstractSearchStrategy {

    /** ratio of exploration to exploitaion (of known good moves) while searching.  */
    private double exploreExploitRatio;

    /** When selecting a random move for a random game, select from only this many of the top moves. */
    private int topMovesToConsider;

    /**
     * Constructor - do not call directly.
     * @param searchable the game controller that has options and can make/undo moves.
     * @param weights coefficients for the evaluation polynomial that indirectly determines the best move.
     */
    UctStrategy( Searchable searchable, ParameterArray weights ) {
        super(searchable, weights);
        exploreExploitRatio = getOptions().getMonteCarloSearchOptions().getExploreExploitRatio();
        topMovesToConsider = getOptions().getMinBestMoves();
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
        boolean interrupted = false;

        UctNode root = new UctNode(lastMove);

        while (numSimulations < maxSimulations && !interrupted) {
            playSimulation(root, parent);
            numSimulations++;
            percentDone_ = (100 * numSimulations) / maxSimulations;
        }
        return root.bestNode.move;
    }

    public boolean playSimulation(UctNode lastMoveNode, SearchTreeNode parent) {

        boolean player1Wins = false;
        if (lastMoveNode.numVisits == 0) {
            player1Wins = playRandomGame(lastMoveNode.move);
        }
        else {
            if (!lastMoveNode.hasChildren()) {
                lastMoveNode.addChildren(searchable_.generateMoves(lastMoveNode.move, weights_, true));
            }
            UctNode nextNode = uctSelect(lastMoveNode);

            // may be null if there are no move valid moves.
            // this may be happening a little more than expected.
            if (nextNode != null) {
                SearchTreeNode child = addNodeToTree(parent, nextNode);

                searchable_.makeInternalMove(nextNode.move);
                player1Wins = playSimulation(nextNode, child);
                searchable_.undoInternalMove(nextNode.move);
            }
        }

        lastMoveNode.numVisits++;
        lastMoveNode.updateWin(player1Wins);

        lastMoveNode.setBestNode();
        return player1Wins;
    }

    /**
     * Selects the best child of parentNode.
     * @return the best child of parentNode. May be null if there are no next moves.
     */
    private UctNode uctSelect(UctNode parentNode) {
        double bestUct = -1.0;
        UctNode selected = null;

        for (UctNode child : parentNode.getChildren()) {
            double uctValue = child.calculateUctValue(exploreExploitRatio, parentNode.numVisits);
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
     * @return whether or not player1 won.
     */
    private boolean playRandomGame(TwoPlayerMove move) {

        return playRandomMove(move, searchable_.copy()); // searchable_); // searchable_.copy());
    }

    /**
     * Plays a semi-random game from the current node position.
     * Its semi random in the sense that we try to avoid obviously bad moves.
     * @return whether or not player1 won.
     */
    private boolean playRandomMove(TwoPlayerMove move, Searchable searchable) {

        if (searchable.done(move, false)) {
            return move.getValue() > 0;
        }
        MoveList moves = searchable.generateMoves(move, weights_, true);
        if (moves.size() == 0) {
            return move.getValue() > 0;
        }
        TwoPlayerMove randomMove = (TwoPlayerMove) moves.getRandomMove(topMovesToConsider);

        searchable.makeInternalMove(randomMove);
        boolean result = playRandomMove(randomMove, searchable);
        //searchable.undoInternalMove(randomMove);     // really do not want to do this for perf reasons.
        return result;
    }

    /**
     * add a move to the visual game tree (if parent not null).
     * If the new node is already in the tree, do not add it, but maybe update values.
     * @return the node added to the tree.
     */
    protected SearchTreeNode addNodeToTree(SearchTreeNode parent, UctNode node ) {

        if (parent == null) return null;
        SearchTreeNode alreadyChild = parent.hasChild(node.move);
        if (alreadyChild != null)  {
            alreadyChild.attributes = node.getAttributes();
            return alreadyChild;
        }
        movesConsidered_++;
        return addNodeToTree(parent, node.move, node.getAttributes());
    }
}