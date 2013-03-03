/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.util;

import com.barrybecker4.common.ILog;
import com.barrybecker4.ui.dialogs.OutputWindow;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Provide support for general logging.
 * You have the option of logging output to the console, to a separate window, or to a file
 *
 * @see OutputWindow
 * @author Barry Becker
 */
public class Log implements ILog {

    /**
     * Must be static because accessed in static method (logMessage)
     * the default is to log to the console
     */
    private int logDestination_ = ILog.LOG_TO_CONSOLE;

    /** an output window for logging  */
    private OutputWindow logWindow_ = null;
    private OutputStream fileOutStream_ = null;
    /** used if logging to String */
    private StringBuilder logBuffer_ = null;

    /**
     * Log Constructor
     */
    public Log()
    {}

    /**
     * Log Constructor
     * @param logWindow window to send output to
     */
    public Log( OutputWindow logWindow ) {
        logWindow_ = logWindow;
        setDestination( logDestination_ );
    }

    /**
     * @return  the current loggin destination
     */
    public int getDestination() {
        return logDestination_;
    }

    /**
     *  Set the log destination
     *  @@ allow multiples using | to combine the hex constants
     */
    public void setDestination( int logDestination ) {
        logDestination_ = logDestination;
        if ( logWindow_ != null ) {
            logWindow_.setVisible(logDestination_ == ILog.LOG_TO_WINDOW);
        }
    }

    public void setLogFile( String fileName ) throws FileNotFoundException {
        fileOutStream_ = new BufferedOutputStream(new FileOutputStream(fileName));
    }

    public void setStringBuilder(StringBuilder bldr)  {
        logBuffer_ = bldr;
    }

    /**
     * Log a message to the logDestination
     * The log destination is defined by logDestination_
     * @param logLevel message will only be logged if this number is less than the application logLevel (debug_)
     * @param message the message to log
     */
    public void print( int logLevel, int appLogLevel, String message ) {

        if ( logLevel <= appLogLevel ) {
            if ((logDestination_ & ILog.LOG_TO_CONSOLE) >0) {
                System.err.println( message );
            }
            if ((logDestination_ & ILog.LOG_TO_WINDOW) > 0) {
                if ( logWindow_ != null )
                    logWindow_.appendText( message );
                else
                    System.err.println("no logWindow to print to. First specify with setLogWindow. message="+message);
            }
            if ((logDestination_ & ILog.LOG_TO_FILE) > 0) {
                if (fileOutStream_ != null)  {
                     try {
                         fileOutStream_.write(message.getBytes());
                     } catch (IOException e) {
                         System.err.println( message );
                         e.printStackTrace();
                     }
                 }
                 else System.err.println("no logFile to print to. First specify with setLogFile. message="+message);
            }
            if ((logDestination_ & ILog.LOG_TO_STRING) > 0) {
                if (logBuffer_ != null)  {
                    logBuffer_.append(message);
                }
                else System.err.println(
                        "no StringBuilder buffer was set to print to. First specify with setStringBuilder.  " +
                        "message="+message);
            }
        }
    }

    public void println( int logLevel, int appLogLevel, String message ) {
        print( logLevel, appLogLevel, message + '\n' );
    }

    public void print( String message ) {
        print( 0, 0, message );
    }

    public void println( String message ) {
        print( 0, 0, message + '\n' );
    }

}



