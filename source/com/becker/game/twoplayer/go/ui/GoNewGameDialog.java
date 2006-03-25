package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.ui.*;
import com.becker.game.twoplayer.go.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;

final class GoNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    // must not initialialize to null
    private NumberInput handicapField_;

    private static final String BLACK_IS =GameContext.getLabel("BLACK_IS");
    private static final String WHITE_IS =GameContext.getLabel("WHITE_IS");

    // constructor
    GoNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer );
    }

    public String getTitle()
    {
        return GameContext.getLabel("GO_OPTIONS");
    }

    protected JPanel createCustomBoardConfigPanel()
    {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );

        handicapField_ =
           new NumberInput(GameContext.getLabel("HANDICAP_LABEL"), ((GoBoard) board_).getHandicap() );

        p.add( handicapField_ );
        assert ( handicapField_!=null );
        return p;
    }

    protected String getPlayer1Label()
    {
        return BLACK_IS;
    }

    protected String getPlayer2Label()
    {
        return WHITE_IS;
    }

    protected void ok()
    {
        GoController gcontroller = (GoController) controller_;

        assert ( handicapField_!=null );
        gcontroller.setHandicap( handicapField_.getIntValue() );

        GameContext.log( 2, "GoOptionsDlg: the handicap is:" + handicapField_.getIntValue());
        super.ok();
    }

}

