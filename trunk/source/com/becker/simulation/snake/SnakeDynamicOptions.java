package com.becker.simulation.snake;

import com.becker.ui.sliders.SliderGroup;
import com.becker.ui.sliders.SliderGroupChangeListener;
import com.becker.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class SnakeDynamicOptions extends JPanel
                          implements SliderGroupChangeListener {

    private SnakeSimulator snakeSim_;


    private static final String DIRECTION_SLIDER = "Direction";
    private static final String TIMESTEP_SLIDER = "Time Step Size";

    private SliderGroup sliderGroup_;

    private static final SliderProperties[] SLIDER_PROPS = {
        new SliderProperties(DIRECTION_SLIDER,    -1.0,       1.0,    0.0,     100),
        new SliderProperties(TIMESTEP_SLIDER,    0.001,       1.0,    0.02,    10000),
    };

    /**
     * Constructor
     */
    SnakeDynamicOptions(SnakeSimulator snake) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        snakeSim_ = snake;
        
        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);
        
        add(sliderGroup_);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(10, 1000));
        add(fill);
    }

    public void reset() {
        sliderGroup_.reset();
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {
        if (sliderName.equals(DIRECTION_SLIDER)) {
            snakeSim_.setDirection(value);
        }
        else if (sliderName.equals(TIMESTEP_SLIDER)) {
            snakeSim_.setTimeStep(value);
        }
    }
}
