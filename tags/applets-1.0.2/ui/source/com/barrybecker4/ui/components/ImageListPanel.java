/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Displays an array of images.
 * The images are displayed in a way that uses the available space
 * effectively without using a scrollbar.
 * The images may get smaller than their actual size, but they
 * will maintain their aspect ratio, and they will not get bigger.
 *
 * @author Barry Becker
 */
public final class ImageListPanel extends JPanel
                                  implements MouseMotionListener, MouseListener {
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
    private static final int TIME_TO_ENLARGE = 900;

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color HIGHLIGHT_COLOR = Color.ORANGE;
    private static final Color SELECTION_COLOR = Color.BLUE;
    private static final Stroke BORDER_STROKE = new BasicStroke( 2.0f );

    private static final int IMAGE_MARGIN = 2;
    private static final int TOTAL_MARGIN = 2 * IMAGE_MARGIN;

    private List<ImageSelectionListener> imgSelectionListeners_;

    /**
     * Use this Constructor if you just want the empty panel initially.
     * Maybe add constructor for unequal sized images where we specify the desired max size.
     */
    public ImageListPanel() {
        imgSelectionListeners_ = new LinkedList<ImageSelectionListener>();

        this.setMinimumSize(new Dimension(100, 100));
        this.addComponentListener( new ComponentAdapter()  {
            @Override
            public void componentResized( ComponentEvent ce ) {
                repaint();
            }
        } );
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
    }

    /**
     * Constructor.
     * @param images array of identically sized images to show in an array.
     */
    public ImageListPanel(List<BufferedImage> images) {
        this();
        setImageList(images);
    }

    public void setImageList(List<BufferedImage> images) {
        assert images!=null;
        images_ = images;
        imageRatio_ = calculateImageRatio(images);
        baseImageWidth_ = images.get(0).getWidth() + TOTAL_MARGIN;
        selectedImages_ = new ArrayList<BufferedImage>();
        highlightedImage_ = null;
        this.repaint();
    }

    public List<BufferedImage> getImageList() {
        return images_;
    }

    public void setMaxNumSelections(int max) {
        maxNumSelections_ = max;
    }

    /**
     * Sometimes we just want to show a single image and have it fit the
     * available area.
     */
    public void setSingleImage(BufferedImage image) {
        if (image == null) {
            System.out.println("warning: setting null image"); //NON-NLS
            return;
        }
        List<BufferedImage> imageList = new ArrayList<BufferedImage>(1);
        imageList.add(image);
        setImageList(imageList);
    }

    /**
     * This is how the client can register itself to receive these events.
     * @param isl the listener to add
     */
    public void addImageSelectionListener( ImageSelectionListener isl )
    {
        imgSelectionListeners_.add(isl);
    }

    /**
     * This is how the client can unregister itself to receive these events.
     * @param isl the listener  to remove
     */
    public void removeImageSelectionListener( ImageSelectionListener isl )
    {
        imgSelectionListeners_.remove(isl);
    }


    public List<Integer> getSelectedImageIndices() {

        if (images_ == null)
            return null;
        List<Integer> selectedIndices = new LinkedList<Integer>();

        for (int i = 0; i < images_.size();  i++ ) {

            BufferedImage img =  images_.get(i);
            if (selectedImages_.contains(img)) {
                selectedIndices.add(i);
            }
        }
        return selectedIndices;
    }

    public void setSelectedImageIndices(List<Integer> selectedIndices) {
        assert selectedIndices != null;
        // replace what we have with the new selections
        selectedImages_.clear();
        for (int i = 0; i < images_.size();  i++ ) {
            if (selectedIndices.contains(i)) {
                BufferedImage img =  images_.get(i);
                selectedImages_.add(img);
                updateImageSelectionListeners(img);
            }
        }
    }

    private static double calculateImageRatio(List<BufferedImage> images) {
        if (images == null) return 1.0;
        // first assert that all the images are the same size
        assert images.size() > 0;
        BufferedImage firstImage = images.get(0);

        int w = firstImage.getWidth();
        int h = firstImage.getHeight();
        for (BufferedImage img : images) {
            assert (img.getWidth() == w) :
                    "Image dimensions " + img.getWidth() + ", " + img.getHeight() +" do not match first: "+ w+ ", "+ h;
        }
        w += TOTAL_MARGIN;
        h += TOTAL_MARGIN;
        return (double)w/(double)h;
    }

    /**
     * This renders the array of images to the screen.
     */
    @Override
    protected void paintComponent( Graphics g ) {

        super.paintComponents( g );
        if (images_ == null || images_.size() == 0)
            return;

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
        double lastRatio = 100000000;
        while (ratio > panelRatio) {
            lastRatio = ratio;
            numRows++;
            numColumns_ = (int)Math.ceil((double)numImages / (double)numRows);
            ratio = imageRatio_ * (double)numColumns_/(double)numRows;
            //System.out.println("ratio="+ ratio +" panelRatio=" + panelRatio + " numRows="+ numRows);
        }
        if (panelRatio - ratio < lastRatio - panelRatio) {
            // then we may have space on right side, but vertical space completely used
            imageDisplayHeight_ = Math.min(getHeight() / numRows, (int)((baseImageWidth_)/imageRatio_) )- TOTAL_MARGIN;
            imageDisplayWidth_ = (int) (imageDisplayHeight_ * imageRatio_);
        } else {
            // horizontal space completely used
            numRows--;
            numColumns_ = (int)Math.ceil((double)numImages / (double)numRows);
            imageDisplayWidth_ = Math.min( getWidth() / numColumns_, baseImageWidth_) - TOTAL_MARGIN;
            imageDisplayHeight_ = (int) (imageDisplayWidth_ / imageRatio_);
        }

        Graphics2D g2 = (Graphics2D)g;
        int enlargedImageIndex = -1;

        g2.setStroke( BORDER_STROKE );

        for (int i = 0; i < images_.size();  i++ ) {

            int colPos = getColumnPosition(i) + IMAGE_MARGIN;
            int rowPos = getRowPosition(i)  + IMAGE_MARGIN;
            BufferedImage img =  images_.get(i);
            if (img.equals(highlightedImage_) && enlargeHighlightedImage_) {
                enlargedImageIndex = i;
            }
            g2.drawImage(img, colPos , rowPos,
                                    imageDisplayWidth_, imageDisplayHeight_, null);

            // put a border around images that are selected or highlighted
            if (highlightedImage_ == img || selectedImages_.contains(img)) {
                g2.setColor((highlightedImage_ == img) ? HIGHLIGHT_COLOR :SELECTION_COLOR);
                g2.drawRect(colPos- IMAGE_MARGIN, rowPos - IMAGE_MARGIN,
                                     imageDisplayWidth_ + TOTAL_MARGIN, imageDisplayHeight_ + TOTAL_MARGIN);
            }
        }

        if (enlargedImageIndex >= 0) {
            int w = highlightedImage_.getWidth();
            int h = highlightedImage_.getHeight();

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
        return row * ( imageDisplayHeight_ + TOTAL_MARGIN);
    }

    private int getColumnPosition(int i) {
        int col = i % numColumns_;
        return col * ( imageDisplayWidth_ + TOTAL_MARGIN);
    }


    /**
     * @return  the image that the mouse is currently over (at x, y coordinates)
     */
    private BufferedImage findImageOver(int x, int y) {

        if (images_ == null) return null;
        int selectedIndex = -1;
        for (int i = 0; i < images_.size(); i++ ) {
            int row = i / numColumns_;
            int col = i % numColumns_;
            int colPos = col * (imageDisplayWidth_ + TOTAL_MARGIN);
            int rowPos = row * (imageDisplayHeight_ + TOTAL_MARGIN);
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
                       @Override
                       public void run ()   {
                            enlargeHighlightedImage_ = true;
                            enlargementTimer.cancel ();
                            repaint();
                       }
                   }, TIME_TO_ENLARGE);
        }
    }


    public void mouseDragged(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}

    /**
     * An image was selected on release of the mouse click.
     */
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
            this.repaint();
        }

         updateImageSelectionListeners(img);
    }

    private void updateImageSelectionListeners(BufferedImage selectedImage) {
        // make sure listeners get notification that an image was selected.
        for (ImageSelectionListener isl : imgSelectionListeners_) {
                isl.imageSelected(selectedImage);
         }
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

