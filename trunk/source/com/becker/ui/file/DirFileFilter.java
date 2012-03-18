/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.ui.file;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * This is a FileFilter for Directories
 * For use with JFileChoosers
 *
 * @author Barry Becker
 */

public class DirFileFilter extends FileFilter {

   protected static final String DIRECTORY_DESC = "dir";

   @Override
   public boolean accept(File f)
   {
         return f.isDirectory();
   }

   @Override
   public String getDescription() {
         return DIRECTORY_DESC;
   }
}
