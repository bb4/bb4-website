/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.file;

import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.filechooser.FileFilter;
import java.io.File;


/**
 * This is a FileFilter for files having some specific extension.
 * For use with JFileChoosers
 *
 * @author Barry Becker
 */
public class ExtensionFileFilter extends FileFilter {

    private String extension_;

    /**
     * @param extension a file extension (excluding the dot)
     */
    public ExtensionFileFilter(String extension) {
        extension_ = extension;
    }

    /**
     * @param file the file to check for acceptance.
     * @return true if f matches the desired extension.
     */
    @Override
    public boolean accept(File file) {
       boolean accept = file.isDirectory();
       if  (!accept) {
            String suffix = GUIUtil.getFileSuffix(file);
            if (suffix != null)
               accept = (suffix.equals(extension_));
       }
       return accept;
    }

    @Override
    public String getDescription() {
         return "*." + extension_;
    }

    /**
     * @param fPath  to verify
     * @return fPath with the proper extension added if it was not there before.
     */
    public static String addExtIfNeeded(String fPath, String ext) {
        String newPath = fPath;
        if (!newPath.endsWith('.' + ext))
                newPath += '.' + ext;
        return newPath;
    }

}
