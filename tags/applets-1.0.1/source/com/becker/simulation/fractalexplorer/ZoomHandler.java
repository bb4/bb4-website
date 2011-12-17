/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.fractalexplorer;

import com.becker.common.math.ComplexNumber;
import com.becker.common.math.ComplexNumberRange;
import com.becker.simulation.fractalexplorer.algorithm.FractalAlgorithm;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Create a zoombox while dragging.
 * Maintain aspect if control key or shift key while dragging.
 *
 * Created on September
 * @author Barry Becker
 */
public class ZoomHandler implements MouseListener, MouseMotionListener {

    FractalAlgorithm algorithm_;

    private static final Color BOUNDING_BOX_COLOR = new Color(255, 100, 0);

    private static final int UNSET = -1;

    // drag start position
    private int dragStartX = UNSET;
    private int dragStartY = UNSET;

    // other corner position while dragging.
    private int currentX = UNSET;
    private int currentY = UNSET;
    private int width;
    private int height;
    private int left;
    private int top;

    /** if control or shift key held down while dragging, maintain aspect ratio. */
    boolean keepAspectRatio = false;

    /**
     * Constructor
     */
    public ZoomHandler(FractalAlgorithm algorithm) {
        algorithm_ = algorithm;
    }

    public void mouseDragged(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
    }

    public void mouseMoved(MouseEvent e) {}

    /**
     * The following methods implement MouseListener
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Remember the mouse button that is pressed.
     */
    public void mousePressed(MouseEvent e) {

        keepAspectRatio = determineIfKeepAspectRation(e);

        dragStartX = e.getX();
        dragStartY = e.getY();
    }

    private boolean determineIfKeepAspectRation(MouseEvent e) {
       return e.isControlDown() || e.isShiftDown();
    }

    public void mouseReleased(MouseEvent e) {

        if (currentX != dragStartX && currentY != dragStartY)   {

            ComplexNumber firstCorner = algorithm_.getComplexPosition(left, top);
            ComplexNumber secondCorner = algorithm_.getComplexPosition(left + width, top + height);
            ComplexNumberRange range = new   ComplexNumberRange(firstCorner, secondCorner);
            algorithm_.setRange(range);
        }
        dragStartX = UNSET;
        dragStartY = UNSET;
        currentX = UNSET;
        currentY = UNSET;
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    /**
     * Draw the bounding box if dragging.
     */
    public void render(Graphics g, double aspectRatio) {
        Graphics2D g2 = (Graphics2D) g;

        if (dragStartX != UNSET && currentX != UNSET)  {

            left = Math.min(currentX, dragStartX);
            top = Math.min(currentY, dragStartY);
            width = Math.abs(currentX - dragStartX);
            height = Math.abs(currentY - dragStartY);

            if (keepAspectRatio)  {
                if (width > height) {
                   height = (int)(width / aspectRatio);
                } else {
                   width = (int)(height * aspectRatio);
                }
            }

            g2.setColor(BOUNDING_BOX_COLOR);
            g2.drawRect(left,  top, width, height);
        }
    }
}
