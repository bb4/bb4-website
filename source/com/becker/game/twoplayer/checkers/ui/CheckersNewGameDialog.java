package com.becker.game.twoplayer.checkers.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerNewGameDialog;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 *  Any special options that are needed for Checkers
 *
 *  @author Barry Becker
 */
public class CheckersNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    // constructor
    public CheckersNewGameDialog( JFrame parent, GameBoardViewer viewer )
    {
        super( parent, viewer );
    }

    public final String getTitle()
    {
        return GameContext.getLabel("CHECKERS_OPTIONS");
    }

}

