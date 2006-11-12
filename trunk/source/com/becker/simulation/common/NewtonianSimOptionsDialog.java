package com.becker.simulation.common;

import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker Date: Nov 5, 2006
 */
public abstract class NewtonianSimOptionsDialog extends SimulatorOptionsDialog {


    // rendering option controls
    private JCheckBox drawMeshCheckbox_;
    private JCheckBox showVelocitiesCheckbox_;
    private JCheckBox showForcesCheckbox_;

    // physics param options controls
    private NumberInput staticFrictionField_;
    private NumberInput dynamicFrictionField_;


    // constructor
    public NewtonianSimOptionsDialog( Frame parent, Simulator simulator )
    {
        super( parent, simulator );
    }

    public String getTitle()
    {
        return "Newtonian Simulation Configuration";
    }


    protected void addAdditionalToggles(JPanel togglesPanel) {

        NewtonianSimulator simulator = (NewtonianSimulator) getSimulator();

        drawMeshCheckbox_ = new JCheckBox( "Show Wireframe", simulator.getDrawMesh() );
        drawMeshCheckbox_.setToolTipText( "draw the "+ simulator.getName() + " showing the underlying wireframe mesh");
        drawMeshCheckbox_.addActionListener( this );
        togglesPanel.add( drawMeshCheckbox_ );

        showVelocitiesCheckbox_ = new JCheckBox( "Show Velocity Vectors", simulator.getShowVelocityVectors() );
        showVelocitiesCheckbox_.setToolTipText( "show lines representing velocity vectors on each partical mass" );
        showVelocitiesCheckbox_.addActionListener( this );
        togglesPanel.add( showVelocitiesCheckbox_ );

        showForcesCheckbox_ = new JCheckBox( "Show Force Vectors", simulator.getShowForceVectors() );
        showForcesCheckbox_.setToolTipText( "show lines representing force vectors on each partical mass" );
        showForcesCheckbox_.addActionListener( this );
        togglesPanel.add( showForcesCheckbox_ );
    }

    protected JPanel createGlobalPhysicalParamPanel()
    {
        JPanel globalParamPanel = new JPanel();
        globalParamPanel.setLayout( new BorderLayout() );

        JPanel frictionPanel = new JPanel();
        frictionPanel.setLayout( new BoxLayout(frictionPanel, BoxLayout.Y_AXIS ) );
        frictionPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Friction" ) );

        NewtonianSimulator simulator = (NewtonianSimulator) getSimulator();

        staticFrictionField_ =
                new NumberInput( "static Friction (.0 small - 0.4 large):  ", simulator.getStaticFriction(),
                                 "This controls amount of static surface friction.", 0.0, 0.4, false);
        dynamicFrictionField_ =
                new NumberInput( "dynamic friction (.0 small - .4 large):  ", simulator.getDynamicFriction() ,
                                  "This controls amount of dynamic surface friction.", 0.0, 0.4, false);

        frictionPanel.add( staticFrictionField_ );
        frictionPanel.add( dynamicFrictionField_ );

        globalParamPanel.add(frictionPanel, BorderLayout.NORTH);

        return globalParamPanel;
    }



    protected void ok()
    {
        super.ok();

        NewtonianSimulator simulator = (NewtonianSimulator) getSimulator();
        // set the common rendering and global physics options
        simulator.setDrawMesh( drawMeshCheckbox_.isSelected() );
        simulator.setShowVelocityVectors( showVelocitiesCheckbox_.isSelected() );
        simulator.setShowForceVectors( showForcesCheckbox_.isSelected() );

        double staticFriction = staticFrictionField_.getValue();
        double dynamicFriction =  dynamicFrictionField_.getValue();
        assert(staticFriction >= dynamicFriction);
        simulator.setStaticFriction( staticFriction);
        simulator.setDynamicFriction( dynamicFriction );
    }

}
