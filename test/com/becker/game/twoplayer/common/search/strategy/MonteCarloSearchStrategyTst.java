package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.becker.game.twoplayer.common.search.examples.EvaluationPerspective;
import com.becker.game.twoplayer.common.search.examples.FourLevelGameTreeExample;
import com.becker.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test minimax strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public abstract class MonteCarloSearchStrategyTst extends AbstractSearchStrategyTst {

    protected MonteCarloSearchOptions monteCarloOptions;


    @Override
   protected void setUp() throws Exception {
       super.setUp();
       monteCarloOptions = searchOptions.getMonteCarloSearchOptions();
   }

   /**
    * @return default search options for all games
    */
   @Override
   public TwoPlayerOptions createTwoPlayerGameOptions() {
       TwoPlayerOptions opts =  super.createTwoPlayerGameOptions();
       // consider all moves (effectively)
       opts.getSearchOptions().getBestMovesSearchOptions().setMinBestMoves(100);
       
       MonteCarloSearchOptions options = opts.getSearchOptions().getMonteCarloSearchOptions();
       options.setExploreExploitRatio(1.0);
       options.setMaxSimulations(10);
       return opts;
   }


    @Override
    protected EvaluationPerspective getEvaluationPerspective() {
        return EvaluationPerspective.ALWAYS_PLAYER1;
    }


    /** It fails if you try with just one randome simulation. */
    public void testTwoSimulationSearchPlayer1() {
        monteCarloOptions.setMaxSimulations(2);
        verifyResult(new FourLevelGameTreeExample(false, getEvaluationPerspective()),
                getTwoSimulationSearchPlayer1Result());
    }
    public void testTwoSimulationSearchPlayer2() {
        monteCarloOptions.setMaxSimulations(2);
        verifyResult(new FourLevelGameTreeExample(true, getEvaluationPerspective()),
                getTwoSimulationSearchPlayer2Result());
    }

    public void testTenSimulationSearchPlayer1() {
        monteCarloOptions.setMaxSimulations(10);
        verifyResult(new FourLevelGameTreeExample(false, getEvaluationPerspective()),
                getTenSimulationSearchPlayer1Result());
    }
    public void testTenSimulationSearchPlayer2() {
        monteCarloOptions.setMaxSimulations(10);
        verifyResult(new FourLevelGameTreeExample(true, getEvaluationPerspective()),
                getTenSimulationSearchPlayer2Result());
    }


    // the following expected results are for UCT search. Derivations may differ.

    protected SearchResult getTwoSimulationSearchPlayer1Result() {
        return new SearchResult("0", -8, 2);
    }
    protected SearchResult getTwoSimulationSearchPlayer2Result() {
        return new SearchResult("0", -8, 2);
    }

    protected SearchResult getTenSimulationSearchPlayer1Result() {
        return new SearchResult("0", -8, 10);
    }
    protected SearchResult getTenSimulationSearchPlayer2Result() {
        return new SearchResult("1", -2, 10);
    }
}

