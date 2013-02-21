// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.algorithm;

import com.barrybecker4.common.ColorMap;
import com.barrybecker4.common.expression.TreeNode;
import com.barrybecker4.simulation.lsystem.algorithm.expression.LExpressionParser;
import com.barrybecker4.ui.renderers.OfflineGraphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import static com.barrybecker4.simulation.lsystem.algorithm.expression.LTokens.*;

/**
 * Everything we need to know to compute the l-System tree.
 *
 * @author Barry Becker
 */
public class LSystemRenderer {

    private static final double LENGTH = 1.0;
    private static final Color BG_COLOR = new Color(0, 30, 10);
    // use a colormap instead
    private ColorMap cmap = new DepthColorMap();
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
        this.angleIncrement = angleInc * Math.PI / 180;
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
        OrientedPosition initialPosition = new OrientedPosition(width/2.0, height/8.0, Math.PI/2.0);
        double length = LENGTH * width / 10.0;

        drawTree(initialPosition, length, root, numIterations);
    }

	/**
	 * Draw the tree recursively.
	 * @param pos the position and angle in radians that the turtle graphics used when rotating '+' or '-'
	 */
	private void drawTree(OrientedPosition pos, double length, TreeNode tree, int numIterations) {

        List<TreeNode> list = new LinkedList<TreeNode>(tree.children);

        for (TreeNode child : list) {
            if (child.hasParens) {
                drawTree(new OrientedPosition(pos), length, child, numIterations);
            }
            else {
                String baseExp = child.getData();

                for (int i = 0; i<baseExp.length(); i++) {
                    processSymbol(length, numIterations, pos, baseExp.charAt(i));
                }
            }
        }
    }

    /** note: current position is changed by the processing of the symbol */
    private void processSymbol(
            double length, int numIterations, OrientedPosition currentPos, char c) {

        if (c == F.getSymbol())  {
            if (numIterations > 0) {
                drawTree(currentPos, scaleFactor * length, root, numIterations - 1);
            }
            else {
                drawF(currentPos, length, numIterations);
            }
        }
        else if (c == MINUS.getSymbol()) {
            currentPos.angle -= angleIncrement;
        }
        else if (c == PLUS.getSymbol()) {
            currentPos.angle += angleIncrement;
        }
        else {
            throw new IllegalStateException("Unexpected char: "+ c);
        }
    }

    private void drawF(OrientedPosition pos, double length, int num) {

        int startX = (int)(pos.x);
        int startY = (int)(pos.y);

        int stopX = (int)(pos.x + scale * length * Math.cos(pos.angle));
        int stopY = (int)(pos.y + scale * length * Math.sin(pos.angle));

        pos.x = stopX;
        pos.y = stopY;

        offlineGraphics_.setColor(cmap.getColorForValue(num));
        offlineGraphics_.drawLine(startX, height - startY, stopX, height - stopY);
        offlineGraphics_.fillCircle(stopX, height - stopY, (int)(length/20) );
    }
}
