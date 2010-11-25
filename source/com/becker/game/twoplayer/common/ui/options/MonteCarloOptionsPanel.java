package com.becker.game.twoplayer.common.ui.options;

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
 * Panel that shows the options for search strategies that use monte carlo methods (like UCT derivatives).
 *
 * @author Barry Becker
 */
public class MonteCarloOptionsPanel extends JPanel {

    SearchOptions searchOptions_;

    private NumberInput maxSimulationsField_;

    private NumberInput exploreExploitRatioField_;

    /** It would be unreasonable to run more than this many simulations. */
    private static final int ABS_MAX_NUM_SIMULATIONS = 100000000;

    /** It would be unreasonable to have a exploreExploit ration more than this. */
    private static final double ABS_MAX_EE_RATIO = 100;

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
        monteCarloOptions.setExploreExploitRatio(exploreExploitRatioField_.getValue());
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
                            GameContext.getLabel("MAX_NUM_SIMULATIONS_TIP"), 1, ABS_MAX_NUM_SIMULATIONS, true);
        exploreExploitRatioField_ =
            new NumberInput(GameContext.getLabel("EXPLORE_EXPLOIT_RATIO"), monteCarloOptions.getExploreExploitRatio(),
                            GameContext.getLabel("EXPLORE_EXPLOIT_RATIO_TIP"), 0, ABS_MAX_EE_RATIO, false);

        add( maxSimulationsField_ );
        add( exploreExploitRatioField_ );
    }

}