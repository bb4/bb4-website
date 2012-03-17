/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.common.util;

import java.io.*;

/**
 * Miscellaneous commonly used file related static utility methods.
 * @author Barry Becker
 */
public final class FileUtil {

    /**
     * Get the correct file separator whether on windows (\) or linux (/).
     * Getting error in applets if trying to use System.getProperty("file.separator")
     */
    public static final String FILE_SEPARATOR = "/";
    
    /**
     * Points to the main project directory.
     * Reads the PROJECT_HOME env variable to figure out where the data files are.
     * If not deployed, you can use System.getenv("PROJECT_HOME") + FILE_SEPARATOR;
     */
    public static final String PROJECT_HOME = "E:/projects/java_projects/trunk" + FILE_SEPARATOR;
    
    /**
     * cannot instantiate static class.
     */
    private FileUtil() {}

    /**
     * @return home directory. Assumes running as an Application.
     */
    public static String getHomeDir() {
        return PROJECT_HOME;
    }

    /**
     * create a PrintWriter with utf8 encoding
     * returns null if there was a problem creating it.
     * @param filename including the full path
     * @return new PrintWriter instance
     */
    public static PrintWriter createPrintWriter( String filename ) {
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
     */
    public static void copyFile( String srcfile, String destfile ) throws IOException {
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
