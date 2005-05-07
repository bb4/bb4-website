package com.becker.game.common.ui;

import com.becker.ui.TexturedToolBar;
import com.becker.ui.GradientButton;
import com.becker.ui.GUIUtil;
import com.becker.game.common.GameContext;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.*;

/**
 *
 * @author Barry Becker
 */
public class GameToolBar extends TexturedToolBar {

    protected static final String CORE_IMAGE_PATH = GameContext.GAME_ROOT+"common/ui/images/";

    private GradientButton newGameButton_;
    private GradientButton undoButton_;
    private GradientButton redoButton_;
    private GradientButton optionsButton_;
    //protected GradientButton resignButton_;
    private GradientButton helpButton_;

    protected static final Dimension MAX_BUTTON_SIZE = new Dimension( 100, 24 );

    private String dir = CORE_IMAGE_PATH;
    private ImageIcon newGameImage = GUIUtil.getIcon(dir+"newGame.gif");
    private ImageIcon helpImage = GUIUtil.getIcon(dir+"help.gif");
    private ImageIcon undoImage = GUIUtil.getIcon(dir+"undo_on.gif");
    private ImageIcon redoImage = GUIUtil.getIcon(dir+"redo_on.gif");
    private ImageIcon undoImageDisabled = GUIUtil.getIcon(dir+"undo_off.gif");
    private ImageIcon redoImageDisabled = GUIUtil.getIcon(dir+"redo_off.gif");
    private ImageIcon optionsImage = GUIUtil.getIcon(dir+"iconDesktop.gif");

    // the thing that processes the toolbar button presses.
    private ActionListener listener_;

    public GameToolBar(ImageIcon texture, ActionListener listener) {
        super(texture);
        listener_ = listener;
        init();

    }

    private void init() {
        newGameButton_ = createToolBarButton( GameContext.getLabel("NEW_GAME_BTN"),
                                                      GameContext.getLabel("NEW_GAME_BTN_TIP"),
                                                      newGameImage );
        undoButton_ = createToolBarButton( "", GameContext.getLabel("UNDO_BTN_TIP"), undoImage );
        undoButton_.setDisabledIcon(undoImageDisabled);
        undoButton_.setEnabled(false);    // nothing to undo initially
        redoButton_ = createToolBarButton( "", GameContext.getLabel("REDO_BTN_TIP"), redoImage );
        redoButton_.setDisabledIcon(redoImageDisabled);
        redoButton_.setEnabled(false);    // nothing to redo initially
        optionsButton_ = createToolBarButton( GameContext.getLabel("OPTIONS_BTN"),
                                              GameContext.getLabel("OPTIONS_BTN_TIP"), optionsImage );
        helpButton_ = createToolBarButton( GameContext.getLabel("HELP_BTN"),
                                           GameContext.getLabel("HELP_BTN_TIP"), helpImage );


        add( newGameButton_ );
        add( undoButton_ );
        add( redoButton_ );
        addCustomToolBarButtons();
        add( optionsButton_ );
        add( Box.createHorizontalGlue() );
        add( helpButton_ );
    }

    /**
     * create a toolbar button.
     */
    public final GradientButton createToolBarButton( String text, String tooltip, Icon icon )
    {
        GradientButton button = new GradientButton( text, icon );
        button.addActionListener( listener_ );
        button.setToolTipText( tooltip );
        button.setMaximumSize( MAX_BUTTON_SIZE );
        return button;
    }

    /**
      * override to add your own game dependent buttons to the toolbar.
      */
    protected void addCustomToolBarButtons()
    {}

    public JButton getNewGameButton() { return newGameButton_; }
    public JButton getUndoButton() { return undoButton_; }
    public JButton getRedoButton() { return redoButton_; }
    public JButton getOptionsButton() { return optionsButton_; }
    //public JButton getResignButton() { return resignButton_; }
    public JButton getHelpButton() { return helpButton_; }

}
