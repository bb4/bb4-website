package com.becker.ui;

import java.io.File;

/**
 * This is a FileFilter for text files.
 * For use with JFileChoosers
 *
 * @author Barry Becker
 */

public class TextFileFilter extends javax.swing.filechooser.FileFilter {

  public final String TEXT_EXT = "txt";


  public boolean accept(File f)
  {
       boolean accept = f.isDirectory();
       if  (!accept) {
            String suffix = GUIUtil.getFileSuffix(f);
            if (suffix != null)
               accept = (suffix.equals(TEXT_EXT));
       }
       return accept;
  }

  public String getDescription() {
         return "*." + TEXT_EXT;
  }

}
