// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.algorithm;

import com.barrybecker4.common.expression.TreeNode;
import com.barrybecker4.simulation.lsystem.algorithm.expression.LExpressionParser;
import com.barrybecker4.ui.renderers.OfflineGraphics;

import javax.vecmath.Vector2d;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import static com.barrybecker4.simulation.lsystem.algorithm.expression.LTokens.*;

/**
 * Everything we need to know to compute the l-System tree.
 *
 * @author Barry Becker
 */
public class LSystemRenderer {

    private static final double LENGTH = 1.0;
    private static final Color BG_COLOR = new Color(0, 30, 10);
    private final int width;
    private final int height;

    private int numIterations;
    private double angleIncrement;
    private double scale;
    private double scaleFactor;

    private TreeNode root;

    /** offline rendering is fast  */
    private final OfflineGraphics offlineGraphics_;

    /** Constructor */
    public LSystemRenderer(int width, int height, String expression, int numIterations, double angleInc,
                           double scale, double scaleFactor) {

        this.width = width;
        this.height = height;
        this.numIterations = numIterations;
        this.angleIncrement = Math.PI - angleInc * Math.PI / 180;
        this.scale = scale;
        this.scaleFactor = scaleFactor;
        LExpressionParser parser = new LExpressionParser();
        root = parser.parse(expression);

        offlineGraphics_ = new OfflineGraphics(new Dimension(width, height), BG_COLOR);
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void reset() {
    }

    public BufferedImage getImage() {
        return offlineGraphics_.getOfflineImage();
    }

    /**
     * draw the tree
     */
    public void render() {

        offlineGraphics_.setColor(Color.RED);
        Vector2d initialPosition = new Vector2d(width/2, height/3.0);
        double length = LENGTH * width / 10.0;

        drawTree(Math.PI/2.0, length, initialPosition, root, numIterations);
    }

	/**
	 * Recompute the polygon set by translating the expression.
	 * @param angle angle in radians that the turtle graphics used when rotating '+' or '-'
	 */
	private void drawTree(double angle, double length, Vector2d pos, TreeNode tree, int numIterations) {
        Vector2d newPos = pos;
        double newAngle = angle;

        for (TreeNode child : tree.children) {
            if (child.hasParens) {
                drawTree(newAngle, length, newPos, child, numIterations);
            }
            else {
                String baseExp = child.getData();

                for (int i = 0; i<baseExp.length(); i++) {
                    char c = baseExp.charAt(i);

                    if (c == F.getSymbol())  {
                        newPos = drawF(newAngle, length, tree, numIterations, newPos);
                        if (numIterations > 0) {
                            drawTree(newAngle, scaleFactor * length, newPos, tree, numIterations - 1);
                        }
                    }
                    else if (c == MINUS.getSymbol()) {
                        newAngle -= angleIncrement;
                    }
                    else if (c == PLUS.getSymbol()) {
                        newAngle += angleIncrement;
                    }
                    else {
                        throw new IllegalStateException("Unexpected char: "+ c);
                    }
                }
            }
        }
    }

    private Vector2d drawF(double angle, double length, TreeNode tree, int numIterations, Vector2d pos) {

        int startX = (int)(pos.x);
        int startY = (int)(pos.y);

        int stopX = (int)(pos.x + scale * length * Math.cos(angle));
        int stopY = (int)(pos.y + scale * length * Math.sin(angle));

        Vector2d newPos = new Vector2d(stopX, stopY);

        offlineGraphics_.drawLine(startX, startY, stopX, stopY);
        offlineGraphics_.fillCircle(stopX, stopY, (int)(length/12) );

        return newPos;
    }
}
