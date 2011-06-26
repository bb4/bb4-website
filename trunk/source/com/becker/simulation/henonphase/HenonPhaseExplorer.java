package com.becker.simulation.henonphase;

import com.becker.common.ColorMap;
import com.becker.simulation.common.Profiler;
import com.becker.simulation.common.ui.Simulator;
import com.becker.simulation.common.ui.SimulatorOptionsDialog;
import com.becker.simulation.henonphase.algorithm.HenonAlgorithm;
import com.becker.simulation.henonphase.algorithm.HenonColorMap;

import javax.swing.*;
import java.awt.*;

/**
 * Interactively explores the Henon Phase attractors.
 * See   http://mathworld.wolfram.com/HenonMap.html
 * See   http://www.complexification.net/gallery/machines/henonPhaseDeep/
 *
 * @author Barry Becker.
 */
public class HenonPhaseExplorer extends Simulator {

    private HenonAlgorithm algorithm_;
    private DynamicOptions options_;
    private ColorMap cmap_;

    private boolean useFixedSize_ = false;

    protected static final double INITIAL_TIME_STEP = 10.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 10;


    public HenonPhaseExplorer() {
        super("Henon Phase Explorer");
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
        reset();
    }

    @Override
    protected void reset() {

        algorithm_ = new HenonAlgorithm();
        cmap_ = new HenonColorMap();

        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);

        if (options_ != null) options_.reset();
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new OptionsDialog( frame_, this );
    }

    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {

            if (!useFixedSize_) {
                algorithm_.setSize(this.getWidth(), this.getHeight());
            }
            algorithm_.timeStep(timeStep_);
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g ) {
        super.paint(g);

        Profiler.getInstance().startRenderingTime();
        g.drawImage(algorithm_.getImage(), 0, 0, null);
        Profiler.getInstance().stopRenderingTime();
    }

    @Override
    public void setScale( double scale ) {}

    @Override
    public double getScale() {
        return 0.01;
    }

    @Override
    public JPanel createDynamicControls() {
        options_ = new DynamicOptions(algorithm_, this);
        return options_;
    }

    public ColorMap getColorMap() {
        return cmap_;
    }
}
