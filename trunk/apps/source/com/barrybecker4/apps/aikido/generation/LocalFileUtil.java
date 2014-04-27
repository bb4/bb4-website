// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.aikido.generation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Move this to bb4-common and use that instead
 * @author Barry Becker
 */
class LocalFileUtil {

    /**
     * @param filename name of file to read from
     * @return text within the file
     * @throws IllegalStateException if could not read the file
     */
    public static String readTextFile(String filename) {
        BufferedReader br = null;
        StringBuilder bldr = new StringBuilder(1000);

        try {
            br = new BufferedReader(new FileReader(filename));

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                bldr.append(sCurrentLine).append('\n');
            }

        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + filename, e);
        }
        finally {
            try {
                if (br != null) br.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return bldr.toString();
    }

    private LocalFileUtil() {}
}
