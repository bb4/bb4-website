/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.animation;

import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;

public final class AnimationFrame extends ApplicationFrame
                                  implements AnimationChangeListener {

    /** Shows the current animation status. Like the framerate for example */
    private Label statusLabel;

    /**
     * Constructor
     * @param component the animation component to show and animate.
     */
    public AnimationFrame( AnimationComponent component ) {
        this( component, null );
    }

    public AnimationFrame( AnimationComponent component, String title ) {

        super( title );

        statusLabel = new Label();
        Container contentPane = this.getContentPane();
        contentPane.setLayout( new BorderLayout() );
        contentPane.add( component, BorderLayout.CENTER );
        contentPane.add( statusLabel, BorderLayout.SOUTH );
        component.setChangeListener(this);

        startAnimation(component);
    }

    private void startAnimation(AnimationComponent component) {
        Thread thread = new Thread( component );
        thread.start();
    }


    public void statusChanged( String message )  {
        statusLabel.setText(message);
    }

}