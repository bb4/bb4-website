/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.renderers;

import com.barrybecker4.ui.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * For fast rendering into an offscreen image.
 * Partially implements methods found in Graphics2D.
 * @author Barry Becker
 */
public class OfflineGraphics  {

    private int width_;
    private int height_;
    private Color bgColor_;
    private BufferedImage offImage_;
    private Graphics2D offlineGraphics_;

    /**
     * Constructor
     * @param dim dimensions of offline image to render in.
     * @param backgroundColor background color for the image.
     */
    public OfflineGraphics(Dimension dim, Color backgroundColor) {

        assert backgroundColor!=null;
        width_ = dim.width;
        height_ = dim.height;
        assert width_  >0  && height_ > 0;
        bgColor_ = backgroundColor;
        offlineGraphics_ = createOfflineGraphics();
        clear();
    }

    public void setColor(Color c) {
        if (offlineGraphics_ != null)
            offlineGraphics_.setColor(c);
    }

    public void setStroke(Stroke s) {
        if (offlineGraphics_ != null)
            offlineGraphics_.setStroke(s);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        if (offlineGraphics_ != null)
            offlineGraphics_.drawLine(x1, y1, x2, y2);
    }

    public void fillRect(int x, int y, int width , int height) {
        if (offlineGraphics_ != null)
            offlineGraphics_.fillRect(x, y, width, height);
    }

    public void drawPoint(int x1, int y1) {
        if (offlineGraphics_ != null)
            offlineGraphics_.drawLine(x1, y1, x1, y1);
    }

    public void fillCircle(int x1, int y1, int rad) {
        if (offlineGraphics_ != null)
            offlineGraphics_.fillOval(x1- rad, y1 - rad, rad*2, rad*2);
    }

    public void drawRect(int x1, int y1, int width, int height) {
        if (offlineGraphics_ != null)
            offlineGraphics_.drawRect(x1, y1, x1, y1);
    }

    public void drawImage(Image img, int x, int y, ImageObserver observer) {
        if (offlineGraphics_ != null)
            offlineGraphics_.drawImage(img, x, y, observer);
    }

    /**
     * @return image we render into for better performance. Created lazily.
     */
    public BufferedImage getOfflineImage() {
        if (offImage_ == null && width_ > 0 && height_ > 0) {
           offImage_ = ImageUtil.createCompatibleImage(width_, height_);
        }
        return offImage_;
    }

    public void clear() {

        offlineGraphics_.setColor( bgColor_ );
        offlineGraphics_.fillRect( 0, 0, width_,  height_);
    }

    /**
     * @return the offline graphics created with lazy initialization.
     */
    private Graphics2D createOfflineGraphics() {
        Graphics2D offlineGraphics = null;
        if (getOfflineImage() != null)  {
            offlineGraphics = getOfflineImage().createGraphics();
            if (offlineGraphics != null)  {
                offlineGraphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );
                offlineGraphics.setRenderingHint( RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY );
                offlineGraphics.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY );
                offlineGraphics.setPaintMode();
            }
        }
        return offlineGraphics;
    }
}
