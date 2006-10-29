package com.becker.ui.filefilter;

import javax.swing.filechooser.*;
import java.io.File;

/**
 * This is a FileFilter for Directories
 * For use with JFileChoosers
 *
 * @author Barry Becker
 */

public class DirFileFilter extends FileFilter {

   protected static final String DIRECTORY_DESC = "dir";

   public boolean accept(File f)
   {
         return f.isDirectory();
   }

   public String getDescription() {
         return DIRECTORY_DESC;
   }
}
