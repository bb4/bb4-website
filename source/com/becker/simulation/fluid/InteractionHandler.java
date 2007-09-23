package com.becker.simulation.fluid;

import java.awt.event.*;

/**
 * Handle mouse interactions - converting them in to physical manifestations.
 *
 * Created on September 23, 2007, 7:46 AM
 * @author becker
 */
public class InteractionHandler implements MouseListener, MouseMotionListener {
        
    private static final float FORCE = 2.0f;
    private static final float SOURCE_DENSITY = 1.8f;
    
    Grid grid_;    
    
    double scale_;
    
    private int currentX, currentY;
    private int lastX, lastY;
    private boolean mouse1Down, mouse3Down;
    
    /**
     * 
     */
    public InteractionHandler(Grid grid,  double scale) {
        scale_ = scale;
        grid_ = grid;
    }
    
    
    /**
     * Make waves or add ink 
     */
    public void mouseDragged(MouseEvent e) {
      
        currentX = e.getX();
        currentY = e.getY();
        int i = (int) (currentX / scale_);
        int j = (int) (currentY / scale_);
 
        
        if (i<1 || i>grid_.getXDim() || j<1 || j> grid_.getYDim()) {
            // System.out.println("out of bounds i="+i+" j="+j);
            return;
        }
        
        // if the left mouse is down, make waves
        if (mouse1Down) {
            //System.out.println("incrementing by " + FORCE * (currentX - lastX) +" ,  "+FORCE * (currentY - lastY));
            grid_.incrementU(i, j, FORCE * (currentX - lastX));
            grid_.incrementV(i, j,  FORCE * (currentY - lastY));               
        }
        // if the right mouse is down, add ink (density)
        if (mouse3Down) {
            grid_.incrementDensity(i, j, SOURCE_DENSITY);
        }
        lastX = currentX;
        lastY = currentY;
    }

    public void mouseMoved(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
        lastX = currentX;
        lastY = currentY;
    }

    /**
     * The following methods implement MouseListener 
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     *Remember the mouse button that is pressed.
     */
    public void mousePressed(MouseEvent e) {
        mouse1Down = ((e.getModifiers() & e.BUTTON1_MASK) == e.BUTTON1_MASK);
        mouse3Down = ((e.getModifiers() & e.BUTTON3_MASK) == e.BUTTON3_MASK);
    }

    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
   
}
