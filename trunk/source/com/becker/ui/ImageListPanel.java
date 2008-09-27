package com.becker.ui;


import com.becker.game.multiplayer.set.ui.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.set.*;
import com.becker.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.*;
import java.util.*;
import java.util.Timer;
import java.util.List;

/**
 *  Displays an array of images.
 * The images are displayed in a way that uses the available space 
 * effectively without using a scrollbar.
 * The images may get smaller than their actual size, but they 
 * will maintain their aspect ratio, and they will not get bigger.
 *
 * @author Barry Becker
 */
public final class ImageListPanel extends JPanel
                                 implements MouseMotionListener, MouseListener
{
    private List<BufferedImage> images_;
    private BufferedImage highlightedImage_ = null;
    private List<BufferedImage> selectedImages_;
    private double imageRatio_;
    private int baseImageWidth_;
    private int numColumns_;
    private int imageDisplayHeight_;
    private int imageDisplayWidth_;
    private int maxNumSelections_ = Integer.MAX_VALUE;
    private boolean enlargeHighlightedImage_ = false;
    private static final int TIME_TO_ENLARGE = 800;

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color HIGHLIGHT_COLOR = Color.ORANGE;
    private static final Color SELECTION_COLOR = Color.BLUE;    
    
    private static final int IMAGE_MARGIN = 2;
    private static final int TOTAL_MARGIN = 2 * IMAGE_MARGIN;
    
    
    
    /**
     * Use this Constructor if you just want the empty panel initially.
     * @@ add constructor fgor unequal sized images where we specify the desired max size.
     * @param images array of identically sized images to show in an array.
     */ 
    public ImageListPanel()
    {                                        
        this.setMinimumSize(new Dimension(100, 100));        
        this.addComponentListener( new ComponentAdapter()  {
            public void componentResized( ComponentEvent ce )
            {
                panelResized();
            }
        } );
    }
    
    /**
     * Constructor.
     * @@ add constructor fgor unequal sized images where we specify the desired max size.
     * @param images array of identically sized images to show in an array.
     */ 
    public ImageListPanel(List<BufferedImage> images)
    {                        
        this();                
        setImageList(images);
    }
    
    public void setImageList(List<BufferedImage> images) {
        images_ = images;
        imageRatio_ = calculateImageRatio(images);
        baseImageWidth_ = images.get(0).getWidth() + TOTAL_MARGIN;
        selectedImages_ = new ArrayList<BufferedImage>();
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.repaint();
    }
    
    public void setMaxNumSelections(int max) {
        maxNumSelections_ = max;
    }

    private static double calculateImageRatio(List<BufferedImage> images) {
        // first assert that all the images are the same size
        assert images.size() > 0;
        BufferedImage firstImage = images.get(0);
        int w = firstImage.getWidth();
        int h = firstImage.getHeight();
        for (BufferedImage img : images) {
            assert (img.getWidth() == w) : "Image dimensions " + img.getWidth() + ", " + img.getHeight() +" do not match first: "+ w+ ", "+ h;
        }
        w += TOTAL_MARGIN;
        h += TOTAL_MARGIN;
        return (double)w/(double)h;
    }

    /**
     * This renders the array of images to the screen.
     */
    protected void paintComponent( Graphics g )
    {
        super.paintComponents( g );
        if (images_ == null) return;
        
        // erase what's there and redraw.        
        g.clearRect( 0, 0, getWidth(), getHeight() );
        g.setColor( BACKGROUND_COLOR );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        double panelRatio = (double)getWidth() / (double)getHeight();
        
        // find the number of rows that will give the closest match on the aspect ratios
        int numImages = images_.size();
        int numRows = 1;
        numColumns_ = numImages;
        double ratio = imageRatio_ * numImages;
        double lastRatio = 1000000;        
        while (ratio > panelRatio) {            
            lastRatio = ratio;
            numRows++;
            numColumns_ = (int)Math.ceil((double)numImages / (double)numRows);        
            ratio = imageRatio_ * (double)numColumns_/(double)numRows;
            //System.out.println("ratio="+ ratio +" panelRatio=" + panelRatio + " numRows="+ numRows);
        } 
        if (panelRatio - ratio < lastRatio - panelRatio) {
            // then we may have space on right side, but vertical space completely used
            imageDisplayHeight_ = Math.min(getHeight() / numRows, (int)(baseImageWidth_/imageRatio_));
            imageDisplayWidth_ = (int) (imageDisplayHeight_ * imageRatio_);               
        } else {
            // horizontal space completely used
            numRows = numRows -1;
            numColumns_ = (int)Math.ceil((double)numImages / (double)numRows);    
            imageDisplayWidth_ = Math.min( getWidth() / numColumns_, baseImageWidth_);
            imageDisplayHeight_ = (int) (imageDisplayWidth_ / imageRatio_);   
        }
        
        Graphics2D g2 = (Graphics2D)g;  
        int enlargedImageIndex = -1;
        
        for (int i = 0; i < images_.size();  i++ ) {
         
            int colPos = getColumnPosition(i);
            int rowPos = getRowPosition(i);
            BufferedImage img =  images_.get(i);
            if (img.equals(highlightedImage_) && enlargeHighlightedImage_) {
                enlargedImageIndex = i;
            }
            g2.drawImage(img, colPos , rowPos, 
                                    imageDisplayWidth_, imageDisplayHeight_, null);
            
            
            // put a border around images that are selected or highlighted
            if (highlightedImage_ == img || selectedImages_.contains(img)) {                
                g2.setColor((highlightedImage_ == img) ? HIGHLIGHT_COLOR :SELECTION_COLOR);                    
                g2.drawRect(colPos-1, rowPos-1,
                                     imageDisplayWidth_ - TOTAL_MARGIN + 2, imageDisplayHeight_ - TOTAL_MARGIN + 2);
            }        
        }
        if (enlargedImageIndex >= 0) {      
            int w = highlightedImage_.getWidth();
            int h = highlightedImage_.getHeight();
            /*
            int y = getRowPosition(enlargedImageIndex);
            int x = getColumnPosition(enlargedImageIndex);
            
            int row = enlargedImageIndex / numColumns_;
            int col = enlargedImageIndex % numColumns_;
            if (row > numRows/2) {
                y = y + imageDisplayHeight_ - h;
            }
            if (col > numColumns_/2) {
                x = x + imageDisplayWidth_ - w;
            }
            g2.drawImage(highlightedImage_, x , y, w, h, null);
             */
            if (w > getWidth()) {
                w = getWidth();
                h = (int)(w/imageRatio_);
            }
            if (h > getHeight()) {
                h = getHeight();
                w = (int)(h * imageRatio_);
            }
            g2.drawImage(highlightedImage_, 0 , 0, w, h, null);
        }
    }
    
    private int getRowPosition(int i) {
        int row = i / numColumns_;
        return row * imageDisplayHeight_ + IMAGE_MARGIN;
    }
    
    private int getColumnPosition(int i) {
        int col = i % numColumns_;
        return col * imageDisplayWidth_ + IMAGE_MARGIN;
    }
    

    private void panelResized() {
        this.repaint();
    }

    /**
     * @return  the image that the mouse is currently over (at x, y coords)
     */
    private BufferedImage findImageOver(int x, int y) {

        int selectedIndex = -1;
        for (int i = 0; i < images_.size(); i++ ) {
            int row = i / numColumns_;
            int col = i % numColumns_;
            int colPos = col * imageDisplayWidth_ + IMAGE_MARGIN;
            int rowPos = row * imageDisplayHeight_ + IMAGE_MARGIN;
            if (  x > colPos && x <= colPos + imageDisplayWidth_
                && y > rowPos && y <= rowPos + imageDisplayHeight_) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex == -1) {
            return null;
        }
        return images_.get(selectedIndex);
    }

    public void mouseMoved(MouseEvent e) {
        BufferedImage image = findImageOver(e.getX(), e.getY());

        boolean changed = image != highlightedImage_;

        if (changed) {
            if (image != null) {
                highlightedImage_ = image;         
            } else {
                highlightedImage_ = null;
            }
            this.repaint();
            
            // start a time that is canceled if the mouse moves
            final Timer enlargementTimer = new Timer();
            enlargeHighlightedImage_ = false;
            enlargementTimer.schedule(
                    new TimerTask () {
                       public void run ()   {
                            enlargeHighlightedImage_ = true;
                            enlargementTimer.cancel ();
                            repaint();
                       }
                   }, TIME_TO_ENLARGE);
        }        
        
    }

    public List<BufferedImage> getSelectedCards() {        
        return selectedImages_;
    }

 
    public void mouseDragged(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {
        BufferedImage img = findImageOver(e.getX(), e.getY());
        if (img != null) {
            if (selectedImages_.contains(img)) {
                selectedImages_.remove(img);
            }
            else {
                // if we are at our limit we first need to remove the first selected
                if (selectedImages_.size() == maxNumSelections_) {
                    selectedImages_.remove(0);
                }
                selectedImages_.add(img);
            }
            //System.out.println("numselected imgs ==" + selectedImages_.size());
            this.repaint();
        }
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
 
}

