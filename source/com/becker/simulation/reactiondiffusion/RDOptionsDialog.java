package com.becker.simulation.reactiondiffusion;

import com.becker.simulation.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Barry Becker
 */
public class RDOptionsDialog extends SimulatorOptionsDialog {

    private JCheckBox offscreenRenderingCheckbox_;

    private JCheckBox showProfilingCheckbox_;


    public RDOptionsDialog( JFrame parent, Simulator simulator ) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        RDSimulator sim = (RDSimulator) getSimulator();

        showProfilingCheckbox_ = new JCheckBox("Show Profiling Information", RDProfiler.getInstance().isEnabled());
        showProfilingCheckbox_.addActionListener(this);
        panel.add(showProfilingCheckbox_);

        offscreenRenderingCheckbox_ = new JCheckBox("Use offscreen rendering", sim.getUseOffScreenRendering());
        offscreenRenderingCheckbox_.addActionListener(this);
        panel.add(offscreenRenderingCheckbox_);

        return panel;
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
        super.actionPerformed(e);
        Object source = e.getSource();
        RDSimulator sim = (RDSimulator) getSimulator();

        if ( source == showProfilingCheckbox_ ) {
            RDProfiler.getInstance().setEnabled(showProfilingCheckbox_.isSelected());
        }
        else if ( source == offscreenRenderingCheckbox_ ) {
            sim.setUseOffscreenRendering(offscreenRenderingCheckbox_.isSelected());
        }
    }

}
