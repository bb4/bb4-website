package com.becker.game.twoplayer.go.ui.gametree;

import com.becker.common.ColorMap;

import java.awt.*;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Use to color the cells in the game tree by their values.
 * We will use this colormap for both the text tree and the graphical tree viewers
 * so they have consistent coloring.
 * Used to color the game tree rows, nodes, and arcs.
 *
 * @author Barry Becker
 */
class GoTreeColorMap extends ColorMap {

        private static final double[] myValues_ = {
                                  -WINNING_VALUE,
                                  -WINNING_VALUE/2.0,
                                  -WINNING_VALUE/10.0,
                                  -WINNING_VALUE/100.0,
                                   0.0,
                                   WINNING_VALUE/100.0,
                                   WINNING_VALUE/10.0,
                                   WINNING_VALUE/2.0,
                                   WINNING_VALUE};

        private static final Color[] myColors_ = {
                                  new Color(180, 0, 20),
                                  new Color(255, 0, 0),
                                  new Color(255, 190, 0),
                                  new Color(255, 255, 0),
                                  new Color(255, 255, 255),    // new Color(240, 240, 240, 120)
                                  new Color(0, 255, 100),
                                  new Color(0, 200, 255),
                                  new Color(0, 0, 255),
                                  new Color(0, 1, 180)
                                };

    /**
     * Create out tree cell colormap.
     */
    public GoTreeColorMap() {

        super(myValues_, myColors_);
    }
}
