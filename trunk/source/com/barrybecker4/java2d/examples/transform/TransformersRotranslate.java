package com.barrybecker4.java2d.examples.transform;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TransformersRotranslate extends Transformers {

    public static void main( String[] args ) {
        Transformers t = new TransformersRotranslate();
        Frame f = t.getFrame();
        f.setVisible( true );
    }

    public AffineTransform getTransform() {
        AffineTransform at = new AffineTransform();
        at.setToRotation( Math.PI / 6 );
        at.translate( 100, 0 );
        return at;
    }
}