package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;
import com.becker.java2d.Utilities;
import com.becker.java2d.SplitImageComponent;

import java.awt.*;
import java.awt.image.*;

public class Bandito
{
    public static void main( String[] args )
    {
        // Create a frame window to hold everything.
        ApplicationFrame f = new ApplicationFrame( "Bandito v1.0" );
        // Create a SplitImageComponent with the source image.
        String filename = Utilities.DEFAULT_IMAGE_DIR+"Ethol with Roses.small.jpg";
        //String filename = "Raphael.jpg";
        SplitImageComponent sic = new SplitImageComponent( filename );

        // Create a BandCombineOp.
        float[][] matrix = {
            {-1, 0, 0, 255},
            {0, 1, 0, 0},
            {0, 0, 1, 0}
        };
        BandCombineOp op = new BandCombineOp( matrix, null );

        // Process the source image raster.
        BufferedImage sourceImage = sic.getImage();
        Raster source = sourceImage.getRaster();
        WritableRaster destination = op.filter( source, null );

        // Create a destination image using the processed
        //   raster and the same color model as the source image.
        BufferedImage destinationImage = new BufferedImage(
                sourceImage.getColorModel(), destination, false, null );
        sic.setSecondImage( destinationImage );

        // Set up the frame window.
        f.getContentPane().setLayout( new BorderLayout() );
        f.getContentPane().add( sic, BorderLayout.CENTER );
        f.setSize( f.getPreferredSize() );
        f.center();
        f.setVisible( true );
    }
}