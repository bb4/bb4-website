package com.becker.game.multiplayer.galactic.ui;

import com.becker.common.ColorMap;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.ui.GUIUtil;
import com.becker.ui.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Draw the entire game tree using a java tree control.
 *
 * @author Barry Becker
 */
final class StatsDialog extends JDialog

{
    /**
     * the options get set directly on the game controller that is passed in.
     */
    private GameController controller_;

    private final JPanel mainPanel_ = new JPanel();
    private final JPanel previewPanel_ = new JPanel();
    private JScrollPane scrollPane_ = null;

    private final GradientButton closeButton_ = new GradientButton();

    private final JLabel infoLabel_ = new JLabel();

    private static final int ROW_HEIGHT = 11;
    private static final int TREE_WIDTH = 420;

    private Board board_ = null;

    // the viewer in the debug window
    private GameBoardViewer boardViewer_ = null;

    // the controller that is actually being played
    private GameControllerInterface mainController_ = null;

    private ColorMap colormap_ = null;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param boardViewer
     */
    public StatsDialog( JFrame parent, GameBoardViewer boardViewer )
    {
        super( parent );
        boardViewer_ = boardViewer;
        controller_ = boardViewer.getController();
        board_ = controller_.getBoard();

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError oom) {
            GameContext.log( 0, "we ran out of memory!" );
            GameContext.log( 0, GUIUtil.getStackTrace( oom ) );
        }
        pack();
    }


    /**
     * ui initialization of the tree control.
     */
    private void initUI()
    {}



    /**
     * called when the ok button is clicked.
     */
    private void close()
    {
        this.setVisible(false);
    }

}

