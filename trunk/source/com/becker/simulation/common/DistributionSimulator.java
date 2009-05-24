package com.becker.simulation.common;

import com.becker.ui.HistogramRenderer;

import com.becker.ui.animation.AnimationFrame;
import java.awt.*;
import java.util.*;
import javax.swing.JFrame;

/**
 * Simluates the the generation of a historgram based on
 * some stochastic processs.
 * 
 * @author Barry Becker Date: Feb 4, 2007
 */
public abstract class DistributionSimulator extends Simulator {


    private static final double TIME_STEP = 1.0;
    private static final int DEFAULT_STEPS_PER_FRAME = 100;

    protected HistogramRenderer histogram_;
    protected int[] data_;

    /** Seeded Random varaible so results are reproducible. */
    protected Random random_ = new Random(0);


    public DistributionSimulator(String title) {
        super(title);
        commonInit();
    }

    protected void reset() {
        initHistogram();
    }

    protected abstract void initHistogram();

    private void commonInit() {
        initCommonUI();
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
        this.setPreferredSize(new Dimension( 600, 500 ));
    }

    protected abstract SimulatorOptionsDialog createOptionsDialog();

    protected double getInitialTimeStep() {
        return TIME_STEP;
    }

    public double timeStep()
    {
        if ( !isPaused() ) {  
            data_[getXPositionToIncrement()]++;
        }
        return timeStep_;
    }
    
    protected abstract int getXPositionToIncrement();


    @Override
    public void paint( Graphics g )
    {
        histogram_.setSize(getWidth(), getHeight());
        histogram_.paint(g);
    }

    protected abstract String getFileNameBase();

    
    protected static void runSimulation(DistributionSimulator simulator) {     
        simulator.setPaused(false);
        JFrame f = new AnimationFrame( simulator );    
    }
    
}


