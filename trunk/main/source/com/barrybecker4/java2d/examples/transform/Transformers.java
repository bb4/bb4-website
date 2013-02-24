package com.barrybecker4.java2d.examples.transform;

import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public abstract class Transformers extends Component {
    Shape mAxes, mShape;
    int mLength = 54, mArrowLength = 4, mTickSize = 4;

    public Transformers() {
        mAxes = createAxes();
        mShape = createShape();
    }

    protected Shape createAxes() {
        GeneralPath path = new GeneralPath();

        // Axes.
        path.moveTo( -mLength, 0 );
        path.lineTo( mLength, 0 );
        path.moveTo( 0, -mLength );
        path.lineTo( 0, mLength );
        // Arrows.
        path.moveTo( mLength - mArrowLength, -mArrowLength );
        path.lineTo( mLength, 0 );
        path.lineTo( mLength - mArrowLength, mArrowLength );
        path.moveTo( -mArrowLength, mLength - mArrowLength );
        path.lineTo( 0, mLength );
        path.lineTo( mArrowLength, mLength - mArrowLength );
        // Half-centimeter tick marks
        float cm = 72 / 2.54f;
        float lengthCentimeter = mLength / cm;
        for ( float i = 0.5f; i < lengthCentimeter; i += 1.0f ) {
            float tick = i * cm;
            path.moveTo( tick, -mTickSize / 2 );
            path.lineTo( tick, mTickSize / 2 );
            path.moveTo( -tick, -mTickSize / 2 );
            path.lineTo( -tick, mTickSize / 2 );
            path.moveTo( -mTickSize / 2, tick );
            path.lineTo( mTickSize / 2, tick );
            path.moveTo( -mTickSize / 2, -tick );
            path.lineTo( mTickSize / 2, -tick );
        }
        // Full-centimeter tick marks
        for ( float i = 1.0f; i < lengthCentimeter; i += 1.0f ) {
            float tick = i * cm;
            path.moveTo( tick, -mTickSize );
            path.lineTo( tick, mTickSize );
            path.moveTo( -tick, -mTickSize );
            path.lineTo( -tick, mTickSize );
            path.moveTo( -mTickSize, tick );
            path.lineTo( mTickSize, tick );
            path.moveTo( -mTickSize, -tick );
            path.lineTo( mTickSize, -tick );
        }
        return path;
    }

    protected Shape createShape() {
        float cm = 72 / 2.54f;
        return new Rectangle2D.Float( cm, cm, 2 * cm, cm );
    }

    public void paint( Graphics g ) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        // Use antialiasing.
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

        // Move the origin to 75, 75.
        AffineTransform at = AffineTransform.getTranslateInstance( 75, 75 );
        g2.transform( at );

        // Draw the shapes in their original locations.
        g2.setPaint( Color.black );
        g2.draw( mAxes );
        g2.draw( mShape );

        // Transform the Graphics2D.
        g2.transform( getTransform() );

        // Draw the shapes in their new locations, but dashed.
        Stroke stroke = new BasicStroke( 1,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[]{3, 1}, 0 );
        g2.setStroke( stroke );
        g2.draw( mAxes );
        g2.draw( mShape );
    }

    public abstract AffineTransform getTransform();

    public Frame getFrame() {
        ApplicationFrame f = new ApplicationFrame( "...more than meets the eye" );
        f.getContentPane().setLayout( new BorderLayout() );
        f.getContentPane().add( this, BorderLayout.CENTER );
        f.setSize( 350, 200 );
        f.center();
        return f;
    }
}