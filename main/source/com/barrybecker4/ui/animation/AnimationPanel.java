/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.animation;


import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for showing an animation
 * @author Barry Becker
 */
public final class AnimationPanel extends JPanel
                                  implements AnimationChangeListener {

    private Label statusLabel;

    /**
     * Constructor
     * @param component animation component to animate.
     */
    public AnimationPanel( AnimationComponent component ) {

        setLayout( new BorderLayout() );
        setFont( new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12 ) );

        this.add( component, BorderLayout.CENTER );
        this.add( statusLabel = new Label(), BorderLayout.SOUTH );
        component.setChangeListener(this);

        startAnimation(component);
    }

    private void startAnimation(AnimationComponent ac) {
        Thread thread = new Thread( ac );
        thread.start();
    }

    public void statusChanged( String message ) {
        if (message != null)
            statusLabel.setText( message );
    }

}