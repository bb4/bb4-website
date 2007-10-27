package com.becker.simulation.reactiondiffusion;

import com.becker.optimization.*;
import com.becker.simulation.common.*;

import javax.swing.*;
import java.awt.*;

/**
 * Reaction diffusion simulator.
 * based on work by Joakim Linde and modified by Barry Becker.
 *
 */
public class RDSimulator extends Simulator {

    private static final String FILE_NAME_BASE = ANIMATION_FRAME_FILE_NAME_PREFIX + "reactiondiffusion/rdFrame";

    private GrayScott grayScott_;
    private RDRenderer renderer_;
    private RDDynamicOptions rdOptions_;

    protected static final double TIME_STEP = 1.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 10;
   

    public RDSimulator() {
        super("Reaction Diffusion");
        commonInit();
    }


    private void commonInit() {
        initCommonUI();

        grayScott_ = new GrayScott(250, 250);
        renderer_ = new RDRenderer(grayScott_);

        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
    }
    
    protected void reset() {
         grayScott_.reset();
         rdOptions_.reset();
    }

    protected SimulatorOptionsDialog createOptionsDialog() {
         return new RDOptionsDialog( frame_, this );
    }


    protected double getInitialTimeStep() {
        return TIME_STEP;
    }

    public double timeStep()
    {
        if ( !isPaused() ) {
            grayScott_.timeStep( timeStep_ );
        }
        return timeStep_;
    }


    public void setScale( double scale ) {
        //envRenderer_.setScale(scale);
    }

    public double getScale() {
        //return envRenderer_.getScale();
        return 0.01;
    }

    public JPanel createDynamicControls() {
        rdOptions_ = new RDDynamicOptions(grayScott_, this);
        return rdOptions_;
    }


    public void doOptimization()
    {
       System.out.println("not yet implemented");
    }

    public int getNumParameters() {
        return 0;
    }

    /**
     * *** implements the key method of the Optimizee interface ***
     *
     * evaluates the fitness.
     */
    public double evaluateFitness( ParameterArray params )
    {
        assert false : "not implemented yet";
        return 0.0;
    }

    public double getOptimalFitness() {
        return 0;
    }


    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        renderer_.render(g2 );
    }

    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }

    public RDRenderer getRenderer() {
        return renderer_;
    }
}
