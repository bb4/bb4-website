/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.ui.animation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AnimationDebugComponent
        extends AnimationComponent
        implements ActionListener  {
    private boolean runNextStep_ = false;
    protected Button stepButton_ = new Button( "advance to next frame" );

    public AnimationDebugComponent() {
        stepButton_.addActionListener( this );
    }

    @Override
    public void run() {

        while ( animating_ ) {
            if ( runNextStep_ ) {
                render();
                timeStep();
                calculateFrameRate();
                runNextStep_ = false;
                repaint();
            }
        }
    }

    public Button getStepButton() {
        return stepButton_;
    }

    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() == stepButton_ )
            runNextStep_ = true;
    }

}