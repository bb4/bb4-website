package com.becker.simulation.reactiondiffusion;

import com.becker.simulation.common.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Barry Becker
 */
public class RDOptionsDialog extends SimulatorOptionsDialog {

    private JCheckBox offscreenRenderingCheckbox_;

    private JCheckBox showProfilingCheckbox_;
    private JCheckBox useParallelRenderingCheckbox_;
    private JCheckBox synchronizeRenderingCheckbox_;


    public RDOptionsDialog( JFrame parent, Simulator simulator ) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        RDSimulator sim = (RDSimulator) getSimulator();

        showProfilingCheckbox_ = addCheckBox("Show Profiling Information",
                        "If checked, profiling statistics will be displayed in the console when paused.",
                        RDProfiler.getInstance().isEnabled());

        offscreenRenderingCheckbox_ =  addCheckBox("Use offscreen rendering",
                        "If checked, rendering graphics to an offscreen buffer before copying to the screen.",
                        sim.getUseOffScreenRendering());

        useParallelRenderingCheckbox_ = addCheckBox("Use parallel rendering",
                        "If you turn this on, your should also turn on synchronized rendering to avoid artifacts",
                        sim.getRenderingOptions().isParallelized());

        synchronizeRenderingCheckbox_ = addCheckBox("Use synchronized rendering",
                        "You don't need this unless parallelized rendering is also checked.",
                        sim.getRenderingOptions().isSynchRendering());

        panel.add(showProfilingCheckbox_);
        panel.add(offscreenRenderingCheckbox_);
        panel.add(useParallelRenderingCheckbox_);
        panel.add(synchronizeRenderingCheckbox_);
               
        return panel;
    }

    private JCheckBox addCheckBox(String label, String tooltip, boolean initiallySelected) {
        JCheckBox cb = new JCheckBox(label, initiallySelected);
        cb.setToolTipText(tooltip);
        cb.addActionListener(this);
        return cb;
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
        else if ( source == useParallelRenderingCheckbox_ ) {
            sim.getRenderingOptions().setParallelized(useParallelRenderingCheckbox_.isSelected());
        }
        else if ( source == synchronizeRenderingCheckbox_ ) {
            sim.getRenderingOptions().setSynchRendering(synchronizeRenderingCheckbox_.isSelected());
        }
    }
}
