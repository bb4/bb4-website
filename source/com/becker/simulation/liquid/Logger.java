package com.becker.simulation.liquid;

import com.becker.common.ILog;
import com.becker.common.util.FileUtil;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import com.becker.simulation.common.NewtonianSimulator;
import com.becker.simulation.common.SimulatorOptionsDialog;
import com.becker.simulation.liquid.config.ConfigurationEnum;
import com.becker.simulation.liquid.model.LiquidEnvironment;
import com.becker.simulation.liquid.rendering.EnvironmentRenderer;
import com.becker.ui.dialogs.OutputWindow;
import com.becker.ui.util.GUIUtil;
import com.becker.ui.util.Log;

import javax.swing.*;
import java.awt.*;

/**
 * Singleton instance of logger.
 *
 * @author Barry Becker
 */
public class Logger {

    /** for debugging */
    public static final int LOG_LEVEL = 0;

    /** The singleton instance */
    private static ILog logger;

    /**
     * Constructor
     */
    private Logger() {}

    public static ILog getInstance()  {
        if (logger == null)   {

            logger = new Log( new OutputWindow( "Log", null ) );
            logger.setDestination(ILog.LOG_TO_WINDOW);
        }
        return logger;
    }

    public static void log(int level, String msg) {
       getInstance().println(level, LOG_LEVEL, msg);
    }
}