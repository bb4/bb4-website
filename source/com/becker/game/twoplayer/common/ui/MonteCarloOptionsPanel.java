package com.becker.game.twoplayer.common.ui;

import com.becker.common.util.Util;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Panel that shows the options for search strategies that use brute force (like minimax and derivatives).
 *
 * @author Barry Becker
 */
public class MonteCarloOptionsPanel extends JPanel {

    SearchOptions searchOptions_;

    private NumberInput maxSimulationsField_;

    /**
     * Constructor
     */
    public MonteCarloOptionsPanel(SearchOptions sOptions) {
        searchOptions_ = sOptions;
        initialize();
    }

    /**
     * @return brute search options
     */
    public MonteCarloSearchOptions updateMonteCarloOptionsOptions() {

        MonteCarloSearchOptions monteCarloOptions = searchOptions_.getMonteCarloSearchOptions();
        monteCarloOptions.setMaxSimulations(maxSimulationsField_.getIntValue());

        return monteCarloOptions;
    }

    /**
     * create the panel with ui element for setting the options
     */
    protected void initialize() {

        this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        
        MonteCarloSearchOptions monteCarloOptions = searchOptions_.getMonteCarloSearchOptions();

        maxSimulationsField_ =
            new NumberInput(GameContext.getLabel("MAX_NUM_SIMULATIONS"), monteCarloOptions.getMaxSimulations(),
                            GameContext.getLabel("MAX_NUM_SIMULATIONS_TIP"), 1, 16, true);
        this.add( maxSimulationsField_ );
    }

}