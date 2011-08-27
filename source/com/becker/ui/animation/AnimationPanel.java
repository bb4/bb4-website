package com.becker.ui.animation;


import javax.swing.*;
import java.awt.*;

public final class AnimationPanel extends JPanel implements AnimationChangeListener {

    private Label mStatusLabel;

    /**
     * Constructor
     * @param ac animation component to animate.
     */
    public AnimationPanel( AnimationComponent ac ) {

        setLayout( new BorderLayout() );
        setFont( new Font( "Serif", Font.PLAIN, 12 ) );

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