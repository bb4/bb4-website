package com.becker.game.twoplayer.common.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.GameOptionsDialog;
import com.becker.game.twoplayer.common.search.SearchStrategy;
import com.becker.game.twoplayer.common.TwoPlayerController;
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
public class TwoPlayerOptionsDialog extends GameOptionsDialog implements ActionListener, ItemListener
{

    // controller params/ algorithm
    protected final JTextField algorithmField_ = null;
    private JRadioButton minimaxButton_;  // alg radio button group
    private JRadioButton negamaxButton_;  // alg radio button group
    protected final GradientButton algorithmButton_ = null;
    private int algorithm_;
    private JTextField lookAheadField_;
    private JTextField bestPercentageField_;
    private JLabel treeUpperBound_ = null;
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


    /**
     * @return algorithm tab panel.
     */
    protected JPanel createControllerParamPanel()
    {
        TwoPlayerController c = get2PlayerController();

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
        minimaxButton_ = new JRadioButton( GameContext.getLabel("MINIMAX_SEARCH") );
        negamaxButton_ = new JRadioButton( GameContext.getLabel("NEGAMAX_SEARCH") );

        p.add( createRadioButtonPanel( minimaxButton_, buttonGroup, true ) );
        p.add( createRadioButtonPanel( negamaxButton_, buttonGroup, false ) );
        algorithm_ = c.getSearchStrategyMethod();
        switch (algorithm_) {
            case SearchStrategy.MINIMAX:
                minimaxButton_.setSelected( true );
                break;
            case SearchStrategy.NEGAMAX:
                negamaxButton_.setSelected( true );
                break;
            default :
                negamaxButton_.setSelected( true );
                break;
        }

        // look ahead
        treeUpperBound_ = new JLabel();

        lookAheadField_ = new JTextField( Integer.toString( c.getLookAhead() ) );
        lookAheadField_.setMaximumSize( new Dimension( 30, ROW_HEIGHT ) );
        JPanel p1 =
                new NumberInputPanel( GameContext.getLabel("MOVES_TO_LOOKAHEAD"), lookAheadField_,
                                      GameContext.getLabel("MOVES_TO_LOOKAHEAD_TIP"));
        lookAheadField_.addKeyListener( new UpperBoundKeyListener( lookAheadField_, treeUpperBound_) );

        // best percentage moves
        bestPercentageField_ = new JTextField( Integer.toString( c.getPercentageBestMoves() ) );
        bestPercentageField_.setMaximumSize( new Dimension( 30, ROW_HEIGHT ) );
        JPanel p2 =
                new NumberInputPanel( GameContext.getLabel("PERCENTAGE_AT_PLY"), bestPercentageField_,
                                      GameContext.getLabel("PERCENTAGE_AT_PLY_TIP"));
        bestPercentageField_.addKeyListener( new UpperBoundKeyListener( bestPercentageField_, treeUpperBound_) );

        JPanel p3 = new JPanel( new FlowLayout() );
        JLabel treeUpperBoundLabel = new JLabel( GameContext.getLabel("UPPER_BOUND") );

        treeUpperBound_.setText( calcTreeUpperBound( c.getLookAhead(), c.getPercentageBestMoves() ) + "  " );
        p3.setAlignmentX( Component.LEFT_ALIGNMENT );
        p3.add( treeUpperBoundLabel );
        p3.add( treeUpperBound_ );

        p.add( p1 );
        p.add( p2 );
        p.add( p3 );

        // alpha-beta pruning option
        alphabetaCheckbox_ = new JCheckBox( GameContext.getLabel("USE_PRUNING"), c.getAlphaBeta() );
        alphabetaCheckbox_.setToolTipText( GameContext.getLabel("USE_PRUNING_TIP") );
        alphabetaCheckbox_.addActionListener( this );
        p.add( alphabetaCheckbox_ );

        // show profile info option
        quiescenceCheckbox_ = new JCheckBox( GameContext.getLabel("USE_QUIESCENCE"), c.getQuiescence() );
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
        gameTreeCheckbox_ = new JCheckBox(GameContext.getLabel("SHOW_GAME_TREE"),
                get2PlayerController().getShowGameTree());
        gameTreeCheckbox_.setToolTipText( GameContext.getLabel("SHOW_GAME_TREE_TIP") );
        gameTreeCheckbox_.addActionListener( this );
        gameTreeCheckbox_.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( gameTreeCheckbox_ );

        // animation option
        computerAnimationCheckbox_ =
            new JCheckBox( GameContext.getLabel("SHOW_ANIMATION"),
                get2PlayerController().getShowComputerAnimation() );
        computerAnimationCheckbox_.setToolTipText( GameContext.getLabel("SHOW_ANIMATION_TIP") );
        computerAnimationCheckbox_.addActionListener( this );
        computerAnimationCheckbox_.setAlignmentX( Component.LEFT_ALIGNMENT );
        //computerAnimationCheckbox_.setEnabled(gameTreeCheckbox_.isSelected());
        p.add( computerAnimationCheckbox_ );

        return p;
    }


    protected void ok()
    {
        TwoPlayerController c = get2PlayerController();

        c.setAlphaBeta( alphabetaCheckbox_.isSelected() );
        c.setQuiescence( quiescenceCheckbox_.isSelected() );

        Integer level = new Integer( lookAheadField_.getText() );
        c.setLookAhead( level.intValue() );

        if ( minimaxButton_.isSelected() )
            c.setSearchStrategyMethod( SearchStrategy.MINIMAX );
        else if ( negamaxButton_.isSelected() )
            c.setSearchStrategyMethod( SearchStrategy.NEGAMAX );
        Integer best = new Integer( bestPercentageField_.getText() );
        c.setPercentageBestMoves( best.intValue() );
        c.setShowGameTree( gameTreeCheckbox_.isSelected() );
        c.setShowComputerAnimation( computerAnimationCheckbox_.isSelected() );

        super.ok();
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
        if ( minimaxButton_ != null && minimaxButton_.isSelected() )
            algorithm_ = SearchStrategy.MINIMAX;
        else if ( negamaxButton_ != null && negamaxButton_.isSelected() )
            algorithm_ = SearchStrategy.NEGAMAX;
    }


    class UpperBoundKeyListener extends KeyAdapter
    {
        JTextField field_ = null;
        JLabel treeBound_ = null;

        // constructor
        // field the field to check for changed text
        public UpperBoundKeyListener( JTextField field, JLabel treeBound )
        {
            field_ = field;
            treeBound_ = treeBound;
        }

        public void keyPressed( KeyEvent evt )
        {
            if ( field_.getText().length() > 0 ) {
                Integer level = new Integer( lookAheadField_.getText() );
                Integer best = new Integer( bestPercentageField_.getText() );
                treeBound_.setText( "" + calcTreeUpperBound( level.intValue(), best.intValue() ) );
            }
        }
    }

}