/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameViewable;
import com.becker.game.twoplayer.common.ui.TwoPlayerNewGameDialog;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.ui.components.NumberInput;

import javax.swing.*;
import java.awt.event.ActionListener;

final class GoNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener {

    /** must not initialize to null */
    private NumberInput handicapField_;

    private static final String BLACK_IS = GameContext.getLabel("BLACK_IS");
    private static final String WHITE_IS = GameContext.getLabel("WHITE_IS");

    /** constructor */
    GoNewGameDialog( JFrame parent, GameViewable viewer ) {
        super( parent, viewer );
    }

    @Override
    protected JPanel createCustomBoardConfigPanel() {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );

        handicapField_ =
           new NumberInput(GameContext.getLabel("HANDICAP_LABEL"), ((GoBoard) board_).getHandicap());

        p.add( handicapField_ );
        assert ( handicapField_!=null );
        return p;
    }

    @Override
    protected String getPlayer1Label() {
        return BLACK_IS;
    }

    @Override
    protected String getPlayer2Label() {
        return WHITE_IS;
    }

    @Override
    protected void ok() {
        GoController gcontroller = (GoController) controller_;

        assert ( handicapField_!=null );
        gcontroller.setHandicap( handicapField_.getIntValue() );

        GameContext.log( 2, "GoOptionsDlg: the handicap is:" + handicapField_.getIntValue());
        super.ok();
    }

}

