package com.becker.java2d.imageproc;

import com.becker.optimization.parameter.ParameterChangeListener;
import com.becker.java2d.Utilities;
import com.becker.common.*;
import com.becker.ui.ApplicationFrame;
import com.becker.optimization.parameter.Parameter;

import com.becker.ui.ImageListPanel;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
 * Allows you to mix filters together using a genetic algorithm
 * in order to produce very interesting results.
 * 
 * TODO:
 *  - perfect candidate for parallelization
 *    - Brian big hair took 106 seconds to run caustics
 *  - mouse over to see full image or Min of image and (1000*1000)
 *  - do single parent exploration by clicking on images.
 */
public class FilterBreeder extends ApplicationFrame 
                                   implements ItemListener, ActionListener, ParameterChangeListener
{
    private static int NUM_CHILD_IMAGES = 20;
    
    private Frame mImageListFrame;
    private ImageListPanel imageListPanel;
    private BufferedImage currentImage;
    
    private ProcessingOperators operations;
    
    private Checkbox accumulateCheckbox;
    private Label statusLabel = new Label( "" );
    private ParameterPanel paramPanel;
    private java.awt.List filterList;
    
    private Parallelizer parallelizer = new Parallelizer();
    
    private List<BufferedImage> images = 
                Collections.synchronizedList(new ArrayList<BufferedImage>(NUM_CHILD_IMAGES));
        

    public FilterBreeder( String imageFile )
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
        System.out.println("path="+imageFile);
        currentImage = getBufferedImage(imageFile);
                 
        // also create image list panel
        imageListPanel = new ImageListPanel();
        imageListPanel.setMaxNumSelections(2);
        imageListPanel.setPreferredSize(new Dimension(700, 300));
        mImageListFrame = new Frame( imageFile );
        mImageListFrame.setLayout( new BorderLayout() );
        mImageListFrame.add( imageListPanel, BorderLayout.CENTER );
        
        Utilities.sizeContainerToComponent( mImageListFrame, imageListPanel );
        //Utilities.centerFrame( mImageListFrame );
        mImageListFrame.setVisible( true );          
    }
    
    private BufferedImage getBufferedImage(String path) {
        Image image = Utilities.blockingLoad( path );
        return  Utilities.makeBufferedImage( image );
    }

    @Override
    protected void createUI()
    {        
        super.createUI();
        setFont( new Font( "Serif", Font.PLAIN, 12 ) );
        setLayout( new BorderLayout() );
        // Set our location to the left of the image frame.
        this.setMinimumSize( new Dimension(300, 500 )); 
        accumulateCheckbox = new Checkbox( "Accumulate", false );
        statusLabel = new Label( "" );    
    }
    
    protected void initializeUI()
    {                
        filterList = operations.getSortedKeys();        
        // When an item is selected, do the corresponding transformation.
        filterList.addItemListener(this);

        Button loadButton = new Button( "Load..." );
        loadButton.addActionListener(this);

        Panel bottom = new Panel( new GridLayout( 2, 1 ) );
        Panel topBottom = new Panel();
        topBottom.add( accumulateCheckbox );
        topBottom.add( loadButton );
        bottom.add( topBottom );
        bottom.add( statusLabel );        
        
        // add placeholder param paner
        paramPanel = new ParameterPanel(null);
        add(paramPanel, BorderLayout.CENTER);
        add( filterList, BorderLayout.WEST );
        add( bottom, BorderLayout.SOUTH );
        this.pack();
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
       
        statusLabel.setText( "Performing " + key + "..." );
        
        // don't allow doing anything while processing
        filterList.setEnabled( false );
        accumulateCheckbox.setEnabled( false );
        
        long time = System.currentTimeMillis();
        applyImageOperator(metaOp);      
        int elapsedTime =(int) ((System.currentTimeMillis() - time)/1000);
        
        filterList.setEnabled( true );
        accumulateCheckbox.setEnabled( true );
        
        statusLabel.setText( "Performing " + key + "...done in " + elapsedTime +" seconds" );
        
        replaceParameterUI(metaOp);        
    }
    
    private void replaceParameterUI(MetaImageOp metaOp) {
        // now show ui for modifying the parameters for this op
        this.remove(paramPanel);
        paramPanel = new ParameterPanel(metaOp.getParameters());
        // We will get called whenever a paramerter is tweeked
        paramPanel.addParameterChangeListener(this);
        this.add(paramPanel, BorderLayout.CENTER);
        this.pack();
    }
    
    /**
     * Called whenever one of the UI parameter widgets was changed by the user.
     * @param param
     */
    public void parameterChanged(Parameter param)
    {
        // we could use param.getName() to get the filter, but its just the currently selected one.
        String key = filterList.getSelectedItem();
        MetaImageOp metaOp = operations.getOperation( key );
        //BufferedImageOp op = metaOp.getInstance();
        applyImageOperator(metaOp);        
    }
    
    private void applyImageOperator(MetaImageOp metaOp) {
 
        // create a bunch of child permutations and add them to the imageListPanel
        images.clear();
        
        List<Runnable> filterTasks = new ArrayList<Runnable>(NUM_CHILD_IMAGES);
        for (int i=0; i<NUM_CHILD_IMAGES; i++) {
            filterTasks.add(new Worker(metaOp));           
        }        
        parallelizer.invokeAll(filterTasks);
         
        imageListPanel.setImageList(images);         
    }
          
    
     /**
      * Called when the load button is pressed.
      * @param ae
      */
     public void actionPerformed( ActionEvent ae ) {
         
        FileDialog fd = new FileDialog( FilterBreeder.this    );
        fd.setVisible(true);
        if ( fd.getFile() == null ) return;
        String path = fd.getDirectory() + fd.getFile();
        
        currentImage = getBufferedImage( path );     
    }

    public static void main( String[] args )
    {
        String imageFile = Utilities.DEFAULT_IMAGE_DIR + "Ethol with Roses.small.jpg";
        if ( args.length > 0 ) 
            imageFile = args[0];
        new FilterBreeder( imageFile );
    }
    
    /**
     * Runs one of the chunks.
     */
    private class Worker implements Runnable {

        private MetaImageOp metaOp;
        
        public Worker(MetaImageOp metaOp) {
           this.metaOp = metaOp;
        }
        
        public void run() {            
            images.add(metaOp.getRandomInstance(0.2f).filter( currentImage, null ));            
        }
    }
}