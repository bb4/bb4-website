package com.becker.common.util;

import com.becker.ui.GUIUtil;
import com.becker.ui.filefilter.ExtensionFileFilter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Miscelaneous commonly used file related static utility methods.
 *@author Barry Becker
 */
public final class FileUtil
{

    private static JFileChooser chooser_ = null;

    /**
     *  This path should be changed if you run the application form of the applets on a different machine.
     *  use this if running under windows
     */
    public static final String USER_HOME = "G:";

    // use this running under Linux
    //public static final String USER_HOME = "/windows"; 
    public static final String PROJECT_DIR = USER_HOME + "/projects/java_projects/trunk/";
    
    /**
     * cannot instantiate static class.
     */
    private FileUtil() {}
    

    /**
     * @param filter optional file filter
     * @return
     */
    public static JFileChooser getFileChooser() {
        return getFileChooser(null);
    }

    /**
     * @param filter optional file filter
     * @return
     */
    public static JFileChooser getFileChooser(FileFilter filter) {
        if (chooser_ == null) {
            chooser_ = GUIUtil.getFileChooser();
            chooser_.setCurrentDirectory( new File( getHomeDir() ) );
            chooser_.setFileFilter(filter);
        }
        return chooser_;
    }

    /**
     * @return home directory. Assumes running as an Application.
     */
    public static String getHomeDir()
    {
        String userHome = FileUtil.USER_HOME;   // System.getProperty("user.home");

        String home =  userHome + "/projects/java_projects/trunk";
        return home;
    }

    public static File getSelectedFileToSave(String extension, File defaultDir) {
        return getSelectedFile("Save", extension, defaultDir);
    }

    public static File getSelectedFileToOpen(String extension, File defaultDir) {
         return getSelectedFile("Open", extension, defaultDir);
    }

    private static File getSelectedFile(String action, String extension, File defaultDir) {

        JFileChooser chooser = FileUtil.getFileChooser(new ExtensionFileFilter(extension));
         chooser.setDialogTitle(action);
         chooser.setApproveButtonText(action);
         // not really very i18nish, but oh ok for now.
         chooser.setApproveButtonToolTipText(action + " the specified file.");

         if (defaultDir != null) {
             chooser.setCurrentDirectory(defaultDir);
         }

         int state = chooser.showOpenDialog( null );
         File file = chooser.getSelectedFile();
         if ( file != null && state == JFileChooser.APPROVE_OPTION )  {
             return file;
         }
         else return null;
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
