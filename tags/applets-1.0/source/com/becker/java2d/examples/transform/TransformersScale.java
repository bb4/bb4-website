package com.becker.java2d.examples.transform;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TransformersScale
        extends Transformers {

    public static void main( String[] args ) {
        Transformers t = new TransformersScale();
        Frame f = t.getFrame();
        f.setVisible( true );
    }

    public AffineTransform getTransform() {
        return AffineTransform.getScaleInstance( 3, 3 );
    }
}