/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Shows files with filter that is passed to the constructor.
 *
 * @author Barry Becker
 */
public class GenericFileFilter implements FilenameFilter {

    /** the filter to use when figuring out what files to select. */
    private String pattern_;

   /**
     * Creates a new instance of GenericFileFilter
     */
    private GenericFileFilter(String pattern) {
         pattern_ = pattern;
    }

    /**
     * Acceptance test.
     */
    public boolean accept(File dir, String name) {
        return (name.contains(pattern_));
    }

    /**
     * @param pattern find files that match this pattern.
     * @return all the files matching the supplied pattern in the specified directory
     */
    public static String[] getFilesMatching(String directory, String pattern) {

        File dir =  new File(directory);
        assert (dir.isDirectory());

        //System.out.println("pattern = "+pattern+ "dir="+dir.getAbsolutePath());
        FilenameFilter filter = new GenericFileFilter(pattern);
        return dir.list(filter);
    }

}

