package com.becker.ui.animation;

import com.becker.ui.animation.*;

import javax.swing.*;
import java.awt.*;

public class AnimationPanel extends JPanel implements AnimationChangeListener
{

    private Label mStatusLabel;

    public AnimationPanel( AnimationComponent ac )
    {

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

    public void statusChanged( String message )
    {
        mStatusLabel.setText( message );
    }

}