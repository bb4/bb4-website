/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.habitat.options;

import com.becker.simulation.common.ui.Simulator;
import com.becker.simulation.common.ui.SimulatorOptionsDialog;
import com.becker.simulation.habitat.HabitatSimulator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class HabitatOptionsDialog extends SimulatorOptionsDialog {

    public HabitatOptionsDialog(Component parent, Simulator simulator) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        HabitatSimulator sim = (HabitatSimulator) getSimulator();

        return panel;
    }
}