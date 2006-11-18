package com.becker.simulation.reactiondiffusion;

import com.becker.ui.legend.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * @author Barry Becker Date: Nov 5, 2006
 */
public class RDDynamicOptions extends JPanel
                              implements ActionListener, AdjustmentListener {

    private GrayScott gs_;
    private RDSimulator simulator_;

    private LabeledSlider kSlider_;
    private LabeledSlider fSlider_;
    private LabeledSlider hSlider_;
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
        restartButton_.setMaximumSize(new Dimension(60, 22));
        restartButton_.addActionListener(this);

        kSlider_ = new LabeledSlider("K = ", GrayScott.K0, 0.0, 0.3);
        kSlider_.addAdjustmentListener(this);

        fSlider_ = new LabeledSlider("F = ", GrayScott.F0, 0.0, 0.3);
        fSlider_.addAdjustmentListener(this);

        hSlider_ = new LabeledSlider("H = ", GrayScott.H0, 0.008, 0.048);
        hSlider_.addAdjustmentListener(this);

        numStepsSlider_ = new LabeledSlider("Num Steps per Frame = ", RDSimulator.DEFAULT_STEPS_PER_FRAME, 1, 100);
        numStepsSlider_.setShowAsInteger(true);
        numStepsSlider_.addAdjustmentListener(this);

        RDRenderer r = simulator_.getRenderer();
        showU_ = new JCheckBox("Show U Value", r.isShowingU());
        showU_.addActionListener(this);
        showV_ = new JCheckBox("Show V Value", r.isShowingV());
        showV_.addActionListener(this);

        legend_ = new ContinuousColorLegend(null, r.getColorMap(), true);

        add(fSlider_);
        add(kSlider_);
        add(hSlider_);
        add(Box.createVerticalStrut(10));
        add(numStepsSlider_);
        add(Box.createVerticalStrut(20));
        add(showU_);
        add(showV_);
        add(Box.createVerticalStrut(20));
        add(restartButton_);
        add(Box.createVerticalStrut(30));
        add(legend_);
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
            legend_.setEditable(!legend_.isEditable());
            repaint();
        }
    }

    /**
     * one of the sliders was moved.
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getAdjustable() == fSlider_.getSlider()) {
            gs_.setF(fSlider_.getValue());
        }
        else if (e.getAdjustable() == kSlider_.getSlider()) {
            gs_.setK(kSlider_.getValue());
        }
        else if (e.getAdjustable() == hSlider_.getSlider()) {
            gs_.setH(hSlider_.getValue());
        }
        else if (e.getAdjustable() == numStepsSlider_.getSlider()) {
            simulator_.setNumStepsPerFrame((int) numStepsSlider_.getValue());
        }
    }

}
