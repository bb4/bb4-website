/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.henonphase;

import com.becker.simulation.common.ui.Simulator;
import com.becker.simulation.common.ui.SimulatorOptionsDialog;
import com.becker.simulation.fractalexplorer.FractalExplorer;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Barry Becker
 */
public class OptionsDialog extends SimulatorOptionsDialog {

    public OptionsDialog(JFrame parent, Simulator simulator) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        HenonPhaseExplorer sim = (HenonPhaseExplorer) getSimulator();
               
        return panel;
    }


    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        Object source = e.getSource();
        HenonPhaseExplorer sim = (HenonPhaseExplorer) getSimulator();
    }
}
