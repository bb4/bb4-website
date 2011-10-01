package com.becker.common.profile;

/**
 * Use this class to get a single performance number for your application.
 * This profiler just contains a single top level entry for a single timing number.
 *
 * @author Barry Becker
 */
public class SimpleProfiler extends Profiler {

    private static final String ROOT = "totalTime";

    /**
     * Default constructor.
     */
    public SimpleProfiler() {
        super.add(ROOT);
    }


    public void start()  {
        start(ROOT);
    }

    public void stop() {
        stop(ROOT);
    }
}