package com.becker.spirograph;

/**
 * @author Barry Becker
 */

import com.becker.ui.GUIUtil;

import javax.swing.*;
import java.awt.*;

public class SuperSpiroGraph extends SpiroGraph
{
    // rat is the ratio of the angular velocity of R3 to R2
    protected static JLabel rad3, rat;
    protected static JSlider RAD3, RAT;

    public void init()
    {
        System.out.println( "in init SuperSpiroGraph.init" );
        this.getContentPane().setLayout( new BorderLayout() );
        SuperGraphRenderer sgr = new SuperGraphRenderer();
        graphRenderer = sgr;
        System.out.println( "done init SuperGRenderer" );

        initComponents();

        rad3 = new JLabel( "Radius3: 60" );
        RAD3 = new JSlider( JSlider.HORIZONTAL, 5, 255, 60 );
        RAD3.addChangeListener( this );
        rat = new JLabel( "Angular velocity ration: 1" );
        RAT = new JSlider( JSlider.HORIZONTAL, 0, 5, 1 );
        RAT.addChangeListener( this );

        p1 = new JPanel();
        p1.setLayout( new GridLayout( 28, 1 ) );
        p1.add( rad1 );
        p1.add( RAD1 );
        p1.add( rad2 );
        p1.add( RAD2 );
        p1.add( rad3 );
        p1.add( RAD3 );
        p1.add( pos );
        p1.add( POS );
        p1.add( vel );
        p1.add( VEL );
        p1.add( width );
        p1.add( WIDTH );
        p1.add( rat );
        p1.add( RAT );

        addButtons( p1 );
        initializeRenderer();
    }

    protected void checkSlider( JSlider src, int v )
    {
        super.checkSlider( src, v );
        SuperGraphRenderer sgr = (SuperGraphRenderer) graphRenderer;
        if ( src == RAD3 ) {
            rad3.setText( "Radius3: " + RAD3.getValue() );
            //sgr.R3 = RAD3.getValue();
            //if (RAD3.getValue() < 0) sgr.sign2=-1.0;
            //else sgr.sign2=1.0;
            //sgr.adjustCircle3();
            if ( v == GraphRenderer.VELOCITY_MAX )
                autoUpdate();

        }
        else if ( src == RAT ) {
            rat.setText( "angulate velocity ratio: " + RAT.getValue() );
            //sgr.rat = RAT.getValue();
        }
    }

    protected void resetRenderer()
    {
        super.resetRenderer();
    }

    //------ Main method - to allow running as an application ---------------------
    public static void main( String[] args )
    {
        SpiroGraph applet = new SuperSpiroGraph();
        JFrame baseFrame_ = GUIUtil.showApplet( applet, "SuperSpiroGraph Applet" );
    }
}

