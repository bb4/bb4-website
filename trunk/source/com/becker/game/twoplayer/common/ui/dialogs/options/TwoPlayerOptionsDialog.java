/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common.ui.dialogs.options;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameOptions;
import com.becker.game.common.ui.dialogs.GameOptionsDialog;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class TwoPlayerOptionsDialog extends GameOptionsDialog
                                    implements ActionListener, ItemListener {

    private JCheckBox gameTreeCheckbox_;

    /**
     * Constructor
     */
    public TwoPlayerOptionsDialog( JFrame parent, TwoPlayerController controller ) {
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

        TwoPlayerOptions options = getTwoPlayerOptions();

        options.setShowGameTree(gameTreeCheckbox_.isSelected() );
        return options;
    }
    
    private TwoPlayerOptions getTwoPlayerOptions() {
        return (TwoPlayerOptions)controller_.getOptions();
    }
    
    @Override
    protected JPanel createControllerParamPanel() {
        
        return new JPanel();
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
}