package com.becker.java2d.examples;

import com.becker.java2d.examples.Transformers;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TransformersTranslation
        extends Transformers
{
    public static void main( String[] args )
    {
        Transformers t = new TransformersTranslation();
        Frame f = t.getFrame();
        f.setVisible( true );
    }

    public AffineTransform getTransform()
    {
        return AffineTransform.getTranslateInstance( 150, 0 );
    }
}