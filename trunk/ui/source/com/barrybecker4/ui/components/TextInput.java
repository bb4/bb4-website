/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

/**
 * A panel that has a label on the left
 * and a text field on the right for entering some text.
 *
 * @author Barry Becker
 */
public class TextInput extends JPanel {

    private JTextField textField_;


    /**
     * Often the initial value cannot be set when initializing the content of a dialog.
     * This uses a default of 0 until the real default can be set with setInitialValue.
     * @param labelText label for the number input element
     */
    public TextInput( String labelText) {
       this(labelText, "");
    }

    /**
     * @param labelText label for the number input element
     * @param initialValue the value to use if nothing else if entered. shows in the ui.
     * @param numColumns width of text field.
     */
    public TextInput( String labelText, String initialValue, int numColumns ) {
        this(labelText, initialValue);
        this.setColumns(numColumns);
    }


    @Override
    public void addKeyListener(KeyListener listener) {
        textField_.addKeyListener(listener);
    }

    /**
     * @param labelText label for the number input element
     * @param initialValue the value to use if nothing else if entered. shows in the ui.
     */
    public TextInput( String labelText, String initialValue ) {
        textField_ = new JTextField(initialValue);
        textField_.setMargin(new Insets(0, 4, 0, 4));
        this.setLayout( new BorderLayout());

        this.setAlignmentX( Component.LEFT_ALIGNMENT );

        JLabel label = new JLabel( labelText );
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        this.add( label, BorderLayout.WEST );

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        panel.add( textField_, BorderLayout.WEST);

        add(panel, BorderLayout.CENTER);
    }

    public void setColumns(int numColumns) {
        textField_.setColumns(numColumns);
    }

    public String getValue() {
        return textField_.getText();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField_.setEnabled(enabled);
    }
}
