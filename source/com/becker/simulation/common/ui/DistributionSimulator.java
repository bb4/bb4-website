package com.becker.simulation.common.ui;

import com.becker.ui.animation.AnimationFrame;
import com.becker.ui.renderers.HistogramRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Simulates the the generation of a histogram based on
 * some stochastic process.
 * 
 * @author Barry Becker
 */
public abstract class DistributionSimulator extends Simulator {

    private static final double TIME_STEP = 1.0;
    private static final int DEFAULT_STEPS_PER_FRAME = 100;

    protected HistogramRenderer histogram_;
    protected int[] data_;

    /** Seeded Random variable so results are reproducible. */
    protected Random random_ = new Random(0);

    public DistributionSimulator(String title) {
        super(title);
        commonInit();
    }

    @Override
    protected void reset() {
        initHistogram();
    }

    protected abstract void initHistogram();

    private void commonInit() {
        initCommonUI();
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
        this.setPreferredSize(new Dimension( 600, 500 ));
    }

    @Override
    protected abstract SimulatorOptionsDialog createOptionsDialog();

    @Override
    protected double getInitialTimeStep() {
        return TIME_STEP;
    }

    @Override
    public double timeStep()
    {
        if ( !isPaused() ) {
            histogram_.increment(getXPositionToIncrement());
        }
        return timeStep_;
    }

    /**
     * @return An x value to add to the histogram.
     * The histogram itself will convert it to the correct x axis bin location.
     */
    protected abstract double getXPositionToIncrement();


    @Override
    public void paint( Graphics g ) {
        histogram_.setSize(getWidth(), getHeight());
        histogram_.paint(g);
    }
    
    protected static void runSimulation(DistributionSimulator simulator) {     
        simulator.setPaused(false);
        JFrame f = new AnimationFrame( simulator );    
    }
}


