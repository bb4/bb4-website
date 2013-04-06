package com.barrybecker4.java2d.examples;

import com.barrybecker4.ui.animation.AnimationComponent;
import com.barrybecker4.ui.animation.AnimationFrame;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class TextBouncer extends AnimationComponent {

    private static final float SHEAR_SCALE = 0.02f;

    public static void main( String[] args ) {
        String s = "Firenze";
        final int size = 64;
        if ( args.length > 0 ) s = args[0];

        Panel controls = new Panel();
        final Choice choice = new Choice();
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = ge.getAllFonts();
        for (Font allFont : allFonts)
            choice.addItem(allFont.getName());
        Font defaultFont = new Font( allFonts[0].getName(), Font.PLAIN, size );

        final TextBouncer bouncer = new TextBouncer( s, defaultFont );
        Frame f = new AnimationFrame( bouncer );
        f.setFont( new Font( "Serif", Font.PLAIN, 12 ) );
        controls.add( bouncer.createCheckbox( "Antialiasing",
                TextBouncer.ANTIALIASING ) );
        controls.add( bouncer.createCheckbox( "Gradient", TextBouncer.GRADIENT ) );
        controls.add( bouncer.createCheckbox( "Shear", TextBouncer.SHEAR ) );
        controls.add( bouncer.createCheckbox( "Rotate", TextBouncer.ROTATE ) );
        controls.add( bouncer.createCheckbox( "Axes", TextBouncer.AXES ) );

        Panel fontControls = new Panel();
        choice.addItemListener( new ItemListener() {
            @Override
            public void itemStateChanged( ItemEvent ie )
            {
                Font font = new Font( choice.getSelectedItem(), Font.PLAIN, size );
                bouncer.setFont( font );
            }
        } );
        fontControls.add( choice );

        Panel allControls = new Panel( new GridLayout( 2, 1 ) );
        allControls.add( controls );
        allControls.add( fontControls );
        f.add( allControls, BorderLayout.NORTH );

        bouncer.setPaused(false);
    }

    private boolean mAntialiasing = false, mGradient = false;
    private boolean mShear = false, mRotate = false, mAxes = false;
    public static final int ANTIALIASING = 0;
    public static final int GRADIENT = 1;
    public static final int SHEAR = 2;
    public static final int ROTATE = 3;
    public static final int AXES = 5;

    private float mDeltaX, mDeltaY;
    private float mX, mY, mWidth, mHeight;
    private float mTheta;
    private float mShearX, mShearY, mShearDeltaX, mShearDeltaY;
    private String mString;

    public TextBouncer( String s, Font f ) {
        mString = s;
        setFont( f );
        reset();
        FontRenderContext frc = new FontRenderContext( null, true, false );
        Rectangle2D bounds = getFont().getStringBounds( mString, frc );
        mWidth = (float) bounds.getWidth();
        mHeight = (float) bounds.getHeight();
        // Make sure points are within range.
        addComponentListener(new BouncerComponentAdapter());
    }

    protected void reset() {
        Random random = new Random();
        mX = random.nextFloat() * 500;
        mY = random.nextFloat() * 500;
        mDeltaX = 0.01f + random.nextFloat()/2.0f;
        mDeltaY = 0.01f + random.nextFloat()/2.0f;
        mShearX = random.nextFloat() / 7;
        mShearY = random.nextFloat() / 7;
        mShearDeltaX = mShearDeltaY = SHEAR_SCALE;
    }

    @Override
    public String getFileNameBase() {
        return null;
    }

    public void setSwitch( int item, boolean value ) {
        switch (item) {
            case ANTIALIASING:
                mAntialiasing = value;
                break;
            case GRADIENT:
                mGradient = value;
                break;
            case SHEAR:
                mShear = value;
                break;
            case ROTATE:
                mRotate = value;
                break;
            case AXES:
                mAxes = value;
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
            @Override
            public void itemStateChanged( ItemEvent ie )
            {
                setSwitch( item, (ie.getStateChange() == ie.SELECTED) );
            }
        } );
        return check;
    }

    @Override
    public double timeStep() {
        Dimension d = getSize();
        if ( mX + mDeltaX < 0 )
            mDeltaX = -mDeltaX;
        else if ( mX + mWidth + mDeltaX >= d.width ) mDeltaX = -mDeltaX;
        if ( mY + mDeltaY < 0 )
            mDeltaY = -mDeltaY;
        else if ( mY + mHeight + mDeltaY >= d.height ) mDeltaY = -mDeltaY;
        mX += mDeltaX;
        mY += mDeltaY;

        mTheta += Math.PI / 384;
        if ( mTheta > (2 * Math.PI) ) mTheta -= (2 * Math.PI);

        double shearThresh = 10.0 * SHEAR_SCALE;
        if ( mShearX + mShearDeltaX > shearThresh )
            mShearDeltaX = -mShearDeltaX;
        else if ( mShearX + mShearDeltaX < -shearThresh ) mShearDeltaX = -mShearDeltaX;
        if ( mShearY + mShearDeltaY > .5 )
            mShearDeltaY = -mShearDeltaY;
        else if ( mShearY + mShearDeltaY < -shearThresh ) mShearDeltaY = -mShearDeltaY;
        mShearX += mShearDeltaX;
        mShearY += mShearDeltaY;
        return 0;
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        setAntialiasing( g2 );
        setTransform( g2 );
        setPaint( g2 );
        // Draw the string.
        g2.setFont( getFont() );
        g2.drawString( mString, mX, mY + mHeight );
        drawAxes( g2 );
    }

    protected void setAntialiasing( Graphics2D g2 ) {
        if ( !mAntialiasing) return;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
    }

    protected void setTransform( Graphics2D g2 ) {
        Dimension d = getSize();
        int cx = d.width / 2;
        int cy = d.height / 2;
        g2.translate( cx, cy );
        if ( mShear ) g2.shear( mShearX, mShearY );
        if ( mRotate ) g2.rotate( mTheta );
        g2.translate( -cx, -cy );
    }

    protected void setPaint( Graphics2D g2 ) {
        if ( mGradient ) {
            GradientPaint gp = new GradientPaint( 0, 0, Color.blue,
                    50, 25, Color.green, true );
            g2.setPaint( gp );
        }
        else
            g2.setPaint( Color.orange );
    }

    protected void drawAxes( Graphics2D g2 ) {
        if ( !mAxes ) return;
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

    private class BouncerComponentAdapter extends ComponentAdapter {
        @Override
        public void componentResized( ComponentEvent ce ) {
            Dimension d = getSize();
            if ( mX < 0 )
                mX = 0;
            else if ( mX + mWidth >= d.width ) mX = d.width - mWidth - 1;
            if ( mY < 0 )
                mY = 0;
            else if ( mY + mHeight >= d.height ) mY = d.height - mHeight - 1;
        }
    }
}