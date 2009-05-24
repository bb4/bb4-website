package com.becker.simulation.reactiondiffusion;

import com.becker.simulation.common.*;

import javax.swing.*;
import java.awt.*;

/**
 * Reaction diffusion simulator.
 * Based on work by Joakim Linde and modified by Barry Becker.
 */
public class RDSimulator extends Simulator {

    private static final String FILE_NAME_BASE = ANIMATION_FRAME_FILE_NAME_PREFIX + "reactiondiffusion/rdFrame";
    private static final int FIXED_SIZE_DIM = 250;

    private GrayScott grayScott_;
    private RDRenderer renderer_;
    private RDDynamicOptions rdOptions_;

    protected static final double INITIAL_TIME_STEP = 1.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 10;

    private boolean useFixedSize_ = false;

    private int oldWidth;
    private int oldHeight;
   

    public RDSimulator() {
        super("Reaction Diffusion");
        commonInit();
    }


    /**
     * @param fixed if true then the render area does not resize automatically.
     */
    public void setUseFixedSize(boolean fixed) {
        useFixedSize_ = fixed;
    }

    public boolean getUseFixedSize() {
        return useFixedSize_;
    }


    private void commonInit() {
        initCommonUI();
        grayScott_ = new GrayScott(1, 1);

        renderer_ = new RDRenderer(grayScott_);

        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
        oldWidth = this.getWidth();
        oldHeight = this.getHeight();
    }
    
    protected void reset() {
         grayScott_.reset();
         rdOptions_.reset();
    }

    protected SimulatorOptionsDialog createOptionsDialog() {
         return new RDOptionsDialog( frame_, this );
    }


    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    public double timeStep()
    {
        if ( !isPaused() ) {
            grayScott_.timeStep( timeStep_ );
        }
        return timeStep_;
    }


    @Override
    public void setScale( double scale ) {
        //envRenderer_.setScale(scale);
    }

    @Override
    public double getScale() {
        //return envRenderer_.getScale();
        return 0.01;
    }

    @Override
    public JPanel createDynamicControls() {
        rdOptions_ = new RDDynamicOptions(grayScott_, this);
        return rdOptions_;
    }


    @Override
    public void paint( Graphics g )
    {
        checkDimensions();

        Graphics2D g2 = (Graphics2D) g;
        renderer_.render(g2 );
    }

    /**
     * Sets to new size if needed.
     */
    private void checkDimensions() {
        int w = FIXED_SIZE_DIM;
        int h = FIXED_SIZE_DIM;
        if (!useFixedSize_) {
            w = getWidth();
            h = getHeight();
            if (w != oldWidth || h != oldHeight) {
                grayScott_.setSize(w, h);
                oldWidth = w;
                oldHeight = h;
            }
        }

        if (w != oldWidth || h != oldHeight) {
            grayScott_.setSize(w, h);
        }
        oldWidth = w;
        oldHeight = h;
    }

    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }

    public RDRenderer getRenderer() {
        return renderer_;
    }
}
