package com.becker.game.twoplayer.common.ui.options;

import com.becker.common.util.Util;
import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.GameOptions;
import com.becker.game.common.ui.GameOptionsDialog;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel that shows the options for search strategies that use brute force (like minimax and derivatives).
 *
 * @author Barry Becker
 */
public class BruteSearchOptionsPanel extends JPanel {

    SearchOptions searchOptions_;

    private NumberInput lookAheadField_;
    private JCheckBox alphabetaCheckbox_;
    private JCheckBox quiescenceCheckbox_;

    /**
     * Constructor
     */
    public BruteSearchOptionsPanel(SearchOptions sOptions) {
        searchOptions_ = sOptions;
        initialize();
    }

    /**
     * @return brute search options
     */
    public BruteSearchOptions updateBruteOptionsOptions() {

        BruteSearchOptions bruteOptions = searchOptions_.getBruteSearchOptions();

        bruteOptions.setAlphaBeta(alphabetaCheckbox_.isSelected());
        bruteOptions.setQuiescence(quiescenceCheckbox_.isSelected());
        bruteOptions.setLookAhead(lookAheadField_.getIntValue());

        return bruteOptions;
    }

    /**
     * create the panel with ui element for setting the options
     */
    protected void initialize() {

        this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        BruteSearchOptions bruteOptions = searchOptions_.getBruteSearchOptions();

        // look ahead
        JLabel treeUpperBound = new JLabel();
        lookAheadField_ =
                new NumberInput(GameContext.getLabel("MOVES_TO_LOOKAHEAD"), bruteOptions.getLookAhead(),
                                GameContext.getLabel("MOVES_TO_LOOKAHEAD_TIP"), 1, 16, true);

        this.add( lookAheadField_ );

        // alpha-beta pruning option
        alphabetaCheckbox_ =
                new JCheckBox( GameContext.getLabel("USE_PRUNING"), bruteOptions.getAlphaBeta());
        alphabetaCheckbox_.setToolTipText( GameContext.getLabel("USE_PRUNING_TIP") );
        this.add( alphabetaCheckbox_ );

        // show profile info option
        quiescenceCheckbox_ = new JCheckBox( GameContext.getLabel("USE_QUIESCENCE"), bruteOptions.getQuiescence() );
        quiescenceCheckbox_.setToolTipText( GameContext.getLabel("USE_QUIESCENCE_TIP") );
        this.add( quiescenceCheckbox_ );
    }

    /**
     * Calculate an upper limit on the number of moves that will be examined by the minimax algorithm.
     * The actual number of moves may be much less if alpha-beta is used or if
     * the natural branch factor for the game is less then the numBestMoves limit
     */
    private static long calcTreeUpperBound( int lookAhead, int numBestMoves )
    {
        long upperBound = numBestMoves;
        for ( int i = 2; i <= lookAhead; i++ )
            upperBound += Math.pow( (double) numBestMoves, (double) i );
        return upperBound;
    }

    /*
    private class UpperBoundKeyListener extends KeyAdapter
    {
        NumberInput field_ = null;
        JLabel treeBound_ = null;

        // constructor
        // field the field to check for changed text
        UpperBoundKeyListener( NumberInput field, JLabel treeBound ) {
            field_ = field;
            treeBound_ = treeBound;
        }

        @Override
        public void keyPressed( KeyEvent evt ) {
            long upperBound =
                    calcTreeUpperBound( lookAheadField_.getIntValue(),
                                        searchOptions_.getPercentageBestMoves());
            treeBound_.setText( "" + Util.formatNumber(upperBound));
        }
    } */

}