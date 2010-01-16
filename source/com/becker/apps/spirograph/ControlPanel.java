package com.becker.apps.spirograph;

import com.becker.ui.components.GradientButton;
import com.becker.ui.sliders.ColorSliderGroup;
import com.becker.ui.sliders.SliderGroupChangeListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A panel that holds sliders and buttons that control the rendering of the spirograph.
 *
 * @author Barry Becker
 */
public class ControlPanel extends JPanel
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
    ControlSliderGroup sliderGroup_;
    protected GraphRenderer graphRenderer_;

    protected JLabel xFunction_, yFunction_;
    protected GradientButton hide_, clear_, draw_, reset_;

    /**
     * Constructor
     */
    public ControlPanel(GraphRenderer graphRenderer, GraphState state) {
        graphRenderer_ = graphRenderer;
        state_ = state;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        sliderGroup_ = new ControlSliderGroup();
        sliderGroup_.addSliderChangeListener(this);
        add(sliderGroup_);
        add(createButtonGroup());

        ColorSliderGroup colorSelector = new ColorSliderGroup();
        colorSelector.setColorChangeListener(state_);
        add(colorSelector);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(100, 1000));
        add(fill);

        JPanel q1 = new JPanel();
        q1.setLayout( new GridLayout( 2, 1, 0, 0 ) );
        q1.add( xFunction_ = new JLabel( "", JLabel.CENTER ) );
        q1.add( yFunction_ = new JLabel( "", JLabel.CENTER ) );
        updateEqn();
        add(q1);
    }

    private JPanel createButtonGroup()
    {
        JPanel bp = new JPanel(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS) );
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
        JPanel bp = new JPanel(new BorderLayout());
        bp.add(button, BorderLayout.CENTER);
        return bp;
    }

    public void updateEqn()
    {
        ParametricEquations equations = sliderGroup_.getEquations();
        xFunction_.setText(equations.getXEquation());
        yFunction_.setText(equations.getYEquation());
    }

    /**
     * Implements SliderChangeListener interface.
     * See SliderGroup
     * Maintains constraints between sliders.
     */
    public void sliderChanged(int src, String sliderName, double sliderValue)
    {
        // I know that all the sliders are integer based.
        int value = (int)sliderValue;

        // System.out.println(sliderName+ ' ' + value);
        int velocity = sliderGroup_.getSliderValueAsInt(ControlSliderGroup.VEL);

        if ( src == ControlSliderGroup.RAD1 ) {
            int n = sliderGroup_.getSliderValueAsInt(ControlSliderGroup.RAD2);
            if ( n < 2 - value ) {
                n = 1 - value;
                sliderGroup_.setSliderValue(ControlSliderGroup.RAD2, n);
                state_.params.setR2(n);
            }
            sliderGroup_.setSliderMinimum(ControlSliderGroup.RAD2, ( 2 - value ));
            state_.params.setR1(value);
            graphRenderer_.adjustCircle1();
            if ( velocity == GraphState.VELOCITY_MAX )
                autoUpdate();
        }
        else if ( src == ControlSliderGroup.RAD2 ) {
            state_.params.setR2(value);
            state_.params.setSign( value < 0 ? -1 : 1);
            graphRenderer_.adjustCircle2();
            if ( velocity == GraphState.VELOCITY_MAX )
                autoUpdate();
        }
        else if ( src == ControlSliderGroup.POS ) {
            state_.params.setPos(value);
            graphRenderer_.adjustDot();
            if ( velocity == GraphState.VELOCITY_MAX )
                autoUpdate();
        }
        else if ( src == ControlSliderGroup.VEL ) {
            state_.setVelocity(value);
        }
        else if ( src == ControlSliderGroup.LINE_WIDTH ) {
            state_.setWidth(value);
        }
        else if ( src == ControlSliderGroup.SEGMENTS ) {
            state_.setNumSegmentsPerRev(value);
        }
        updateEqn();
    }

    /**
     * a button was pressed.
     * @param e event
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();

        if ( source instanceof GradientButton ) {
            String obj = ((AbstractButton) source).getText();
            if ( sliderGroup_.getSliderValue(ControlSliderGroup.RAD2) != 0 ) {
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
}