/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.animation;

import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;

public final class AnimationFrame extends ApplicationFrame
                                  implements AnimationChangeListener {

    private Label mStatusLabel;

    /**
     * Constructor
     * @param ac the animation component to show and animate.
     */
    public AnimationFrame( AnimationComponent ac ) {
        this( ac, null );
    }

    public AnimationFrame( AnimationComponent ac, String title ) {
        super( title );

        Container contentPane = this.getContentPane();
        contentPane.setLayout( new BorderLayout() );
        contentPane.add( ac, BorderLayout.CENTER );
        contentPane.add( mStatusLabel = new Label(), BorderLayout.SOUTH );

        // Listen for the status changes.
        ac.setChangeListener( this );

        // Kick off the animation.
        Thread t = new Thread( ac );
        t.start();
    }


    public void statusChanged( String message )  {
        mStatusLabel.setText( message );
    }

}