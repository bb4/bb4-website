package com.becker.simulation.fluid;

import com.becker.common.util.Util;
import java.awt.event.*;

/**
 * Handle mouse interactions - converting them in to physical manifestations.
 *
 * Created on September 23, 2007, 7:46 AM
 * @author becker
 */
public class InteractionHandler implements MouseListener, MouseMotionListener {
        
    public static final float DEFAULT_FORCE = 3.0f;
    public static final float DEFAULT_SOURCE_DENSITY = 1.0f;
    
    private float force_ = DEFAULT_FORCE;
    private float sourceDensity_ = DEFAULT_SOURCE_DENSITY;
    
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
    
    public void setForce(double force) {
        force_ = (float) force;
    }
    
    public void setSourceDensity(double sourceDensity) {
        sourceDensity_ = (float) sourceDensity;
    }
    
    
    /**
     * Make waves or add ink 
     */
    public void mouseDragged(MouseEvent e) {
      
        currentX = e.getX();
        currentY = e.getY();
        int i = (int) (currentX / scale_);
        int j = (int) (currentY / scale_);
 
        // apply the change to a convolution kernal area
        int startX = Math.max(1, i - 1);
        int stopX = Math.min(grid_.getXDim(), i+1);
        int startY = Math.max(1, j - 1);
        int stopY = Math.min(grid_.getYDim(), j+1);
      
        
        for (int ii=startX; ii<stopX; ii++) {
             for (int jj=startY; jj<stopY; jj++) {
                 float weight = (ii == i && jj == j)? 1.0f : 0.3f;
                 applyChange(ii, jj, weight);
             }
        }
       
        lastX = currentX;
        lastY = currentY;
    }

    private void applyChange(int i, int j, float weight) {
         
        // if the left mouse is down, make waves
        if (mouse1Down) {
            //System.out.println("incrementing by " + FORCE * (currentX - lastX) +" ,  "+FORCE * (currentY - lastY));
            float fu = (float) (weight * force_ * (currentX - lastX) / scale_);
            float fv =  (float) (weight *force_ * (currentY - lastY) / scale_);
            //System.out.println("fu="+Util.formatNumber(fu) + "  fv="+ Util.formatNumber(fv));
            grid_.incrementU(i, j, fu);
            grid_.incrementV(i, j, fv);               
        }  
        else if (mouse3Down) {   
            // if the right mouse is down, add ink (density)
            grid_.incrementDensity(i, j, weight * sourceDensity_);
        }
        else {
            System.out.println("dragged with no button down");
        }
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
