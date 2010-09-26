package com.becker.simulation.fluid;

import com.becker.simulation.common.NewtonianSimOptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Bary Becker
 */
class FluidOptionsDialog extends NewtonianSimOptionsDialog
                          implements ActionListener
{


    // constructor
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

        FluidSimulator simulator = (FluidSimulator) getSimulator();

        /*
        waveSpeedField_ = new JTextField( Double.toString( simulator.getSnake().getWaveSpeed() ) );
        waveSpeedField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p1 =
                new NumberInputPanel( "Wave Speed (.001 slow - .9 fast):  ", waveSpeedField_ );
        p1.setToolTipText( "This controls the speed at which the force function that travels down the body of the snake" );
        liquidParamPanel.add( p1 );
        */

        customParamPanel.add(liquidParamPanel, BorderLayout.NORTH);

        return customParamPanel;
    }

    protected void ok() {

        super.ok();

        // set the snake params
        FluidSimulator simulator = (FluidSimulator) getSimulator();
    }

}