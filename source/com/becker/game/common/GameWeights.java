package com.becker.game.common;

import com.becker.optimization.ParameterArray;

/**
 * The GameWeights define the coefficients to use by the
 * evaluation polynomial used by each computer player.
 *
 * @author Barry Becker
 */
public class GameWeights
{
    private static final double MIN_WEIGHT = 0.0;

    private static int numWeights_;

    private ParameterArray defaultWeights_ = null;
    private ParameterArray p1Weights_ = null;
    private ParameterArray p2Weights_ = null;

    private String[] names_ = null;
    private String[] descriptions_ = null;

    public GameWeights( ParameterArray defaultWeights )
    {
        numWeights_ = defaultWeights.size(); // this will not change once set.

        defaultWeights_ = defaultWeights;
        names_ = new String[numWeights_];
        descriptions_ = new String[numWeights_];

        for ( int i = 0; i < numWeights_; i++ ) {
            names_[i] = "Weight " + i;
            descriptions_[i] = "The weighting coefficient for the " + i + "th term of the evaluation polynomial";
        }
        init();
    }

    public GameWeights( double[] defaultWeights, double[] maxWeights, String[] names, String[] descriptions )
    {
        numWeights_ = defaultWeights.length;
        double[] minVals = new double[numWeights_];

        for (int i=0; i<numWeights_; i++)
        {
            minVals[i] = MIN_WEIGHT;
            //maxVals[i] = MAX_WEIGHT; // for all game programs
        }
        defaultWeights_ =  new ParameterArray(defaultWeights, minVals, maxWeights, names);

        names_ = names;
        descriptions_ = descriptions;

        init();
    }

    private void init()
    {
        p1Weights_ = defaultWeights_.copy();
        p2Weights_ = defaultWeights_.copy();
    }

    public static int getNumWeights()
    {
        return numWeights_;
    }

    /**
     * @return the weights for player1. It a reference, so changing them will change the weights in this structure
     */
    public final ParameterArray getPlayer1Weights()
    {
        return p1Weights_;
    }

    /**
     * @return the weights for player1. It a reference, so changing them will change the weights in this structure
     */
    public final ParameterArray getPlayer2Weights()
    {
        return p2Weights_;
    }

    public final void setPlayer1Weights( ParameterArray p1Weights)
    {
       verify(p1Weights);
       p1Weights_ = p1Weights;
    }

    public final void setPlayer2Weights( ParameterArray p2Weights)
    {
       verify(p2Weights);
       p1Weights_ = p2Weights;
    }

    private static final void verify( ParameterArray wts)
    {
       assert wts.size() == numWeights_:
               "Incorrect number of weights: "+ wts.size()+" you need "+ numWeights_;
    }

    /**
     * @return the default weights. It a reference, so changing them will change the weights in this structure
     */
    public final ParameterArray getDefaultWeights()
    {
        return defaultWeights_;
    }

    /**
     * @return short description of weight i
     */
    public final String getName( int i )
    {
        return names_[i];
    }

    /**
     * @return description of weight i (good for putting in a tooltip)
     */
    public final String getDescription( int i )
    {
        return descriptions_[i];
    }

    /**
     * @return the maximum allowed value of weight i
     */
    public final double getMaxWeight( int i )
    {
        return defaultWeights_.get(i).maxValue;
    }

    /**
     * nicely print the weights
     */
    public final String toString()
    {
        return "Player1's weights are:"+p1Weights_+"\nPlayer2's weights are "+p2Weights_;
    }
}

