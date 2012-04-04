// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui.rendering;

import com.becker.puzzle.tantrix.model.PathColor;

import java.awt.*;

/**
 * Renders a single tantrix tile.
 *
 * @author Barry Becker
 */
public class PathColorInterpreter {

    private static final Color BLUE_COLOR = new Color(10, 40, 250);
    private static final Color RED_COLOR = new Color(220, 20, 30);
    private static final Color GREEN_COLOR = new Color(10, 250, 20);
    private static final Color YELLOW_COLOR = new Color(240, 240, 10);
    private static final Color WHITE_COLOR = new Color(251, 250, 255);

    public static Color getColorForPathColor(PathColor pathColor) {

        Color color = Color.GRAY;
        switch (pathColor) {
            case BLUE: color = BLUE_COLOR; break;
            case RED : color = RED_COLOR;  break;
            case GREEN : color = GREEN_COLOR; break;
            case YELLOW : color = YELLOW_COLOR; break;
            case WHITE : color = WHITE_COLOR; break;
        }
        return color; // never returns
    }

    /** hidden constructor */
    private PathColorInterpreter() {}
}
