package com.becker.simulation.fluid;

import com.becker.ui.legend.*;
import com.becker.ui.*;
import com.becker.ui.sliders.SliderGroupChangeListener;
import com.becker.ui.sliders.SliderGroup;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dynamic controls for the Fluid simulation.
 * @author Barry Becker 
 */
public class FluidDynamicOptions extends JPanel
                              implements ActionListener, SliderGroupChangeListener {


    private FluidSimulator simulator_;

    private JCheckBox useConcurrency_;

    private ContinuousColorLegend legend_;
    
    private static final String DR_SLIDER = "Diffusion Rate";
    private static final String VISC_SLIDER = "Viscosity";
    private static final String FORCE_SLIDER = "Force";    
    private static final String SD_SLIDER = "Source Density";
    private static final String NS_SLIDER = "Num Steps per Frame";

    private SliderGroup sliderGroup_;
    
    private static final String[] SLIDER_NAMES = {
        DR_SLIDER,  VISC_SLIDER,   FORCE_SLIDER,  SD_SLIDER,   NS_SLIDER
    };
    private static final double[] SLIDER_MIN = {
         0,                  0,                    0.01,                   0.01,             FluidSimulator.DEFAULT_STEPS_PER_FRAME/10.0
    };
    private static final double[] SLIDER_MAX = {
         9.0,             8.0,                 30.0,                     4.0,               4.0*FluidSimulator.DEFAULT_STEPS_PER_FRAME
    }; 
    private static final double[] SLIDER_INITIAL = {
        FluidEnvironment.DEFAULT_DIFFUSION_RATE,  
                          FluidEnvironment.DEFAULT_VISCOSITY,   
                                                   InteractionHandler.DEFAULT_FORCE,   
                                                                              InteractionHandler.DEFAULT_SOURCE_DENSITY,    
                                                                                                   FluidSimulator.DEFAULT_STEPS_PER_FRAME
    };
    private static final double[] SCALE_FACTORS = {
         100.0,           100.0,             100.0,                 100.0,         1.0
    }; 
 

    public FluidDynamicOptions(FluidSimulator simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        simulator_ = simulator;

        sliderGroup_ = new SliderGroup(SLIDER_NAMES, SLIDER_MIN, SLIDER_MAX, SLIDER_INITIAL, SCALE_FACTORS);
        sliderGroup_.addSliderChangeListener(this);
    
        JPanel checkBoxes = createCheckBoxes();
        
        legend_ = new ContinuousColorLegend(null, simulator_.getRenderer().getColorMap(), true);

        add(sliderGroup_);
      
        add(Box.createVerticalStrut(10));
        add(checkBoxes);
        add(Box.createVerticalStrut(10));
        add(legend_);
    }
    
    private JPanel createCheckBoxes() {
        
        JPanel checkBoxes = new JPanel(new FlowLayout());
        
        EnvironmentRenderer r = simulator_.getRenderer(); 
   
        useConcurrency_ = new JCheckBox("Parallel", false); // add var here
        useConcurrency_.setToolTipText("Will take advantage of multiple processors if present.");
        useConcurrency_.addActionListener(this);
        checkBoxes.add(Box.createHorizontalGlue());
        checkBoxes.add(useConcurrency_);
        
        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }


    public void reset() {       
            sliderGroup_.reset();          
    }
    
    /**
     * One of the buttons was pressed
     */
    public void actionPerformed(ActionEvent e) {
        EnvironmentRenderer r = simulator_.getRenderer();
    
         if (e.getSource() == useConcurrency_) {
            // gs_.setParallelized(!gs_.isParallelized());
        }
    }
       
        
    /**
     * one of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {
        
        float v = (float) value;
        if (sliderName.equals(VISC_SLIDER)) {
            simulator_.getEnvironment().setViscosity(v);
        }
        else if (sliderName.equals(DR_SLIDER)) {
             simulator_.getEnvironment().setViscosity(v);
        }   
        else if (sliderName.equals(FORCE_SLIDER)) {
            simulator_.getInteractionHandler().setForce(v);
        }
        else if (sliderName.equals(SD_SLIDER)) {
            simulator_.getInteractionHandler().setSourceDensity(v);
        }
        else if (sliderName.equals(NS_SLIDER)) {
            simulator_.setNumStepsPerFrame((int) v);
        }
    }

}
