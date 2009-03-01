package com.becker.apps.imagebreeder;

import com.becker.java2d.Utilities;
import com.becker.java2d.imageproc.ParameterPanel;
import com.becker.java2d.imageproc.ProcessingOperators;
import com.becker.java2d.imageproc.MetaImageOp;
import com.becker.ui.GUIUtil;
import com.becker.ui.ApplicationApplet;
import com.becker.optimization.parameter.Parameter;

import com.becker.java2d.ui.ImageListPanel;
import com.becker.java2d.ui.ImageSelectionListener;
import com.becker.ui.sliders.LabeledSlider;
import com.becker.ui.sliders.SliderChangeListener;
import com.becker.common.util.ImageUtil;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * Allows you to mix filters together using a genetic algorithm
 * in order to produce very interesting results.
 *
 * TODO:
 *  - Brian big hair took 106 seconds to run caustics
 */
public class ImageBreederApplet extends ApplicationApplet
                                implements ItemListener,
                                            ActionListener,
                                            ImageSelectionListener,
                                            SliderChangeListener
{
    private static int NUM_CHILD_IMAGES = 20;
    private static float DEFAULT_VARIANCE = 0.2f;
    private float variance = DEFAULT_VARIANCE;
    private static final String IMAGE_DIR = "com/becker/apps/imagebreeder/ui/images/";

    private static final String DEFAULT_IMAGE = "brian_in_surf1_big_hair_smaller.jpg";

    private BufferedImage currentImage;

    private ImageListPanel imageListPanel;
    private JButton loadButton;
    private JButton newGenerationButton;
    private JButton lastGenerationButton;

    private Label statusLabel = new Label( "" );
    private JPanel mainPanel;
    private ParameterPanel paramPanel;
    private java.awt.List filterList;

    private ProcessingOperators operations;
    private Map<BufferedImage, List<Parameter>> imgToParamsMap;
    private Map<BufferedImage, List<Parameter>> lastImgToParamsMap;
    private List<BufferedImage> lastImages;
    private List<Integer> lastSelectedIndices;
    private int lastSelectedFilterIndex;
    private int currentSelectedFilterIndex;
    private int generationCountForFilter = 0;

    /**
     * constructor
     */
    public ImageBreederApplet()
    {
        System.out.println("empty constructor for ImageBreeder");
        GUIUtil.setStandAlone(true);
        operations = new ProcessingOperators();
        createImageFrame(IMAGE_DIR + DEFAULT_IMAGE);
    }

    /**
     * constructor
     * @param imageFile
     */
    public ImageBreederApplet( String imageFile )
    {
        operations = new ProcessingOperators();
        createImageFrame( imageFile );
    }

    /**
     * The generated images are shown in a separate window.
     * @param imageFile
     */
    private void createImageFrame( String imageFile )
    {
        ImageIcon img = GUIUtil.getIcon(imageFile);
        currentImage = ImageUtil.makeBufferedImage(img.getImage());
        //Utilities.getBufferedImage(imageFile);

        // also create image list panel
        imageListPanel = new ImageListPanel();
        imageListPanel.setMaxNumSelections(2);
        imageListPanel.setPreferredSize(new Dimension(700, 300));
        imageListPanel.addImageSelectionListener(this);

        Frame mImageListFrame=new Frame( imageFile );
        mImageListFrame.setLayout( new BorderLayout() );
        mImageListFrame.add( imageListPanel, BorderLayout.CENTER );

        Utilities.sizeContainerToComponent( mImageListFrame, imageListPanel );
        mImageListFrame.setVisible( true );
    }

    protected JPanel createMainPanel()
    {
        filterList = operations.getSortedKeys();
        // When an item is selected, do the corresponding transformation.
        filterList.addItemListener(this);
        // arbitrarily select the first one
        filterList.select(0);
        lastSelectedFilterIndex = filterList.getSelectedIndex();
        currentSelectedFilterIndex = lastSelectedFilterIndex;

        mainPanel = new JPanel(new BorderLayout());

        // add placeholder param panel
        paramPanel = new ParameterPanel(null);
        mainPanel.add(paramPanel, BorderLayout.CENTER);
        mainPanel.add( filterList, BorderLayout.WEST );
        mainPanel.add( createBottomControls(), BorderLayout.SOUTH );

        createImagesForSelectedFilter();

        return mainPanel;
    }

    public Dimension getSize() {
        return new Dimension(400, 500);
    }

    private JPanel createBottomControls() {

        loadButton = new JButton( "Load..." );
        loadButton.addActionListener(this);

        LabeledSlider varianceSlider = new LabeledSlider("Variance" , DEFAULT_VARIANCE, 0.0, 0.5);
        varianceSlider.addChangeListener(this);

        newGenerationButton = new JButton( "New Generation" );
        newGenerationButton.setToolTipText("Create a new generation of images using the current selected image as a parent.");
        newGenerationButton.addActionListener(this);

        lastGenerationButton = new JButton( "Last Generation" );
        newGenerationButton.setToolTipText("Go back to the last generation of images that were based on the current selectoion's parent.");
        lastGenerationButton.addActionListener(this);
        // initially there is nothing to go back to.
        lastGenerationButton.setEnabled(false);

        JPanel controls = new JPanel(new BorderLayout());

        JPanel topControls = new JPanel( new GridLayout( 2, 1 ) );
        JPanel top = new JPanel();
        JPanel middle = new JPanel();

        top.add( loadButton );
        top.add(varianceSlider);

        middle.add(newGenerationButton);
        middle.add(lastGenerationButton);
        controls.setPreferredSize(new Dimension(300, 120));

        topControls.add( top );
        topControls.add( middle );

        controls.add( topControls, BorderLayout.WEST);
        controls.add( statusLabel , BorderLayout.SOUTH);

        return controls;
    }

    private JPanel fill() {
        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(1000, 10));
        return fill;
    }

    /**
     * Called when an item in the list of transformations is called.
     * @param ie
     */
    public void itemStateChanged( ItemEvent ie ) {

        if ( ie.getStateChange() != ItemEvent.SELECTED )
            return;
        if ( filterList.getSelectedIndex() != lastSelectedFilterIndex) {
            lastSelectedFilterIndex = currentSelectedFilterIndex;
            currentSelectedFilterIndex =  filterList.getSelectedIndex();
            generationCountForFilter = 0;
            updateParameterUI(true);
        }
    }

    private void updateParameterUI(boolean recalc) {
        String key = filterList.getSelectedItem();
        MetaImageOp metaOp = operations.getOperation( key );
        replaceParameterUI(metaOp);
        if (recalc) {
            applyImageOperator(metaOp);
        }
    }

    private void replaceParameterUI(MetaImageOp metaOp) {
        // now show ui for modifying the parameters for this op
        mainPanel.remove(paramPanel);
        paramPanel = new ParameterPanel(metaOp.getBaseParameters());
        // don't called whenever a parameter is tweeked
        // paramPanel.addParameterChangeListener(this);
        mainPanel.add(paramPanel, BorderLayout.CENTER);
        mainPanel.doLayout(); //doLayout(); //pack();
        mainPanel.validate();
        mainPanel.repaint();
    }

    private void createImagesForSelectedFilter() {
        // we could use param.getName() to get the filter, but its just the currently selected one.
        String key = filterList.getSelectedItem();
        MetaImageOp metaOp = operations.getOperation( key );
        applyImageOperator(metaOp);
    }

    private void restoreLastGeneration() {
        if (lastSelectedFilterIndex != filterList.getSelectedIndex() && generationCountForFilter <= 1) {
            filterList.select(lastSelectedFilterIndex);
            updateParameterUI(false);
        }
        imgToParamsMap = lastImgToParamsMap;
        imageListPanel.setImageList(lastImages);
        imageListPanel.setSelectedImageIndices(lastSelectedIndices);
        lastImgToParamsMap = null;
        lastImages = null;
        lastGenerationButton.setEnabled(false);
    }

    private void applyImageOperator(MetaImageOp metaOp)  {

        String key = filterList.getSelectedItem();
        statusLabel.setText( "Performing " + key + "..." );
        // create a bunch of child permutations and add them to the imageListPanel

        lastImgToParamsMap = imgToParamsMap;
        lastImages = imageListPanel.getImageList();
        lastSelectedIndices = imageListPanel.getSelectedImageIndices();
        generationCountForFilter++;

        enableUI(false);
        long time = System.currentTimeMillis();

        ImageBreeder fb = new ImageBreeder(currentImage, metaOp, variance);
        List<BufferedImage> images = fb.breedImages(NUM_CHILD_IMAGES);
        imgToParamsMap = fb.getImgToParamsMap();

        int elapsedTime =(int) ((System.currentTimeMillis() - time)/1000);
        statusLabel.setText( "Performing " + key + "...done in " + elapsedTime +" seconds" );

        imageListPanel.setImageList(images);

        enableUI(true);
    }

    private void enableUI(boolean enable) {
        filterList.setEnabled( enable );
        newGenerationButton.setEnabled(enable);
        loadButton.setEnabled(enable);
        if (lastImages!= null) {
            lastGenerationButton.setEnabled(enable);
        }
    }

     /**
      * Called when the load button or go button is pressed.
      * @param ae
      */
     public void actionPerformed( ActionEvent ae ) {

         JButton button = (JButton)ae.getSource();
         if (button == newGenerationButton) {
             createImagesForSelectedFilter();
         }
         else if (button == lastGenerationButton) {
             restoreLastGeneration();
         }
         else if (button == loadButton) {
             Frame f = null;
             FileDialog fd = new FileDialog( f  );
             fd.setVisible(true);
             if ( fd.getFile() == null ) return;
             String path = fd.getDirectory() + fd.getFile();

             currentImage = Utilities.getBufferedImage( path );
             createImagesForSelectedFilter();
         } else {
             assert false : "unexpected source: "+button.getText();
         }
    }

    public void sliderChanged(LabeledSlider slider) {
         variance = (float)slider.getValue();
    }

    /**
     * Make the parameters setting smatch the last selected image.
     * @param img
     */
    public void imageSelected(BufferedImage img) {
         List<Parameter> params = imgToParamsMap.get(img);
         assert(params != null);
         //System.out.println("image selected params = " + params);
         paramPanel.updateParameters(params);
    }


    public static void main( String[] args )
    {
        String imageFile = IMAGE_DIR + DEFAULT_IMAGE;
        if ( args.length > 0 && !args[0].equals("imagebreeder") )  {
            imageFile = args[0];
        }

        ImageBreederApplet breeder = new ImageBreederApplet(imageFile);
        GUIUtil.showApplet( breeder, "Image Breeder");
    }
}