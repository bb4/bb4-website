package com.becker.ui.file;

import com.becker.common.util.FileUtil;
import com.becker.ui.GUIUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;

/**
 * Miscelaneous commonly used file chooser related utility methods.
 * @author Barry Becker
 */
public final class FileChooserUtil
{

    private static JFileChooser chooser_ = null;

    /**
     * cannot instantiate static class.
     */
    private FileChooserUtil() {}


    /**
     * @return a generic file chooser.
     */
    public static JFileChooser getFileChooser() {
        return getFileChooser(null);
    }

    /**
     * @param filter optional file filter
     * @return file chooser with specified filter.
     */
    public static JFileChooser getFileChooser(FileFilter filter) {
        if (chooser_ == null) {
            chooser_ = GUIUtil.getFileChooser();
            chooser_.setCurrentDirectory( new File( FileUtil.getHomeDir() ) );
            chooser_.setFileFilter(filter);
        }
        return chooser_;
    }


    public static File getSelectedFileToSave(String extension, File defaultDir) {
        return getSelectedFile("Save", extension, defaultDir);
    }

    public static File getSelectedFileToOpen(String extension, File defaultDir) {
         return getSelectedFile("Open", extension, defaultDir);
    }

    private static File getSelectedFile(String action, String extension, File defaultDir) {

        JFileChooser chooser = FileChooserUtil.getFileChooser(new ExtensionFileFilter(extension));
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
}