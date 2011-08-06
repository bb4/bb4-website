package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.becker.game.twoplayer.common.search.examples.EvaluationPerspective;
import com.becker.game.twoplayer.common.search.examples.FourLevelGameTreeExample;
import com.becker.game.twoplayer.common.search.examples.ThreeLevelGameTreeExample;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test negascout strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class NegaScoutSearchStrategyTest extends AbstractBruteSearchStrategyTst {

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
        return new SearchResult("1", 8, 7);   // inherited should be 2 or 8?
    }
    @Override
    protected SearchResult getTwoLevelPlayer2Result() {
        return new SearchResult("0", 7, 5);
    }

    @Override
    protected SearchResult getTwoLevelQuiescensePlayer1Result() {
        return new SearchResult("0", 3, 9);
    }
    @Override
    protected SearchResult getTwoLevelQuiescensePlayer2Result() {
        return new SearchResult("1", 4, 11);
    }
    @Override
    protected SearchResult getTwoLevelQuiescenseABPlayer1Result() {
        return new SearchResult("0", 3, 9);
    }

    @Override
    protected SearchResult getLadderMax3QuiescensePlayer1Result() {
        return new SearchResult("0", 3, 9);
    }
    @Override
    protected SearchResult getLadderMax3QuiescensePlayer2Result() {
        return new SearchResult("1", 4, 11);
    }
    @Override
    protected SearchResult getLadderMax4QuiescensePlayer1Result() {
        return new SearchResult("0", 3, 9);
    }
    @Override
    protected SearchResult getLadderMax4QuiescensePlayer2Result() {
        return new SearchResult("1", 4, 11);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult( "1", 4, 7);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithoutABResultPlayer1() {
        return new SearchResult("0", 5, 5);
    }

    @Override
    protected SearchResult getThreeLevelPlayer1Result() {
        return new SearchResult("0", -4, 11);
    }
    @Override
    protected SearchResult getThreeLevelPlayer2Result() {
        return new SearchResult("0", -5, 12);
    }
    @Override
    protected SearchResult getThreeLevelPlayer2WithABResult() {
        return new SearchResult( "0", -5, 12);
    }

    @Override
    protected SearchResult getFourLevelPlayer1Result() {
        return new SearchResult("0", 33, 23);
    }
    @Override
    protected SearchResult getFourLevelPlayer2Result() {
        return new SearchResult("1", 22, 27);
    }
    @Override
    protected SearchResult getFourLevelABPlayer1Result() {
        return new SearchResult("0", 33, 23);
    }
    @Override
    protected SearchResult getFourLevelABPlayer2Result() {
        return new SearchResult("1", 22, 27);
    }
}