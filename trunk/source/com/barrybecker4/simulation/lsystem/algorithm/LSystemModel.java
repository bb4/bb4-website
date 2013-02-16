// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.algorithm;

import com.barrybecker4.ui.renderers.OfflineGraphics;

import javax.vecmath.Vector3f;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Everything we need to know to compute the henon phase diagram.
 *
 * @author Barry Becker
 */
public class LSystemModel {

    private final int width;
    private final int height;

    private int numIterations;
    private double angle;
    private double scale;

    private LSystem lsystem;

    /** offline rendering is fast  */
    private final OfflineGraphics offlineGraphics_;

    /** constructor */
    public LSystemModel(int width, int height, int numIterations, double angle, double scale) {

        this.width = width;
        this.height = height;
        this.numIterations = numIterations;
        this.angle = angle;
        this.scale = scale;
        lsystem = new LSystem();

        offlineGraphics_ = new OfflineGraphics(new Dimension(width, height), Color.BLACK);
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

        String tree = lsystem.generateTree(numIterations);
        double angleRad = angle * Math.PI/180.;
        drawTree(angleRad, tree);
        /*
        for (int i=0; i< numSteps; i++)   {
            int xpos = (int)(width * (width/2.0 + 0.5));
            int ypos = (int)(height * (height/2.0 + 0.5));


            offlineGraphics_.drawPoint(xpos, ypos);
        } */
    }

	/**
	 * Recompute the polygon set by translating the expression.
	 * @param angle angle in radians that the turtle graphics used when rotating '+' or '-'
	 */
	private void drawTree(double angle, String expTree) {

		int vertInd, len = expTree.length();
		float x, y, a;
		double size	= 0.2;
		Vector3f pos = new Vector3f();


		x = 0.0f;
		y = 0.0f;
		a = (float) (Math.PI/2.0);
		pos.set(x, y, a);

		int polyInd = 0;

        /*
		for (int i=0; i<len; i++) {
			switch (expTree.charAt(i)) {
			case 'F':
				a = pos.getZ();
				x = (float)(pos.getX() + size * Math.cos(a));
				y = (float)(pos.getY() + size * Math.sin(a));
				pos.set(x, y, a);
				vertInd = m_polySet.addVertex(pos);
				if (polyInd >= pg.getSize())
					pg.setSize(2*polyInd);
				pg.setEntry(polyInd++, vertInd);
				break;
			case '[':
				branchPos.push(pos);
				pos	= PdVector.copyNew(pos);
				branchPoly.push(pg);
				branchCnt.push(new Integer(polyInd));
				pg		= new PiVector(defMaxPolygonLength);
				polyInd = 0;
				pg.setEntry(polyInd++, vertInd);
				break;
			case ']':
				pg.setSize(polyInd);
				m_polySet.addPolygon(pg);
				pos	= (PdVector)branchPos.pop();
				pg = (PiVector)branchPoly.pop();
				Integer cnt = (Integer)branchCnt.pop();
				polyInd = cnt.intValue();
				vertInd = pg.getEntry(polyInd-1);
				break;
			case '+':
				pos.setEntry(2, pos.getEntry(2)+delta);
				break;
			case '-':
				pos.setEntry(2, pos.getEntry(2)-delta);
				break;
			default:
			}
		}
		pg.setSize(polyInd);
		m_polySet.addPolygon(pg);
		*/
	}
}
