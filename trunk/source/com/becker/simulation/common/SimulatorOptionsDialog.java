package com.becker.simulation.common;

import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * @author Barry Becker Date: Sep 18, 2005
 */
public abstract class SimulatorOptionsDialog extends OptionsDialog implements ActionListener
{

    // the options get set directly on the snake simulator object that is passed in
    private Simulator simulator_;

    // rendering option controls
    private JCheckBox antialiasingCheckbox_ = null;
    private JCheckBox recordAnimationCheckbox_ = null;

    // aniumation param options controls
    private NumberInput timeStepField_;
    private NumberInput numStepsPerFrameField_;
    private NumberInput scaleField_;


    // bottom buttons
    private GradientButton startButton_ = new GradientButton();

    // constructor
    public SimulatorOptionsDialog( Frame parent, Simulator simulator )
    {
        super( parent );
        simulator_ = simulator;

        initUI();
    }

    public Simulator getSimulator()
    {
        return simulator_;
    }

    protected void initUI()
    {
        setResizable( true );
        mainPanel_.setLayout( new BorderLayout() );

        JPanel buttonsPanel = createButtonsPanel();

        JPanel renderingParamPanel = createRenderingParamPanel();
        JPanel globalPhysicalParamPanel = createGlobalPhysicalParamPanel();
        JPanel customParamPanel = createCustomParamPanel();

        // contains the two tabls : options for creating a new game, or loading a saved game
        JTabbedPane tabbedPanel = new JTabbedPane();
        tabbedPanel.add( "Rendering", renderingParamPanel );
        tabbedPanel.setToolTipTextAt( 0, "change the rendering options for the " +
                                         simulator_.getName() + " simulation" );
        if (globalPhysicalParamPanel != null) {
            tabbedPanel.add( "Animation", globalPhysicalParamPanel );
        }
        tabbedPanel.setToolTipTextAt( 0, "change the animation and physical constants controlling the " +
                                         simulator_.getName() + " in the simulation" );
        tabbedPanel.add( simulator_.getName() + " Specific", customParamPanel );
        tabbedPanel.setToolTipTextAt( 0, "change the custom options for the " + simulator_.getName() + " simulation" );
        tabbedPanel.setSelectedComponent(customParamPanel);

        mainPanel_.add( tabbedPanel, BorderLayout.CENTER );
        mainPanel_.add( buttonsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( mainPanel_ );
        this.getContentPane().repaint();
        this.pack();
    }

    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( startButton_, "Done", "Use these selections when running " + simulator_.getName() + '.' );
        initBottomButton( cancelButton_, "Cancel", "Resume the current simulation without changing the options" );

        buttonsPanel.add( startButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }

    public String getTitle()
    {
        return "Simulation Configuration";
    }

    private JPanel createRenderingParamPanel()
    {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout( new BorderLayout() );

        JPanel togglesPanel = new JPanel();
        togglesPanel.setLayout( new BoxLayout( togglesPanel, BoxLayout.Y_AXIS ) );
        togglesPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Toggle Options" ) );

        JPanel textInputsPanel = new JPanel();
        textInputsPanel.setLayout( new BoxLayout( textInputsPanel, BoxLayout.Y_AXIS ) );
        textInputsPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Animation Options" ) );

        antialiasingCheckbox_ = new JCheckBox( "Use Antialiasing", simulator_.getAntialiasing() );
        antialiasingCheckbox_.setToolTipText( "this toggle the use of antialising when rendering lines." );
        antialiasingCheckbox_.addActionListener( this );
        togglesPanel.add( antialiasingCheckbox_ );

        addAdditionalToggles(togglesPanel) ;

        recordAnimationCheckbox_ = new JCheckBox( "Record Animation Frames", simulator_.getRecordAnimation() );
        recordAnimationCheckbox_.setToolTipText( "Record each animation frame to a unique file" );
        recordAnimationCheckbox_.addActionListener( this );
        togglesPanel.add( recordAnimationCheckbox_ );

        timeStepField_ =
                new NumberInput("Time Step (.001 slow - .9 fast but unstable):  ",  simulator_.getTimeStep(),
                                "This controls the size of the numerical intergration steps",
                                0.001, 0.9, false);
        numStepsPerFrameField_ =
                new NumberInput("Num Steps Per Frame (1 slow but smooth - 1000 (fast but choppy):  ",
                                simulator_.getNumStepsPerFrame(),
                               "This controls the number of the numerical intergration steps per animation frame",
                               1, 1000, true);

        textInputsPanel.add( timeStepField_ );
        textInputsPanel.add( numStepsPerFrameField_ );

        scaleField_ =
                new NumberInput( "Geometry Scale (1.0 = standard size):  ", simulator_.getScale(),
                                 "This controls the size of the " + simulator_.getName(),
                                 0.01, 1000, false);
        scaleField_.setEnabled( false );
        textInputsPanel.add( scaleField_ );

        paramPanel.add( togglesPanel, BorderLayout.CENTER );
        paramPanel.add( textInputsPanel, BorderLayout.SOUTH );

        return paramPanel;
    }

    /**
     * override if you want to add more toggles.
     */
    protected void addAdditionalToggles(JPanel togglesPanel) {
    }

    /**
     * override if you want this panel of options for your simulation.
     */
    protected JPanel createGlobalPhysicalParamPanel()
    {
        return null;
    }

    /**
     * For custom parameters that don't fall in other categories.
     */
    protected abstract JPanel createCustomParamPanel();


    protected void ok()
    {
        // set the common rendering and global options
        simulator_.setAntialiasing( antialiasingCheckbox_.isSelected() );
        simulator_.setRecordAnimation( recordAnimationCheckbox_.isSelected() );

        simulator_.setTimeStep( timeStepField_.getValue() );
        simulator_.setNumStepsPerFrame( numStepsPerFrameField_.getIntValue() );
        simulator_.setScale( scaleField_.getValue() );

        this.setVisible( false );
    }

    public void actionPerformed( ActionEvent e )
    {

        Object source = e.getSource();

        if ( source == startButton_ ) {
            ok();
        }
        else if ( source == cancelButton_ ) {
            cancel();
        }
    }
}