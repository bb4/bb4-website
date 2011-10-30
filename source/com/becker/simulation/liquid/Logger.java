/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.liquid;

import com.becker.common.ILog;
import com.becker.ui.dialogs.OutputWindow;
import com.becker.ui.util.Log;

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