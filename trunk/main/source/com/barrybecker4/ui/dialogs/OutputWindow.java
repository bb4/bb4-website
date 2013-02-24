/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.dialogs;

import com.barrybecker4.ui.components.ScrollingTextArea;

import javax.swing.*;
import java.awt.*;

/**
 * Use this dialog to show the user a body of text
 *
 * @author Barry Becker
 */
public class OutputWindow extends AbstractDialog {

    protected ScrollingTextArea textArea_ = null;

    private static final Font TEXT_FONT = new Font("Times-Roman", Font.PLAIN, 10 );
    private static final long serialVersionUID = 1234L;

    /**
     * Constructor
     */
    public OutputWindow( String title, JFrame parent ) {
        super( parent );
        this.setTitle( title );
        this.setModal( false );
        showContent();
    }

    @Override
    protected JComponent createDialogContent() {
        textArea_ = new ScrollingTextArea();

        // if its editable then we can copy from it
        textArea_.setEditable( true );
        textArea_.setFont( TEXT_FONT );
        return textArea_;
    }

    /**
     * add this text to what is already there
     */
    public void appendText( String text ) {
        if ( text != null )
            textArea_.append( text );
    }

    /**
     * replace current text with this text
     */
    public void setText( String text )  {
        textArea_.setText( text );
    }
}