package com.becker.puzzle.set;

import com.becker.game.common.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * Set game toolbar
 *
 * @author Barry Becker
 */
public class SetToolBar extends TexturedToolBar {

    protected static final String CORE_IMAGE_PATH = GameContext.GAME_ROOT + "common/ui/images/";
    private static final long serialVersionUID = 0L;

    private GradientButton newGameButton_;

    private GradientButton addButton_;
    private GradientButton solveButton_;
    private GradientButton helpButton_;


    private static final String DIR = CORE_IMAGE_PATH;
    private static final ImageIcon newGameImage = GUIUtil.getIcon(DIR+"newGame.gif");
    private static final ImageIcon addImage = GUIUtil.getIcon(DIR+"plus.gif");
    private static final ImageIcon solveImage = GUIUtil.getIcon(DIR+"greenFlag.gif");
    private static final ImageIcon helpImage = GUIUtil.getIcon(DIR+"help.gif");



    public SetToolBar(ImageIcon texture, ActionListener listener) {
        super(texture, listener);
        init();
    }

    private void init() {
        newGameButton_ = createToolBarButton( GameContext.getLabel("NEW_GAME_BTN"),
                                                      GameContext.getLabel("NEW_GAME_BTN_TIP"),
                                                      newGameImage );

        addButton_ = createToolBarButton( "Add Card", "Add another card to those shown", addImage );
        solveButton_ = createToolBarButton( "Show Sets",
                                            "Have the computer determine all the sets present and show them in a separate window.", solveImage );
        helpButton_ = createToolBarButton( GameContext.getLabel("HELP_BTN"),
                                           GameContext.getLabel("HELP_BTN_TIP"), helpImage );

        add( newGameButton_ );
        add( addButton_ );
        add( solveButton_ );
        add( javax.swing.Box.createHorizontalGlue() );
        add( helpButton_ );
    }


    public JButton getNewGameButton() { return newGameButton_; }
    public JButton getUndoButton() { return addButton_; }
    public JButton getRedoButton() { return solveButton_; }
    public JButton getHelpButton() { return helpButton_; }

}
