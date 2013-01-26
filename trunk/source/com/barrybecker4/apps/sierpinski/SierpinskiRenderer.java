/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.sierpinski;

import java.awt.*;

/**
 * This class draws the Sierpinski triangle.
 * @author Barry Becker
 */
public class SierpinskiRenderer {

    public static final int DEFAULT_LINE_WIDTH = 23;
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final int MARGIN = 30;

    private int width;
    private int height;

    private Graphics2D g2;
    private GraphicsStyler styler;
    private TriangleRenderer triangleRenderer;

    /**
     * Constructor.
     */
    public SierpinskiRenderer() {
        styler = new GraphicsStyler(DEFAULT_LINE_WIDTH);
        triangleRenderer = new TriangleRenderer(styler);
    }

    public void setDepth(int depth) {
        triangleRenderer.setDepth(depth);
    }

    public void setLineWidth(float width) {
        styler.setLineWidth(width);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /** draw the sierpinski triangle */
    public void paint(Graphics g) {

        g2 = (Graphics2D) g;
        clear();

        Point A = new Point(width/2, MARGIN);
        Point B = new Point(MARGIN, height - MARGIN);
        Point C = new Point(width - 2*MARGIN, height - MARGIN);

        Triangle triangle = new Triangle(A, B, C);
        triangleRenderer.render(triangle, g2);
    }

    /** erase everything so we can start anew. */
    private void clear() {
        g2.setBackground(BACKGROUND_COLOR);
        g2.clearRect(0,0, width, height);
        // this smooths the lines when we draw.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
    }


}