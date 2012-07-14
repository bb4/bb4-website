// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * An entry in a list of radio buttons
 * @author Barry Becker
 */
public class RadioButtonPanel extends JPanel {

    public RadioButtonPanel(JRadioButton radioButton, ButtonGroup buttonGroup, boolean selected ) {

        setLayout( new BorderLayout() );
        setAlignmentX( Component.LEFT_ALIGNMENT );

        radioButton.setSelected( selected );
        //radioButton.addItemListener( this );
        buttonGroup.add( radioButton );

        JLabel l = new JLabel( "    " );
        l.setBackground( new Color( 255, 255, 255, 0 ) );
        add( l, BorderLayout.WEST );  // indent it

        add( radioButton );
    }
}
