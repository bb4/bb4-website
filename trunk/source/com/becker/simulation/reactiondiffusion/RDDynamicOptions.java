package com.becker.simulation.reactiondiffusion;

import com.becker.simulation.reactiondiffusion.algorithm.GrayScottController;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottModel;
import com.becker.ui.legend.*;
import com.becker.ui.sliders.SliderGroupChangeListener;
import com.becker.ui.sliders.SliderGroup;

import com.becker.ui.sliders.SliderProperties;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * @author Barry Becker Date: Nov 5, 2006
 */
public class RDDynamicOptions extends JPanel
                              implements ActionListener, SliderGroupChangeListener {

    private GrayScottController gs_;
    private RDSimulator simulator_;

    private JCheckBox showU_;
    private JCheckBox showV_;
    private JCheckBox useConcurrency_;
    private JCheckBox useFixedSize_;

    private static final String K_SLIDER = "K";
    private static final String F_SLIDER = "F";
    private static final String H_SLIDER = "H";    
    private static final String BH_SLIDER = "Bump Height";
    private static final String SH_SLIDER = "Specular Highlight";
    private static final String NS_SLIDER = "Num Steps per Frame";
    private static final String TIMESTEP_SLIDER = "Time Step Size";
    
    private SliderGroup sliderGroup_;
    private static final double MIN_NUM_STEPS = RDSimulator.DEFAULT_STEPS_PER_FRAME/10.0;
    private static final double MAX_NUM_STEPS = 4.0*RDSimulator.DEFAULT_STEPS_PER_FRAME;

    private static final SliderProperties[] SLIDER_PROPS = {
        new SliderProperties(K_SLIDER,      0,           0.3,      GrayScottModel.K0,     1000),
        new SliderProperties(F_SLIDER,      0,           0.3,      GrayScottModel.F0,     1000),
        new SliderProperties(H_SLIDER,      0.008,      0.05,      GrayScottController.H0,     10000),
        new SliderProperties(BH_SLIDER,     0,          30.0,     0.0,               10),
        new SliderProperties(SH_SLIDER,     0,          1.0,      0.0,               100),
        new SliderProperties(NS_SLIDER,  MIN_NUM_STEPS,   MAX_NUM_STEPS,   RDSimulator.DEFAULT_STEPS_PER_FRAME, 1),
        new SliderProperties(TIMESTEP_SLIDER,   0.1,     2.0,     RDSimulator.INITIAL_TIME_STEP,    100),
    };


    /**
     * Constructor
     */
    RDDynamicOptions(GrayScottController gs, RDSimulator simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        gs_ = gs;
        simulator_ = simulator;
        
        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);

        JPanel uvCheckBoxes = createCheckBoxes();
        ContinuousColorLegend legend_ =
                new ContinuousColorLegend(null, simulator_.getRenderer().getColorMap(), true);
        
        add(sliderGroup_);
        add(Box.createVerticalStrut(10));
        add(uvCheckBoxes);
        add(Box.createVerticalStrut(10));
        add(legend_);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(10, 1000));
        add(fill);
    }
    
    private JPanel createCheckBoxes() {
     
        RDRenderer r = simulator_.getRenderer();
        showU_ = new JCheckBox("U Value", r.isShowingU());
        showU_.addActionListener(this);
        showV_ = new JCheckBox("V Value", r.isShowingV());
        showV_.addActionListener(this);
        useConcurrency_ = new JCheckBox("Parallel", gs_.isParallelized());
        useConcurrency_.setToolTipText("Will take advantage of multiple processors if present.");
        useConcurrency_.addActionListener(this);
        useFixedSize_ = new JCheckBox("Fixed Size", simulator_.getUseFixedSize());
        useFixedSize_.addActionListener(this);
         JPanel checkBoxes = new JPanel(new GridLayout(0, 2));
        checkBoxes.add(showU_);     //, BorderLayout.EAST);
        checkBoxes.add(useConcurrency_);
        checkBoxes.add(showV_);   
        checkBoxes.add(useFixedSize_);
        
        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }


    public void reset() {
        sliderGroup_.reset();
    }
    
    /**
     * One of the buttons was pressed/
     */
    public void actionPerformed(ActionEvent e) {
        RDRenderer r = simulator_.getRenderer();

        if (e.getSource() == showU_) {
            r.setShowingU(!r.isShowingU());
        }
        else if (e.getSource() == showV_) {
            r.setShowingV(!r.isShowingV());
            repaint();
        }
        else if (e.getSource() == useConcurrency_) {
            gs_.setParallelized(!gs_.isParallelized());
        }
        else if (e.getSource() == useFixedSize_) {
            simulator_.setUseFixedSize(useFixedSize_.isSelected());
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {
        if (sliderName.equals(F_SLIDER)) {
            gs_.getModel().setF(value);
        }
        else if (sliderName.equals(K_SLIDER)) {
            gs_.getModel().setK(value);
        }
        else if (sliderName.equals(H_SLIDER)) {
            gs_.setH(value);
        }
        else if (sliderName.equals(BH_SLIDER)) {
            simulator_.getRenderer().setHeightScale(value);
            sliderGroup_.setEnabled(SH_SLIDER, value > 0);
            //specularSlider_.setEnabled(value > 0);
        }
        else if (sliderName.equals(SH_SLIDER)) {
            simulator_.getRenderer().setSpecular(value);
        }
        else if (sliderName.equals(NS_SLIDER)) {
            simulator_.setNumStepsPerFrame((int) value);
        }
        else if (sliderName.equals(TIMESTEP_SLIDER)) {
            simulator_.setTimeStep(value);
        }
    }

}
