package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.GameController;
import com.becker.game.common.board.Board;
import com.becker.game.common.ui.viewer.GameBoardViewer;
import com.becker.ui.components.GradientButton;
import com.becker.ui.dialogs.AbstractDialog;

import javax.swing.*;



/**
 * Show summary information about all planets (or maybe just the ones that you own).
 *
 * @author Barry Becker
 */
final class PlanetDetailsDialog extends AbstractDialog
{

    private final JPanel mainPanel_ = new JPanel();

    private final GradientButton closeButton_ = new GradientButton();
    private final JLabel infoLabel_ = new JLabel();


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param boardViewer
     */
    public PlanetDetailsDialog( JFrame parent, GameBoardViewer boardViewer )
    {
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
    protected JComponent createDialogContent()
    {   
        return new JPanel();
    }

}

