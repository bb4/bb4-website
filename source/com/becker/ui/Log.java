package com.becker.ui;

import com.becker.common.ILog;

import java.io.*;

/**
 * Provide support for general logging.
 * You have the option of logging output to the console, to a separate window, or to a file
 *
 * @see com.becker.ui.OutputWindow
 * @author Barry Becker
 */
public class Log implements ILog
{

    // must be static because accessed in satic method (logMessage)
    // the default is to log to the console
    private int logDestination_ = LOG_TO_CONSOLE;

    // an output window for logging
    private OutputWindow logWindow_ = null;
    private OutputStream fileOutStream_ = null;
    // used if logging to String
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
    public Log( OutputWindow logWindow )
    {
        logWindow_ = logWindow;
        setDestination( logDestination_ );
    }

    /**
     * @return  the current loggin destination
     */
    public int getDestination()
    {
        return logDestination_;
    }

    /**
     *  Set the log destination
     *  @@ allow multiples using | to combine the hex constants
     */
    public void setDestination( int logDestination )
    {
        logDestination_ = logDestination;
        if ( logWindow_ != null ) {
            if ( logDestination_ == LOG_TO_WINDOW )
                logWindow_.setVisible(true);
            else
                logWindow_.setVisible(false);
        }
    }

    /**
     * @param w The static log window to send the output to.
     */
    public void setLogWindow( OutputWindow w )
    {
        logWindow_ = w;
    }


    public void setLogFile( String fileName ) throws FileNotFoundException
    {
        fileOutStream_ = new BufferedOutputStream(new FileOutputStream(fileName));
    }

    public void setStringBuilder(StringBuilder bldr)
    {
        logBuffer_ = bldr;
    }

    /**
     * Log a message to the logDestination
     * The log destination is defined by logDestination_
     * @param logLevel message will only be logged if this number is less than the application logLevel (debug_)
     * @param message the message to log
     */
    public void print( int logLevel, int appLogLevel, String message )
    {
        if ( logLevel <= appLogLevel ) {
            if ((logDestination_ & LOG_TO_CONSOLE) >0) {
                System.err.println( message );
            }
            if ((logDestination_ & LOG_TO_WINDOW) > 0) {
                if ( logWindow_ != null )
                    logWindow_.appendText( message );
                else
                    System.err.println("no logWindow to print to. First specify with setLogWindow. message="+message);
            }
            if ((logDestination_ & LOG_TO_FILE) > 0) {
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
            if ((logDestination_ & LOG_TO_STRING) > 0) {
                if (logBuffer_ != null)
                {
                    logBuffer_.append(message);
                }
                else System.err.println("no StringBuilder buffer was set to print to. First specify with setStringBuilder.  message="+message);
            }
        }
    }

    public void println( int logLevel, int appLogLevel, String message )
    {
        print( logLevel, appLogLevel, message + '\n' );
    }

    public void print( String message )
    {
        print( 0, 0, message );
    }

    public void println( String message )
    {
        print( 0, 0, message + '\n' );
    }

}



