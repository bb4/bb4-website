package com.becker.game.twoplayer.pente.ui;

import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.common.GameContext;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerNewGameDialog;

import javax.swing.*;
import java.awt.event.ActionListener;

public class PenteNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    public PenteNewGameDialog( JFrame parent, GameBoardViewer viewer )
    {
        super( parent, viewer );
    }

    public final String getTitle()
    {
        return GameContext.getLabel("PENTE_OPTIONS");
    }

}

