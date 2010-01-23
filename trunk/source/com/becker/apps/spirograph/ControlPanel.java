package com.becker.apps.spirograph;

import com.becker.ui.components.GradientButton;
import com.becker.ui.sliders.ColorSliderGroup;

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
                          implements ActionListener, GraphStateChangeListener
{
    public static final String HIDE_DECORATION_LABEL = "Hide Decoration";
    private static final String SHOW_DECORATIONLABEL = "Show Decoration";
    private static final String CLEAR_LABEL = "Clear Graph";
    private static final String RESET_LABEL = "Reset";
    public static final String DRAW_LABEL = "Draw Graph";
    private static final String PAUSE_LABEL = "Pause";
    private static final String RESUME_LABEL = "Resume Drawing";

    private ControlSliderGroup sliderGroup_;
    private GraphState state_;
    protected GraphPanel graphPanel_;

    private JLabel xFunction_, yFunction_;
    private GradientButton hide_;
    private GradientButton draw_;

    /**
     * Constructor
     */
    public ControlPanel(GraphPanel graphPanel, GraphState state) {
        graphPanel_ = graphPanel;
        state_ = state;
        state_.addStateListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        sliderGroup_ = new ControlSliderGroup(graphPanel, state);

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
        parameterChanged();
        add(q1);
    }

    private JPanel createButtonGroup()
    {
        JPanel bp = new JPanel(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS) );
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                                                       BorderFactory.createEtchedBorder(EtchedBorder.RAISED)));
        hide_ = createButton(HIDE_DECORATION_LABEL);
        GradientButton clear = createButton(CLEAR_LABEL);
        GradientButton reset = createButton(RESET_LABEL);
        draw_ = createButton( DRAW_LABEL );

        JPanel bl= new JPanel(new BorderLayout());
        bl.add(hide_, BorderLayout.CENTER);
        p.add( createButtonPanel(hide_) );
        p.add( createButtonPanel(clear) );
        p.add( createButtonPanel(reset) );
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

    /**
     * a button was pressed.
     * @param e event
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        assert source instanceof GradientButton;
        String obj = ((AbstractButton) source).getText();

        if ( sliderGroup_.getRadius2Value() != 0 ) {
            if ( DRAW_LABEL.equals(obj) ) {
                draw_.setText( PAUSE_LABEL );
                graphPanel_.start();
            }
            else if ( PAUSE_LABEL.equals(obj) ) {
                graphPanel_.setPaused( true );
                draw_.setText( RESUME_LABEL );
            }
            else if ( RESUME_LABEL.equals(obj) ) {
                graphPanel_.setPaused( false );
                draw_.setText( PAUSE_LABEL );   // WAS DRAW
            }
        }

        if ( RESET_LABEL.equals(obj) ) {
            graphPanel_.reset();
            draw_.setText( DRAW_LABEL );
        }
        else if ( HIDE_DECORATION_LABEL.equals(obj) ) {
            state_.setShowDecoration(false);
            hide_.setText(SHOW_DECORATIONLABEL);
            graphPanel_.repaint();
        }
        else if ( SHOW_DECORATIONLABEL.equals(obj) ) {
            hide_.setText(HIDE_DECORATION_LABEL);
            state_.setShowDecoration(true);
            graphPanel_.repaint();
        }
        else if ( CLEAR_LABEL.equals(obj) ) {
            graphPanel_.clear();
        }
    }

    /** implements GraphStateChangeListener interface */
    public void parameterChanged() {
        ParametricEquations equations = sliderGroup_.getEquations();
        xFunction_.setText(equations.getXEquation());
        yFunction_.setText(equations.getYEquation());
        if (state_.isMaxVelocity()) {
            draw_.setText( DRAW_LABEL );
        }
    }

    /** implements GraphStateChangeListener interface */
    public void renderingComplete() {
        draw_.setText( DRAW_LABEL );
    }
}