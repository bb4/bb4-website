package com.becker.game.twoplayer.go.ui;

import com.becker.game.twoplayer.common.*;
import com.becker.common.*;

import java.awt.*;

/**
 * Use to color the cells in the game tree by their values.
 * We will use this colormap for both the text tree and the graphical tree viewers
 * so they have consistent coloring.
 * Used to color the game tree rows, nodes, and arcs.
 *
 * @author Barry Becker Date: Oct 29, 2006
 */
public class GoTreeColorMap extends ColorMap {


        private static final double[] myValues_ = {
                                  -TwoPlayerController.WINNING_VALUE,
                                  -TwoPlayerController.WINNING_VALUE/2.0,
                                  -TwoPlayerController.WINNING_VALUE/10.0,
                                  -TwoPlayerController.WINNING_VALUE/40.0,
                                  -TwoPlayerController.WINNING_VALUE/100.0,
                                   0.0,
                                   TwoPlayerController.WINNING_VALUE/100.0,
                                   TwoPlayerController.WINNING_VALUE/40.0,
                                   TwoPlayerController.WINNING_VALUE/10.0,
                                   TwoPlayerController.WINNING_VALUE/2.0,
                                   TwoPlayerController.WINNING_VALUE};

        private static final Color[] myColors_ = {
                                  new Color(140, 0, 0),
                                  new Color(255, 10, 10),
                                  new Color(240, 200, 0),
                                  new Color(255, 255, 80),
                                  new Color(200, 200, 100),
                                  new Color(240, 240, 240, 120),
                                  new Color(100, 200, 200),
                                  new Color(70, 255, 200),
                                  new Color(0, 190, 255),
                                  new Color(10, 10, 255),
                                  new Color(0, 0, 140)
                                };

    /**
     * Create out tree cell colormap.
     */
    public GoTreeColorMap() {

        super(myValues_, myColors_);
    }
}
