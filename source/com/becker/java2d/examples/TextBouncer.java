package com.becker.java2d.examples;

import com.becker.ui.animation.AnimationComponent;
import com.becker.ui.animation.AnimationFrame;
import com.becker.ui.animation.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class TextBouncer
        extends AnimationComponent
{
    public static void main( String[] args )
    {
        String s = "Firenze";
        final int size = 64;
        if ( args.length > 0 ) s = args[0];

        Panel controls = new Panel();
        final Choice choice = new Choice();
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = ge.getAllFonts();
        for ( int i = 0; i < allFonts.length; i++ )
            choice.addItem( allFonts[i].getName() );
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
        choice.addItemListener( new ItemListener()
        {
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

        f.setVisible( true );
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

    public TextBouncer( String s, Font f )
    {
        mString = s;
        setFont( f );
        reset();
        FontRenderContext frc = new FontRenderContext( null, true, false );
        Rectangle2D bounds = getFont().getStringBounds( mString, frc );
        mWidth = (float) bounds.getWidth();
        mHeight = (float) bounds.getHeight();
        // Make sure points are within range.
        addComponentListener( new ComponentAdapter()
        {
            public void componentResized( ComponentEvent ce )
            {
                Dimension d = getSize();
                if ( mX < 0 )
                    mX = 0;
                else if ( mX + mWidth >= d.width ) mX = d.width - mWidth - 1;
                if ( mY < 0 )
                    mY = 0;
                else if ( mY + mHeight >= d.height ) mY = d.height - mHeight - 1;
            }
        } );
    }

    protected void reset() {
        Random random = new Random();
        mX = random.nextFloat() * 500;
        mY = random.nextFloat() * 500;
        mDeltaX = random.nextFloat() * 3;
        mDeltaY = random.nextFloat() * 3;
        mShearX = random.nextFloat() / 2;
        mShearY = random.nextFloat() / 2;
        mShearDeltaX = mShearDeltaY = .05f;
    }
    
    public String getFileNameBase() {
        return null;
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
            public void itemStateChanged( ItemEvent ie )
            {
                setSwitch( item, (ie.getStateChange() == ie.SELECTED) );
            }
        } );
        return check;
    }

    public double timeStep()
    {
        Dimension d = getSize();
        if ( mX + mDeltaX < 0 )
            mDeltaX = -mDeltaX;
        else if ( mX + mWidth + mDeltaX >= d.width ) mDeltaX = -mDeltaX;
        if ( mY + mDeltaY < 0 )
            mDeltaY = -mDeltaY;
        else if ( mY + mHeight + mDeltaY >= d.height ) mDeltaY = -mDeltaY;
        mX += mDeltaX;
        mY += mDeltaY;

        mTheta += Math.PI / 192;
        if ( mTheta > (2 * Math.PI) ) mTheta -= (2 * Math.PI);

        if ( mShearX + mShearDeltaX > .5 )
            mShearDeltaX = -mShearDeltaX;
        else if ( mShearX + mShearDeltaX < -.5 ) mShearDeltaX = -mShearDeltaX;
        if ( mShearY + mShearDeltaY > .5 )
            mShearDeltaY = -mShearDeltaY;
        else if ( mShearY + mShearDeltaY < -.5 ) mShearDeltaY = -mShearDeltaY;
        mShearX += mShearDeltaX;
        mShearY += mShearDeltaY;
        return 0;
    }

    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        setAntialiasing( g2 );
        setTransform( g2 );
        setPaint( g2 );
        // Draw the string.
        g2.setFont( getFont() );
        g2.drawString( mString, mX, mY + mHeight );
        drawAxes( g2 );
    }

    protected void setAntialiasing( Graphics2D g2 )
    {
        if ( mAntialiasing == false ) return;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
    }

    protected void setTransform( Graphics2D g2 )
    {
        Dimension d = getSize();
        int cx = d.width / 2;
        int cy = d.height / 2;
        g2.translate( cx, cy );
        if ( mShear ) g2.shear( mShearX, mShearY );
        if ( mRotate ) g2.rotate( mTheta );
        g2.translate( -cx, -cy );
    }

    protected void setPaint( Graphics2D g2 )
    {
        if ( mGradient ) {
            GradientPaint gp = new GradientPaint( 0, 0, Color.blue,
                    50, 25, Color.green, true );
            g2.setPaint( gp );
        }
        else
            g2.setPaint( Color.orange );
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