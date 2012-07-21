/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze.ui;

import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A maze generator and solver
 *@author Barry Becker
 */
public class TopControlPanel extends JPanel {

    /** the passage thickness in pixels */
    private static final int PASSAGE_THICKNESS = 40;
    private static final int INITIAL_ANIMATION_SPEED = 20;

    protected NumberInput thicknessField_ = null;

    // ui for entering the direction probabilities.
    protected NumberInput forwardProbField_;
    protected NumberInput leftProbField_;
    protected NumberInput rightProbField_;
    protected NumberInput animationSpeedField_;

    protected GradientButton regenerateButton_;
    protected GradientButton solveButton_;


    /** constructor */
    public TopControlPanel(ActionListener buttonListener) {

        thicknessField_ = new NumberInput("Thickness", PASSAGE_THICKNESS,
                                          "The passage thickness", 2, 200, true);
        animationSpeedField_ = new NumberInput("Speed", INITIAL_ANIMATION_SPEED,
                                               "The animation speed (large number is slow).", 1, 100, true);

        forwardProbField_ = new NumberInput("Forward", 0.34,
                                            "The probability of moving straight forward", 0, 1.0, false);
        leftProbField_ = new NumberInput("Left", 0.33,
                                         "The probability of moving left", 0, 1.0, false);
        rightProbField_ = new NumberInput("Right", 0.33,
                                          "The probability of moving right", 0, 1.0, false);

        add( thicknessField_ );
        add( animationSpeedField_ );
        add( Box.createHorizontalStrut( 15 ) );
        add( forwardProbField_ );
        add( leftProbField_ );
        add( rightProbField_ );

        regenerateButton_ = new GradientButton( "Generate" );
        regenerateButton_.addActionListener( buttonListener );
        add( regenerateButton_ );

        solveButton_ = new GradientButton( "Solve" );
        solveButton_.addActionListener( buttonListener );
        add( solveButton_ );
    }


    public boolean isSolveButton(Object source) {
        return solveButton_ == source;
    }

    public boolean isRegenerateButton(Object source) {
        return regenerateButton_ == source;
    }

    public int getThickness() {
        return  thicknessField_.getIntValue();
    }

    public double getForwardPropability() {
        return forwardProbField_.getValue();
    }

    public double getLeftProbability() {
        return leftProbField_.getValue();
    }

    public double getRightProbability() {
        return rightProbField_.getValue();
    }

    public int getAnimationSpeed() {
        return animationSpeedField_.getIntValue();
    }
}