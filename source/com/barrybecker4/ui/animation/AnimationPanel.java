/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.animation;


import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

public final class AnimationPanel extends JPanel
                                  implements AnimationChangeListener {

    private Label mStatusLabel;

    /**
     * Constructor
     * @param ac animation component to animate.
     */
    public AnimationPanel( AnimationComponent ac ) {

        setLayout( new BorderLayout() );
        setFont( new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12 ) );

        this.add( ac, BorderLayout.CENTER );
        this.add( mStatusLabel = new Label(), BorderLayout.SOUTH );

        // Listen for the status changes.
        ac.setChangeListener( this );

        // Kick off the animation.
        Thread t = new Thread( ac );
        t.start();
    }

    public void statusChanged( String message ) {
        if (message != null)
            mStatusLabel.setText( message );
    }

}