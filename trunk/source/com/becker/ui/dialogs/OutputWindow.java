package com.becker.ui.dialogs;

import javax.swing.*;
import java.awt.*;

/**
 * Use this dialog to show the user a body of text
 *
 * @author Barry Becker
 */
public class OutputWindow extends AbstractDialog {

    protected JTextArea textArea_ = null;

    private static final Font TEXT_FONT = new Font( "Times-Roman", Font.PLAIN, 10 );
    private static final Dimension DEFAUT_SIZE = new Dimension( 500, 400 );
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
        textArea_ = new JTextArea( "" );
        textArea_.setWrapStyleWord( true );
        // if its editable then we can copy from it
        textArea_.setEditable( true );
        textArea_.setFont( TEXT_FONT );
        //textArea_.setMaximumSize(new Dimension(DEFAUT_SIZE.getWidths());

        JScrollPane scrollPane = new JScrollPane( textArea_ );
        scrollPane.setPreferredSize( DEFAUT_SIZE );

        //this.getContentPane().add( scrollPane, "Center" );
        return scrollPane;
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