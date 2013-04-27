// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class ScrollingTextArea extends JScrollPane implements Appendable {

    private JTextArea textArea;

    /** Constructor */
    public ScrollingTextArea() {

        textArea = createTextArea(0, 0);
        this.setViewportView(textArea);
    }

    public ScrollingTextArea(int rows, int cols) {

        textArea = createTextArea(rows, cols);
        this.setViewportView(textArea);
    }

    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }

    public void setText(String txt) {
        textArea.setText(txt);
    }

    public String getText() {
        return textArea.getText();
    }

    /**
     * Always scroll to the bottom so what was appended can be seen.
     * @param txt text to append
     */
    @Override
    public void append(String txt)  {
        textArea.append(txt);
        // this is not needed for java 6 or above.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    private JTextArea createTextArea(int rows, int cols) {
        JTextArea text = new JTextArea(rows, cols);
        text.setMargin(new Insets(5,5,5,5));
        text.setLineWrap(true);
        text.setWrapStyleWord( true );
        text.setEditable(false);
        return text;
    }
}
