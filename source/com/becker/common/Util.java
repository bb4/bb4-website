package com.becker.common;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;

/**
 * Miscelaneous commonly used static utility methods.
 * May want to break this up into FileUtil, etc.
 */
public final class Util
{
    // This path should be changed if you run the application form of the applets on a different machine.
    // use this if running under windows
    public static final String PROJECT_DIR = "/home/becker/projects/java_projects/";

    private static final DecimalFormat expFormat_ = new DecimalFormat("###,###.##E0");
    private static final DecimalFormat format_ = new DecimalFormat("###,###.##");
    private static final DecimalFormat intFormat_ = new DecimalFormat("#,###");

    private Util() {};

    /**
     * Copy source file to destination file.
     *
     * @param srcfile The source file
     * @param destfile The destination file
     * @throws SecurityException
     * @throws java.io.IOException
     */
    public static void copyFile( String srcfile, String destfile ) throws IOException
    {
        byte[] bytearr = new byte[512];
        int len = 0;
        BufferedInputStream input = new BufferedInputStream( new FileInputStream( srcfile ) );
        BufferedOutputStream output = new BufferedOutputStream( new FileOutputStream( destfile ) );
        try {
            while ( (len = input.read( bytearr )) != -1 ) {
                output.write( bytearr, 0, len );
            }
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        } catch (SecurityException exc) {
            exc.printStackTrace();
        } finally {
            input.close();
            output.close();
        }
    }

    /**
     *	create a PrintWriter with utf8 encoding
     *  returns null if there was a problem creating it.
     *	@param filename including the full path
     */
    public static PrintWriter createPrintWriter( String filename )
    {
        PrintWriter outfile = null;
        try {
            outfile = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream( filename, false ),
                                    "UTF-8" ) ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outfile;
    }

    /**
     * @param num the number to format.
     * @return a nicely formatted string representation of the number.
     */
    public static String formatNumber(double num)
    {
        double absnum = Math.abs(num);
        if ((num - (long)num) == 0.0) {
            // it is an integer
            format_.setMinimumFractionDigits(0);
            format_.setMaximumFractionDigits(0);
        }
        else {
            if (absnum > 100000.0 || absnum < 0.000000001) {
                return expFormat_.format(num);
            }
            if (absnum > 100.0 || num == 0.0) {
                format_.setMinimumFractionDigits(1);
                format_.setMaximumFractionDigits(1);
            }
            else if (absnum > 1.0) {
                format_.setMinimumFractionDigits(1);
                format_.setMaximumFractionDigits(2);
            }
            else if (absnum > 0.0001) {
                format_.setMinimumFractionDigits(2);
                format_.setMaximumFractionDigits(5);
            }
            else if (absnum>0.000001) {
                format_.setMinimumFractionDigits(3);
                format_.setMaximumFractionDigits(8);
            }
            else {
                format_.setMinimumFractionDigits(6);
                format_.setMaximumFractionDigits(10);
            }
        }

        return format_.format(num);
    }

    /**
     * @param num the number to format.
     * @return a nicely formatted string representation of the number.
     */
    public static String formatNumber(int num)
    {
        return intFormat_.format(num);
    }


    /**
     * @param className  the class to load.
     * @return  the loaded class.
     */
    public static Class loadClass(String className)
    {
        return loadClass(className, null);
    }

    /**
     * @param className  the class to load.
     * @param defaultClassName  the backup class to load if className does not exist.
     * @return  the loaded class.
     */
    public static Class loadClass(String className, String defaultClassName)
    {
        Class theClass = null;
        try {
            System.out.println( "about to load "+className );
            theClass = Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Unable to find the class "+ className+". Check your classpath.");
            //System.out.println("The current classpath is :"+System.getProperty("java.class.path"));
            if (defaultClassName == null) {
                e.printStackTrace();
                return null;
            }
            System.out.println("Attempting to load "+defaultClassName+" instead.");
            try {
                theClass = Class.forName(defaultClassName);
            } catch (ClassNotFoundException cne) {
                 cne.printStackTrace();
            }
        }
        System.out.println("class "+className+" was loaded succesfully.");
        return theClass;
    }

    /**
     * print a generic set of objects (like a set or list).
     * @param c the set to print.
     */
    public static void printCollection(Collection c)
    {
       System.out.println( stringify(c) );
    }

    /**
     * get string form of a generic set of objects (like a set or list).
     * @param c the set to print.
     */
    public static String stringify(Collection c)
    {
        StringBuffer sBuf = new StringBuffer();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            sBuf.append( it.next().toString() );
            sBuf.append('\n');
        }
        return sBuf.toString();
    }


    public static void sleep(int millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            };
        }
    }

}

