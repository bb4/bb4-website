// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.sedit;

import com.barrybecker4.common.util.Base64Codec;
import com.barrybecker4.ui.components.ScrollingTextArea;
import com.barrybecker4.ui.file.ExtensionFileFilter;
import com.barrybecker4.ui.file.FileChooserUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleEditor extends ScrollingTextArea {

    private static final boolean COMPRESS = true;

    /**
     * a top level menu to allow opening and saving of edited files.
     */
    SimpleEditor(int rows, int cols) {
        super(rows, cols);
    }

    public void loadFile(String fileName) {
        try {
            BufferedReader reader =
                 new BufferedReader( new FileReader( fileName ) );

            StringBuilder bldr = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                bldr.append(line);
            }
            String text = bldr.toString();

            if (COMPRESS)
                text = Base64Codec.decompress(text);

            setText(text);
            reader.close();
        } catch (IOException e) {
             e.printStackTrace();
        }
    }

     public void saveFile(String fileName) {
         try {
             BufferedWriter out =
                     new BufferedWriter(new FileWriter( fileName ));

             String text = getText();
             if (COMPRESS)
                 text = Base64Codec.compress(text);
             out.write(text);
             out.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
}
