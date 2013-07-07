// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.legend;

import com.barrybecker4.common.app.AppContext;
import com.barrybecker4.ui.util.ColorMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Use the controls within this edit bar to edit the color legend.
 * Does not show is isEditable is false.
 * Not static so we can call methods in the owning legend class.
 *
 * @author Barry Becker
 */
class LegendEditBar extends JPanel
                    implements MouseListener, MouseMotionListener {

    static final int MARGIN = 5;
    private static final Color EDIT_BAR_BG = new Color(255, 255, 255, 180);
    private static final int MARKER_SIZE = 6;
    private static final int MARKER_HALF_SIZE = 3;
    private static final BasicStroke MARKER_STROKE = new BasicStroke(0.5f);

    ColorMap cmap_;
    private double ratio_;
    private int dragIndex_ = -1;
    private int dragPosition_;
    private Component owner;

    LegendEditBar(ColorMap colormap, Component owner) {
        cmap_ = colormap;
        this.owner = owner;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        g2.setColor(EDIT_BAR_BG);
        g2.fillRect(MARGIN, 0, getWidth() - 2*MARGIN, MARKER_SIZE + 4);

        ratio_ = (double) (getWidth() - 2*MARGIN) / cmap_.getValueRange();

        g2.setStroke(MARKER_STROKE);
        for (int i = 0; i < cmap_.getNumValues(); i++) {
            if (dragIndex_ != i)  {
                int x = getPositionForValue(cmap_.getValue(i));
                drawMarker(cmap_.getColor(i), x, g2);
            }
        }
        if (dragIndex_ > 0) {
            drawMarker(cmap_.getColor(dragIndex_), dragPosition_, g2);
        }
    }

    /**
     *  Draw a little triangular marker for the draggable control point.
     */
    private void drawMarker(Color c, int xpos, Graphics2D g2) {
        g2.setColor(c);
        int[] xpoints = {xpos - MARKER_HALF_SIZE, xpos + MARKER_HALF_SIZE, xpos};
        int[] ypoints = {1, 1, MARKER_SIZE + 2};
        Polygon triangle = new Polygon(xpoints, ypoints, 3);
        g2.fillPolygon(triangle);
        g2.setColor(Color.BLACK);
        //g2.drawPolygon(triangle);
        g2.drawLine(xpoints[1], ypoints[1], xpoints[2], ypoints[2]);
    }

    private double getValueForPosition(int x) {
        return ((double)x - MARGIN) / ratio_ + cmap_.getMinValue();
    }

    private int getPositionForValue(double v) {
         return (int) (MARGIN + ratio_ * (v - cmap_.getMinValue()));
    }

    /**
     * @return -1 if no control index under the given x pos
     */
    private int getControlIndex(int xpos) {

        double v = getValueForPosition(xpos);
        int i = cmap_.getClosestIndexForValue(v);
        int diff = Math.abs(xpos - getPositionForValue(cmap_.getValue(i)));
        if (diff <= MARKER_HALF_SIZE + 1)
            return i;
        else
            return -1;
    }

    /**
     * @return the index at or to the left of xpos
     */
    private int getLeftControlIndex(int xpos) {

        double v = getValueForPosition(xpos);
        return cmap_.getLeftIndexForValue(v);
    }

    public void mouseClicked(MouseEvent e) {

        int xpos = e.getX();
        int index = getControlIndex(xpos);

        if (e.getButton() == MouseEvent.BUTTON3) {
            // delete on right click
            if (index != -1) {
                cmap_.removeControlPoint(index);
            }
        }
        else if (e.getClickCount() > 1) {
            Color oldColor = cmap_.getColorForValue(getValueForPosition(xpos));
            Color newControlColor =
                        JColorChooser.showDialog(this, AppContext.getLabel("NEW_POINT_PATH"), oldColor);
            if (newControlColor != null) {
                if (index == -1) {
                    // add a new control point and marker here if no point is double clicked on.
                    cmap_.insertControlPoint(getLeftControlIndex(xpos)+1, getValueForPosition(xpos), newControlColor);
                }
                else {
                    // get a new color for this control point  double clicked on
                    cmap_.setColor(index, newControlColor);
                }
                owner.repaint();
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        int index = getControlIndex(e.getX());

        if (index > 0 && index < (cmap_.getNumValues() - 1)) {
            // we are dragging the control point.
            // Note: can't drag the first and last control points.
            dragIndex_ = index;
            dragPosition_ = e.getX();
        }
    }

    public void mouseReleased(MouseEvent e) {
        // dropped
        updateDrag(e.getX());
        dragIndex_ = -1;
        owner.repaint();
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    private void updateDrag(int xpos) {
        if (dragIndex_ > 0) {
            double v = getValueForPosition(xpos);
            if (v < cmap_.getValue(dragIndex_+1) && v > cmap_.getValue(dragIndex_ - 1)) {
                cmap_.setValue(dragIndex_, v);
                dragPosition_ = xpos;
                //repaint();
                paint( getGraphics() );
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        updateDrag(e.getX());
    }

    public void mouseMoved(MouseEvent e) {}
}
