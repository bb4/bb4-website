package com.becker.simulation.fractals;

import com.becker.simulation.fractals.algorithm.FractalAlgorithm;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottModel;
import com.becker.ui.legend.ContinuousColorLegend;
import com.becker.ui.sliders.SliderGroup;
import com.becker.ui.sliders.SliderGroupChangeListener;
import com.becker.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class DynamicOptions extends JPanel
                     implements ActionListener, SliderGroupChangeListener {

    private FractalAlgorithm algorithm_;
    private FractalExplorer simulator_;
    private JCheckBox useConcurrency_;
    private JCheckBox useFixedSize_;

    private static final String K_SLIDER = "K";
    private static final String BH_SLIDER = "Bump Height";
    private static final String SH_SLIDER = "Specular Highlight";
    private static final String NS_SLIDER = "Num Steps per Frame";
    private static final String TIMESTEP_SLIDER = "Time Step Size";

    private SliderGroup sliderGroup_;
    private static final double MIN_NUM_STEPS = FractalExplorer.DEFAULT_STEPS_PER_FRAME/10.0;
    private static final double MAX_NUM_STEPS = 4.0 * FractalExplorer.DEFAULT_STEPS_PER_FRAME;

    private static final SliderProperties[] SLIDER_PROPS = {
        new SliderProperties(K_SLIDER,      0,           0.3,      GrayScottModel.K0,     1000),
        new SliderProperties(BH_SLIDER,     0,          30.0,     0.0,               10),
        new SliderProperties(SH_SLIDER,     0,          1.0,      0.0,               100),
        new SliderProperties(NS_SLIDER,  MIN_NUM_STEPS,   MAX_NUM_STEPS,   FractalExplorer.DEFAULT_STEPS_PER_FRAME, 1),
        new SliderProperties(TIMESTEP_SLIDER,   0.1,     2.0,     FractalExplorer.INITIAL_TIME_STEP,    100),
    };


    /**
     * Constructor
     */
    DynamicOptions(FractalAlgorithm algorithm, FractalExplorer simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        algorithm_ = algorithm;
        simulator_ = simulator;
        
        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);

        ContinuousColorLegend legend_ =
                new ContinuousColorLegend(null, simulator_.getColorMap(), true);

        JPanel checkBoxes = createCheckBoxes();
        add(sliderGroup_);
        add(Box.createVerticalStrut(10));
        add(checkBoxes);
        add(Box.createVerticalStrut(10));
        add(legend_);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(10, 1000));
        add(fill);
    }
    
    private JPanel createCheckBoxes() {
     
        //RDRenderingOptions renderingOptions = simulator_.getRenderingOptions();

        useConcurrency_ = new JCheckBox("Parallel", algorithm_.isParallelized());
        useConcurrency_.setToolTipText(
                "Take advantage of multiple processors for calculation and rendering if present.");
        useConcurrency_.addActionListener(this);

        useFixedSize_ = new JCheckBox("Fixed Size", simulator_.getUseFixedSize());
        useFixedSize_.addActionListener(this);

        JPanel checkBoxes = new JPanel(new GridLayout(0, 2));

        checkBoxes.add(useConcurrency_);
        checkBoxes.add(useFixedSize_);
        
        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }


    public void reset() {
        sliderGroup_.reset();
    }
    
    /**
     * One of the buttons was pressed.
     */
    public void actionPerformed(ActionEvent e) {
        //RDRenderingOptions renderingOptions = simulator_.getRenderingOptions();


        if (e.getSource() == useConcurrency_) {
            boolean isParallelized = !algorithm_.isParallelized();
            algorithm_.setParallelized(isParallelized);
        }
        else if (e.getSource() == useFixedSize_) {
            simulator_.setUseFixedSize(useFixedSize_.isSelected());
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        if (sliderName.equals(K_SLIDER)) {
            //algorithm_.getModel().setK(value);
        }
        /*
        else if (sliderName.equals(BH_SLIDER)) {
            simulator_.getRenderingOptions().setHeightScale(value);
            sliderGroup_.setEnabled(SH_SLIDER, value > 0);
        }
        else if (sliderName.equals(SH_SLIDER)) {
            simulator_.getRenderingOptions().setSpecular(value);
        }*/
        else if (sliderName.equals(NS_SLIDER)) {
            simulator_.setNumStepsPerFrame((int) value);
        }
        else if (sliderName.equals(TIMESTEP_SLIDER)) {
            simulator_.setTimeStep(value);
        }
    }

}
