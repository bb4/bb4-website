package com.becker.ui;

import java.io.File;

/**
 * This is a FileFilter for Directories
 * For use with JFileChoosers
 *
 * @author Barry Becker
 */

public class DirFileFilter extends javax.swing.filechooser.FileFilter {

   protected static String DIRECTORY_DESC = "dir";

   public boolean accept(File f)
   {
         return f.isDirectory();
   }

   public String getDescription() {
         return DIRECTORY_DESC;
   }
}
