package com.becker.java2d.examples;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TransformersTransrotate
        extends Transformers
{
    public static void main( String[] args )
    {
        Transformers t = new TransformersTransrotate();
        Frame f = t.getFrame();
        f.setVisible( true );
    }

    public AffineTransform getTransform()
    {
        AffineTransform at = new AffineTransform();
        at.setToTranslation( 100, 0 );
        at.rotate( Math.PI / 6 );
        return at;
    }
}