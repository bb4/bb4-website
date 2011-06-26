package com.becker.simulation.henonphase;

import com.becker.simulation.henonphase.algorithm.HenonAlgorithm;
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

    private HenonAlgorithm algorithm_;
    private HenonPhaseExplorer simulator_;
    private JCheckBox useConcurrency_;
    private JCheckBox useFixedSize_;
    private JCheckBox useUniformSeeds_;

    private static final String PHASE_ANGLE_SLIDER = "Phase Angle";
    private static final String NUM_TRAVELORS_SLIDER = "Num Travelor Particles";
    private static final String ITER_PER_FRAME_SLIDER = "Num Iterations per Frame";
    private static final String ITER_SLIDER = "Max Iterations";

    private SliderGroup sliderGroup_;


    private static final SliderProperties[] SLIDER_PROPS = {

        new SliderProperties(PHASE_ANGLE_SLIDER,     0,    2.0 * Math.PI,    HenonAlgorithm.DEFAULT_PHASE_ANGLE,  1000.0),
        new SliderProperties(NUM_TRAVELORS_SLIDER,  1,  10000,    HenonAlgorithm.DEFAULT_NUM_TRAVELERS),
        new SliderProperties(ITER_PER_FRAME_SLIDER,  1,  HenonAlgorithm.DEFAULT_MAX_ITERATIONS/10, HenonAlgorithm.DEFAULT_FRAME_ITERATIONS),
        new SliderProperties(ITER_SLIDER,      100,           100000,      HenonAlgorithm.DEFAULT_MAX_ITERATIONS),
    };


    /**
     * Constructor
     */
    DynamicOptions(HenonAlgorithm algorithm, HenonPhaseExplorer simulator) {

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

        useUniformSeeds_ = new JCheckBox("Uniform seeds", algorithm_.getUseUniformSeeds());
        useUniformSeeds_.addActionListener(this);

        JPanel checkBoxes = new JPanel(new GridLayout(0, 1));

        checkBoxes.add(useConcurrency_);
        checkBoxes.add(useFixedSize_);
        checkBoxes.add(useUniformSeeds_);
        
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
        else if (e.getSource() == useUniformSeeds_) {
            algorithm_.toggleUseUniformSeeds();
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        if (sliderName.equals(ITER_SLIDER)) {
            algorithm_.setMaxIterations((int)value);
        }

        else if (sliderName.equals(PHASE_ANGLE_SLIDER)) {
            algorithm_.setPhaseAngle(value);
        }
        else if (sliderName.equals(NUM_TRAVELORS_SLIDER)) {
            algorithm_.setNumTravelors((int)value);
        }
        else if (sliderName.equals(ITER_PER_FRAME_SLIDER)) {
            algorithm_.setStepsPerFrame((int)value);
        }
    }

}
