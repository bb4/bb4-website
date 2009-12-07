package com.becker.common;

import java.io.FileNotFoundException;

/**
 * Provide support for general logging.
 *
 * @author Barry Becker
 */
public interface ILog {


    // you can specify the debug, profile info, warning, and error resources to go to one
    // or more of these places.
    int LOG_TO_CONSOLE = 0x1;
    int LOG_TO_WINDOW = 0x2;
    int LOG_TO_FILE = 0x4;
    int LOG_TO_STRING = 0x8;

    /**
     *  Set the log destination
     *  @@ allow multiples using | to combine the hex constants
     */
    void setDestination( int logDestination );

    /**
     * @return  the current loggin destination
     */
    int getDestination();

    /**
     * @param fileName the name of the file to send the output to.
     */
    void setLogFile( String fileName ) throws FileNotFoundException;

    /** for logging to a string. */
    void setStringBuilder(StringBuilder bldr);

    /**
     * Log a message to the logDestination
     * @param logLevel message will only be logged if this number is less than the application logLevel (debug_)
     * @param appLogLevel the applications log level.
     * @param message the message to log
     */
    void print( int logLevel, int appLogLevel, String message );

    /**
     * Log a message to the logDestination followed by a newline.
     * @param logLevel message will only be logged if this number is less than the application logLevel (debug_)
     * @param appLogLevel the applications log level.
     * @param message the message to log
     */
    void println( int logLevel, int appLogLevel, String message );

    void print( String message );

    void println( String message );
}



