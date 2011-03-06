package com.becker.game.twoplayer.common.ui.options;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.GameOptions;
import com.becker.game.common.ui.dialogs.GameOptionsDialog;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.SearchAttribute;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class TwoPlayerOptionsDialog extends GameOptionsDialog
                                    implements ActionListener, ItemListener {

    /** search alg radio button group */
    private JRadioButton[] strategyButtons_;
    private SearchStrategyType algorithm_;
    private JCheckBox gameTreeCheckbox_;

    
    private BruteSearchOptionsPanel bruteOptionsPanel_;
    private MonteCarloOptionsPanel monteCarloOptionsPanel_;
    private BestMovesOptionsPanel bestMovesOptionsPanel_;

    /**
     * Constructor
     */
    public TwoPlayerOptionsDialog( JFrame parent, GameController controller ) {
        super( parent, controller);
    }

    /**
     * @return controller
     */
    private TwoPlayerController get2PlayerController() {
        return (TwoPlayerController) controller_;
    }

    @Override
    public GameOptions getOptions() {

        TwoPlayerOptions options = (TwoPlayerOptions) get2PlayerController().getOptions();
        SearchOptions searchOptions = getSearchOptions();

        bruteOptionsPanel_.updateBruteOptionsOptions();
        monteCarloOptionsPanel_.updateMonteCarloOptionsOptions();
        bestMovesOptionsPanel_.updateBestMovesOptions();

        searchOptions.setSearchStrategyMethod(getSelectedStrategy(searchOptions.getSearchStrategyMethod()));
        options.setShowGameTree(gameTreeCheckbox_.isSelected() );
        return options;
    }

    /**
     * @return algorithm tab panel.
     */
    @Override
    protected JPanel createControllerParamPanel() {

        SearchOptions searchOptions = getSearchOptions();

        JPanel p = new JPanel();
        p.setName(GameContext.getLabel("ALGORITHM"));

        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createTitledBorder(
                       BorderFactory.createEtchedBorder(),
                         GameContext.getLabel("PERFORMANCE_OPTIONS")) );

        JLabel label = new JLabel( "     " );  // initial space
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( label );

        // radio buttons for which search algorithm to use
        JLabel algorithmLabel = new JLabel( GameContext.getLabel("SELECT_SEARCH_ALGORITHM") );
        algorithmLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( algorithmLabel );

        p.add(createStrategyRadioButtons());

        bruteOptionsPanel_ = new BruteSearchOptionsPanel(searchOptions.getBruteSearchOptions());
        monteCarloOptionsPanel_ = new MonteCarloOptionsPanel(searchOptions.getMonteCarloSearchOptions());
        bestMovesOptionsPanel_ = new BestMovesOptionsPanel(searchOptions.getBestMovesSearchOptions());


        p.add( bruteOptionsPanel_ );
        p.add( monteCarloOptionsPanel_ );
        p.add(bestMovesOptionsPanel_);
        showOptionsBasedOnAlgorithm();
        return p;
    }

    /**
     * @return Radio buttons for selecting the strategy.
     */
    private JPanel createStrategyRadioButtons() {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );

        SearchOptions searchOptions = getSearchOptions();

        ButtonGroup buttonGroup = new ButtonGroup();
        int numStrategies = SearchStrategyType.values().length;
        strategyButtons_ = new JRadioButton[numStrategies];

        algorithm_ = searchOptions.getSearchStrategyMethod();
        for (int i=0; i<numStrategies; i++) {
            SearchStrategyType alg = SearchStrategyType.values()[i];
            strategyButtons_[i] = new JRadioButton(alg.getLabel());
            strategyButtons_[i].setToolTipText(alg.getTooltip());
            p.add( createRadioButtonPanel( strategyButtons_[i], buttonGroup, algorithm_ == alg ));
        }
        return p;
    }

    private SearchOptions getSearchOptions() {
        TwoPlayerOptions options = (TwoPlayerOptions) get2PlayerController().getOptions();
        return  options.getSearchOptions();
    }

    /**
     * @return debug params tab panel
     */
    @Override
    protected JPanel createDebugParamPanel() {
        JPanel outerContainer = new JPanel(new BorderLayout());
        JPanel p = createDebugOptionsPanel();

        addDebugLevel(p);
        addLoggerSection(p);
        addProfileCheckBox(p);

        // show game tree option
        TwoPlayerOptions options = get2PlayerController().getTwoPlayerOptions();
        gameTreeCheckbox_ = new JCheckBox(GameContext.getLabel("SHOW_GAME_TREE"), options.getShowGameTree());
        gameTreeCheckbox_.setToolTipText( GameContext.getLabel("SHOW_GAME_TREE_TIP") );
        gameTreeCheckbox_.addActionListener( this );
        gameTreeCheckbox_.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( gameTreeCheckbox_ );

        outerContainer.add(p, BorderLayout.NORTH);

        return outerContainer;
    }

    /**
     * Invoked when a radio button has changed its selection state.
     */
    @Override
    public void itemStateChanged( ItemEvent e ) {
        super.itemStateChanged(e);
        algorithm_ = getSelectedStrategy(getSearchOptions().getSearchStrategyMethod());
        showOptionsBasedOnAlgorithm();
    }

    private void showOptionsBasedOnAlgorithm() {

        boolean bruteForceStrategy = algorithm_.hasAttribute(SearchAttribute.BRUTE_FORCE);
        bruteOptionsPanel_.setVisible(bruteForceStrategy);
        monteCarloOptionsPanel_.setVisible(!bruteForceStrategy);
    }

    private SearchStrategyType getSelectedStrategy(SearchStrategyType defaultStrategy) {
        int numStrategies = SearchStrategyType.values().length;
        for (int i=0; i<numStrategies; i++) {
            if (strategyButtons_[i].isSelected()) {
                return SearchStrategyType.values()[i];
            }
        }
        return defaultStrategy;
    }
}