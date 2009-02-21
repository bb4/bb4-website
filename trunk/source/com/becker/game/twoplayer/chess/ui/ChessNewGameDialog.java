package com.becker.game.twoplayer.chess.ui;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.ui.*;

import javax.swing.*;
import java.awt.event.*;

/**
 *  Any special options that are needed for Chess
 *
 *  @author Barry Becker
 */
public class ChessNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    // constructor
    public ChessNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer );
    }

    public final String getTitle()
    {
        return GameContext.getLabel("CHESS_OPTIONS");
    }
}
