package com.becker.java2d.examples;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TransformersShear
        extends Transformers
{
    public static void main( String[] args )
    {
        Transformers t = new TransformersShear();
        Frame f = t.getFrame();
        f.setVisible( true );
    }

    public AffineTransform getTransform()
    {
        AffineTransform at = AffineTransform.getTranslateInstance( 150, 0 );
        at.shear( -.5, 0 );
        return at;
    }
}