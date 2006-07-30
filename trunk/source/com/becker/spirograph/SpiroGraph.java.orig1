package com.becker.spirograph;

import com.becker.java2d.ImageUtil;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * That old spirograph game from the 70's brought into the computer age
 * Based on work originially done by David Little.
 *
 * @author Barry Becker
 * @version SpiroGraph1.1
 */
public class SpiroGraph extends JApplet
        implements ChangeListener, ActionListener
{
    protected static JPanel p1,q1;
    protected static BorderLayout p0, q0;
    protected GraphRenderer graphRenderer;
    protected static Color COLOR;
    protected static JLabel rad1, rad2, pos, vel, width, X, Y, swatch, red, green, blue;
    protected static JSlider RAD1, RAD2, POS, VEL, WIDTH, RED, GREEN, BLUE;
    protected static GradientButton hide, clear, draw, reset;

    ResizableAppletPanel resizablePanel_ = null;

    public void init()
    {
        GUIUtil.setCustomLookAndFeel();

        //setLayout(new BorderLayout());
        System.out.println( "in init SpiroGraph.init" );
        this.getContentPane().setLayout( new BorderLayout() );
        graphRenderer = new GraphRenderer();

        initComponents();

        p1 = new JPanel();
        p1.setLayout( new GridLayout( 24, 1 ) );
        p1.add( rad1 );
        p1.add( RAD1 );
        p1.add( rad2 );
        p1.add( RAD2 );
        p1.add( pos );
        p1.add( POS );
        p1.add( vel );
        p1.add( VEL );
        p1.add( width );
        p1.add( WIDTH );
        addButtons( p1 );

        initializeRenderer();
    }

    protected void addButtons( JPanel p1 )
    {
        p1.add( new Label( " " ) );
        p1.add( hide );
        p1.add( clear );
        p1.add( reset );
        p1.add( draw );
        p1.add( new JLabel( " " ) );
        p1.add( swatch );
        p1.add( red );
        p1.add( RED );
        p1.add( green );
        p1.add( GREEN );
        p1.add( blue );
        p1.add( BLUE );
        updateSwatch();
    }

    protected void initComponents()
    {
        rad1 = new JLabel( "Radius1: 60" );
        RAD1 = new JSlider( JSlider.HORIZONTAL, 5, 255, 60 );
        RAD1.addChangeListener( this );
        rad2 = new JLabel( "Radius2: 60" );
        RAD2 = new JSlider( JSlider.HORIZONTAL, -59, 200, 60 );
        RAD2.addChangeListener( this );
        pos = new JLabel( "Position: 60" );
        POS = new JSlider( JSlider.HORIZONTAL, -300, 300, 60 );
        POS.addChangeListener( this );
        vel = new JLabel( "Velocity: " + 3 );
        VEL = new JSlider( JSlider.HORIZONTAL, 1, GraphRenderer.VELOCITY_MAX, 3 );
        VEL.addChangeListener( this );
        width = new JLabel( "Line Width: " + GraphRenderer.INITIAL_LINE_WIDTH );
        WIDTH = new JSlider( JSlider.HORIZONTAL, 1, 50, GraphRenderer.INITIAL_LINE_WIDTH );
        WIDTH.addChangeListener( this );
        red = new JLabel( "Red: 0" );
        RED = new JSlider( JSlider.HORIZONTAL, 0, 255, 0 );
        RED.addChangeListener( this );
        green = new JLabel( "Green: 0" );
        GREEN = new JSlider( JSlider.HORIZONTAL, 0, 255, 0 );
        GREEN.addChangeListener( this );
        blue = new JLabel( "Blue: 0" );
        BLUE = new JSlider( JSlider.HORIZONTAL, 0, 255, 0 );
        BLUE.addChangeListener( this );

        // buttons for controlling the animation
        hide = new GradientButton( "Hide" );
        hide.addActionListener( this );
        clear = new GradientButton( "Clear" );
        clear.addActionListener( this );
        reset = new GradientButton( "Reset" );
        reset.addActionListener( this );
        draw = new GradientButton( "Draw" );
        draw.addActionListener( this );
        swatch = new JLabel();
        swatch.setBackground( Color.white );
    }

    public void initializeRenderer()
    {

        JPanel mainPanel = new JPanel( new BorderLayout() );

        q1 = new JPanel();
        q1.setLayout( new GridLayout( 1, 2, 0, 0 ) );
        q1.add( X = new JLabel( "", JLabel.CENTER ) );
        q1.add( Y = new JLabel( "", JLabel.CENTER ) );
        updateEqn();

        mainPanel.add( "East", p1 );
        mainPanel.add( "Center", graphRenderer );
        mainPanel.add( "South", q1 );

        resizablePanel_ = new ResizableAppletPanel( mainPanel );
        this.getContentPane().add( resizablePanel_ );

        System.out.println( "the image w =" + graphRenderer.W + " h=" + graphRenderer.H );

        graphRenderer.offImage = ImageUtil.createCompatibleImage( graphRenderer.W, graphRenderer.H );
        if ( graphRenderer.offImage != null ) {
            graphRenderer.offg = graphRenderer.offImage.createGraphics();
            graphRenderer.offg.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON );
        }
        else
            System.out.println( "error the offg is null!" );
        graphRenderer.clear();
    }

    public void updateEqn()
    {
        int r = RAD2.getValue();
        int Rr = RAD1.getValue() + r;
        int p = POS.getValue();
        String s1 = "-", s2 = "-";

        if ( r == 0 ) {
            X.setText( "x(t)=undefined" );
            Y.setText( "y(t)=undefined" );
        }
        else if ( p == 0 ) {
            X.setText( "x(t)=" + Rr + "cos(t)" );
            Y.setText( "y(t)=" + Rr + "sin(t)" );
        }
        else {
            if ( p < 0 && r < 0 ) {
                p *= -1;
                r *= -1;
                s1 = "+";
            }
            else if ( p < 0 && r > 0 ) {
                p *= -1;
                s1 = "+";
                s2 = "+";
            }
            else if ( p > 0 && r < 0 ) {
                r *= -1;
                s2 = "+";
            }
            X.setText( "x(t)=" + Rr + "cos(t)" + s1 + p + "cos(" + Rr + "t/" + r + ")" );
            Y.setText( "y(t)=" + Rr + "sin(t)" + s2 + p + "sin(" + Rr + "t/" + r + ")" );
        }
    }

    public void updateSwatch()
    {
        COLOR = new Color( RED.getValue(), GREEN.getValue(), BLUE.getValue() );
        System.out.println( "swatch color = " + COLOR );
        swatch.setBackground( COLOR );
        swatch.setOpaque( true );
        swatch.repaint();
    }

    protected void checkSlider( JSlider src, int v )
    {
        if ( src == RAD1 ) {
            rad1.setText( "Radius1: " + RAD1.getValue() );
            int n = RAD2.getValue();
            int m = RAD1.getValue();
            if ( n < 2 - m ) {
                n = 1 - m;
                RAD2.setValue( n );
                rad2.setText( "Radius2: " + n );
                graphRenderer.R2 = n;
            }
            RAD2.setMinimum( 2 - m );
            graphRenderer.R1 = RAD1.getValue();
            graphRenderer.adjustCircle1();
            if ( v == GraphRenderer.VELOCITY_MAX )
                autoUpdate();
        }
        else if ( src == RAD2 ) {
            rad2.setText( "Radius2: " + RAD2.getValue() );
            graphRenderer.R2 = RAD2.getValue();
            if ( RAD2.getValue() < 0 )
                graphRenderer.sign = -1.0;
            else
                graphRenderer.sign = 1.0;
            graphRenderer.adjustCircle2();
            if ( v == GraphRenderer.VELOCITY_MAX )
                autoUpdate();
        }
        else if ( src == POS ) {
            pos.setText( "Position: " + POS.getValue() );
            graphRenderer.p = POS.getValue();
            graphRenderer.adjustDot();
            if ( v == GraphRenderer.VELOCITY_MAX )
                autoUpdate();
        }
        else if ( src == VEL ) {
            vel.setText( "Velocity: " + v );
            graphRenderer.v = v;
        }
        else if ( src == WIDTH ) {
            width.setText( "Line Width: " + WIDTH.getValue() );
            graphRenderer.width = WIDTH.getValue();
        }
        else if ( src == RED ) {
            red.setText( "Red: " + RED.getValue() );
            updateSwatch();
        }
        else if ( src == GREEN ) {
            green.setText( "Green: " + GREEN.getValue() );
            updateSwatch();
        }
        else if ( src == BLUE ) {
            blue.setText( "Blue: " + BLUE.getValue() );
            updateSwatch();
        }
    }

    public void stateChanged( ChangeEvent e )
    {
        //System.out.println( "ChangeEvent :" + e.getSource().toString() );
        Object src = e.getSource();
        if ( src instanceof JSlider ) {
            int velocity = VEL.getValue();
            checkSlider( (JSlider) src, velocity );
            updateEqn();
        }
    }

    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();

        if ( source instanceof GradientButton ) {
            String obj = ((GradientButton) source).getText();
            if ( RAD2.getValue() != 0 ) {
                if ( obj.equals( "Draw" ) ) {
                    draw.setText( "Pause" );
                    graphRenderer.n = (double) WIDTH.getValue();
                    GraphRenderer.thread = new Thread( graphRenderer );
                    GraphRenderer.thread.start();
                }
                else if ( obj.equals( "Pause" ) ) {
                    graphRenderer.setPaused( true );
                    draw.setText( "Resume" );
                }
                else if ( obj.equals( "Resume" ) ) {
                    graphRenderer.setPaused( false );
                    draw.setText( "Pause" );
                }
            }

            if ( obj.equals( "Reset" ) ) {
                resetRenderer();
            }
            else if ( obj.equals( "Hide" ) ) {
                graphRenderer.drawAxes();
                hide.setText( "Show" );
            }
            else if ( obj.equals( "Show" ) ) {
                hide.setText( "Hide" );
                graphRenderer.drawAxes();
            }
            else if ( obj.equals( "Clear" ) ) {
                graphRenderer.clear();
            }
        }
    }

    protected void autoUpdate()
    {
        graphRenderer.clear();
        resetRenderer();
        GraphRenderer.thread = new Thread( graphRenderer );
        GraphRenderer.thread.start();
    }

    protected void resetRenderer()
    {
        // stop the thread
        GraphRenderer.thread = null;

        //GraphRenderer.thread = new Thread(graphRenderer);
        graphRenderer.theta = 0.0;
        graphRenderer.phi = 0.0;
        graphRenderer.setPoint();
        graphRenderer.adjustCircle2();
        draw.setText( "Draw" );
    }

    /**
     * This method allow javascript to resize the applet from the browser.
     */
    public void setSize( int width, int height )
    {
        //System.out.println("in setSize w="+width+" h="+height);
        resizablePanel_.setSize( width, height );
    }

    //------ Main method - to allow running as an application ---------------------
    public static void main( String[] args )
    {
        SpiroGraph applet = new SpiroGraph();
        JFrame baseFrame_ = GUIUtil.showApplet( applet, "SpiroGraph Applet" );
    }
}

