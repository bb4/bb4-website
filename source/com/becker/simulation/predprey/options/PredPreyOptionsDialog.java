package com.becker.simulation.predprey.options;

import com.becker.simulation.common.ui.Simulator;
import com.becker.simulation.common.ui.SimulatorOptionsDialog;
import com.becker.simulation.predprey.PredPreySimulator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class PredPreyOptionsDialog extends SimulatorOptionsDialog {

    public PredPreyOptionsDialog(JFrame parent, Simulator simulator) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        PredPreySimulator sim = (PredPreySimulator) getSimulator();

        return panel;
    }

}