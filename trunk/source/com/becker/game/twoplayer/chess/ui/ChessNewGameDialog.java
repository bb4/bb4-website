package com.becker.game.twoplayer.chess.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerNewGameDialog;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 *  Any special options that are needed for Chess
 *
 *  @author Barry Becker
 */
public class ChessNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    // constructor
    public ChessNewGameDialog( JFrame parent, GameBoardViewer viewer )
    {
        super( parent, viewer );
    }

    public final String getTitle()
    {
        return GameContext.getLabel("CHESS_OPTIONS");
    }
}

