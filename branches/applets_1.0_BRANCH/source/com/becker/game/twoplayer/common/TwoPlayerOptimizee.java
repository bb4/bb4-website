package com.becker.game.twoplayer.common;

import com.becker.game.common.GameWeights;
import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Two sets of gameweights that can be optimized by repetitive play.
 *
 * @author Barry Becker
 */
class TwoPlayerOptimizee implements Optimizee {

    private TwoPlayerController controller_;

    /**
     * Constructor.
     * @param controller game controller
     */
    public TwoPlayerOptimizee(TwoPlayerController controller) {
        controller_ = controller;
    }

    /**
     * If true is returned, then compareFitness will be used and evaluateFitness will not
     * otherwise the reverse will be true.
     * @return return true if we evaluate the fitness by comparison
     */
    public boolean  evaluateByComparison()
    {
        return true;
    }

    /**
     * Attributes a measure of fitness to the specified set of parameters.
     * There's no good way for a game playing program to do this because it
     * can only evaluate itself relative to another player.
     * see compareFitness below.
     * @param params the set of parameters to misc
     * @return the fitness measure. The higher the better
     */
    public double evaluateFitness( ParameterArray params )
    {
       return 0.0;
    }

    public double getOptimalFitness() {
        return 0;
    }

    /**
     * @return the number of factors we take into consideration when optimizing.
     */
    public int getNumParameters() {
        return controller_.getComputerWeights().getDefaultWeights().size();
    }

    /**
     * Compares to sets of game parameters.
     * It does this by playing the computer against itself. One computer player has the params1
     * weights and the other computer player uses the params2 weights.
     * If the player using params1 wins then a positive value proportional to the strength of the win is returned.
     *
     * To remove the advantage we get from playing first, 2 runs are done
     *  The first one where params1 plays first, and the second where params2 plays first.
     *  This should remove the bias.
     *
     * @param params1 set of weight for one of the sides
     * @param params2 set of weights for the other side
     * @return the amount that params1 are better than params2. May be negative if params1 are better.
     */
    public double compareFitness( ParameterArray params1, ParameterArray params2 ) {
        GameWeights weights = controller_.getComputerWeights();
        weights.setPlayer1Weights(params1);
        weights.setPlayer2Weights(params2);
        double run1 = runComputerVsComputer();

        weights.setPlayer1Weights(params2);
        weights.setPlayer2Weights(params1);
        double run2 = runComputerVsComputer();

        return (run1 - run2);
    }

    /**
     * Run a computer player against itself
     * @return if positive then computer1 won, else computer2 won.
     *   the magnitude of this returned value indicates how much it won by.
     */
    private double runComputerVsComputer() {
        boolean done = false;
        controller_.reset();
        controller_.computerMovesFirst();

        if (controller_.get2PlayerViewer() != null)  {
            controller_.get2PlayerViewer().showComputerVsComputerGame();
        }
        else {
            // run in batch mode where the viewer is not available.
            while ( !done ) {
                done = controller_.getSearchable().done(controller_.findComputerMove( false ), true);
                // if done the final move was played
                if ( !done ) {
                    done = controller_.getSearchable().done(controller_.findComputerMove( true ), true);
                }
            }
        }
        if (controller_.getPlayers().getPlayer1().hasWon())
            return controller_.getStrengthOfWin();
        else
            return -controller_.getStrengthOfWin();
    }
}