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
public class LSystemModel {

    public static final String DEFAULT_EXPRESSION = "F(+F)F(-F)F";

    public static final double INITIAL_ANGLE = 90.0;
    private static final double LENGTH = 5.0;

    private final int width;
    private final int height;

    private int numIterations;
    private double angleIncrement;
    private double scale;

    private LExpressionParser parser;
    private TreeNode root;

    /** offline rendering is fast  */
    private final OfflineGraphics offlineGraphics_;

    /** Constructor */
    public LSystemModel(int width, int height, int numIterations, double angleInc, double scale) {

        this.width = width;
        this.height = height;
        this.numIterations = numIterations;
        this.angleIncrement = angleInc;
        this.scale = scale;
        parser = new LExpressionParser();
        root = parser.parse(DEFAULT_EXPRESSION);

        offlineGraphics_ = new OfflineGraphics(new Dimension(width, height), Color.BLACK);
    }

    public void setExpression(String exp) {
        root = parser.parse(exp);
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
        offlineGraphics_.drawLine(100, 90, 200, 190);
        System.out.println("drew");

        double angleRad = INITIAL_ANGLE * Math.PI/180.0;
        Vector2d initialPosition = new Vector2d(width/2, height/3);

        drawTree(angleRad, initialPosition, root, numIterations);
    }

	/**
	 * Recompute the polygon set by translating the expression.
	 * @param angle angle in radians that the turtle graphics used when rotating '+' or '-'
	 */
	private void drawTree(double angle, Vector2d pos, TreeNode tree, int numIterations) {

		if (numIterations == 0) {
            drawBaseTree(angle, pos, tree);
        }
        else {
            for (TreeNode child : tree.children) {
                if (child.hasParens) {
                    drawTree(angle, pos, child, numIterations);
                }
                else {
                    String baseExp = child.getData();
                    System.out.println("baseExp = "+ baseExp);
                    double newAngle = angle;
                    for (int i = 0; i<baseExp.length(); i++) {
                        char c = baseExp.charAt(i);

                        if (c == F.getSymbol())  {
                            drawTree(newAngle, pos, tree, numIterations - 1);
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
    }

    private void drawBaseTree(double angle, Vector2d pos, TreeNode tree) {
         for (TreeNode child : tree.children) {
             if (child.hasParens) {
                 drawBaseTree(angle, pos, child);
             }
             else {
                 String baseExp = child.getData();
                 System.out.println("baseBaseExp = "+ baseExp);
                 double newAngle = angle;
                 Vector2d newPos = pos;
                 for (int i = 0; i<baseExp.length(); i++) {
                     char c = baseExp.charAt(i);

                     if (c == F.getSymbol())  {
                         int startX = (int)(newPos.x);
                         int startY = (int)(newPos.y);

                         int stopX = (int)(newPos.x + scale * LENGTH * Math.cos(newAngle));
                         int stopY = (int)(newPos.y + scale * LENGTH * Math.sin(newAngle));

                         newPos = new Vector2d(stopX, stopY);

                         offlineGraphics_.drawLine(startX, startY, stopX, stopY);
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
}
