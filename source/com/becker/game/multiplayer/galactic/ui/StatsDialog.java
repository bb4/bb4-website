/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.GameController;
import com.becker.game.common.board.Board;
import com.becker.game.common.ui.viewer.GameBoardViewer;
import com.becker.ui.components.GradientButton;
import com.becker.ui.dialogs.AbstractDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Draw stats about the players and planet ownership in the Galaxy.
 *
 * @author Barry Becker
 */
final class StatsDialog extends AbstractDialog {

    private final JPanel mainPanel_ = new JPanel();

    private final GradientButton closeButton_ = new GradientButton();


    /**
     * constructor.
     * @param parent frame to display relative to
     * @param boardViewer
     */
    public StatsDialog(Component parent, GameBoardViewer boardViewer ) {
        super( parent );
        GameBoardViewer boardViewer_=boardViewer;
        GameController controller_=boardViewer.getController();
        Board board_=controller_.getBoard();
        showContent();
    }

    /**
     * ui initialization of the tree control.
     */
    @Override
    protected JComponent createDialogContent() {
        return new JPanel();
    }
}

