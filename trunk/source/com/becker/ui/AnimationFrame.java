package com.becker.ui;

import java.awt.*;

public class AnimationFrame extends ApplicationFrame implements ChangeListener
{

    private Label mStatusLabel;

    public AnimationFrame( AnimationComponent ac )
    {
        this( ac, null );
    }

    public AnimationFrame( AnimationComponent ac, String title )
    {
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


    public void statusChanged( String message )
    {
        mStatusLabel.setText( message );
    }

}