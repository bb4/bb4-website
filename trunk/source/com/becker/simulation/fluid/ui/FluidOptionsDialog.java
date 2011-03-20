package com.becker.simulation.fluid.ui;

import com.becker.simulation.common.NewtonianSimOptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different simulation options.
 *
 * @author Bary Becker
 */
class FluidOptionsDialog extends NewtonianSimOptionsDialog
                         implements ActionListener {

    FluidOptionsDialog( JFrame parent, FluidSimulator simulator ) {
        super( parent, simulator );
    }

    protected JPanel createCustomParamPanel() {

        JPanel customParamPanel = new JPanel();
        customParamPanel.setLayout( new BorderLayout() );

        JPanel liquidParamPanel = new JPanel();
        liquidParamPanel.setLayout( new BoxLayout(liquidParamPanel, BoxLayout.Y_AXIS ) );
        liquidParamPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Liquid Parameters" ) );

        //FluidSimulator simulator = (FluidSimulator) getSimulator();
        customParamPanel.add(liquidParamPanel, BorderLayout.NORTH);

        return customParamPanel;
    }
}