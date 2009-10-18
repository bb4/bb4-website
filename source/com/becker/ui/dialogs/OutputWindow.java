package com.becker.ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Use this dialog to show the user a body of text
 *
 * @author Barry Becker
 */
public class OutputWindow extends JDialog
{

    protected JPanel mainPanel_ = null;
    protected JTextArea textArea_ = null;

    // cache a pointer to this in case we have children
    protected JFrame parent_ = null;
    private static final Font TEXT_FONT = new Font( "Times-Roman", Font.PLAIN, 10 );
    private static final Dimension DEFAUT_SIZE = new Dimension( 500, 400 );
    private static final long serialVersionUID = 1234L;

    // constructor
    public OutputWindow( String title, JFrame parent )
    {
        super( parent );
        parent_ = parent;
        this.setTitle( title );

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pack();
        this.setModal( false );
    }

    // @return true if the dialog is canceled
    public boolean showDialog()
    {
        setVisible( true );
        return true;
    }

    protected void initUI()
    {
        textArea_ = new JTextArea( "" );
        textArea_.setWrapStyleWord( true );
        // if its editable then we can copy from it
        textArea_.setEditable( true );
        textArea_.setFont( TEXT_FONT );
        //textArea_.setMaximumSize(new Dimension(DEFAUT_SIZE.getWidth());

        JScrollPane scrollPane = new JScrollPane( textArea_ );
        scrollPane.setPreferredSize( DEFAUT_SIZE );

        this.getContentPane().add( scrollPane, "Center" );
    }

    /**
     * add this text to what is already there
     */
    public void appendText( String text )
    {
        if ( text != null )
            textArea_.append( text );
    }

    /**
     * repalce current text with this text
     */
    public void setText( String text )
    {
        textArea_.setText( text );
    }

    protected void processWindowEvent( WindowEvent e )
    {
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            cancel();
        }
        super.processWindowEvent( e );
    }

    protected void cancel()
    {
        this.setVisible( false );
    }

    public void close()
    {
        this.setVisible( false );
        this.dispose();
    }
}