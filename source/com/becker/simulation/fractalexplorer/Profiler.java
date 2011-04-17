package com.becker.simulation.fractalexplorer;

import com.becker.common.util.Util;


/**
 * Singleton for RD profiling.
 *
 * @author Barry Becker
 */
public class Profiler extends com.becker.common.profile.Profiler {

    protected static final String RENDERING = "rendering";
    protected static final String CALCULATION = "calculation";

    private static Profiler instance;

    /**
     * @return singleton instance.
     */
    public static Profiler getInstance() {
        if (instance == null) {
            instance = new Profiler();
        }
        return instance;
    }

    /**
     * Private constructor. Use getInstance instead.
     */
    protected Profiler() {
        add(CALCULATION);
        add(RENDERING);
    }

    public void initialize() {
        resetAll();
    }

    @Override
    public void print() {

        if (!isEnabled()) {
            return;
        }
        double calcTime = getEntry(CALCULATION).getTimeInSeconds();
        double renderingTime = getEntry(RENDERING).getTimeInSeconds();
        double ratio = calcTime / renderingTime;
        printMessage("Ratio of calculation to rendering time:" + Util.formatNumber(ratio) );
        super.print();
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
    }

}
