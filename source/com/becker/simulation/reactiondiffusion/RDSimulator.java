package com.becker.simulation.reactiondiffusion;

import com.becker.common.ColorMap;
import com.becker.simulation.common.*;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottController;
import com.becker.simulation.reactiondiffusion.rendering.RDRenderingOptions;

import javax.swing.*;
import java.awt.*;

/**
 * Reaction diffusion simulator.
 * Based on work by Joakim Linde and modified by Barry Becker.
 */
public class RDSimulator extends Simulator {

    private GrayScottController grayScott_;
    private RDViewer viewer_;
    private RDDynamicOptions rdOptions_;

    protected static final double INITIAL_TIME_STEP = 1.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 10;


    public RDSimulator() {
        super("Reaction Diffusion");
        commonInit();
    }

    /**
     * @param fixed if true then the render area does not resize automatically.
     */
    public void setUseFixedSize(boolean fixed) {
        viewer_.setUseFixedSize(fixed);
    }

    public boolean getUseFixedSize() {
        return viewer_.getUseFixedSize();
    }

    public void setUseOffscreenRendering(boolean use) {
        viewer_.setUseOffscreenRendering(use);
    }

    public boolean getUseOffScreenRendering() {
        return viewer_.getUseOffScreenRendering();
    }

    @Override
    public void setPaused( boolean bPaused )
    {
        super.setPaused(bPaused);
        if (isPaused())   {
            RDProfiler.getInstance().print();
            RDProfiler.getInstance().resetAll();
        }
    }

    private void commonInit() {
        initCommonUI();
        grayScott_ = new GrayScottController(1, 1);

        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);

        viewer_ = new RDViewer(grayScott_);
        this.add(viewer_);
    }

    @Override
    protected void reset() {
        grayScott_.reset();
        rdOptions_.reset();
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new RDOptionsDialog( frame_, this );
    }


    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    @Override
    public double timeStep()
    {
        if ( !isPaused() ) {
            RDProfiler.getInstance().startCalculationTime();
            grayScott_.timeStep( timeStep_ );
            RDProfiler.getInstance().stopCalculationTime();
        }
        return timeStep_;
    }


    @Override
    public void paint( Graphics g )
    {
        super.paint(g);
        viewer_.setSize(getSize());
        RDProfiler.getInstance().startRenderingTime();
        viewer_.paint(g);
        RDProfiler.getInstance().stopRenderingTime();
    }

    @Override
    public void setScale( double scale ) {}

    @Override
    public double getScale() {
        return 0.01;
    }

    @Override
    public JPanel createDynamicControls() {
        rdOptions_ = new RDDynamicOptions(grayScott_, this);
        return rdOptions_;
    }

    public ColorMap getColorMap() {
        return viewer_.getColorMap();
    }

    public RDRenderingOptions getRenderingOptions() {
        return viewer_.getRenderingOptions();
    }
}
