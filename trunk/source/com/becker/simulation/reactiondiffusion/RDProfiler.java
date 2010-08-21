package com.becker.simulation.reactiondiffusion;

import com.becker.common.Profiler;
import com.becker.common.util.Util;
import com.becker.game.common.GameContext;
import com.becker.simulation.common.Simulator;
import com.becker.simulation.common.SimulatorOptionsDialog;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottController;
import com.becker.simulation.reactiondiffusion.rendering.RDRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Singleton for RD profiling.
 *
 * @author Barry Becker
 */
public class RDProfiler extends Profiler {

    protected static final String RENDERING = "rendering";
    protected static final String CALCULATION = "calculation";

    private static RDProfiler instance;

    private int numFrames;


    /**
     * @return singleton instance.
     */
    public static RDProfiler getInstance() {
        if (instance == null) {
            instance = new RDProfiler();
        }
        return instance;
    }

    /**
     * Private constructor. Use getInstance instead.
     */
    protected RDProfiler() {
        add(CALCULATION);
        add(RENDERING);
    }


    public void initialize() {
        resetAll();
        setEnabled(GameContext.isProfiling());
        setLogger(GameContext.getLogger());
    }


    @Override
    public void print() {

        if (!isEnabled()) return;
        double calcTime = getEntry(CALCULATION).getTimeInSeconds();
        double renderingTime = getEntry(RENDERING).getTimeInSeconds();
        double ratio = calcTime / renderingTime;
        printMessage("Number of Frames: " + Util.formatNumber(numFrames));
        printMessage("Calculation time per frame (sec):" + Util.formatNumber(calcTime/numFrames));
        printMessage("Rendering time per frame   (sec):" + Util.formatNumber(renderingTime/numFrames));
        printMessage("Ratio of calculation to rendering time:" + Util.formatNumber(ratio) );
        super.print();
    }

    @Override
    public void resetAll()  {
        super.resetAll();
        numFrames = 0;
    }

    public void startCalculationTime() {
        this.start(CALCULATION);
    }

    public void stopCalculationTime() {
        this.stop(CALCULATION);
    }

    public void startRenderingTime() {
        this.start(RENDERING);
    }

    public void stopRenderingTime() {
        this.stop(RENDERING);
        numFrames++;
    }

}
