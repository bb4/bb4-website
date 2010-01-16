package com.becker.apps.spirograph;

import com.becker.ui.sliders.SliderGroup;
import com.becker.ui.sliders.SliderProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * That old spirograph game from the 70's brought into the computer age
 * Based on work originially done by David Little.
 *
 * @author Barry Becker
 */
public class ControlSliderGroup extends SliderGroup
{
    // slider indices   (perhaps make private)
    public static final int RAD1 = 0;
    public static final int RAD2 = 1;
    public static final int POS = 2;
    public static final int VEL = 3;
    public static final int LINE_WIDTH = 4;
    public static final int SEGMENTS = 5;

    private static final SliderProperties[] SLIDER_PROPS = {
        new SliderProperties("Radius1",         5,        255,      60),
        new SliderProperties("Radius2",       -59,        200,      60),
        new SliderProperties("Position",      -300,       300,      60),
        new SliderProperties("Speed",           1, GraphState.VELOCITY_MAX,     3),
        new SliderProperties("Line Width",      1,         50, GraphState.INITIAL_LINE_WIDTH),
        new SliderProperties("Num Segments/Revolution",  GraphState.DEFAULT_NUM_SEGMENTS/10,
                4*GraphState.DEFAULT_NUM_SEGMENTS,   GraphState.DEFAULT_NUM_SEGMENTS),
    };


    public ControlSliderGroup() {

        super(SLIDER_PROPS);

    }

    public static GraphState createGraphState() {
        GraphState state = new GraphState();
        state.params.setR1((float) SLIDER_PROPS[RAD1].getInitialValue());
        state.params.setR2((float) SLIDER_PROPS[RAD2].getInitialValue());
        state.params.setPos((float) SLIDER_PROPS[POS].getInitialValue());
        state.setVelocity((int) SLIDER_PROPS[VEL].getInitialValue());
        state.setWidth((int) SLIDER_PROPS[LINE_WIDTH].getInitialValue());
        state.setNumSegmentsPerRev((int) SLIDER_PROPS[SEGMENTS].getInitialValue());
        return state;
    }

    /**
     * @return the x and y parametric equations in a 2 element list.
     */
    public ParametricEquations getEquations()
    {
        int rad = getSliderValueAsInt(ControlSliderGroup.RAD2);
        int combinedRad = getSliderValueAsInt(ControlSliderGroup.RAD1) + rad;
        int pos = getSliderValueAsInt(ControlSliderGroup.POS);

        return new ParametricEquations(rad, combinedRad, pos);
    }
}