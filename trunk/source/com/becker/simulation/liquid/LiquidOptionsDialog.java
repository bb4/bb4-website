package com.becker.simulation.liquid;

import com.becker.simulation.common.NewtonianSimOptionsDialog;
import com.becker.simulation.common.NewtonianSimulator;
import com.becker.simulation.liquid.config.ConfigurationEnum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Bary Becker
 */
class LiquidOptionsDialog extends NewtonianSimOptionsDialog {

     /** type of distribution function to test.   */
    private JComboBox configurationChoiceField_;
    private JCheckBox showPressureCheckbox_;
    private JCheckBox showCellStatusCheckbox_;

    /** constructor  */
    LiquidOptionsDialog( JFrame parent, LiquidSimulator simulator ) {
        super( parent, simulator );
    }


    @Override
    protected void addAdditionalToggles(JPanel togglesPanel) {

        NewtonianSimulator sim = (NewtonianSimulator) getSimulator();
        togglesPanel.add(createMeshCheckBox(sim));
        togglesPanel.add(createVelocitiesCheckBox(sim));
        togglesPanel.add(createPressureCheckBox(sim));
        togglesPanel.add(createCellStatusCheckBox(sim));
    }

    protected JCheckBox createPressureCheckBox(NewtonianSimulator sim) {
        showPressureCheckbox_ = new JCheckBox( "Show Pressures", sim.getShowForceVectors());
        showPressureCheckbox_.setToolTipText( "show colors for the different pressures" );
        showPressureCheckbox_.addActionListener( this );
        return showPressureCheckbox_;
    }

    protected JCheckBox createCellStatusCheckBox(NewtonianSimulator sim) {
        showCellStatusCheckbox_ = new JCheckBox( "Show Cell Status", sim.getShowForceVectors());
        showCellStatusCheckbox_.setToolTipText( "show status for each of the cells" );
        showCellStatusCheckbox_.addActionListener( this );
        return showCellStatusCheckbox_;
    }


    @Override
    protected JPanel createCustomParamPanel() {

        JPanel customParamPanel = new JPanel();
        customParamPanel.setLayout( new BorderLayout() );

        JPanel liquidParamPanel = new JPanel();
        liquidParamPanel.setLayout( new BoxLayout(liquidParamPanel, BoxLayout.Y_AXIS ) );
        liquidParamPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Liquid Parameters" ) );

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
    
    @Override
    public void actionPerformed( ActionEvent e ) {
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

        simulator.getRenderingOptions().setShowPressures(showPressureCheckbox_.isSelected());
        simulator.getRenderingOptions().setShowCellStatus(showCellStatusCheckbox_.isSelected());

        super.ok();
    }
}