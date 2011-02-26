package com.becker.apps.misc.sierpinksi;

import java.awt.*;

/**
 * This class draws the Sierpinski triangle.
 * @author Barry Becker
 */
public class SierpinskiRenderer {

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color LINE_COLOR = new Color(0, 0, 0);
    private static final int MARGIN = 20;

    private int width;
    private int height;
    private int maxDepth = 1;
    private Graphics2D g2;

    /**
     * Constructor.
     */
    public SierpinskiRenderer() {
    }

    public void setDepth(int depth) {
        this.maxDepth = depth;
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

        drawSierpinski(A, B, C, 0);
    }

    /** erase everything so we can start anew. */
    private void clear() {
        g2.setBackground(BACKGROUND_COLOR);
        g2.clearRect(0,0, width, height);
    }

    /**
     * Recursive method to actually draw the algorithm
     * This is the secret sauce of the whole application.
     */
    private void drawSierpinski(Point A, Point B, Point C, int depth) {

        drawTriangle(A, B, C);
    }

    private Point midpoint(Point P1, Point P2) {
        return  new Point((P1.x + P2.x)/2, (P1.y + P2.y)/2);
    }

    private void drawTriangle(Point A, Point B, Point C) {
        drawTriangle(A, B, C, false);
    }

    private void drawTriangle(Point A, Point B, Point C, boolean fill) {
        g2.setColor(LINE_COLOR);

        Polygon triangle = new Polygon();
        triangle.addPoint(A.x, A.y);
        triangle.addPoint(B.x, B.y);
        triangle.addPoint(C.x, C.y);

        if (fill)
            g2.fillPolygon(triangle);
        else
            g2.drawPolygon(triangle);
    }
}