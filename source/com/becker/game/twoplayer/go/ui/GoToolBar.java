package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.ui.GameToolBar;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * @author Barry Becker
 */
class GoToolBar extends GameToolBar {

    // go needs an extra button for passing
    // do not initiallize to null or it will not work because of the way initialization happens
    private GradientButton passButton_;

    GoToolBar(ImageIcon texture, ActionListener listener) {
        super(texture, listener);
    }


     /**
     * add the button for passing.
     */
    @Override
    protected final void addCustomToolBarButtons()
    {
        passButton_ = createToolBarButton( GameContext.getLabel("PASS_BTN"),
                                           GameContext.getLabel("PASS_BTN_TIP"),
                                           null/*passImage_*/ );
        add( passButton_ );
    }

    public JButton getPassButton() { return passButton_; }

}
