package com.becker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A panel that allows the user to enter a color for something using the standard color chooser.
 *
 * @author Barry Becker
 */
public class ColorInputPanel extends JPanel
{

    /**
     * @param label  the label for this panel entry
     * @param toolTip  the tooltip for the color button givin ghte user instructions.
     * @param colorButton  the button to click to bring up the chooser. This button's background is maintains the color.
     */
    public ColorInputPanel(final String label, String toolTip, final JButton colorButton)
    {

        //JPanel colorPanel = new JPanel();
        this.setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
        this.setAlignmentX( Component.LEFT_ALIGNMENT );

        JLabel colorLabel = new JLabel( label );
        this.add( colorLabel );

        colorButton.setToolTipText( toolTip );

        colorButton.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed( ActionEvent evt )
                    {
                        Object source = evt.getSource();
                        if ( source == colorButton ) {
                            Color selectedColor = JColorChooser.showDialog( colorButton, label, colorButton.getBackground() );
                            colorButton.setBackground( selectedColor );
                        }
                    }
                } );
        this.add(colorButton);
    }
}
