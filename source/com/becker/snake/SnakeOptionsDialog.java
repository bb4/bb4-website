package com.becker.snake;

import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 */
class SnakeOptionsDialog extends OptionsDialog implements ActionListener
{

    // contains the two tabls : options for creating a new game, or loading a saved game
    private JTabbedPane tabbedPanel_ = new JTabbedPane();

    private JPanel renderingParamPanel_ = null;
    private JPanel snakeParamPanel_ = null;

    // the options get set directly on the snake simulator object that is passed in
    private SnakeSimulator simulator_;

    // rendering option controls
    private JCheckBox antialiasingCheckbox_ = null;
    private JCheckBox drawMeshCheckbox_ = null;
    private JCheckBox showVelocitiesCheckbox_ = null;
    private JCheckBox showForcesCheckbox_ = null;
    private JCheckBox recordAnimationCheckbox_ = null;
    //protected JCheckBox hCheckbox_ = null;

    // snake param options controls
    private JTextField timeStepField_;
    private JTextField numStepsPerFrameField_;
    private JTextField scaleField_;
    //Color gridColor_ = GRID_COLOR

    // snake param options controls
    private JTextField waveSpeedField_;
    private JTextField waveAmplitudeField_;
    private JTextField wavePeriodField_;
    private JTextField massScaleField_;
    private JTextField springKField_;
    private JTextField springDampingField_;
    private JTextField staticFrictionField_;
    private JTextField dynamicFrictionField_;

    // bottom buttons
    private GradientButton startButton_ = new GradientButton();

    private static final int TEXT_FIELD_WIDTH = 50;
    private static final Dimension TEXT_FIELD_DIM = new Dimension( TEXT_FIELD_WIDTH, ROW_HEIGHT );

    // constructor
    public SnakeOptionsDialog( Frame parent, SnakeSimulator simulator )
    {
        super( parent );
        simulator_ = simulator;

        initUI();
    }

    public SnakeSimulator getSimulator()
    {
        return simulator_;
    }

    protected void initUI()
    {
        setResizable( true );
        mainPanel_.setLayout( new BorderLayout() );

        JPanel buttonsPanel = createButtonsPanel();

        renderingParamPanel_ = createRenderingParamPanel();
        snakeParamPanel_ = createSnakeParamPanel();

        tabbedPanel_.add( "Rendering Options", renderingParamPanel_ );
        tabbedPanel_.setToolTipTextAt( 0, "change the rendering options for the snake simulation" );
        tabbedPanel_.add( "Snake Parameters", snakeParamPanel_ );
        tabbedPanel_.setToolTipTextAt( 0, "change the physical constants controlling the snake in the simulation" );

        mainPanel_.add( tabbedPanel_, BorderLayout.CENTER );
        mainPanel_.add( buttonsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( mainPanel_ );
        this.getContentPane().repaint();
        this.pack();
    }

    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( startButton_, "Start Simulation", "Start a snake simulation based on above selections" );
        initBottomButton( cancelButton_, "Cancel", "Resume the current simulation without changing the options" );

        buttonsPanel.add( startButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }

    public String getTitle()
    {
        return "Snake Simulation Configuration";
    }

    private JPanel createRenderingParamPanel()
    {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout( new BorderLayout() );

        JPanel togglesPanel = new JPanel();
        togglesPanel.setLayout( new BoxLayout( togglesPanel, BoxLayout.Y_AXIS ) );
        togglesPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "toggle options" ) );

