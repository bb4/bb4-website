/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.file;

/**
 * This is a FileFilter for text files.
 * For use with JFileChoosers
 *
 * @author Barry Becker
 */

public class TextFileFilter extends ExtensionFileFilter {

    public static final String TEXT_EXTENSION = "txt";

    public TextFileFilter() {
       super(TEXT_EXTENSION);
    }

}
