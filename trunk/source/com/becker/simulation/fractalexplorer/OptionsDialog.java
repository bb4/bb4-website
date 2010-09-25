package com.becker.simulation.fractalexplorer;

import com.becker.simulation.common.Simulator;
import com.becker.simulation.common.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Barry Becker
 */
public class OptionsDialog extends SimulatorOptionsDialog {


    public OptionsDialog( JFrame parent, Simulator simulator ) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        FractalExplorer sim = (FractalExplorer) getSimulator();
               
        return panel;
    }



    @Override
    public void actionPerformed( ActionEvent e )
    {
        super.actionPerformed(e);
        Object source = e.getSource();
        FractalExplorer sim = (FractalExplorer) getSimulator();


    }
}
