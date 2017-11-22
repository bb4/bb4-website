/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph;

import com.barrybecker4.apps.spirograph.model.GraphState;
import com.barrybecker4.apps.spirograph.model.GraphStateChangeListener;
import com.barrybecker4.apps.spirograph.model.ParametricEquations;
import com.barrybecker4.common.app.AppContext;
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

    private ControlSliderGroup sliderGroup;
    private GraphState state;
    private GraphPanel graphPanel;

    private JLabel xFunction, yFunction;
    private GradientButton hide;
    private GradientButton draw;

    /**
     * Constructor
     */
    public ControlPanel(GraphPanel graphPanel, GraphState state) {

        setBorder(BorderFactory.createEmptyBorder(4, 4, 12, 3));
        this.graphPanel = graphPanel;
        this.state = state;
        this.state.addStateListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        sliderGroup = new ControlSliderGroup(graphPanel, state);

        add(sliderGroup);
        add(createButtonGroup());

        ColorSliderGroup colorSelector = new ColorSliderGroup();
        colorSelector.setColorChangeListener(this.state);
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
        hide = createButton(AppContext.getLabel("HIDE_DECORATION"));
        GradientButton reset = createButton(AppContext.getLabel("RESET"));
        draw = createButton(AppContext.getLabel("DRAW"));

        JPanel bl= new JPanel(new BorderLayout());
        bl.add(hide, BorderLayout.CENTER);
        p.add( createButtonPanel(hide) );
        p.add( createButtonPanel(reset) );
        p.add( createButtonPanel(draw) );

        bp.add(p, BorderLayout.CENTER);
        return bp;
    }

    private JPanel createFunctionPanel() {
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new GridLayout(2, 1, 0, 0));
        functionPanel.add(xFunction = new JLabel("", JLabel.CENTER));
        functionPanel.add(yFunction = new JLabel("", JLabel.CENTER));
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

        if ( sliderGroup.getRadius2Value() != 0 ) {
            if (AppContext.getLabel("DRAW").equals(obj) ) {
                draw.setText(AppContext.getLabel("PAUSE"));
                graphPanel.startDrawingGraph();
            }
            else if (AppContext.getLabel("PAUSE").equals(obj) ) {
                graphPanel.setPaused( true );
                draw.setText(AppContext.getLabel("RESUME"));
            }
            else if (AppContext.getLabel("RESUME").equals(obj) ) {
                graphPanel.setPaused( false );
                draw.setText(AppContext.getLabel("PAUSE"));
            }
        }

        if (AppContext.getLabel("RESET").equals(obj) ) {
            graphPanel.reset();
            draw.setText(AppContext.getLabel("DRAW"));
        }
        else if (AppContext.getLabel("HIDE_DECORATION").equals(obj) ) {
            hide.setText(AppContext.getLabel("SHOW_DECORATION"));
            state.setShowDecoration(false);
            graphPanel.repaint();
        }
        else if (AppContext.getLabel("SHOW_DECORATION").equals(obj) ) {
            hide.setText(AppContext.getLabel("HIDE_DECORATION"));
            state.setShowDecoration(true);
            graphPanel.repaint();
        }
    }

    /** implements GraphStateChangeListener interface */
    @Override
    public void parameterChanged() {
        ParametricEquations equations = sliderGroup.getEquations();
        xFunction.setText(equations.getXEquation());
        yFunction.setText(equations.getYEquation());
        if (state.isMaxVelocity()) {
            draw.setText(AppContext.getLabel("DRAW"));
        }
    }

    /** implements GraphStateChangeListener interface */
    @Override
    public void renderingComplete() {
        draw.setText(AppContext.getLabel("DRAW"));
    }
}