package com.barrybecker4.java2d.examples.transform;

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