package com.barrybecker4.java2d.examples.transform;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TransformersRotation extends Transformers {

    public static void main( String[] args ) {
        Transformers t = new TransformersRotation();
        Frame f = t.getFrame();
        f.setVisible( true );
    }

    @Override
    public AffineTransform getTransform() {
        return AffineTransform.getRotateInstance( -Math.PI / 6 );
    }
}