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
    private JCheckBox drawMeshCheckbox_ = null;
    private JCheckBox showVelocitiesCheckbox_ = null;
    private JCheckBox showForcesCheckbox_ = null;
    private JCheckBox recordAnimationCheckbox_ = null;

    // aniumation param options controls
    private JTextField timeStepField_;
    private JTextField numStepsPerFrameField_;
    private JTextField scaleField_;

    // physics param options controls
    private JTextField staticFrictionField_;
    private JTextField dynamicFrictionField_;

    // bottom buttons
    private GradientButton startButton_ = new GradientButton();


    protected static final int TEXT_FIELD_WIDTH = 50;
    protected static final Dimension TEXT_FIELD_DIM = new Dimension( TEXT_FIELD_WIDTH, ROW_HEIGHT );

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
        tabbedPanel.setToolTipTextAt( 0, "change the rendering options for the " + simulator_.getName() + " simulation" );
        tabbedPanel.add( "Animation", globalPhysicalParamPanel );
        tabbedPanel.setToolTipTextAt( 0, "change the animation and physical constants controlling the " + simulator_.getName() + " in the simulation" );
        tabbedPanel.add( simulator_.getName() + "Specific", customParamPanel );
        tabbedPanel.setToolTipTextAt( 0, "change the custom options for the " + simulator_.getName() + " simulation" );

        mainPanel_.add( tabbedPanel, BorderLayout.CENTER );
        mainPanel_.add( buttonsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( mainPanel_ );
        this.getContentPane().repaint();
        this.pack();
    }

    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( startButton_, "Start Simulation", "Start a " + simulator_.getName() + " simulation based on above selections" );
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

        drawMeshCheckbox_ = new JCheckBox( "Show Wireframe", simulator_.getDrawMesh() );
        drawMeshCheckbox_.setToolTipText( "draw the "+ simulator_.getName() + " showing the underlying wireframe mesh");
        drawMeshCheckbox_.addActionListener( this );
        togglesPanel.add( drawMeshCheckbox_ );

        showVelocitiesCheckbox_ = new JCheckBox( "Show Velocity Vectors", simulator_.getShowVelocityVectors() );
        showVelocitiesCheckbox_.setToolTipText( "show lines representing velocity vectors on each partical mass" );
        showVelocitiesCheckbox_.addActionListener( this );
        togglesPanel.add( showVelocitiesCheckbox_ );

        showForcesCheckbox_ = new JCheckBox( "Show Force Vectors", simulator_.getShowForceVectors() );
        showForcesCheckbox_.setToolTipText( "show lines representing force vectors on each partical mass" );
        showForcesCheckbox_.addActionListener( this );
        togglesPanel.add( showForcesCheckbox_ );

        recordAnimationCheckbox_ = new JCheckBox( "Record Animation Frames", simulator_.getRecordAnimation() );
        recordAnimationCheckbox_.setToolTipText( "Record each animation frame to a unique file" );
        recordAnimationCheckbox_.addActionListener( this );
        togglesPanel.add( recordAnimationCheckbox_ );

        timeStepField_ = new JTextField( Double.toString( simulator_.getTimeStep() ) );
        timeStepField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p1 =
                new NumberInputPanel( "Time Step (.001 slow - .9 fast but unstable):  ", timeStepField_ );
        p1.setToolTipText( "This controls the size of the numerical intergration steps" );
        textInputsPanel.add( p1 );

        numStepsPerFrameField_ = new JTextField( Integer.toString( simulator_.getNumStepsPerFrame() ) );
        numStepsPerFrameField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p2 =
                new NumberInputPanel( "Num Steps Per Frame (1 slow but smooth - 1000 (fast but choppy):  ", numStepsPerFrameField_ );
        p2.setToolTipText( "This controls the number of the numerical intergration steps per animation frame" );
        textInputsPanel.add( p2 );

        scaleField_ = new JTextField( Double.toString( simulator_.getScale() ) );
        scaleField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p3 =
                new NumberInputPanel( "Geometry Scale (1.0 = standard size):  ", scaleField_ );
        p3.setToolTipText( "This controls the size of the " + simulator_.getName() );
        scaleField_.setEnabled( false );
        textInputsPanel.add( p3 );

        paramPanel.add( togglesPanel, BorderLayout.CENTER );
        paramPanel.add( textInputsPanel, BorderLayout.SOUTH );

        return paramPanel;
    }
    

    private JPanel createGlobalPhysicalParamPanel()
    {
        JPanel globalParamPanel = new JPanel();
        globalParamPanel.setLayout( new BorderLayout() );

        JPanel frictionPanel = new JPanel();
        frictionPanel.setLayout( new BoxLayout(frictionPanel, BoxLayout.Y_AXIS ) );
        frictionPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Friction" ) );

        staticFrictionField_ = new JTextField( Double.toString( simulator_.getStaticFriction() ) );
        staticFrictionField_.setMaximumSize( TEXT_FIELD_DIM );
        staticFrictionField_.setEnabled( true );
        JPanel p7 =
                new NumberInputPanel( "static Friction (.0 small - .4 large):  ", staticFrictionField_ );
        p7.setToolTipText( "This controls amount of static surface friction." );
        frictionPanel.add( p7 );

        dynamicFrictionField_ = new JTextField( Double.toString( simulator_.getDynamicFriction() ) );
        dynamicFrictionField_.setMaximumSize( TEXT_FIELD_DIM );
        dynamicFrictionField_.setEnabled( true );
        JPanel p8 =
                new NumberInputPanel( "dynamic friction (.0 small - .4 large):  ", dynamicFrictionField_ );
        p8.setToolTipText( "This controls amount of dynamic surface friction." );
        frictionPanel.add( p8 );

        globalParamPanel.add(frictionPanel, BorderLayout.NORTH);

        return globalParamPanel;
    }

    protected abstract JPanel createCustomParamPanel();


    protected void ok()
    {
        // set the common rendering and global physics options
        simulator_.setAntialiasing( antialiasingCheckbox_.isSelected() );
        simulator_.setDrawMesh( drawMeshCheckbox_.isSelected() );
        simulator_.setShowVelocityVectors( showVelocitiesCheckbox_.isSelected() );
        simulator_.setShowForceVectors( showForcesCheckbox_.isSelected() );
        simulator_.setRecordAnimation( recordAnimationCheckbox_.isSelected() );

        Double timeStep = new Double( timeStepField_.getText() );
        simulator_.setTimeStep( timeStep );

        Integer numSteps = new Integer( numStepsPerFrameField_.getText() );
        simulator_.setNumStepsPerFrame( numSteps );

        Double scale = new Double( scaleField_.getText() );
        simulator_.setScale( scale );

        Double staticFriction = new Double( staticFrictionField_.getText() );
        Double dynamicFriction = new Double( dynamicFrictionField_.getText() );
        assert(staticFriction >= dynamicFriction);
        simulator_.setStaticFriction( staticFriction );
        simulator_.setDynamicFriction( dynamicFriction );

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