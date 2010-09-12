package com.becker.simulation.fractals;

import com.becker.common.math.ComplexNumber;
import com.becker.simulation.fluid.Grid;
import com.becker.simulation.fractals.algorithm.FractalAlgorithm;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Handle mouse interactions - converting them in to physical manifestations.
 *
 * Created on September 23, 2007, 7:46 AM
 * @author Barry Becker
 */
public class ZoomHandler implements MouseListener, MouseMotionListener {

    FractalAlgorithm algorithm_;

    private int dragStartX, dragStartY;
    private boolean mouse1Down, mouse3Down;

    /**
     * Constructor
     */
    public ZoomHandler(FractalAlgorithm algorithm) {
        algorithm_ = algorithm;
    }

    /**
     * Make waves or add ink 
     */
    public void mouseDragged(MouseEvent e) {
        System.out.println("dragged");
    }

    /** */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * The following methods implement MouseListener 
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Remember the mouse button that is pressed.
     */
    public void mousePressed(MouseEvent e) {
        mouse1Down = ((e.getModifiers() & e.BUTTON1_MASK) == e.BUTTON1_MASK);
        mouse3Down = ((e.getModifiers() & e.BUTTON3_MASK) == e.BUTTON3_MASK);
        dragStartX = e.getX();
        dragStartY = e.getY();
        //System.out.println("mousr pressed start=" +dragStartX +" , " + dragStartY);
    }

    public void mouseReleased(MouseEvent e) {
        int currentX = e.getX();
        int currentY = e.getY();
        //System.out.println("mouse repleased curre=" + currentX +" , "  + currentY);
        if (currentX != dragStartX && currentY != dragStartY)   {

            ComplexNumber firstCorner = algorithm_.getComplexPosition(dragStartX, dragStartY);
            ComplexNumber secondCorner = algorithm_.getComplexPosition(currentX, currentY);
            algorithm_.setRange(firstCorner, secondCorner);
        }
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
