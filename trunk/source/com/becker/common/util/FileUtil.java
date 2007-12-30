package com.becker.common.util;

import java.io.*;

/**
 * Miscelaneous commonly used file related static utility methods.
 *@author Barry Becker
 */
public final class FileUtil
{

    // This path should be changed if you run the application form of the applets on a different machine.
    // use this if running under windows
    public static final String USER_HOME = "G:";
    // use this running under Linux
    //public static final String USER_HOME = "/windows"; 
    public static final String PROJECT_DIR = USER_HOME + "/projects/java_projects/trunk/";
    
    /**
     * cannot instantiate static class.
     */
    private FileUtil() {}
    
    

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
    
}
