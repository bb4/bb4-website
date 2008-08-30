package com.becker.java2d.imageproc;

import com.becker.java2d.SplitImageComponent;
import com.becker.java2d.Utilities;
import com.becker.common.*;
import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class Sampler extends ApplicationFrame 
                                   implements ItemListener, ActionListener, ParameterChangeListener
{
    private Frame mImageFrame;
    private SplitImageComponent mSplitImageComponent;
    private ProcessingOperators operations;
    
    private Checkbox accumulateCheckbox;
    private Label statusLabel = new Label( "" );
    private ParameterPanel paramPanel;
    private java.awt.List filterList;

    public Sampler( String imageFile )
    {
        super( "Sampler v1.0" );
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
        // Create the image frame.
        mSplitImageComponent = new SplitImageComponent( imageFile );
        mSplitImageComponent.setPreferredSize(new Dimension(600, 700));
        mImageFrame = new Frame( imageFile );
        mImageFrame.setLayout( new BorderLayout() );
        mImageFrame.add( mSplitImageComponent, BorderLayout.CENTER );

        Utilities.sizeContainerToComponent( mImageFrame, mSplitImageComponent );
        Utilities.centerFrame( mImageFrame );
        mImageFrame.setVisible( true );
    }

    @Override
    protected void createUI()
    {        
        super.createUI();
        setFont( new Font( "Serif", Font.PLAIN, 12 ) );
        setLayout( new BorderLayout() );
        // Set our location to the left of the image frame.
        this.setPreferredSize( new Dimension(300, 350 )); 
        accumulateCheckbox = new Checkbox( "Accumulate", false );
        statusLabel = new Label( "" );    
    }
    
    protected void initializeUI()
    {                
        Point pt = mImageFrame.getLocation();
        setLocation( pt.x - getSize().width, pt.y );

        filterList = operations.getSortedKeys();
        add( filterList, BorderLayout.CENTER );

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
        add( bottom, BorderLayout.SOUTH );
        
        // add placeholder param paner
        paramPanel = new ParameterPanel(null);
        add(paramPanel, BorderLayout.EAST);
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
        BufferedImageOp op = metaOp.getInstance();
                       
        String previous = mImageFrame.getTitle() + " + ";
        if ( accumulateCheckbox.getState() == false )
            previous = "";
        mImageFrame.setTitle( previous + key );
        statusLabel.setText( "Performing " + key + "..." );
        
        // don't allow doing anything while processing
        filterList.setEnabled( false );
        accumulateCheckbox.setEnabled( false );
        
        applyImageOperator(op);        
        
        filterList.setEnabled( true );
        accumulateCheckbox.setEnabled( true );
        
        statusLabel.setText( "Performing " + key + "...done." );
        
        // now show ui for modifying the parameters for this op
        this.remove(paramPanel);
        paramPanel = new ParameterPanel(metaOp.getParameters());
        // We will get called whenever a paramerter is tweeked
        paramPanel.addParameterChangeListener(this);
        this.add(paramPanel, BorderLayout.EAST);
        this.pack();
    }
    
    public void parameterChanged()
    {
        String key = filterList.getSelectedItem();
        MetaImageOp metaOp = operations.getOperation( key );
        BufferedImageOp op = metaOp.getInstance();
        applyImageOperator(op);        
    }
    
    private void applyImageOperator(BufferedImageOp op) {
        BufferedImage source = mSplitImageComponent.getSecondImage();
        if ( source == null || accumulateCheckbox.getState() == false )
            source = mSplitImageComponent.getImage();
        BufferedImage destination = op.filter( source, null );
        mSplitImageComponent.setSecondImage( destination );
        mSplitImageComponent.setSize(
                mSplitImageComponent.getPreferredSize() );        
        mImageFrame.setSize( mImageFrame.getPreferredSize() );
    }
    
     /**
      * Called when the load button is pressed.
      * @param ae
      */
     public void actionPerformed( ActionEvent ae ) {
         
        FileDialog fd = new FileDialog( Sampler.this );
        fd.setVisible(true);
        if ( fd.getFile() == null ) return;
        String path = fd.getDirectory() + fd.getFile();
        mSplitImageComponent.setImage( path );
        mSplitImageComponent.setSecondImage( null );
        Utilities.sizeContainerToComponent(
                mImageFrame, mSplitImageComponent );
        mImageFrame.validate();
        mImageFrame.repaint();
    }

    public static void main( String[] args )
    {
        String imageFile = Utilities.DEFAULT_IMAGE_DIR + "Ethol with Roses.small.jpg";
        if ( args.length > 0 ) 
            imageFile = args[0];
        new Sampler( imageFile );
    }
}