package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.becker.game.twoplayer.common.search.examples.EvaluationPerspective;
import com.becker.game.twoplayer.common.search.examples.FourLevelGameTreeExample;
import com.becker.game.twoplayer.common.search.examples.ThreeLevelGameTreeExample;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test negamax strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class NegaMaxSearchStrategyTest extends AbstractSearchStrategyTst {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaMaxStrategy(searchable, weights);
    }

    @Override
    protected EvaluationPerspective getEvaluationPerspective() {
        return EvaluationPerspective.CURRENT_PLAYER;
    }
}