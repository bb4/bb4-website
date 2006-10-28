package com.becker.java2d.examples;

import com.becker.ui.animation.AnimationComponent;
import com.becker.ui.animation.AnimationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class Bouncer extends AnimationComponent
{
    public static void main( String[] args )
    {
        final Bouncer bouncer = new Bouncer();
        JFrame f = new AnimationFrame( bouncer );
        bouncer.setFont( new Font( "Serif", Font.PLAIN, 12 ) );

        Panel controls = new Panel();
        controls.add( bouncer.createCheckbox( "Anti.", Bouncer.ANTIALIASING ) );
        controls.add( bouncer.createCheckbox( "Trans.", Bouncer.TRANSFORM ) );
        controls.add( bouncer.createCheckbox( "Gradient", Bouncer.GRADIENT ) );
        controls.add( bouncer.createCheckbox( "Outline", Bouncer.OUTLINE ) );
        controls.add( bouncer.createCheckbox( "Dotted", Bouncer.DOTTED ) );
        controls.add( bouncer.createCheckbox( "Axes", Bouncer.AXES ) );
        controls.add( bouncer.createCheckbox( "Clip", Bouncer.CLIP ) );
        f.getContentPane().add( controls, BorderLayout.NORTH );
        f.setVisible( true );
    }

    // Tweakable variables
    private boolean mAntialiasing, mGradient, mOutline;
    private boolean mTransform, mDotted, mAxes, mClip;
    // ...and the constants that represent them. See setSwitch().
    public static final int ANTIALIASING = 0;
    public static final int GRADIENT = 1;
    public static final int OUTLINE = 2;
    public static final int TRANSFORM = 3;
    public static final int DOTTED = 4;
    public static final int AXES = 5;
    public static final int CLIP = 6;

    public static final float SPEED = 3;
    public static final int NUM_BALLS = 20;
    public static final float GRAVITY = .3f;

    private float[] mPoints;
    private float[] mDeltas;
    private float mTheta;
    private int mN;
    private Shape mClipShape;

    public Bouncer()
    {
        mN = NUM_BALLS;  //38
        mPoints = new float[mN + 3];
        mDeltas = new float[mN + 3];
        Random random = new Random();
        for ( int i = 0; i < mN; i++ ) {
            mPoints[i] = random.nextFloat() * 500;
            mDeltas[i] = random.nextFloat() * SPEED;
        }
        // Make sure points are within range.
        addComponentListener( new ComponentAdapter()
        {
            public void componentResized( ComponentEvent ce )
            {
                Dimension d = getSize();
                for ( int i = 0; i < mN; i++ ) {
                    int limit = ((i % 2) == 0) ? d.width : d.height;
                    if ( mPoints[i] < 0 )
                        mPoints[i] = 0;
                    else if ( mPoints[i] >= limit ) mPoints[i] = limit - 1;
                }
            }
        } );
    }

    public void setSwitch( int item, boolean value )
    {
        switch (item) {
            case ANTIALIASING:
                mAntialiasing = value;
                break;
            case GRADIENT:
                mGradient = value;
                break;
            case OUTLINE:
                mOutline = value;
                break;
            case TRANSFORM:
                mTransform = value;
                break;
            case DOTTED:
                mDotted = value;
                break;
            case AXES:
                mAxes = value;
                break;
            case CLIP:
                mClip = value;
                break;
            default:
                break;
        }
    }

    protected Checkbox createCheckbox( String label, final int item )
    {
        Checkbox check = new Checkbox( label );
        check.addItemListener( new ItemListener()
        {
            public void itemStateChanged( ItemEvent ie )
            {
                setSwitch( item, (ie.getStateChange() == ie.SELECTED) );
            }
        } );
        return check;
    }

    public String getFileNameBase()
    {
        return "D:/f";
    }

    public double timeStep()
    {
        Dimension d = getSize();
        for ( int i = 0; i < mN; i++ ) {
            boolean xAxis = ((i % 2) == 0);
            if ( !xAxis )
                mDeltas[i] += GRAVITY;
            float value = mPoints[i] + mDeltas[i];
            int limit = xAxis ? d.width : d.height;
            if ( value < 0 || value > limit ) {
                mDeltas[i] = -mDeltas[i];
                if ( value < 0 )
                    mPoints[i] = -value;
                else if ( value > limit )
                    mPoints[i] = limit - (value - limit);
            }
            else
                mPoints[i] = value;
        }
        mTheta += Math.PI / 192;
        if ( mTheta > (2 * Math.PI) ) mTheta -= (2 * Math.PI);
        return 0;
    }

    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        setAntialiasing( g2 );
        setClip( g2 );
        setTransform( g2 );
        Shape shape = createShape();
        setPaint( g2 );
        // Fill the shape.
        g2.fill( shape );
        // Maybe draw the outline.
        if ( mOutline ) {
            setStroke( g2 );
            g2.setPaint( Color.blue );
            g2.draw( shape );
        }
        drawAxes( g2 );
    }

    protected void setAntialiasing( Graphics2D g2 )
    {
        if ( mAntialiasing == false ) return;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
    }

    protected void setClip( Graphics2D g2 )
    {
        if ( mClip == false ) return;
        if ( mClipShape == null ) {
            Dimension d = getSize();
            FontRenderContext frc = g2.getFontRenderContext();
            Font font = new Font( "Serif", Font.PLAIN, 144 );
            String s = "Spoon!";
            GlyphVector gv = font.createGlyphVector( frc, s );
            Rectangle2D bounds = font.getStringBounds( s, frc );
            mClipShape = gv.getOutline( (d.width - (float) bounds.getWidth()) / 2,
                    (d.height + (float) bounds.getHeight()) / 2 );
        }
        g2.clip( mClipShape );
    }

    protected void setTransform( Graphics2D g2 )
    {
        if ( mTransform == false ) return;
        Dimension d = getSize();
        g2.rotate( mTheta, d.width / 2, d.height / 2 );
    }

    protected Shape createShape()
    {
        GeneralPath path = new GeneralPath( GeneralPath.WIND_EVEN_ODD,
                mPoints.length );
        path.moveTo( mPoints[0], mPoints[1] );
        for ( int i = 2; i < mN; i += 6 )
            path.curveTo( mPoints[i], mPoints[i + 1],
                    mPoints[i + 2], mPoints[i + 3],
                    mPoints[i + 4], mPoints[i + 5] );
        path.closePath();
        return path;
    }

    protected void setPaint( Graphics2D g2 )
    {
        if ( mGradient ) {
            GradientPaint gp = new GradientPaint( 0, 0, Color.yellow,
                    50, 25, Color.red, true );
            g2.setPaint( gp );
        }
        else
            g2.setPaint( Color.orange );
    }

    protected void setStroke( Graphics2D g2 )
    {
        if ( mDotted == false ) return;
        // Create a dotted stroke.
        Stroke stroke = new BasicStroke( 1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10,
                new float[]{4, 4}, 0 );
        g2.setStroke( stroke );
    }

    protected void drawAxes( Graphics2D g2 )
    {
        if ( mAxes == false ) return;
        g2.setPaint( getForeground() );
        g2.setStroke( new BasicStroke() );
        Dimension d = getSize();
        int side = 20;
        int arrow = 4;
        int w = d.width / 2, h = d.height / 2;
        g2.drawLine( w - side, h, w + side, h );
        g2.drawLine( w + side - arrow, h - arrow, w + side, h );
        g2.drawLine( w, h - side, w, h + side );
        g2.drawLine( w + arrow, h + side - arrow, w, h + side );
    }
}