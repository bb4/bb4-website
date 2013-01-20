// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.animation;

import com.barrybecker4.common.util.ImageUtil;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * Records animation frame images to the disk with a name tht includes the frame number.
 * @author Barry Becker
 */
public class FrameRecorder {

    /** An image showing the current animation frame */
    protected volatile Image image;

    private String fileNameBase;


    /** Constructor */
    public FrameRecorder(final String fileNameBase) {
       this.fileNameBase = fileNameBase;
    }

    /**
     * Same the current frame image if there is one.
     * @return true if the image was successfully saved.
     */
    public boolean saveFrame(long frameIndex) {

        if (image != null) {
            String fname = fileNameBase + Long.toString( 1000000 + frameIndex);
            ImageUtil.saveAsImage(fname, image, ImageUtil.ImageType.PNG);
            return true;
        }
        return false;
    }

    /**
     * render the animation component as an image
     */
    protected void renderImage(Component comp) {

        Graphics2D g = (Graphics2D)comp.getGraphics();
        if ( g != null ) {
            Dimension dimensions = comp.getSize();

            if ( checkImage(dimensions, comp) ) {
                Graphics imageGraphics = image.getGraphics();
                // Clear the image background.
                imageGraphics.setColor( comp.getBackground() );
                imageGraphics.fillRect( 0, 0, dimensions.width, dimensions.height );
                imageGraphics.setColor( comp.getForeground() );
                // Draw this component offscreen.
                comp.paint(imageGraphics);
                // Now put the offscreen image on the screen.
                g.drawImage(image, 0, 0, null );
                // Clean up.
                imageGraphics.dispose();
            }
        }
    }

    /**
     * Check for offscreen image. Creates it if needed.
     */
    protected boolean checkImage( Dimension dimensions, Component comp) {

        if ( dimensions.width <= 0 || dimensions.height <= 0 ) return false;
        if ( image == null || image.getWidth( null ) != dimensions.width
                || image.getHeight( null ) != dimensions.height) {
            image = comp.createImage(dimensions.width, dimensions.height);
        }
        return true;
    }
}