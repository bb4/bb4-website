package com.becker.simulation.liquid;

import com.becker.simulation.liquid.model.LiquidEnvironment;
import com.becker.simulation.snake.LocomotionParameters;
import com.becker.simulation.snake.SnakeSimulator;
import com.becker.ui.sliders.SliderGroup;
import com.becker.ui.sliders.SliderGroupChangeListener;
import com.becker.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;

/**
 * Dynamic controls for the liquid simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class LiquidDynamicOptions extends JPanel
                          implements SliderGroupChangeListener {

    private LiquidSimulator liquidSim_;

    private static final String VISCOSITY_SLIDER = "Viscosity";
    private static final String B0_SLIDER = "b0";
    private static final String DYNAMIC_FRICTION_SLIDER = "Dynamic friction";
    private static final String TIMESTEP_SLIDER = "Time Step Size";

    private SliderGroup sliderGroup_;


    /**
     * Constructor
     */
    LiquidDynamicOptions(LiquidSimulator liquid) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        liquidSim_ = liquid;
        
        sliderGroup_ = new SliderGroup(createSliderProperties());
        sliderGroup_.addSliderChangeListener(this);
        
        add(sliderGroup_);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(10, 1000));
        add(fill);
    }

    private SliderProperties[] createSliderProperties() {

        SliderProperties[] sliderProps;
        sliderProps = new SliderProperties[] {
                //                                       MIN  MAX   INITIAL   SCALE
                new SliderProperties(VISCOSITY_SLIDER, 0.0, 0.1, LiquidEnvironment.DEFAULT_VISCOSITY, 100),
                new SliderProperties(B0_SLIDER, 0.0, 10.0, LiquidEnvironment.DEFAULT_B0, 100),
                new SliderProperties(DYNAMIC_FRICTION_SLIDER, 0.0, 10.0, 0.1, 100),
                new SliderProperties(TIMESTEP_SLIDER, 0.001, 0.2, 0.01, 1000)};
         return sliderProps;
    }


    public void reset() {
        sliderGroup_.reset();
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        if (sliderName.equals(VISCOSITY_SLIDER)) {
            liquidSim_.getEnvironment().setViscosity(value);
        }
        else if (sliderName.equals(B0_SLIDER)) {
            liquidSim_.getEnvironment().setB0(value);
        }
        else if (sliderName.equals(DYNAMIC_FRICTION_SLIDER)) {
            liquidSim_.setDynamicFriction(value);
        }
        else if (sliderName.equals(TIMESTEP_SLIDER)) {
            liquidSim_.setTimeStep(value);
        }
    }
}
