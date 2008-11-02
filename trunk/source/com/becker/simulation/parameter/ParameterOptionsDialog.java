package com.becker.simulation.parameter;


import com.becker.simulation.dice.*;
import com.becker.ui.*;
import com.becker.simulation.common.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker Date: 2007
 */
public class ParameterOptionsDialog extends SimulatorOptionsDialog {


    /** number of dice to use.   */
    private JComboBox parameterChoiceField_;
    private JCheckBox showRedistribution_;
    
    private ParameterDistributionType paramType;
    
    

    /**
     * constructor
     */ 
    public ParameterOptionsDialog( Frame parent, Simulator simulator )
    {
        super( parent, simulator );
    }

    public String getTitle()
    {
        return "Paremeter Simulation Configuration";
    }

    protected JPanel createCustomParamPanel()
    {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new BorderLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout( new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Parameter");
        parameterChoiceField_ = new JComboBox();
        parameterChoiceField_.setModel(
                new DefaultComboBoxModel(ParameterDistributionType.values()));

        ParameterSimulator simulator = (ParameterSimulator) getSimulator();
        showRedistribution_ = new JCheckBox("Show Redistribution", simulator.isShowRedistribution());
        innerPanel.add( parameterChoiceField_ );
        innerPanel.add(showRedistribution_);
        JPanel fill = new JPanel();
        paramPanel.add(innerPanel, BorderLayout.NORTH);
        paramPanel.add(fill, BorderLayout.CENTER);

        return paramPanel;
    }

    protected void ok()
    {
        super.ok();

        ParameterSimulator simulator = (ParameterSimulator) getSimulator();
        // set the common rendering and global physics options
        simulator.setParameter(
                ParameterDistributionType.values()[parameterChoiceField_.getSelectedIndex()].getParameter());
        simulator.setShowRedistribution(showRedistribution_.isSelected());
    }

}
