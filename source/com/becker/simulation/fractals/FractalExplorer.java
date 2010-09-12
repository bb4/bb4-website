package com.becker.simulation.fractals;

import com.becker.common.ColorMap;
import com.becker.simulation.common.Simulator;
import com.becker.simulation.common.SimulatorOptionsDialog;
import com.becker.simulation.fractals.algorithm.FractalAlgorithm;
import com.becker.simulation.fractals.algorithm.FractalModel;
import com.becker.simulation.fractals.algorithm.MandelbrotAlgorithm;

import javax.swing.*;
import java.awt.*;

/**
 * Interactively explores the Mandelbrot set.
 * @author Barry Becker.
 */
public class FractalExplorer extends Simulator {

    private FractalAlgorithm algorithm_;
    private FractalModel model_;
    private DynamicOptions options_;
    private FractalRenderer renderer_;
    private ZoomHandler handler_;

    private boolean useFixedSize_ = true;

    protected static final double INITIAL_TIME_STEP = 1.0;
    protected static final int DEFAULT_STEPS_PER_FRAME = 1;


    public FractalExplorer() {
        super("Fractal Explorer");
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
        model_ = new FractalModel();
        algorithm_ = new MandelbrotAlgorithm(model_);

        renderer_ = new FractalRenderer(model_, new FractalColorMap());
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);

        handler_ = new ZoomHandler(algorithm_);
        this.addMouseListener(handler_);
        this.addMouseMotionListener(handler_);
    }

    @Override
    protected void reset() {
        options_.reset();
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
    public double timeStep()
    {
        if ( !isPaused() ) {
            if (!useFixedSize_) {
                model_.setSize(this.getWidth(), this.getHeight());
            }
            algorithm_.timeStep( timeStep_ );
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g )
    {
        super.paint(g);

        renderer_.render((Graphics2D) g);
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
        return renderer_.getColorMap();
    }
}
