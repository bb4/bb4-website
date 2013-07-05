/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph;

import com.barrybecker4.apps.spirograph.model.GraphState;
import com.barrybecker4.apps.spirograph.model.GraphStateChangeListener;
import com.barrybecker4.apps.spirograph.model.ParametricEquations;
import com.barrybecker4.common.AppContext;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.sliders.ColorSliderGroup;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A panel that holds sliders and buttons that control the rendering of the spirograph.
 *
 * @author Barry Becker
 */
public class ControlPanel extends JPanel
                          implements ActionListener, GraphStateChangeListener {

    private ControlSliderGroup sliderGroup_;
    private GraphState state_;
    private GraphPanel graphPanel_;

    private JLabel xFunction_, yFunction_;
    private GradientButton hide_;
    private GradientButton draw_;

    /**
     * Constructor
     */
    public ControlPanel(GraphPanel graphPanel, GraphState state) {

        setBorder(BorderFactory.createEmptyBorder(4, 4, 12, 3));
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

        add(createFunctionPanel());

        parameterChanged();
    }

    private JPanel createButtonGroup()  {
        JPanel bp = new JPanel(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS) );
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                                                       BorderFactory.createEtchedBorder(EtchedBorder.RAISED)));
        hide_ = createButton(AppContext.getLabel("HIDE_DECORATION"));
        GradientButton reset = createButton(AppContext.getLabel("RESET"));
        draw_ = createButton(AppContext.getLabel("DRAW"));

        JPanel bl= new JPanel(new BorderLayout());
        bl.add(hide_, BorderLayout.CENTER);
        p.add( createButtonPanel(hide_) );
        p.add( createButtonPanel(reset) );
        p.add( createButtonPanel(draw_) );

        bp.add(p, BorderLayout.CENTER);
        return bp;
    }

    private JPanel createFunctionPanel() {
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new GridLayout(2, 1, 0, 0));
        functionPanel.add(xFunction_ = new JLabel("", JLabel.CENTER));
        functionPanel.add(yFunction_ = new JLabel("", JLabel.CENTER));
        return functionPanel;
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
    @Override
    public void actionPerformed( ActionEvent e ) {

        Object source = e.getSource();
        assert source instanceof GradientButton;
        String obj = ((AbstractButton) source).getText();

        if ( sliderGroup_.getRadius2Value() != 0 ) {
            if (AppContext.getLabel("DRAW").equals(obj) ) {
                draw_.setText(AppContext.getLabel("PAUSE"));
                graphPanel_.startDrawingGraph();
            }
            else if (AppContext.getLabel("PAUSE").equals(obj) ) {
                graphPanel_.setPaused( true );
                draw_.setText(AppContext.getLabel("RESUME"));
            }
            else if (AppContext.getLabel("RESUME").equals(obj) ) {
                graphPanel_.setPaused( false );
                draw_.setText(AppContext.getLabel("PAUSE"));
            }
        }

        if (AppContext.getLabel("RESET").equals(obj) ) {
            graphPanel_.reset();
            draw_.setText(AppContext.getLabel("DRAW"));
        }
        else if (AppContext.getLabel("HIDE_DECORATION").equals(obj) ) {
            hide_.setText(AppContext.getLabel("SHOW_DECORATION"));
            state_.setShowDecoration(false);
            graphPanel_.repaint();
        }
        else if (AppContext.getLabel("SHOW_DECORATION").equals(obj) ) {
            hide_.setText(AppContext.getLabel("HIDE_DECORATION"));
            state_.setShowDecoration(true);
            graphPanel_.repaint();
        }
    }

    /** implements GraphStateChangeListener interface */
    @Override
    public void parameterChanged() {
        ParametricEquations equations = sliderGroup_.getEquations();
        xFunction_.setText(equations.getXEquation());
        yFunction_.setText(equations.getYEquation());
        if (state_.isMaxVelocity()) {
            draw_.setText(AppContext.getLabel("DRAW"));
        }
    }

    /** implements GraphStateChangeListener interface */
    @Override
    public void renderingComplete() {
        draw_.setText(AppContext.getLabel("DRAW"));
    }
}