package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.examples.EvaluationPerspective;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test negascout strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class NegaScoutSearchStrategyTest extends AbstractSearchStrategyTst {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaScoutStrategy(searchable, weights);
    }

    @Override
    protected EvaluationPerspective getEvaluationPerspective() {
        return EvaluationPerspective.CURRENT_PLAYER;
    }


    @Override
    protected SearchResult getTwoLevelPlayer1Result() {
        return new SearchResult("0", 7, 5);
    }
    
    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer1() {
        return new SearchResult("0", -5, 5);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult("1", 4, 7);
    }

    @Override
    protected SearchResult getThreeLevelPlayer1Result() {
        return new SearchResult("0", -5, 12);
    }

    @Override
    protected SearchResult getThreeLevelWithABResult() {
        return new SearchResult("0", -5, 12);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithoutABResultPlayer1() {
        return new SearchResult("0", -5, 5);
    }

}