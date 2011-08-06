package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.examples.*;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test negamax memory strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class NegaMaxMemorySearchStrategyTest extends NegaMaxSearchStrategyTest {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaMaxMemoryStrategy(searchable, weights);
    }

    // suspect result
    public void testTwoLevelQuiescenseABPlayer2Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        bruteSearchOptions.setAlphaBeta(true);
        GameTreeExample eg = new TwoLevelQuiescentExample(false, getEvaluationPerspective());
        verifyResult(eg, getTwoLevelQuiescenseABPlayer2Result());
    }

    protected SearchResult getTwoLevelQuiescenseABPlayer1Result() {
        return new SearchResult("0", 3, 4);
    }
    // Suspect result. Shou;d be "1" 4 or close to it.
    protected SearchResult getTwoLevelQuiescenseABPlayer2Result() {
        return new SearchResult("0", 2, 9);  // seems wrong
    }
    
    @Override
    protected SearchResult getThreeLevelPlayer1WithABResult() {
        return new SearchResult( "0", -4, 8);
    }
    @Override
    protected SearchResult getThreeLevelPlayer2WithABResult() {
        return new SearchResult( "0", -5, 5);
    }

    @Override
    protected SearchResult getLadderMax4QuiescensePlayer2Result() {
        return new SearchResult("1", 4, 15);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer1() {
        return new SearchResult( "0", 5, 3);
    }
    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult( "1", 4, 4);
    }

    @Override
    protected SearchResult getFourLevelABPlayer1Result() {
        return new SearchResult("0", 6, 9);
    }
    @Override
    protected SearchResult getFourLevelABPlayer2Result() {
        return new SearchResult("1", 14, 12);
    }

}