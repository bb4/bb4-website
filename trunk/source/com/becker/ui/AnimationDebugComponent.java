package com.becker.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AnimationDebugComponent
        extends AnimationComponent
        implements ActionListener
{
    private boolean runNextStep = false;
    protected Button stepButton = new Button( "advance to next frame" );

    public AnimationDebugComponent()
    {
        stepButton.addActionListener( this );
    }

    public void run()
    {
        while ( mTrucking ) {
            if ( runNextStep ) {
                render();
                timeStep();
                calculateFrameRate();
                runNextStep = false;
                repaint();
            }
        }
    }

    public Button getStepButton()
    {
        return stepButton;
    }

    public void actionPerformed( ActionEvent event )
    {
        if ( event.getSource() == stepButton )
            runNextStep = true;
    }

}