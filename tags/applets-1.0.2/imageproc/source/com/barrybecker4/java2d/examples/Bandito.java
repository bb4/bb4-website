package com.barrybecker4.java2d.examples;

import com.barrybecker4.java2d.Utilities;
import com.barrybecker4.java2d.ui.SplitImageComponent;
import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class Bandito {

    public static void main( String[] args ) {
        // Create a frame window to hold everything.
        ApplicationFrame f = new ApplicationFrame( "Bandito v1.0" );
        // Create a SplitImageComponent with the source image.
        String filename = Utilities.DEFAULT_IMAGE_DIR + "EtholWithRoses.small.jpg";
        SplitImageComponent sic = new SplitImageComponent( filename );

        // Create a BandCombineOp.
        float[][] matrix = {
            {-1, 0, 0, 0, 255},
            {0, 1, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 1, 0}
        };

        BandCombineOp op = new BandCombineOp( matrix, null );

        // Process the source image raster.
        BufferedImage sourceImage = sic.getImage();
        Raster source = sourceImage.getRaster();
        System.out.println("source numbands="+ source.getNumBands());
        WritableRaster destination = op.filter( source, null );

        // Create a destination image using the processed
        //   raster and the same color model as the source image.
        BufferedImage destinationImage = new BufferedImage(
                sourceImage.getColorModel(), destination, false, null );
        sic.setSecondImage( destinationImage );

        // Set up the frame window.
        f.getContentPane().setLayout( new BorderLayout() );
        f.getContentPane().add( sic, BorderLayout.CENTER );
    }

    private Bandito() {}
}