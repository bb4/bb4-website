package com.becker.simulation.liquid;

import com.becker.simulation.liquid.config.ConfigurationEnum;
import com.becker.simulation.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Bary Becker
 */
class LiquidOptionsDialog extends NewtonianSimOptionsDialog
{

     /** type of distribution function to test.   */
    private JComboBox configurationChoiceField_;

    // constructor
    LiquidOptionsDialog( Frame parent, LiquidSimulator simulator ) {
        super( parent, simulator );
    }


    protected JPanel createCustomParamPanel() {

        JPanel customParamPanel = new JPanel();
        customParamPanel.setLayout( new BorderLayout() );

        JPanel liquidParamPanel = new JPanel();
        liquidParamPanel.setLayout( new BoxLayout(liquidParamPanel, BoxLayout.Y_AXIS ) );
        liquidParamPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Liquid Parameters" ) );

        //LiquidSimulator simulator = (LiquidSimulator) getSimulator();

        configurationChoiceField_ = createConfigChoice();

        liquidParamPanel.add( configurationChoiceField_ );
        customParamPanel.add(liquidParamPanel, BorderLayout.NORTH);

        return customParamPanel;
    }

    private JComboBox createConfigChoice() {

        JComboBox configurationChoice = new JComboBox();

        configurationChoice.setModel(
                new DefaultComboBoxModel(ConfigurationEnum.values()));
        configurationChoice.setToolTipText(ConfigurationEnum.values()[0].getDescription());
        configurationChoice.addActionListener(this);
        return configurationChoice;
    }
    
    public void actionPerformed( ActionEvent e )
    {
        super.actionPerformed(e);
        
        Object source = e.getSource();

        if ( source == configurationChoiceField_ ) {
            
            ConfigurationEnum selectedValue =
                   ((ConfigurationEnum)configurationChoiceField_.getSelectedItem());
            configurationChoiceField_.setToolTipText(selectedValue.getDescription());
        }
    }


    @Override
    protected void ok() {

        // set the liquid environment
        LiquidSimulator simulator = (LiquidSimulator) getSimulator();

        ConfigurationEnum selected = (ConfigurationEnum) configurationChoiceField_.getSelectedItem();

        simulator.loadEnvironment(selected.getFileName());

        super.ok();
    }

}