        JPanel textInputsPanel = new JPanel();
        textInputsPanel.setLayout( new BoxLayout( textInputsPanel, BoxLayout.Y_AXIS ) );
        textInputsPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "text input options" ) );

        antialiasingCheckbox_ = new JCheckBox( "Use Antialiasing", simulator_.getAntialiasing() );
        antialiasingCheckbox_.setToolTipText( "this toggle the use of antialising when rendering lines." );
        antialiasingCheckbox_.addActionListener( this );
        togglesPanel.add( antialiasingCheckbox_ );

        drawMeshCheckbox_ = new JCheckBox( "Show Snake Wireframe", simulator_.getSnake().getDrawMesh() );
        drawMeshCheckbox_.setToolTipText( "draw the snake showing the underlying awireframe mesh" );
        drawMeshCheckbox_.addActionListener( this );
        togglesPanel.add( drawMeshCheckbox_ );

        showVelocitiesCheckbox_ = new JCheckBox( "Show Velocity Vectors", simulator_.getSnake().getShowVelocityVectors() );
        showVelocitiesCheckbox_.setToolTipText( "show lines representing velocity vectors on each partical mass" );
        showVelocitiesCheckbox_.addActionListener( this );
        togglesPanel.add( showVelocitiesCheckbox_ );

        showForcesCheckbox_ = new JCheckBox( "Show Force Vectors", simulator_.getSnake().getShowForceVectors() );
        showForcesCheckbox_.setToolTipText( "show lines representing force vectors on each partical mass" );
        showForcesCheckbox_.addActionListener( this );
        togglesPanel.add( showForcesCheckbox_ );

        recordAnimationCheckbox_ = new JCheckBox( "Record Animation Frames", simulator_.getSnake().getDrawMesh() );
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

        scaleField_ = new JTextField( Double.toString( simulator_.getSnake().getScale() ) );
        scaleField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p3 =
                new NumberInputPanel( "Geometry Scale (1.0 = standard size):  ", scaleField_ );
        p3.setToolTipText( "This controls the size of the snake" );
        scaleField_.setEnabled( false );
        textInputsPanel.add( p3 );

        paramPanel.add( togglesPanel, BorderLayout.CENTER );
        paramPanel.add( textInputsPanel, BorderLayout.SOUTH );

        return paramPanel;
    }

    private JPanel createSnakeParamPanel()
    {
        JPanel p = new JPanel(); //new FlowLayout());
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );

        JLabel label = new JLabel( "snake params" );
        p.add( label );

        waveSpeedField_ = new JTextField( Double.toString( simulator_.getSnake().getWaveSpeed() ) );
        waveSpeedField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p1 =
                new NumberInputPanel( "Wave Speed (.001 slow - .9 fast):  ", waveSpeedField_ );
        p1.setToolTipText( "This controls the speed at which the force function that travels down the body of the snake" );
        p.add( p1 );

        waveAmplitudeField_ = new JTextField( Double.toString( simulator_.getSnake().getWaveAmplitude() ) );
        waveAmplitudeField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p2 =
                new NumberInputPanel( "Wave Amplitude (.001 small - 2.0 large):  ", waveAmplitudeField_ );
        p2.setToolTipText( "This controls the amplitude of the force function that travels down the body of the snake" );
        p.add( p2 );

        wavePeriodField_ = new JTextField( Double.toString( simulator_.getSnake().getWavePeriod() ) );
        wavePeriodField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p3 =
                new NumberInputPanel( "Wave Period (1.0 small - 4.0 large):  ", wavePeriodField_ );
        p3.setToolTipText( "This controls the period (in number of PI radians) of the force function that travels down the body of the snake" );
        p.add( p3 );

        massScaleField_ = new JTextField( Double.toString( simulator_.getSnake().getMassScale() ) );
        massScaleField_.setMaximumSize( TEXT_FIELD_DIM );
        massScaleField_.setEnabled( false );
        JPanel p4 =
                new NumberInputPanel( "Mass Scale (.3 small - 2.0 large):  ", massScaleField_ );
        p4.setToolTipText( "This controls the overall mass of the snake. A high number indicates that the snake weighs a lot." );
        p.add( p4 );

        springKField_ = new JTextField( Double.toString( simulator_.getSnake().getSpringK() ) );
        springKField_.setMaximumSize( TEXT_FIELD_DIM );
        springKField_.setEnabled( false );
        JPanel p5 =
                new NumberInputPanel( "Spring Stiffness  (1.0 small - 2.0 large):  ", springKField_ );
        p5.setToolTipText( "This controls the stiffness of the springs used to make up the snake's body." );
        p.add( p5 );

        springDampingField_ = new JTextField( Double.toString( simulator_.getSnake().getSpringDamping() ) );
        springDampingField_.setMaximumSize( TEXT_FIELD_DIM );
        springDampingField_.setEnabled( false );
        JPanel p6 =
                new NumberInputPanel( "Spring Damping (.1 small - 3.0 large):  ", springDampingField_ );
        p6.setToolTipText( "This controls how quickly the spring returns to rest once released." );
        p.add( p6 );

        staticFrictionField_ = new JTextField( Double.toString( simulator_.getSnake().getStaticFriction() ) );
        staticFrictionField_.setMaximumSize( TEXT_FIELD_DIM );
        staticFrictionField_.setEnabled( true );
        JPanel p7 =
                new NumberInputPanel( "static Friction (.0 small - .1 large):  ", staticFrictionField_ );
        p7.setToolTipText( "This controls amount of static surface friction." );
        p.add( p7 );

        dynamicFrictionField_ = new JTextField( Double.toString( simulator_.getSnake().getDynamicFriction() ) );
        dynamicFrictionField_.setMaximumSize( TEXT_FIELD_DIM );
        dynamicFrictionField_.setEnabled( true );
        JPanel p8 =
                new NumberInputPanel( "dynamic friction (.0 small - .1 large):  ", dynamicFrictionField_ );
        p8.setToolTipText( "This controls amount of dynamic surface friction." );
        p.add( p8 );

        return p;
    }

    private void ok()
    {

        // set the rendering options

        simulator_.setAntialiasing( antialiasingCheckbox_.isSelected() );
        simulator_.getSnake().setDrawMesh( drawMeshCheckbox_.isSelected() );
        simulator_.getSnake().setShowVelocityVectors( showVelocitiesCheckbox_.isSelected() );
        simulator_.getSnake().setShowForceVectors( showForcesCheckbox_.isSelected() );
        simulator_.setRecordAnimation( recordAnimationCheckbox_.isSelected() );

        Double timeStep = new Double( timeStepField_.getText() );
        simulator_.setTimeStep( timeStep.doubleValue() );

        Integer numSteps = new Integer( numStepsPerFrameField_.getText() );
        simulator_.setNumStepsPerFrame( numSteps.intValue() );

        Double scale = new Double( scaleField_.getText() );
        simulator_.getSnake().setScale( scale.doubleValue() );

        // set the snake params

        Double waveSpeed = new Double( waveSpeedField_.getText() );
        simulator_.getSnake().setWaveSpeed( waveSpeed.doubleValue() );

        Double waveAmplitude = new Double( waveAmplitudeField_.getText() );
        simulator_.getSnake().setWaveAmplitude( waveAmplitude.doubleValue() );

        Double wavePeriod = new Double( wavePeriodField_.getText() );
        simulator_.getSnake().setWavePeriod( wavePeriod.doubleValue() );

        Double massScale = new Double( massScaleField_.getText() );
        simulator_.getSnake().setMassScale( massScale.doubleValue() );

        Double springK = new Double( springKField_.getText() );
        simulator_.getSnake().setSpringK( springK.doubleValue() );

        Double springDamping = new Double( springDampingField_.getText() );
        simulator_.getSnake().setSpringDamping( springDamping.doubleValue() );

        Double staticFriction = new Double( staticFrictionField_.getText() );
        simulator_.getSnake().setStaticFriction( staticFriction.doubleValue() );

        Double dynamicFriction = new Double( dynamicFrictionField_.getText() );
        simulator_.getSnake().setDynamicFriction( dynamicFriction.doubleValue() );

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