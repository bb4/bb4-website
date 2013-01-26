// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.sierpinski;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

/**
 * This class draws a Sierpinski triangle to a specified depth.
 * @author Barry Becker
 */
public class TriangleRenderer {

    private static final boolean FILL = false;
    private int maxDepth = 1;

    private Graphics2D g2;
    private GraphicsStyler styler;

    /**
     * Constructor.
     */
    public TriangleRenderer(GraphicsStyler styler) {
        this.styler = styler;
    }

    public void setDepth(int depth) {
        assert depth > 0 && depth < 20 : "Unreasonable max depth of "+ depth +" specified.";
        maxDepth = depth;
    }

    /**
     * Recursive method to actually draw the algorithm
     * This is the secret sauce of the whole application.
     */
    public void render(Triangle triangle, Graphics2D g2) {
        this.g2 = g2;
        draw(triangle, 0);
    }

    /**
     * Recursive method to actually draw the algorithm.
     * This is the secret sauce of the whole application.
     */
    private void draw(Triangle triangle, int depth) {

        styler.setStyle(depth, g2);

        drawTriangle(triangle);
        Point a = midpoint(triangle.B, triangle.C);
        Point b = midpoint(triangle.A, triangle.C);
        Point c = midpoint(triangle.B, triangle.A);

        if (depth >= maxDepth) {
            drawTriangle(new Triangle(a, b, c), true);
        }
        else {
            draw(new Triangle(triangle.A, c, b), depth + 1);
            draw(new Triangle(c, triangle.B, a), depth + 1);
            draw(new Triangle(b, a, triangle.C), depth + 1);
        }
    }

    private Point midpoint(Point P1, Point P2) {
        return  new Point((P1.x + P2.x)/2, (P1.y + P2.y)/2);
    }

    private void drawTriangle(Triangle triangle) {
        drawTriangle(triangle, FILL);
    }

    private void drawTriangle(Triangle sTriangle, boolean fill) {

        Polygon triangle = sTriangle.getPoly();

        if (fill) {
            g2.fillPolygon(triangle);
        }
        else {
            g2.drawPolygon(triangle);
        }
    }
}