// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewable;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.online.ui.OnlineGameManagerPanel;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.NumberInput;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 * Panel to show params for a game board that has a rectilinear grid.
 *
 * @author Barry Becker
 */
public class GridBoardParamPanel extends JPanel {

    protected NumberInput rowSizeField_;
    protected NumberInput colSizeField_;

    /**
     *  constructor
     */
    public GridBoardParamPanel(int initialNumRows, int initialNumCols, JPanel customConfigPanel) {
        setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Board Configuration" ) );
        JLabel label = new JLabel( GameContext.getLabel("BOARD_SIZE") + ": " );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( label );

        rowSizeField_ = new NumberInput(GameContext.getLabel("NUMBER_OF_ROWS"), initialNumRows);
        colSizeField_ = new NumberInput( GameContext.getLabel("NUMBER_OF_COLS"), initialNumCols);

        rowSizeField_.setAlignmentX( Component.LEFT_ALIGNMENT );
        colSizeField_.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( rowSizeField_ );
        p.add( colSizeField_ );


        // add a custom section if desired (override createCustomBoardConfigPanel in derived class)

        if ( customConfigPanel != null )
            p.add( customConfigPanel );

        add(p, BorderLayout.CENTER);
        add(new JPanel(), BorderLayout.EAST);
    }


    public int getRowSize() {
        return rowSizeField_.getIntValue();
    }

    public int getColSize() {
        return colSizeField_.getIntValue();
    }
}