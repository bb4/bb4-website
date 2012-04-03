// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui;

import com.becker.common.format.FormatUtil;
import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.model.HexTile;
import com.becker.puzzle.tantrix.model.PathColor;
import com.becker.ui.util.GUIUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import static com.becker.puzzle.tantrix.ui.TantrixBoardRenderer.MARGIN;

/**
 * Renders a single tantrix tile.
 *
 * @author Barry Becker
 */
public class HexTileRenderer {

    private static final Font TILE_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 9 );

    private static final Stroke TILE_STROKE = new BasicStroke(1);
    private static final Color TILE_BORDER_COLOR = new Color(70, 70, 70);
    private static final Color PATH_BORDER_COLOR = new Color(10, 10, 10);
    private static final Color TILE_BG_COLOR = new Color(200, 200, 200);
    private static final double DEG_TO_RAD =  Math.PI / 180.0;

    private static final float PATH_BG_FRAC = 0.29f;
    private static final float PATH_FRAC = 0.25f;
    private static final double ROOT3 = Math.sqrt(3.0);
    private static final double ROOT3D2 = ROOT3/2.0;

    /**
     * Create an instance
     */
    public HexTileRenderer() {}

    /**
     * Draw the poker hand (the cards are all face up or all face down)
     */
    public void render(Graphics2D g2, Location location, HexTile tile, double radius) {

        boolean isOddRow = location.getRow() % 2 == 1;
        double x = MARGIN + ((location.getCol() - (isOddRow ? -0.75 : -0.25)) * 2 * radius * ROOT3D2);
        double y = MARGIN + ((location.getRow() + 1.6) * 3.0 * radius / 2.0);
        Point point = new Point((int)x, (int)y);
        point.setLocation(x, y);
        drawHexagon(g2, point, radius);
        drawPath(g2, 0, tile, point, radius);
        drawPath(g2, 1, tile, point, radius);
        drawPath(g2, 2, tile, point, radius);

        g2.setColor(Color.BLACK);
        g2.setFont(TILE_FONT);
        g2.drawString(FormatUtil.formatNumber(tile.getTantrixNumber()),
                      (int)(x + radius/2), (int)(y + radius/2));
    }

    private void drawHexagon(Graphics2D g2, Point point, double radius) {

        int numPoints = 7;
        int[] xpoints = new int[numPoints];
        int[] ypoints = new int[numPoints];

        for (int i = 0; i <= 6; i++) {
            double angStart = rad(30 + 60 * i);
            xpoints[i] = (int)(point.getX() + radius * Math.cos(angStart));
            ypoints[i] = (int)(point.getY() + radius * Math.sin(angStart));
        }

        Polygon poly = new Polygon(xpoints, ypoints, numPoints);
        g2.setColor(TILE_BG_COLOR);
        g2.fillPolygon(poly);
        g2.setColor(TILE_BORDER_COLOR);
        g2.setStroke(TILE_STROKE);
        g2.drawPolygon(poly);
    }

    private void drawPath(Graphics2D g2, int pathIndex, HexTile tile, Point position, double size) {
        Set<PathColor> set = new HashSet<PathColor>();
        int i = 0;
        do {
            PathColor c = tile.getEdgeColor(i++);
            set.add(c);
        } while (set.size() <= pathIndex);

        int firstPathIndex = i-1;
        PathColor pathColor = tile.getEdgeColor(pathIndex);

        while (pathColor != tile.getEdgeColor(i)) {
            System.out.println("i="+ i + " pathColor="+pathColor+ " curcol=" + tile.getEdgeColor(i));
            i++;
            assert(i<6): "Should never exceed 6";
        }

        int secondPathIndex = i;
        int diff = secondPathIndex - firstPathIndex;
        Color color =
                PathColorInterpreter.getColorForPathColor(tile.getEdgeColor(firstPathIndex));
        // account for the rotation.
        System.out.println("diff="+ diff + " rot=" + tile.getRotation().ordinal()
                + " pinf=" + firstPathIndex + ", " + secondPathIndex
                + " color=" + tile.getEdgeColor(firstPathIndex));
        firstPathIndex += tile.getRotation().ordinal();

        switch (diff) {
            case 1: drawTightCurvedPath(g2, position, firstPathIndex, color, size); break;
            case 5: drawTightCurvedPath(g2, position, secondPathIndex, color, size); break;
            case 2: drawCurvedPath(g2, position, firstPathIndex, color, size); break;
            case 4: drawCurvedPath(g2, position, secondPathIndex, color, size); break;
            case 3: drawStraightPath(g2, position, firstPathIndex, color, size); break;
        }
    }

    private void drawTightCurvedPath(Graphics2D g2, Point position, int firstIndex,
                                     Color color, double radius) {
        int startAngle = firstIndex * 60 + 30;
        int angle = 120;
        double rstartAng = rad(startAngle);
        Point center = new Point((int)(position.getX() + radius * Math.cos(rstartAng)),
                                 (int)(position.getY() + radius * Math.sin(rstartAng)));

        drawPathArc(g2, center, color, radius, startAngle + 90, angle);
    }

    private void drawCurvedPath(Graphics2D g2, Point position, int firstIndex,
                                Color color, double radius) {
        int startAngle = firstIndex * 60 + 60;
        int angle = 60;
        double rstartAng = rad(startAngle);
        Point center = new Point((int)(position.getX() + radius * Math.cos(rstartAng)),
                                 (int)(position.getY() + radius * Math.sin(rstartAng)));

        drawPathArc(g2, center, color, radius, startAngle + 150, angle);
    }

    private void drawPathArc(Graphics2D g2, Point2D center, Color color, double size,
                             int startAngle, int angle) {
        g2.setColor(PATH_BORDER_COLOR);
        g2.setStroke(getPathBGStroke(size));
        int s = (int)size;
        g2.drawArc((int)center.getX(), (int)center.getY(), s, s, startAngle, angle);
        g2.setColor(color);
        g2.setStroke(getPathStroke(size));
        g2.drawArc((int)center.getX(), (int)center.getY(), s, s, startAngle, angle);
    }

    private void drawStraightPath(Graphics2D g2, Point2D position, int firstIndex,
                                  Color color, double radius) {

        double theta1 = rad(firstIndex * 60);
        double theta2 = rad(firstIndex * 60 + 180);

        double halfWidth = radius * ROOT3D2 * ROOT3D2;
        int startX = (int)(position.getX() + halfWidth * Math.cos(theta1));
        int startY = (int)(position.getY() + halfWidth * Math.sin(theta1));
        int endX = (int)(position.getX() + halfWidth * Math.cos(theta2));
        int endY = (int)(position.getY() + halfWidth * Math.sin(theta2));
        g2.setColor(PATH_BORDER_COLOR);
        g2.setStroke(getPathBGStroke(radius));
        g2.drawLine(startX, startY, endX, endY);
        g2.setColor(color);
        g2.setStroke(getPathStroke(radius));
        g2.drawLine(startX, startY, endX, endY);
    }

    private static Stroke getPathStroke(double thickness) {
        return new BasicStroke((int)(PATH_FRAC * thickness));
    }

    private static Stroke getPathBGStroke(double thickness) {
        return new BasicStroke((int)(PATH_BG_FRAC * thickness));
    }

    private static double rad(double angleInDegrees) {
        return angleInDegrees * DEG_TO_RAD;
    }
}
