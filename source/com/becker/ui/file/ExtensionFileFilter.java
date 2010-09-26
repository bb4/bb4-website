package com.becker.ui.file;

import com.becker.ui.GUIUtil;

import javax.swing.filechooser.FileFilter;
import java.io.File;


/**
 * This is a FileFilter for files having some specific extension.
 * For use with JFileChoosers
 *
 * @author Barry Becker Date: Oct 29, 2006
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
     * @param f
     * @return true if f matches the desired extesion.
     */
    public boolean accept(File f)
    {
       boolean accept = f.isDirectory();
       if  (!accept) {
            String suffix = GUIUtil.getFileSuffix(f);
            if (suffix != null)
               accept = (suffix.equals(extension_));
       }
       return accept;
    }

    public String getDescription() {
         return "*." + extension_;
    }


    /**
     * @param fPath  to verify
     * @return fPath with the proper extesion added if it was not there before.
     */
    public static String addExtIfNeeded(String fPath, String ext) {
        String newPath = fPath;
        if (!newPath.endsWith('.' + ext))
                newPath += '.' + ext;
        return newPath;
    }

}
