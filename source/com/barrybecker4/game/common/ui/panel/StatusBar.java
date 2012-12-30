// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewable;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.common.ui.dialogs.HelpDialog;
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.ui.components.ResizableAppletPanel;
import com.barrybecker4.ui.components.TexturedPanel;
import com.barrybecker4.ui.dialogs.OutputWindow;
import com.barrybecker4.ui.util.GUIUtil;
import com.barrybecker4.ui.util.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Shows game status.
 *
 * @author Barry Becker
 */
public class StatusBar extends TexturedPanel {

    /** font for the undo/redo buttons    */
    private static final Font STATUS_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 10 );

    /**
     * Construct the panel.
     */
    public StatusBar(ImageIcon texture) {
        super(texture);

        JLabel statusBarLabel = new JLabel();
        statusBarLabel.setFont(STATUS_FONT);
        statusBarLabel.setOpaque(false);
        statusBarLabel.setText( GameContext.getLabel("STATUS_MSG"));

        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(1000, 16));
        add(statusBarLabel, BorderLayout.WEST);
    }

}
