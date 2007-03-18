package com.becker.game.twoplayer.common.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.GameOptionsDialog;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class TwoPlayerOptionsDialog extends GameOptionsDialog
                                    implements ActionListener, ItemListener
{
    private JRadioButton[] strategyButtons_;  // search alg radio button group
    private SearchStrategyType algorithm_;
    private NumberInput lookAheadField_;
    private NumberInput bestPercentageField_;
    private JCheckBox alphabetaCheckbox_;
    private JCheckBox quiescenceCheckbox_;
    private JCheckBox gameTreeCheckbox_;
    private JCheckBox computerAnimationCheckbox_;


    // constructor
    public TwoPlayerOptionsDialog( JFrame parent, GameController controller )
    {
        super( parent, controller);
    }


    private TwoPlayerController get2PlayerController()
    {
        return (TwoPlayerController)controller_;
    }

    public GameOptions getOptions() {

        TwoPlayerOptions options = (TwoPlayerOptions) get2PlayerController().getOptions();

        options.setAlphaBeta(alphabetaCheckbox_.isSelected());
        options.setQuiescence( quiescenceCheckbox_.isSelected() );
        options.setLookAhead( lookAheadField_.getIntValue() );

        options.setSearchStrategyMethod(getSelectedStrategy());
        options.setPercentageBestMoves(bestPercentageField_.getIntValue() );
        options.setShowGameTree( gameTreeCheckbox_.isSelected() );
        options.setShowComputerAnimation( computerAnimationCheckbox_.isSelected() );
        return options;
    }

    /**
     * @return algorithm tab panel.
     */
    protected JPanel createControllerParamPanel()
    {
        TwoPlayerOptions options = get2PlayerController().getTwoPlayerOptions();

        JPanel p = new JPanel();
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

        ButtonGroup buttonGroup = new ButtonGroup();
        int numStrategies = SearchStrategyType.values().length;
        strategyButtons_ = new JRadioButton[numStrategies];
        algorithm_ = options.getSearchStrategyMethod();
        for (int i=0; i<numStrategies; i++) {
            SearchStrategyType alg = SearchStrategyType.values()[i];
            strategyButtons_[i] = new JRadioButton( alg.getLabel());
            p.add( createRadioButtonPanel( strategyButtons_[i], buttonGroup, algorithm_ == alg ) );
        }

        // look ahead
        JLabel treeUpperBound = new JLabel();
        lookAheadField_ =
                new NumberInput( GameContext.getLabel("MOVES_TO_LOOKAHEAD"), options.getLookAhead(),
                                 GameContext.getLabel("MOVES_TO_LOOKAHEAD_TIP"), 1, 16, true);
        lookAheadField_.addKeyListener( new UpperBoundKeyListener( lookAheadField_, treeUpperBound) );

        // best percentage moves
        bestPercentageField_ =
                new NumberInput( GameContext.getLabel("PERCENTAGE_AT_PLY"), options.getPercentageBestMoves(),
                                 GameContext.getLabel("PERCENTAGE_AT_PLY_TIP"), 0, 100, true);
        bestPercentageField_.addKeyListener( new UpperBoundKeyListener( bestPercentageField_, treeUpperBound) );

        JPanel p3 = new JPanel( new FlowLayout() );
        JLabel treeUpperBoundLabel = new JLabel( GameContext.getLabel("UPPER_BOUND") );

        treeUpperBound.setText(calcTreeUpperBound(options.getLookAhead(), options.getPercentageBestMoves() ) + "  ");
        p3.setAlignmentX( Component.LEFT_ALIGNMENT );
        p3.add( treeUpperBoundLabel );
        p3.add( treeUpperBound );

        p.add( lookAheadField_ );
        p.add( bestPercentageField_ );
        p.add( p3 );

        // alpha-beta pruning option
        alphabetaCheckbox_ = new JCheckBox( GameContext.getLabel("USE_PRUNING"), options.getAlphaBeta() );
        alphabetaCheckbox_.setToolTipText( GameContext.getLabel("USE_PRUNING_TIP") );
        alphabetaCheckbox_.addActionListener( this );
        p.add( alphabetaCheckbox_ );

        // show profile info option
        quiescenceCheckbox_ = new JCheckBox( GameContext.getLabel("USE_QUIESCENCE"), options.getQuiescence() );
        quiescenceCheckbox_.setToolTipText( GameContext.getLabel("USE_QUIESCENCE_TIP") );
        quiescenceCheckbox_.addActionListener( this );
        //quiescenceCheckbox_.setEnabled(false); // not currently implemented
        p.add( quiescenceCheckbox_ );

        p.setName(GameContext.getLabel("ALGORITHM"));

        return p;
    }

    /**
     * @return debug params tab panel
     */
    protected JPanel createDebugParamPanel()
    {
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

        // animation option
        computerAnimationCheckbox_ =
            new JCheckBox( GameContext.getLabel("SHOW_ANIMATION"), options.getShowComputerAnimation() );
        computerAnimationCheckbox_.setToolTipText( GameContext.getLabel("SHOW_ANIMATION_TIP") );
        computerAnimationCheckbox_.addActionListener( this );
        computerAnimationCheckbox_.setAlignmentX( Component.LEFT_ALIGNMENT );
        //computerAnimationCheckbox_.setEnabled(gameTreeCheckbox_.isSelected());
        p.add( computerAnimationCheckbox_ );

        return p;
    }



    /**
     * called when a button has been pressed
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        super.actionPerformed(e);

        if (source == gameTreeCheckbox_)
            computerAnimationCheckbox_.setEnabled(gameTreeCheckbox_.isSelected());
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

    /**
     * Invoked when a radio button has changed its selection state.
     */
    public void itemStateChanged( ItemEvent e )
    {
        super.itemStateChanged(e);
        algorithm_ = getSelectedStrategy();
    }

    private SearchStrategyType getSelectedStrategy() {
        int numStrategies = SearchStrategyType.values().length;
        for (int i=0; i<numStrategies; i++) {
            if (strategyButtons_[i].isSelected()) {
                return SearchStrategyType.values()[i];
            }
        }
        return SearchStrategyType.MINIMAX; // default
    }


    private class UpperBoundKeyListener extends KeyAdapter
    {
        NumberInput field_ = null;
        JLabel treeBound_ = null;

        // constructor
        // field the field to check for changed text
        UpperBoundKeyListener( NumberInput field, JLabel treeBound )
        {
            field_ = field;
            treeBound_ = treeBound;
        }

        public void keyPressed( KeyEvent evt )
        {
            treeBound_.setText( "" + calcTreeUpperBound( lookAheadField_.getIntValue(),  bestPercentageField_.getIntValue()) );
        }
    }

}