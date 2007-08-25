package com.becker.simulation.reactiondiffusion;

import com.becker.ui.legend.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * @author Barry Becker Date: Nov 5, 2006
 */
public class RDDynamicOptions extends JPanel
                              implements ActionListener, ChangeListener {

    private GrayScott gs_;
    private RDSimulator simulator_;

    private LabeledSlider kSlider_;
    private LabeledSlider fSlider_;
    private LabeledSlider hSlider_;
    private LabeledSlider heightSlider_;
    private LabeledSlider specularSlider_;
    private LabeledSlider numStepsSlider_;
    private Button restartButton_;

    private JCheckBox showU_;
    private JCheckBox showV_;

    private ContinuousColorLegend legend_;


    public RDDynamicOptions(GrayScott gs, RDSimulator simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        gs_ = gs;
        simulator_ = simulator;

        restartButton_ = new Button("Restart");
        restartButton_.setMaximumSize(new Dimension(60, 20));
        restartButton_.addActionListener(this);

        kSlider_ = new LabeledSlider("K = ", GrayScott.K0, 0.0, 0.3);
        kSlider_.addChangeListener(this);

        fSlider_ = new LabeledSlider("F = ", GrayScott.F0, 0.0, 0.3);
        fSlider_.addChangeListener(this);

        hSlider_ = new LabeledSlider("H = ", GrayScott.H0, 0.008, 0.048);
        hSlider_.addChangeListener(this);

        heightSlider_ = new LabeledSlider("Bump Height = ", 0.0, 0.0, 30.0);
        heightSlider_.addChangeListener(this);

        specularSlider_ = new LabeledSlider("Specular Higlight = ", 0.0, 0.0, 1.0);
        specularSlider_.addChangeListener(this);
        specularSlider_.setEnabled(false);

        numStepsSlider_ = new LabeledSlider("Num Steps per Frame = ", RDSimulator.DEFAULT_STEPS_PER_FRAME, 1, 100);
        numStepsSlider_.setShowAsInteger(true);
        numStepsSlider_.addChangeListener(this);

        JPanel uvCheckBoxes = createUVCheckBoxes();
        
        legend_ = new ContinuousColorLegend(null, simulator_.getRenderer().getColorMap(), true);

        add(fSlider_);
        add(kSlider_);
        add(hSlider_);
        add(Box.createVerticalStrut(10));
        add(heightSlider_);
        add(specularSlider_);
        add(Box.createVerticalStrut(10));
        add(numStepsSlider_);
        add(Box.createVerticalStrut(10));
        add(uvCheckBoxes);
        add(Box.createVerticalStrut(10));
        add(restartButton_);
        add(Box.createVerticalStrut(10));
        add(legend_);
    }
    
    private JPanel createUVCheckBoxes() {
        
        JPanel uvCheckBoxes = new JPanel(new FlowLayout());
        
        //.setPreferredSize(new Dimension(300, 30));
        RDRenderer r = simulator_.getRenderer();
        showU_ = new JCheckBox("Show U Value", r.isShowingU());
        showU_.addActionListener(this);
        showV_ = new JCheckBox("Show V Value", r.isShowingV());
        showV_.addActionListener(this);
        uvCheckBoxes.add(showU_); //, BorderLayout.EAST);
        uvCheckBoxes.add(Box.createHorizontalGlue());
        uvCheckBoxes.add(showV_); //, BorderLayout.CENTER);
        uvCheckBoxes.setBorder(BorderFactory.createEtchedBorder());
        return uvCheckBoxes;
    }


    /**
     * The restart button was pressed.
     */
    public void actionPerformed(ActionEvent e) {
        RDRenderer r = simulator_.getRenderer();

        if (e.getSource() == restartButton_) {
            gs_.reset();
            fSlider_.setValue(gs_.getF());
            kSlider_.setValue(gs_.getK());
            hSlider_.setValue(gs_.getH());
        }
        else if (e.getSource() == showU_) {
            r.setShowingU(!r.isShowingU());
        }
        else if (e.getSource() == showV_) {
            r.setShowingV(!r.isShowingV());
            repaint();
        }
    }

    /**
     * one of the sliders was moved.
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == fSlider_.getSlider()) {
            gs_.setF(fSlider_.getValue());
        }
        else if (e.getSource() == kSlider_.getSlider()) {
            gs_.setK(kSlider_.getValue());
        }
        else if (e.getSource() == hSlider_.getSlider()) {
            gs_.setH(hSlider_.getValue());
        }
        else if (e.getSource() == heightSlider_.getSlider()) {
            simulator_.getRenderer().setHeightScale(heightSlider_.getValue());
            specularSlider_.setEnabled(heightSlider_.getValue() > 0);
        }
        else if (e.getSource() == specularSlider_.getSlider()) {
            simulator_.getRenderer().setSpecular(specularSlider_.getValue());
        }
        else if (e.getSource() == numStepsSlider_.getSlider()) {
            simulator_.setNumStepsPerFrame((int) numStepsSlider_.getValue());
        }
    }

}