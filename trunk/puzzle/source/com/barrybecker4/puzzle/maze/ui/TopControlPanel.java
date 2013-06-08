/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze.ui;

import com.barrybecker4.puzzle.maze.MazeController;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.Box;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A maze generator and solver
 *@author Barry Becker
 */
public class TopControlPanel extends JPanel
                             implements ActionListener {

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

    private MazeController controller;


    /** constructor */
    public TopControlPanel(MazeController controller) {

        this.controller = controller;
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
        regenerateButton_.addActionListener( this );
        add( regenerateButton_ );

        solveButton_ = new GradientButton( "Solve" );
        solveButton_.addActionListener( this );
        add( solveButton_ );
    }


    /**
     * called when a button is pressed.
     */
    @Override
    public void actionPerformed( ActionEvent e )  {

        Object source = e.getSource();

        if (source == regenerateButton_) {
            regenerate();
        }
        if (source == solveButton_) {
            controller.solve(getAnimationSpeed());
        }
        this.repaint();
    }

    public void regenerate() {
        controller.regenerate(getThickness(), getAnimationSpeed(),
                    getForwardPropability(), getLeftProbability(), getRightProbability());
    }


    private int getThickness() {
        return  thicknessField_.getIntValue();
    }

    private double getForwardPropability() {
        return forwardProbField_.getValue();
    }

    private double getLeftProbability() {
        return leftProbField_.getValue();
    }

    private double getRightProbability() {
        return rightProbField_.getValue();
    }

    private int getAnimationSpeed() {
        return animationSpeedField_.getIntValue();
    }

}