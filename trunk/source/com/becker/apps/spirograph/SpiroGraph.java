package com.becker.apps.spirograph;

import com.becker.ui.*;
import com.becker.ui.sliders.ColorSliderGroup;
import com.becker.ui.sliders.SliderGroupChangeListener;
import com.becker.ui.sliders.SliderGroup;

import com.becker.ui.sliders.SliderProperties;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * That old spirograph game from the 70's brought into the computer age
 * Based on work originially done by David Little.
 *
 *TODO:
 *  Move to simulation.spirograph package
 *
 * @author Barry Becker
 */
public class SpiroGraph extends ApplicationApplet
        implements ActionListener, SliderGroupChangeListener
{
    public static final String HIDE_LABEL = "Hide Axes";
    private static final String SHOW_LABEL = "Show Axes";
    private static final String CLEAR_LABEL = "Clear Graph";
    private static final String RESET_LABEL = "Reset";
    public static final String DRAW_LABEL = "Draw Graph";
    private static final String PAUSE_LABEL = "Pause";
    private static final String RESUME_LABEL = "Resume Drawing";

    GraphState state_;
    SliderGroup sliderGroup_;
    protected GraphRenderer graphRenderer_;

    private static final int RAD1 = 0;
    private static final int RAD2 = 1;
    private static final int POS = 2;
    private static final int VEL = 3;
    //private static final int WIDTH = 4;
    private static final int SEGMENTS = 5;

    private static final SliderProperties[] SLIDER_PROPS = {
        new SliderProperties("Radius1",          5,           255,      60),
        new SliderProperties("Radius2",       -59,           200,      60),
        new SliderProperties("Position",      -300,          300,      60),
        new SliderProperties("Speed",             1,          GraphState.VELOCITY_MAX,     3),
        new SliderProperties("Line Width",        1,         50,      GraphState.INITIAL_LINE_WIDTH),
        new SliderProperties("Num Segments/Revolution",  GraphState.DEFAULT_NUM_SEGMENTS/10,
                4*GraphState.DEFAULT_NUM_SEGMENTS,   GraphState.DEFAULT_NUM_SEGMENTS),
    };

    protected JLabel xFunction_, yFunction_;
    protected GradientButton hide_, clear_, draw_, reset_;


    public SpiroGraph() {}

    private JPanel createButtonGroup()
    {
        JPanel bp = new JPanel(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS) );
        //p.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                                                       BorderFactory.createEtchedBorder(EtchedBorder.RAISED)));

        hide_ = createButton( HIDE_LABEL );
        clear_ = createButton( CLEAR_LABEL );
        reset_ = createButton( RESET_LABEL );
        draw_ = createButton( DRAW_LABEL );

        JPanel bl= new JPanel(new BorderLayout());
        bl.add(hide_, BorderLayout.CENTER);
        p.add( createButtonPanel(hide_) );
        p.add( createButtonPanel(clear_) );
        p.add( createButtonPanel(reset_) );
        p.add( createButtonPanel(draw_) );

        bp.add(p, BorderLayout.CENTER);
        return bp;
    }

    private GradientButton createButton( String label) {
        GradientButton button = new GradientButton( label );
        button.addActionListener( this );
        return button;
    }

    private JPanel createButtonPanel(GradientButton button) {
        JPanel bp= new JPanel(new BorderLayout());
        bp.add(button, BorderLayout.CENTER);
        return bp;
    }

    public JPanel createMainPanel()
    {
        state_ = createGraphState();

        JPanel p1;
        p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS) );

        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);
        p1.add(sliderGroup_);

        p1.add(createButtonGroup());

        ColorSliderGroup colorSelector = new ColorSliderGroup();
        colorSelector.setColorChangeListener(state_);
        p1.add(colorSelector);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(100, 1000));
        p1.add(fill);

        graphRenderer_ = new GraphRenderer(state_, draw_);

        JPanel mainPanel = new JPanel( new BorderLayout() );

        JPanel q1 = new JPanel();
        q1.setLayout( new GridLayout( 1, 2, 0, 0 ) );
        q1.add( xFunction_ = new JLabel( "", JLabel.CENTER ) );
        q1.add( yFunction_ = new JLabel( "", JLabel.CENTER ) );
        updateEqn();

        mainPanel.add( "East", p1 );
        mainPanel.add( "Center", graphRenderer_ );
        mainPanel.add( "South", q1 );


        return mainPanel;
        // setSize(getContentPane().getWidth(), getContentPane().getHeight());
    }

    private GraphState createGraphState() {
        GraphState state = new GraphState();
        state.setR1((float) SLIDER_PROPS[RAD1].getInitialValue());
        state.setR2((float) SLIDER_PROPS[RAD2].getInitialValue());
        state.setPos((float) SLIDER_PROPS[POS].getInitialValue());
        state.setVelocity((int) SLIDER_PROPS[VEL].getInitialValue());
        state.setWidth((int) SLIDER_PROPS[WIDTH].getInitialValue());
        state.setNumSegmentsPerRev((int) SLIDER_PROPS[SEGMENTS].getInitialValue());
        return state;
    }


    public void updateEqn()
    {
        int rad = sliderGroup_.getSliderValueAsInt(RAD2);
        int combinedRad = sliderGroup_.getSliderValueAsInt(RAD1) + rad;
        int p = sliderGroup_.getSliderValueAsInt(POS);
        String s1 = "-", s2 = "-";

        if ( rad == 0 ) {
            xFunction_.setText( "x(t)=undefined" );
            yFunction_.setText( "y(t)=undefined" );
        }
        else if ( p == 0 ) {
            xFunction_.setText( "x(t)=" + combinedRad + "cos(t)" );
            yFunction_.setText( "y(t)=" + combinedRad + "sin(t)" );
        }
        else {
            if ( p < 0 && rad < 0 ) {
                p *= -1;
                rad *= -1;
                s1 = "+";
            }
            else if ( p < 0 && rad > 0 ) {
                p *= -1;
                s1 = "+";
                s2 = "+";
            }
            else if ( p > 0 && rad < 0 ) {
                rad *= -1;
                s2 = "+";
            }
            xFunction_.setText( "x(t)=" + combinedRad + "cos(t)" + s1 + p + "cos(" + combinedRad + "t/" + rad + ')' );
            yFunction_.setText( "y(t)=" + combinedRad + "sin(t)" + s2 + p + "sin(" + combinedRad + "t/" + rad + ')' );
        }
    }


    /**
     * Implements SliderChangeListener interface.
     * See SliderGroup
     * Maintains constraints between sliders.
     */
    public void sliderChanged(int src, String sliderName, double sliderValue )
    {
        // I know that all the sliders are integer based.
        int value = (int)sliderValue;

        //System.out.println(sliderName+ ' ' + value);
        int velocity = sliderGroup_.getSliderValueAsInt(VEL);

        if ( src == RAD1 ) {
            int n = sliderGroup_.getSliderValueAsInt(RAD2);
            if ( n < 2 - value ) {
                n = 1 - value;
                sliderGroup_.setSliderValue(RAD2, n);
                state_.setR2(n);
            }
            sliderGroup_.setSliderMinimum(RAD2, ( 2 - value ));
            state_.setR1(value);
            graphRenderer_.adjustCircle1();
            if ( velocity == GraphState.VELOCITY_MAX )
                autoUpdate();
        }
        else if ( src == RAD2 ) {
            state_.setR2(value);
            state_.setSign( value < 0 ? -1 : 1);
            graphRenderer_.adjustCircle2();
            if ( velocity == GraphState.VELOCITY_MAX )
                autoUpdate();
        }
        else if ( src == POS ) {
            state_.setPos( value);
            graphRenderer_.adjustDot();
            if ( velocity == GraphState.VELOCITY_MAX )
                autoUpdate();
        }
        else if ( src == VEL ) {
            state_.setVelocity(value);
        }
        else if ( src == WIDTH ) {
            state_.setWidth(value);
        }
        else if ( src == SEGMENTS ) {
            state_.setNumSegmentsPerRev(value);
        }
        updateEqn();
    }

    /**
     * a button was pressed.
     * @param e
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();

        if ( source instanceof GradientButton ) {
            String obj = ((GradientButton) source).getText();
            if ( sliderGroup_.getSliderValue(RAD2) != 0 ) {
                if ( DRAW_LABEL.equals(obj) ) {
                    draw_.setText( PAUSE_LABEL );
                    GraphRenderer.thread_ = new Thread( graphRenderer_ );
                    GraphRenderer.thread_.start();
                }
                else if ( PAUSE_LABEL.equals(obj) ) {
                    graphRenderer_.setPaused( true );
                    draw_.setText( RESUME_LABEL );
                }
                else if ( RESUME_LABEL.equals(obj) ) {
                    graphRenderer_.setPaused( false );
                    draw_.setText( PAUSE_LABEL );   // WAS DRAW
                }
            }

            if ( RESET_LABEL.equals(obj) ) {
                graphRenderer_.reset();
                draw_.setText( DRAW_LABEL );
            }
            else if ( HIDE_LABEL.equals(obj) ) {
                // hides axes by drawing in XOR mode
                graphRenderer_.drawAxes();
                state_.setShowAxes(false);
                hide_.setText( SHOW_LABEL );
            }
            else if ( SHOW_LABEL.equals(obj) ) {
                hide_.setText( HIDE_LABEL );
                state_.setShowAxes(true);
                graphRenderer_.drawAxes();
            }
            else if ( CLEAR_LABEL.equals(obj) ) {
                graphRenderer_.clear();
            }
        }
    }

    protected void autoUpdate()
    {
        graphRenderer_.clear();
        graphRenderer_.reset();
        draw_.setText( DRAW_LABEL );
        GraphRenderer.thread_ = new Thread( graphRenderer_ );
        GraphRenderer.thread_.start();
    }

    //------ Main method - to allow running as an application ---------------------
    public static void main( String[] args )
    {
        SpiroGraph applet = new SpiroGraph();
        GUIUtil.showApplet( applet, "SpiroGraph" );
    }
}

