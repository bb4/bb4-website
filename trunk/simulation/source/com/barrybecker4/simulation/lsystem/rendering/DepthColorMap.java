// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.rendering;

import com.barrybecker4.common.ColorMap;

import java.awt.Color;

/**
 * Default colormap for visualization.
 * May be edited in the UI.
 *
 * @author Barry Becker
 */
public class DepthColorMap extends ColorMap {

    private static final double MIN_VALUE = 0;
    private static final  double MAX_VALUE = 20.0;
    private static final  double RANGE = MAX_VALUE - MIN_VALUE;

    private static final double[] VALUES = {
          0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
    };


    private static final Color[] COLORS =  {
        new Color(255, 0, 0),
        new Color(255, 90, 0),
        new Color(255, 160, 0),
        new Color(255, 240, 0),
        new Color(230, 255, 0),
        new Color(180, 255, 10),
        new Color(110, 255, 0),
        new Color(0, 255, 0),
        new Color(0, 220, 60),
        new Color(0, 255, 120),
        new Color(0, 200, 255),
        new Color(0, 120, 255),
        new Color(10, 80, 255),
        new Color(10, 0, 255),
        new Color(100, 0, 255),
        new Color(160, 0, 255),
        new Color(255, 0, 255),
        new Color(240, 90, 255),
        new Color(240, 160, 250),
        new Color(255, 200, 230),
        new Color(255, 255, 230)
    };

    public DepthColorMap() {
        super(VALUES, COLORS);
    }
}