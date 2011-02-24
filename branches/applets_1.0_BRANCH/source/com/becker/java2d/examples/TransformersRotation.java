package com.becker.java2d.examples;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TransformersRotation
        extends Transformers
{
    public static void main( String[] args )
    {
        Transformers t = new TransformersRotation();
        Frame f = t.getFrame();
        f.setVisible( true );
    }

    public AffineTransform getTransform()
    {
        return AffineTransform.getRotateInstance( -Math.PI / 6 );
    }
}