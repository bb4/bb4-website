package com.becker.game.twoplayer.common.ui.options;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.becker.ui.components.NumberInput;

import javax.swing.*;

/**
 * Panel that shows the options for search strategies that use monte carlo methods (like UCT derivatives).
 *
 * @author Barry Becker
 */
public class BestMovesOptionsPanel extends JPanel {

    BestMovesSearchOptions bestMovesOptions_;

    private NumberInput percentLessThanBestThreshField_;
    private NumberInput bestPercentageField_;
    private NumberInput minBestMovesField_;


    /**
     * Constructor
     */
    public BestMovesOptionsPanel(BestMovesSearchOptions sOptions) {
        bestMovesOptions_ = sOptions;
        initialize();
    }

    /**
     * @return brute search options
     */
    public void updateBestMovesOptions() {

        bestMovesOptions_.setPercentLessThanBestThresh(percentLessThanBestThreshField_.getIntValue());
        bestMovesOptions_.setPercentageBestMoves(bestPercentageField_.getIntValue());
        bestMovesOptions_.setMinBestMoves(minBestMovesField_.getIntValue());
    }

    /**
     * create the panel with ui element for setting the options
     */
    protected void initialize() {

        this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        percentLessThanBestThreshField_ =
            new NumberInput(GameContext.getLabel("PERCENT_LESS_THAN_BEST_THRESH"), bestMovesOptions_.getPercentLessThanBestThresh(),
                            GameContext.getLabel("PERCENT_LESS_THAN_BEST_THRESH_TIP"), 0, 100, true);

        bestPercentageField_ =
                new NumberInput( GameContext.getLabel("PERCENTAGE_AT_PLY"), bestMovesOptions_.getPercentageBestMoves(),
                                 GameContext.getLabel("PERCENTAGE_AT_PLY_TIP"), 0, 100, true);
        minBestMovesField_ =
                new NumberInput( GameContext.getLabel("MIN_BEST_MOVES"), bestMovesOptions_.getMinBestMoves(),
                                 GameContext.getLabel("MIN_BEST_MOVES_TIP"), 1, 100, true);

        add( percentLessThanBestThreshField_ );
        add( bestPercentageField_ );
        add( minBestMovesField_ );
    }

}