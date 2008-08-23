package com.becker.java2d.imageproc;

import com.becker.common.util.FileUtil;
import com.becker.java2d.SplitImageComponent;
import com.becker.java2d.Utilities;
import com.becker.common.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class Sampler extends Frame
{
    private Frame mImageFrame;
    private SplitImageComponent mSplitImageComponent;
    private ProcessingOperators operations;


    public static void main( String[] args )
    {
        String imageFile = FileUtil.PROJECT_DIR + "source/com/becker/java2d/images/Ethol with Roses.small.jpg";
        if ( args.length > 0 ) imageFile = args[0];
        new Sampler( imageFile );
    }

    public Sampler( String imageFile )
    {
        super( "Sampler v1.0" );
        operations = new ProcessingOperators();
        createImageFrame( imageFile );
        createUI();
        setVisible( true );
    }


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

    private void createUI()
    {
        setFont( new Font( "Serif", Font.PLAIN, 12 ) );
        setLayout( new BorderLayout() );
        // Set our location to the left of the image frame.
        setSize( 200, 350 );
        Point pt = mImageFrame.getLocation();
        setLocation( pt.x - getSize().width, pt.y );

        final Checkbox accumulateCheckbox = new Checkbox( "Accumulate", false );
        final Label statusLabel = new Label( "" );

        final java.awt.List list = operations.getSortedKeys();
        add( list, BorderLayout.CENTER );

        // When an item is selected, do the corresponding transformation.
        list.addItemListener( new ItemListener()
        {
            public void itemStateChanged( ItemEvent ie )
            {
                if ( ie.getStateChange() != ItemEvent.SELECTED ) return;
                String key = list.getSelectedItem();
                BufferedImageOp op = operations.getOperation( key ).getInstance();
                BufferedImage source = mSplitImageComponent.getSecondImage();
                boolean accumulate = accumulateCheckbox.getState();
                if ( source == null || accumulate == false )
                    source = mSplitImageComponent.getImage();
                String previous = mImageFrame.getTitle() + " + ";
                if ( accumulate == false )
                    previous = "";
                mImageFrame.setTitle( previous + key );
                statusLabel.setText( "Performing " + key + "..." );
                list.setEnabled( false );
                accumulateCheckbox.setEnabled( false );
                BufferedImage destination = op.filter( source, null );
                mSplitImageComponent.setSecondImage( destination );
                mSplitImageComponent.setSize(
                        mSplitImageComponent.getPreferredSize() );
                mImageFrame.setSize( mImageFrame.getPreferredSize() );
                list.setEnabled( true );
                accumulateCheckbox.setEnabled( true );
                statusLabel.setText( "Performing " + key + "...done." );
            }
        } );

        Button loadButton = new Button( "Load..." );
        loadButton.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent ae )
            {
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
        } );

        Panel bottom = new Panel( new GridLayout( 2, 1 ) );
        Panel topBottom = new Panel();
        topBottom.add( accumulateCheckbox );
        topBottom.add( loadButton );
        bottom.add( topBottom );
        bottom.add( statusLabel );
        add( bottom, BorderLayout.SOUTH );

        addWindowListener( new WindowAdapter()
        {
            public void windowClosing( WindowEvent e )
            {
                mImageFrame.dispose();
                dispose();
                System.exit( 0 );
            }
        } );
    }
}