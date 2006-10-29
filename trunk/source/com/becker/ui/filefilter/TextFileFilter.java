package com.becker.ui.filefilter;

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
