package com.becker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * NumberInputPanel is a panel that has a label on the left
 * and an input on the right that accepts only numbers
 *
 * @author Barry Becker
 */
public class NumberInputPanel extends JPanel
{

    /**
     * @param labelText label for the number input element
     * @param numberField filed for the user to enter the desired number
     */
    public NumberInputPanel( String labelText, final JTextField numberField )
    {
       this( labelText,   numberField, null);
    }

    /**
     * @param labelText label for the number input element
     * @param numberField filed for the user to enter the desired number
     * @param toolTip the tooltip for the whole panel
     */
    public NumberInputPanel( String labelText, final JTextField numberField, String toolTip )
    {

        setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
        setAlignmentX( Component.LEFT_ALIGNMENT );
        JLabel label = new JLabel( labelText );
        add( label );

        numberField.setToolTipText( "enter a number in the suggested range" );
        numberField.setPreferredSize( new Dimension( 60, 15 ) );
        numberField.setMinimumSize( new Dimension( 30, 15 ) );
        //numberField.addActionListener( parent );
        numberField.addKeyListener( new KeyAdapter()
        {
            public void keyTyped( KeyEvent key )
            {
                char c = key.getKeyChar();
                if ( c >= 'A' && c <= 'z' ) {
                    JOptionPane.showMessageDialog( null,
                            "no non-numeric characters allowed!", "Error", JOptionPane.ERROR_MESSAGE );
                    // clear the input text since it is in error
                    numberField.setText( "" );
                    key.consume(); // don't let it get entered
                }
                else if ( (c < '0' || c > '9') && (c != 8) ) {  // 8=backspace
                    key.consume(); // don't let it get entered
                }
            }
        } );
        add( new JPanel());
        add( numberField );

        if (toolTip!=null)
            this.setToolTipText(toolTip);
        else
            this.setToolTipText(labelText);
    }
}
