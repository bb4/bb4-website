package com.becker.java2d.imageproc;

import com.becker.java2d.Utilities;
import com.becker.common.*;
import com.becker.ui.ApplicationFrame;
import com.becker.optimization.parameter.Parameter;

import com.becker.java2d.ui.ImageListPanel;
import com.becker.java2d.ui.ImageSelectionListener;
import com.becker.ui.sliders.LabeledSlider;
import com.becker.ui.sliders.SliderChangeListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.JButton;

/**
 * Allows you to mix filters together using a genetic algorithm
 * in order to produce very interesting results.
 * 
 * TODO:
 *  - Brian big hair took 106 seconds to run caustics
 */
public class FilterBreederApp extends ApplicationFrame 
                                     implements ItemListener, 
                                                         ActionListener, 
                                                         ImageSelectionListener,
                                                         SliderChangeListener
{
    
    private static int NUM_CHILD_IMAGES = 20;
    private static float DEFAULT_VARIANCE = 0.2f;
    private float variance = DEFAULT_VARIANCE;

    private BufferedImage currentImage;
    
    private Frame mImageListFrame;
    private ImageListPanel imageListPanel;
    private JButton loadButton;
    private JButton goButton;
    private Label statusLabel = new Label( "" );
    private ParameterPanel paramPanel;
    private java.awt.List filterList;
    
    private ProcessingOperators operations;
    private Map<BufferedImage, List<Parameter>> imgToParamsMap;

    public FilterBreederApp( String imageFile )
    {
        super( "Filter Breeder" );
        operations = new ProcessingOperators();
        createImageFrame( imageFile );       
        initializeUI();      
    }

    /**
     * The image to be manipulated goes in a separate frame.
     * @param imageFile
     */
    private void createImageFrame( String imageFile )
    {              
        currentImage = Utilities.getBufferedImage(imageFile);
                 
        // also create image list panel
        imageListPanel = new ImageListPanel();
        imageListPanel.setMaxNumSelections(2);
        imageListPanel.setPreferredSize(new Dimension(700, 300));
        imageListPanel.addImageSelectionListener(this);
                
        mImageListFrame = new Frame( imageFile );
        mImageListFrame.setLayout( new BorderLayout() );
        mImageListFrame.add( imageListPanel, BorderLayout.CENTER );
        
        Utilities.sizeContainerToComponent( mImageListFrame, imageListPanel );
        mImageListFrame.setVisible( true );          
    }

    @Override
    protected void createUI()
    {        
        super.createUI();
        setFont( new Font( "Serif", Font.PLAIN, 12 ) );
        setLayout( new BorderLayout() );
        // Set our location to the left of the image frame.
        this.setMinimumSize( new Dimension(300, 500 )); 
        statusLabel = new Label( "" );    
    }
    
    protected void initializeUI()
    {                
        filterList = operations.getSortedKeys();        
        // When an item is selected, do the corresponding transformation.
        filterList.addItemListener(this);
        // arbitrarily select the first one
        filterList.select(0);

        loadButton = new JButton( "Load..." );
        loadButton.addActionListener(this);
        
        LabeledSlider varianceSlider = new LabeledSlider("Variance" , DEFAULT_VARIANCE, 0.0, 0.5);
        varianceSlider.addChangeListener(this);
        
        goButton = new JButton( "Go" );
        goButton.addActionListener(this);

        Panel bottom = new Panel( new GridLayout( 2, 1 ) );
        Panel topBottom = new Panel();
        
        topBottom.add( loadButton );
        topBottom.add(varianceSlider);
        topBottom.add(goButton);
        
        bottom.add( topBottom );
        bottom.add( statusLabel );        
        
        // add placeholder param panel
        paramPanel = new ParameterPanel(null);
        add(paramPanel, BorderLayout.CENTER);
        add( filterList, BorderLayout.WEST );
        add( bottom, BorderLayout.SOUTH );
        this.pack();
        
        createImagesForSelectedFilter();
    }
    
    /**
     * Called when an item in the list of transformations is called.
     * @param ie
     */
    public void itemStateChanged( ItemEvent ie ) {
               
        if ( ie.getStateChange() != ItemEvent.SELECTED ) 
            return;
        String key = filterList.getSelectedItem();
        MetaImageOp metaOp = operations.getOperation( key );
       
        applyImageOperator(metaOp);              
        
        replaceParameterUI(metaOp);        
    }
    
    private void replaceParameterUI(MetaImageOp metaOp) {
        // now show ui for modifying the parameters for this op
        this.remove(paramPanel);
        paramPanel = new ParameterPanel(metaOp.getBaseParameters());
        // don't called whenever a paramerter is tweeked
        // paramPanel.addParameterChangeListener(this);
        this.add(paramPanel, BorderLayout.CENTER);
        this.pack();
    }

    
    private void createImagesForSelectedFilter() {
        // we could use param.getName() to get the filter, but its just the currently selected one.
        String key = filterList.getSelectedItem();
        MetaImageOp metaOp = operations.getOperation( key );
        applyImageOperator(metaOp);        
    }
    
    private void applyImageOperator(MetaImageOp metaOp)  {
 
        String key = filterList.getSelectedItem();
        statusLabel.setText( "Performing " + key + "..." );
        // create a bunch of child permutations and add them to the imageListPanel
        
        enableUI(false);
        long time = System.currentTimeMillis();
        
        FilterBreeder fb = new FilterBreeder(currentImage, metaOp, variance);
        List<BufferedImage> images = fb.breedImages(NUM_CHILD_IMAGES);
        imgToParamsMap = fb.getImgToParamsMap();         
       
        int elapsedTime =(int) ((System.currentTimeMillis() - time)/1000);
        statusLabel.setText( "Performing " + key + "...done in " + elapsedTime +" seconds" );
         
        imageListPanel.setImageList(images);     
        enableUI(true);
    }
          
    private void enableUI(boolean enable) {
        filterList.setEnabled( enable );
        goButton.setEnabled(enable);
        loadButton.setEnabled(enable);
    }
    
     /**
      * Called when the load button or go button is pressed.
      * @param ae
      */
     public void actionPerformed( ActionEvent ae ) {
         
         JButton button = (JButton)ae.getSource();
         if (button == goButton) {
             createImagesForSelectedFilter();
         }
         else if (button == loadButton) {
             FileDialog fd = new FileDialog( FilterBreederApp.this        );
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
     * @param evt
     */
    public void imageSelected(BufferedImage img) {
         List<Parameter> params = imgToParamsMap.get(img);
         //System.out.println("image selected params = " + params);
         paramPanel.updateParameters(params);
    }
             

    public static void main( String[] args )
    {
        String imageFile = Utilities.DEFAULT_IMAGE_DIR + "Ethol with Roses.small.jpg";
        if ( args.length > 0 )  {
            imageFile = args[0];
        }
        new FilterBreederApp( imageFile );
    }
}