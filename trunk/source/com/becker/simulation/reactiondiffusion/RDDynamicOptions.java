package com.becker.simulation.reactiondiffusion;

import com.becker.ui.legend.*;
import com.becker.ui.*;
import com.becker.ui.sliders.LabeledSlider;
import com.becker.ui.sliders.SliderChangeListener;
import com.becker.ui.sliders.SliderGroup;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * @author Barry Becker Date: Nov 5, 2006
 */
public class RDDynamicOptions extends JPanel
                              implements ActionListener, SliderChangeListener {

    private GrayScott gs_;
    private RDSimulator simulator_;

    private JCheckBox showU_;
    private JCheckBox showV_;
    private JCheckBox useConcurrency_;

    private ContinuousColorLegend legend_;
    

    private static final String K_SLIDER = "K";
    private static final String F_SLIDER = "F";
    private static final String H_SLIDER = "H";    
    private static final String BH_SLIDER = "Bump Height";
    private static final String SH_SLIDER = "Specular Highlight";
    private static final String NS_SLIDER = "Num Steps per Frame";
    
    private SliderGroup sliderGroup_;
    
    private static final String[] SLIDER_NAMES = {
        K_SLIDER,      F_SLIDER,     H_SLIDER,    BH_SLIDER,  SH_SLIDER,  NS_SLIDER
    };
    private static final double[] SLIDER_MIN = {
         0,                     0,                 0.008,            0.0,             0.0,             RDSimulator.DEFAULT_STEPS_PER_FRAME/10.0
    };
    private static final double[] SLIDER_MAX = {
         0.3,                 0.3,              0.048,            30.0,            1.0,             4.0*RDSimulator.DEFAULT_STEPS_PER_FRAME
    }; 
    private static final double[] SLIDER_INITIAL = {
     GrayScott.K0,  GrayScott.F0,   GrayScott.H0,  0.0,             0.0,            RDSimulator.DEFAULT_STEPS_PER_FRAME
    };
    private static final double[] SCALE_FACTORS = {
        1000,             1000,              10000,             10,             100,            1
    }; 
 
    

    public RDDynamicOptions(GrayScott gs, RDSimulator simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        gs_ = gs;
        simulator_ = simulator;
        
        sliderGroup_ = new SliderGroup(SLIDER_NAMES, SLIDER_MIN, SLIDER_MAX, SLIDER_INITIAL, SCALE_FACTORS);
        sliderGroup_.addSliderChangeListener(this);

        JPanel uvCheckBoxes = createCheckBoxes();      
        legend_ = new ContinuousColorLegend(null, simulator_.getRenderer().getColorMap(), true);
        
        add(sliderGroup_);
        add(Box.createVerticalStrut(10));
        add(uvCheckBoxes);
        add(Box.createVerticalStrut(10));
        add(legend_);
        add(Box.createVerticalGlue());
    }
    
    private JPanel createCheckBoxes() {
        
        JPanel checkBoxes = new JPanel(new FlowLayout());
        
        RDRenderer r = simulator_.getRenderer();
        showU_ = new JCheckBox("U Value", r.isShowingU());
        showU_.addActionListener(this);
        showV_ = new JCheckBox("V Value", r.isShowingV());
        showV_.addActionListener(this);
        useConcurrency_ = new JCheckBox("Parallel", gs_.isParallelized());
        useConcurrency_.setToolTipText("Will take advantage of multiple processors if present.");
        useConcurrency_.addActionListener(this);
        checkBoxes.add(showU_); //, BorderLayout.EAST);
        checkBoxes.add(Box.createHorizontalGlue());
        checkBoxes.add(showV_); //, BorderLayout.CENTER);
        checkBoxes.add(Box.createHorizontalGlue());
        checkBoxes.add(useConcurrency_);
        
        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }


    public void reset() {
        //gs_.reset();
        sliderGroup_.reset();
        /*
        fSlider_.setValue(gs_.getF());
        kSlider_.setValue(gs_.getK());
        hSlider_.setValue(gs_.getH());
         **/
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
        } else if (e.getSource() == useConcurrency_) {
            gs_.setParallelized(!gs_.isParallelized());
        }
    }

    /**
     * one of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {
        if (sliderName.equals(F_SLIDER)) {
            gs_.setF(value);
        }
        else if (sliderName.equals(K_SLIDER)) {
            gs_.setK(value);
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
    }

}
