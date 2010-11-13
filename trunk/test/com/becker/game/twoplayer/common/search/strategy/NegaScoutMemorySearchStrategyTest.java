package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.examples.EvaluationPerspective;
import com.becker.game.twoplayer.common.search.examples.LadderQuiescentExample;
import com.becker.game.twoplayer.common.search.examples.OneLevelGameTreeExample;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test negascout memory strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class NegaScoutMemorySearchStrategyTest extends NegaScoutSearchStrategyTest {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaScoutMemoryStrategy(searchable, weights);
    }


    @Override
    protected SearchResult getTwoLevelQuiescensePlayer2Result() {
        return new SearchResult("1", 4, 10);
    }   

    @Override
    protected SearchResult getTwoLevelQuiescenseABPlayer2Result() {
        return new SearchResult("1", 4, 10);
    }

    @Override
    protected SearchResult getLadderMax3QuiescensePlayer2Result() {
        return new SearchResult("1", 4, 10);
    }

    @Override
    protected SearchResult getLadderMax4QuiescensePlayer2Result() {
        return new SearchResult("1", 4, 10);
    }

    @Override
    protected SearchResult getFourLevelPlayer1Result() {
        return new SearchResult("0", 33, 22);
    }
    @Override
    protected SearchResult getFourLevelPlayer2Result() {
        return new SearchResult("1", 22, 27);
    }


    @Override
    protected SearchResult getFourLevelABPlayer1Result() {
        return new SearchResult("0", 33, 22);
    }

}