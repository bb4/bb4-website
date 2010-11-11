package com.becker.game.twoplayer.common.ui.gametree;

import com.becker.common.ColorMap;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.becker.ui.legend.ContinuousColorLegend;

import javax.swing.*;
import java.awt.*;

/**
 * Contains the move details and color legend underneath
 *
 * @author Barry Becker
 */
public final class MoveInfoPanel extends JPanel {


    MoveDetailsPanel moveDetails_;
    
    /**
     * Constructor
     */
    public MoveInfoPanel(ColorMap colormap) {

        moveDetails_ = new MoveDetailsPanel();

        ContinuousColorLegend colorLegend =
                new ContinuousColorLegend("Relative Score for Player", colormap, true);

        this.setLayout(new BorderLayout());
        this.add(moveDetails_, BorderLayout.CENTER);
        this.add(colorLegend, BorderLayout.SOUTH);
    }


    public void setText(AbstractTwoPlayerBoardViewer viewer, TwoPlayerMove m, SearchTreeNode lastNode) {
        moveDetails_.setText(viewer, m, lastNode);
    }
}

