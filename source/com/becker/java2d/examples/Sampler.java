package com.becker.java2d.examples;

import com.becker.java2d.SplitImageComponent;
import com.becker.java2d.Utilities;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.*;

public class Sampler
        extends Frame
{
    private Frame mImageFrame;
    private SplitImageComponent mSplitImageComponent;
    private Hashtable mOps;

    public static void main( String[] args )
    {
        String imageFile = "images/Ethol with Roses.small.jpg";
        if ( args.length > 0 ) imageFile = args[0];
        new Sampler( imageFile );
    }

    public Sampler( String imageFile )
    {
        super( "Sampler v1.0" );
        createOps();
        createImageFrame( imageFile );
        createUI();
        setVisible( true );
    }

    private void createOps()
    {
        mOps = new Hashtable();
        createConvolutions();
        createTransformations();
        createLookups();
        createRescales();
        createColorOps();
    }

    private void createConvolutions()
    {
        float ninth = 1.0f / 9.0f;
        float[] blurKernel = {
            ninth, ninth, ninth,
            ninth, ninth, ninth,
            ninth, ninth, ninth
        };
        mOps.put( "Blur", new ConvolveOp(
                new Kernel( 3, 3, blurKernel ),
                ConvolveOp.EDGE_NO_OP, null ) );

        float[] edge = {
            0f, -1f, 0f,
            -1f, 4f, -1f,
            0f, -1f, 0f
        };
        mOps.put( "Edge detector", new ConvolveOp(
                new Kernel( 3, 3, edge ),
                ConvolveOp.EDGE_NO_OP, null ) );

        float[] sharp = {
            0f, -1f, 0f,
            -1f, 5f, -1f,
            0f, -1f, 0f
        };
        mOps.put( "Sharpen", new ConvolveOp(
                new Kernel( 3, 3, sharp ) ) );
    }

    private void createTransformations()
    {
        AffineTransform at;
        at = AffineTransform.getRotateInstance( Math.PI / 6, 0, 285 );
        mOps.put( "Rotate nearest neighbor", new AffineTransformOp( at, null ) );

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        mOps.put( "Rotate bilinear", new AffineTransformOp( at, rh ) );

        at = AffineTransform.getScaleInstance( .5, .5 );
        mOps.put( "Scale .5, .5", new AffineTransformOp( at, null ) );

        at = AffineTransform.getRotateInstance( Math.PI / 6 );
        mOps.put( "Rotate bilinear (origin)", new AffineTransformOp( at, rh ) );
    }

    private void createLookups()
    {
        short[] brighten = new short[256];
        short[] betterBrighten = new short[256];
        short[] posterize = new short[256];
        short[] invert = new short[256];
        short[] straight = new short[256];
        short[] zero = new short[256];
        for ( int i = 0; i < 256; i++ ) {
            brighten[i] = (short) (128 + i / 2);
            betterBrighten[i] = (short) (Math.sqrt( (double) i / 255.0 ) * 255.0);
            posterize[i] = (short) (i - (i % 32));
            invert[i] = (short) (255 - i);
            straight[i] = (short) i;
            zero[i] = (short) 0;
        }
        mOps.put( "Brighten", new LookupOp( new ShortLookupTable( 0, brighten ),
                null ) );
        mOps.put( "Better Brighten", new LookupOp(
                new ShortLookupTable( 0, betterBrighten ), null ) );
        mOps.put( "Posterize", new LookupOp(
                new ShortLookupTable( 0, posterize ), null ) );
        mOps.put( "Invert", new LookupOp( new ShortLookupTable( 0, invert ), null ) );

        short[][] redOnly = {invert, straight, straight};
        short[][] greenOnly = {straight, invert, straight};
        short[][] blueOnly = {straight, straight, invert};
        mOps.put( "Red invert", new LookupOp( new ShortLookupTable( 0, redOnly ),
                null ) );
        mOps.put( "Green invert", new LookupOp(
                new ShortLookupTable( 0, greenOnly ), null ) );
        mOps.put( "Blue invert", new LookupOp(
                new ShortLookupTable( 0, blueOnly ), null ) );

        short[][] redRemove = {zero, straight, straight};
        short[][] greenRemove = {straight, zero, straight};
        short[][] blueRemove = {straight, straight, zero};
        mOps.put( "Red remove", new LookupOp(
                new ShortLookupTable( 0, redRemove ), null ) );
        mOps.put( "Green remove", new LookupOp(
                new ShortLookupTable( 0, greenRemove ), null ) );
        mOps.put( "Blue remove", new LookupOp(
                new ShortLookupTable( 0, blueRemove ), null ) );
    }

    private void createRescales()
    {
        mOps.put( "Rescale .5, 0", new RescaleOp( .5f, 0, null ) );
        mOps.put( "Rescale .5, 64", new RescaleOp( .5f, 64, null ) );
        mOps.put( "Rescale 1.2, 0", new RescaleOp( 1.2f, 0, null ) );
        mOps.put( "Rescale 1.5, 0", new RescaleOp( 1.5f, 0, null ) );
    }

    private void createColorOps()
    {
        mOps.put( "Grayscale", new ColorConvertOp(
                ColorSpace.getInstance( ColorSpace.CS_GRAY ), null ) );
    }

    private void createImageFrame( String imageFile )
    {
        // Create the image frame.
        mSplitImageComponent = new SplitImageComponent( imageFile );
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

        // Make a sorted list of the operators.
        Enumeration e = mOps.keys();
        Vector names = new Vector();
        while ( e.hasMoreElements() )
            names.addElement( e.nextElement() );
        Collections.sort( names );
        final java.awt.List list = new java.awt.List();
        for ( int i = 0; i < names.size(); i++ )
            list.add( (String) names.elementAt( i ) );
        add( list, BorderLayout.CENTER );

        // When an item is selected, do the corresponding transformation.
        list.addItemListener( new ItemListener()
        {
            public void itemStateChanged( ItemEvent ie )
            {
                if ( ie.getStateChange() != ItemEvent.SELECTED ) return;
                String key = list.getSelectedItem();
                BufferedImageOp op = (BufferedImageOp) mOps.get( key );
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