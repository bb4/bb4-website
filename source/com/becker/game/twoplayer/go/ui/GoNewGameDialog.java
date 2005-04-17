package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.go.GoBoard;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.common.ui.TwoPlayerNewGameDialog;
import com.becker.ui.NumberInputPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

final class GoNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    // must not initialialize to null
    private JTextField handicapField_;

    private static final String BLACK_IS =GameContext.getLabel("BLACK_IS");
    private static final String WHITE_IS =GameContext.getLabel("WHITE_IS");

    // constructor
    GoNewGameDialog( JFrame parent, GameBoardViewer viewer )
    {
        super( parent, viewer );
    }

    public final String getTitle()
    {
        return GameContext.getLabel("GO_OPTIONS");
    }

    protected final JPanel createCustomBoardConfigPanel()
    {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        //p.setBorder(BorderFactory.createEtchedBorder());
        //JLabel label = new JLabel("GO SPECIFIC OPTIONS:");

        handicapField_ = new JTextField( Integer.toString( ((GoBoard) board_).getHandicap() ) );
        handicapField_.setMaximumSize( new Dimension( 30, ROW_HEIGHT ) );
        JPanel handicapOption =
                createHandicapPanel( GameContext.getLabel("HANDICAP_LABEL"), handicapField_ );

        p.add( handicapOption );
        assert ( handicapField_!=null );
        return p;
    }

    private static JPanel createHandicapPanel( String labelText, JTextField handicapField )
    {
        return new NumberInputPanel( labelText, handicapField );
    }

    protected final String getPlayer1Label()
    {
        return BLACK_IS;
    }

    protected final String getPlayer2Label()
    {
        return WHITE_IS;
    }

    protected final void ok()
    {
        GoController gcontroller = (GoController) controller_;

        assert ( handicapField_!=null );
        Integer handicap = new Integer( handicapField_.getText() );
        gcontroller.setHandicap( handicap.intValue() );

        GameContext.log( 2, "GoOptionsDlg: the handicap is:" + handicap );
        super.ok();
    }

}

