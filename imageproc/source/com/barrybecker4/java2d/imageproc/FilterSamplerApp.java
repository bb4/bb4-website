package com.barrybecker4.java2d.imageproc;

import com.barrybecker4.java2d.Utilities;
import com.barrybecker4.java2d.ui.SplitImageComponent;
import com.barrybecker4.optimization.parameter.ParameterChangeListener;
import com.barrybecker4.optimization.parameter.types.Parameter;
import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

/**
 * Allows you to test filters and modify their parameters on the fly.
 * Based on the Sampler program that comes with Java2D by Knudsen.
 */
public class FilterSamplerApp extends ApplicationFrame
                     implements ItemListener, ActionListener, ParameterChangeListener {

    private Frame imageFrame;
    private SplitImageComponent splitImageComponent;
    private ProcessingOperators operations;

    private Checkbox accumulateCheckbox;
    private Label statusLabel = new Label( "" );
    private ParameterPanel paramPanel;
    private List filterList;

    public FilterSamplerApp( String imageFile ) {
        super( "Filter Sampler" );
        operations = new ProcessingOperators();
        createImageFrame( imageFile );
        initializeUI();
    }

    /**
     * The image to be manipulated goes in a separate frame.
     * @param imageFile image to load
     */
    private void createImageFrame( String imageFile ) {

        splitImageComponent = new SplitImageComponent( imageFile );
        splitImageComponent.setPreferredSize(new Dimension(600, 700));
        splitImageComponent.setSplitX(40);

        imageFrame = new Frame( imageFile );
        imageFrame.setLayout(new BorderLayout());
        imageFrame.add(splitImageComponent, BorderLayout.CENTER);

        Utilities.sizeContainerToComponent(imageFrame, splitImageComponent);
        Utilities.centerFrame(imageFrame);
        imageFrame.setVisible(true);
    }

    @Override
    public void createUI() {
        super.createUI();
        setFont( new Font( "Serif", Font.PLAIN, 12 ) );
        setLayout( new BorderLayout() );
        // Set our location to the left of the image frame.
        this.setMinimumSize( new Dimension(300, 500 ));
        accumulateCheckbox = new Checkbox( "Accumulate", false );
        statusLabel = new Label( "" );
    }

    protected void initializeUI() {
        Point pt = imageFrame.getLocation();
        setLocation( pt.x - getSize().width, pt.y );

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

        // add placeholder param panel
        paramPanel = new ParameterPanel(null);
        add(paramPanel, BorderLayout.CENTER);
        add( filterList, BorderLayout.WEST );
        add( bottom, BorderLayout.SOUTH );
        this.pack();
    }

    /**
     * Called when an item in the list of transformations is called.
     * @param ie item event
     */
    @Override
    public void itemStateChanged( ItemEvent ie ) {

        if ( ie.getStateChange() != ItemEvent.SELECTED )
            return;
        String key = filterList.getSelectedItem();
        MetaImageOp metaOp = operations.getOperation( key );
        BufferedImageOp op = metaOp.getInstance();

        String previous = imageFrame.getTitle() + " + ";
        if (!accumulateCheckbox.getState())
            previous = "";
        imageFrame.setTitle(previous + key);
        statusLabel.setText( "Performing " + key + "..." );

        // don't allow doing anything while processing
        filterList.setEnabled( false );
        accumulateCheckbox.setEnabled( false );

        applyImageOperator(op);

        filterList.setEnabled( true );
        accumulateCheckbox.setEnabled( true );

        statusLabel.setText( "Performing " + key + "...done." );

        replaceParameterUI(metaOp);
    }

    private void replaceParameterUI(MetaImageOp metaOp) {
        // now show ui for modifying the parameters for this op
        this.remove(paramPanel);
        paramPanel = new ParameterPanel(metaOp.getBaseParameters());
        // We will get called whenever a paramerter is tweeked
        paramPanel.addParameterChangeListener(this);
        this.add(paramPanel, BorderLayout.CENTER);
        this.pack();
    }

    /**
     * Called whenever one of the UI parameter widgets was changed by the user.
     * @param param changed parameter
     */
    @Override
    public void parameterChanged(Parameter param) {
        // we could use param.getName() to get the filter, but its just the currently selected one.
        String key = filterList.getSelectedItem();
        MetaImageOp metaOp = operations.getOperation( key );
        BufferedImageOp op = metaOp.getInstance();
        applyImageOperator(op);
    }

    private void applyImageOperator(BufferedImageOp op) {
        BufferedImage source = splitImageComponent.getSecondImage();
        if ( source == null || !accumulateCheckbox.getState()) {
            source = splitImageComponent.getImage();
        }
        BufferedImage destination = op.filter( source, null );

        splitImageComponent.setSecondImage( destination );
        splitImageComponent.setSize(
                splitImageComponent.getPreferredSize());

        imageFrame.setSize(imageFrame.getPreferredSize());
    }

     /**
      * Called when the load button is pressed.
      * @param ae action event
      */
     @Override
     public void actionPerformed( ActionEvent ae ) {

        FileDialog fd = new FileDialog( FilterSamplerApp.this    );
        fd.setVisible(true);
        if ( fd.getFile() == null ) return;
        String path = fd.getDirectory() + fd.getFile();
        splitImageComponent.setImage(path);
        splitImageComponent.setSecondImage(null);
        Utilities.sizeContainerToComponent(
                imageFrame, splitImageComponent);
        imageFrame.validate();
        imageFrame.repaint();
    }

    public static void main( String[] args ) {
        String imageFile = Utilities.DEFAULT_IMAGE_DIR + "EtholWithRoses.small.jpg";
        if ( args.length > 0 )
            imageFile = args[0];
        new FilterSamplerApp( imageFile );
    }
}