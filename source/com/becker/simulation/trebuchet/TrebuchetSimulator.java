package com.becker.simulation.trebuchet;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.Parameter;
import com.becker.common.*;
import com.becker.common.util.FileUtil;
import com.becker.common.util.Util;
import com.becker.optimization.*;
import com.becker.simulation.common.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

/**
 * Physically base dynamic simulation of a trebuchet firing.
 *
 *  @@ Try simulating using Breve.
 *  Currently can't get working because of seg fault (because need to recompile for 64 bit?)
 */
public class TrebuchetSimulator extends NewtonianSimulator
                                implements ChangeListener
{

    private Trebuchet trebuchet_ = null;

    JSlider zoomSlider_;

    private static final String FILE_NAME_BASE = ANIMATION_FRAME_FILE_NAME_PREFIX + "trebuchet/trebuchetFrame";


    // the amount to advance the animation in time for each frame in seconds
    private static final double TIME_STEP = 0.002;

    private static final Color BACKGROUND_COLOR = new Color(253, 250, 253);

    private static final int NUM_PARAMS = 3;


    public TrebuchetSimulator()
    {
        super("Trebuchet");
        reset();        
        this.setPreferredSize(new Dimension( 800, 900));
    }

    public TrebuchetSimulator( Trebuchet trebuchet )
    {
        super("Trebuchet");
        commonInit( trebuchet );
    }

    private void commonInit( Trebuchet trebuchet )
    {
        trebuchet_ = trebuchet;
        setNumStepsPerFrame(4);
        this.setBackground(BACKGROUND_COLOR);
        initCommonUI();
        this.render();
    }
    
    protected void reset() {
        final Trebuchet trebuchet = new Trebuchet();
        commonInit( trebuchet );
    }

    public Color getBackground()
    {
        return BACKGROUND_COLOR;
    }

    public JPanel createTopControls()
    {                 
         JPanel controls = super.createTopControls();     
        
        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new FlowLayout());
        JLabel zoomLabel = new JLabel( " Zoom" );
        zoomSlider_ = new JSlider( JSlider.HORIZONTAL, 15, 255, 200 );
        zoomSlider_.addChangeListener( this );
        zoomPanel.add(zoomLabel);
        zoomPanel .add(zoomSlider_);
        this.add(zoomPanel);

        controls.add(zoomLabel);
        controls.add(zoomSlider_);
        return controls;
    }

    public void doOptimization()
    {
        Optimizer optimizer;
        if (GUIUtil.isStandAlone())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, FileUtil.PROJECT_DIR+"performance/trebuchet/trebuchet_optimization.txt" );
        Parameter[] params = new Parameter[NUM_PARAMS];
        //params[0] = new Parameter( WAVE_SPEED, 0.0001, 0.02, "wave speed" );
        //params[1] = new Parameter( WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude" );
        //params[2] = new Parameter( WAVE_PERIOD, 0.5, 9.0, "wave period" );
        ParameterArray paramArray = new ParameterArray( params );

        setPaused(false);
        optimizer.doOptimization(  OptimizationType.GENETIC_SEARCH, paramArray, 0.3);
    }

    public int getNumParameters() {
        return NUM_PARAMS;
    }

    protected double getInitialTimeStep() {
        return TIME_STEP;
    }


    protected SimulatorOptionsDialog createOptionsDialog() {
         return new TrebuchetOptionsDialog( frame_, this );
    }


    public double timeStep()
    {
        if ( !isPaused() ) {
            timeStep_ = trebuchet_.stepForward( timeStep_ );
        }
        return timeStep_;
    }

    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor( BACKGROUND_COLOR );
        g2.fillRect( 0, 0, (int) getSize().getWidth(), (int)  getSize().getHeight() );

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                useAntialiasing_ ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );


        // draw the trebuchet in its current position
        trebuchet_.render( g2 );
    }



    public void setScale( double scale ) {
        trebuchet_.setScale(scale);
    }
    public double getScale() {
        return trebuchet_.getScale();
    }

    public void setShowVelocityVectors( boolean show ) {
        RenderablePart.setShowVelocityVectors(show);
    }
    public boolean getShowVelocityVectors() {
        return RenderablePart.getShowVelocityVectors();
    }

    public void setShowForceVectors( boolean show ) {
        RenderablePart.setShowForceVectors(show);
    }
    public boolean getShowForceVectors() {
        return RenderablePart.getShowForceVectors();
    }

    public void setDrawMesh( boolean use ) {
        //trebuchet_.setDrawMesh(use);
    }
    public boolean getDrawMesh() {
        //return trebuchet_.getDrawMesh();
        return false;
    }


    public void setStaticFriction( double staticFriction ) {
        // do nothing
    }
    public double getStaticFriction() {
        // do nothing
        return 0.1;
    }

    public void setDynamicFriction( double dynamicFriction ) {
       // do nothing
    }
    public double getDynamicFriction() {
        // do nothing
        return 0.01;
    }


    // api for setting trebuchet params  /////////////////////////////////
    public Trebuchet getTrebuchet()
    {
        return trebuchet_;
    }


    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }

    public void stateChanged(ChangeEvent event) {
        Object src = event.getSource();
        if (src == zoomSlider_) {
            double v = (double) zoomSlider_.getValue() / 200.0;
            trebuchet_.setScale(v);
            this.repaint();
        }
    }


    /////////////// the next methods implement the Optimizee interface  /////////////////////////

    /**
     * evaluates the trebuchet's fitness.
     * The measure is purely based on its velocity.
     * If the trebuchet becomes unstable, then 0.0 is returned.
     */
    public double evaluateFitness( ParameterArray params )
    {
        //trebuchet_.setWaveSpeed( params.get( 0 ).value );
        //trebuchet_.setWaveAmplitude( params.get( 1 ).value );
        //trebuchet_.setWavePeriod( params.get( 2 ).value );

        boolean stable = true;
        boolean improved = true;
        double oldVelocity = 0.0;
        int ct = 0;

        while ( stable && improved ) {
            // let it run for a while
            Util.sleep( 1000 + (int) (3000 / (1.0 + 0.2 * ct)));

            ct++;
            //stable = trebuchet_.isStable();
        }
        if ( !stable )   {
            System.out.println( "Trebuchet Sim unstable" );
            return 0.0;
        }
        else
            return oldVelocity;
    }


}