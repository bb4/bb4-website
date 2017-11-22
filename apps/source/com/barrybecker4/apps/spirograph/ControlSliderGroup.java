/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph;

import com.barrybecker4.apps.spirograph.model.GraphState;
import com.barrybecker4.apps.spirograph.model.ParametricEquations;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

/**
 * That old spirograph game from the 70's brought into the computer age
 * Based on work originially done by David Little.
 *
 * use checkboxes instead of buttons for
 *  - show decoration (was show axis)
 *
 * @author Barry Becker
 */
public class ControlSliderGroup extends SliderGroup
                                implements SliderGroupChangeListener {
    // slider indices
    private static final int RADIUS1 = 0;
    private static final int RADIUS2 = 1;
    private static final int POSITION = 2;
    private static final int VELOCITY = 3;
    private static final int LINE_WIDTH = 4;
    private static final int SEGMENTS = 5;

    /** Initialize the sliders in the group */
    private static final SliderProperties[] SLIDER_PROPS = {
        new SliderProperties("Radius1",      5,       255,      60),
        new SliderProperties("Radius2",    -59,       200,      60),
        new SliderProperties("Position",  -300,       300,      60),
        new SliderProperties("Speed",        1,  GraphState.VELOCITY_MAX, GraphState.VELOCITY_MAX/2),
        new SliderProperties("Line Width",   1,        50, GraphState.INITIAL_LINE_WIDTH),
        new SliderProperties("Num Segments/Revolution",  GraphState.DEFAULT_NUM_SEGMENTS/12,
                4*GraphState.DEFAULT_NUM_SEGMENTS,   GraphState.DEFAULT_NUM_SEGMENTS),
    };

    private GraphState state;
    private GraphPanel graphPanel;

    /**
     * Constructor.
     */
    public ControlSliderGroup(GraphPanel graphPanel, GraphState state) {
        super(SLIDER_PROPS);
        this.graphPanel = graphPanel;
        this.state = state;
        addSliderChangeListener(this);
    }

    public double getRadius2Value()  {
        return getSliderValue(RADIUS2);
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

        if ( src == ControlSliderGroup.RADIUS1) {
            int n = getSliderValueAsInt(ControlSliderGroup.RADIUS2);
            if ( n < 2 - value ) {
                n = 1 - value;
                setSliderValue(ControlSliderGroup.RADIUS2, n);
                state.setR2(n);
            }
            setSliderMinimum(ControlSliderGroup.RADIUS2, ( 2 - value ));
            state.setR1(value);
        }
        else if ( src == ControlSliderGroup.RADIUS2) {
            state.setR2(value);
        }
        else if ( src == ControlSliderGroup.POSITION) {
            state.setPos(value);
        }
        else if ( src == ControlSliderGroup.VELOCITY) {
            state.setVelocity(value);
        }
        else if ( src == ControlSliderGroup.LINE_WIDTH ) {
            state.setWidth(value);
        }
        else if ( src == ControlSliderGroup.SEGMENTS ) {
            state.setNumSegmentsPerRev(value);

        }
        else {
            throw new IllegalArgumentException("Unexpected slider index=" + src);
        }
        autoUpdate();
    }

    private void autoUpdate()
    {
        if ( state.isMaxVelocity())  {
            graphPanel.reset();
            graphPanel.drawCompleteGraph();
        }
        else {
            graphPanel.repaint();
        }
    }

    public static GraphState createGraphState() {
        GraphState state = new GraphState();
        state.params.setR1((float) SLIDER_PROPS[RADIUS1].getInitialValue());
        state.params.setR2((float) SLIDER_PROPS[RADIUS2].getInitialValue());
        state.params.setPos((float) SLIDER_PROPS[POSITION].getInitialValue());
        state.setVelocity((int) SLIDER_PROPS[VELOCITY].getInitialValue());
        state.setWidth((int) SLIDER_PROPS[LINE_WIDTH].getInitialValue());
        state.setNumSegmentsPerRev((int) SLIDER_PROPS[SEGMENTS].getInitialValue());
        return state;
    }

    /**
     * @return the x and y parametric equations in a 2 element list.
     */
    public ParametricEquations getEquations()
    {
        int rad = getSliderValueAsInt(ControlSliderGroup.RADIUS2);
        int combinedRad = getSliderValueAsInt(ControlSliderGroup.RADIUS1) + rad;
        int pos = getSliderValueAsInt(ControlSliderGroup.POSITION);

        return new ParametricEquations(rad, combinedRad, pos);
    }
}