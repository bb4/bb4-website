// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui.rendering;

import com.becker.puzzle.tantrix.model.HexTile;
import com.becker.puzzle.tantrix.model.PathColor;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

/**
 * Constants and static methods for working with hexagons
 *
 * @author Barry Becker
 */
public class HexUtil {

    private static final double DEG_TO_RAD =  Math.PI / 180.0;
    public static final double ROOT3 = Math.sqrt(3.0);
    public static final double ROOT3D2 = ROOT3/2.0;

    /**
     * Create an instance
     */
    private HexUtil() {}


    public static double rad(double angleInDegrees) {
        return angleInDegrees * DEG_TO_RAD;
    }
}
