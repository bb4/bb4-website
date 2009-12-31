package com.becker.game.twoplayer.common.search.test.strategy;

import com.becker.game.common.GameController;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.strategy.AbstractSearchStrategy;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.twoplayer.common.search.test.ISearchableHelper;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import junit.framework.TestCase;

/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will excersize these methods for specific game instances.
 *
 * @author Barry Becker
 */
public abstract class AbstractSearchStrategyTst extends TestCase {

    protected TwoPlayerController controller;
    protected TwoPlayerOptions options;
    protected ISearchableHelper helper;

    private static final int DEFAULT_LOOKAHEAD = 2;
    private static final int DEFAULT_BEST_PERCENTAGE = 100;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = createSearchableHelper();
        controller = helper.createController();
        options = createDefaultOptions();
        controller.setOptions(options);
    }

    protected TwoPlayerOptions createDefaultOptions() {
        TwoPlayerOptions options = helper.createTwoPlayerGameOptions();
        options.setLookAhead(DEFAULT_LOOKAHEAD);
        options.setAlphaBeta(true);
        options.setPercentageBestMoves(DEFAULT_BEST_PERCENTAGE);
        options.setQuiescence(false);
        return options;
    }


    protected abstract SearchableHelper createSearchableHelper();

    protected abstract SearchStrategyType getSearchStrategyToTest();

    /**
     * verify that we can retrieve the expected search value.
     */
    public void testSearch() {
        controller.restoreFromFile(helper.getTestFile());
        options.setSearchStrategyMethod(getSearchStrategyToTest());
        System.out.println("now comparins " +  getExpectedNextMove() + " with " + getNextMove());
        assertEquals("We did not get the next move that we expected after searching.", 
                getExpectedNextMove(),getNextMove());
    }


    protected abstract TwoPlayerMove getExpectedNextMove();

    protected TwoPlayerMove getNextMove() {
        SearchStrategy strategy =
                controller.getTwoPlayerOptions().getSearchStrategy(controller.getSearchable(),
                                                                   controller.getDefaultWeights());
        TwoPlayerMove lastMove = (TwoPlayerMove)controller.getLastMove();
        SearchTreeNode root = new SearchTreeNode(lastMove);

        return strategy.search(lastMove, SearchStrategy.INFINITY, -SearchStrategy.INFINITY, root);
    }
}