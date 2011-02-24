package com.becker.simulation.snake;

import com.becker.simulation.common.NewtonianSimOptionsDialog;
import com.becker.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Bary Becker
 */
class SnakeOptionsDialog extends NewtonianSimOptionsDialog implements ActionListener
{

    // snake param options controls
    private NumberInput waveSpeedField_;
    private NumberInput waveAmplitudeField_;
    private NumberInput wavePeriodField_;
    private NumberInput massScaleField_;
    private NumberInput springKField_;
    private NumberInput springDampingField_;



    // constructor
    SnakeOptionsDialog( JFrame parent, SnakeSimulator simulator ) {
        super( parent, simulator );
    }


    protected JPanel createCustomParamPanel() {

        JPanel customParamPanel = new JPanel();
        customParamPanel.setLayout( new BorderLayout() );

        JPanel snakeParamPanel = new JPanel();
        snakeParamPanel.setLayout( new BoxLayout(snakeParamPanel, BoxLayout.Y_AXIS ) );
        snakeParamPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Snake Parameters" ) );
        

        Snake snake = ((SnakeSimulator) getSimulator()).getSnake();

        waveSpeedField_ =
                new NumberInput("Wave Speed (.001 slow - .9 fast):  ", snake.getWaveSpeed(),
                     "This controls the speed at which the force function that travels down the body of the snake",
                     0.001, 0.9, false);
        waveAmplitudeField_ =
                new NumberInput("Wave Amplitude (.001 small - 2.0 large):  ", snake.getWaveAmplitude(),
                    "This controls the amplitude of the force function that travels down the body of the snake",
                    0.001, 0.9, false);
        wavePeriodField_ =
                new NumberInput("Wave Period (1.0 small - 4.0 large):  ", snake.getWavePeriod(),
                    "This controls the period (in number of PI radians) of the force function "
                    + "that travels down the body of the snake", 1.0, 4.0, false);
        massScaleField_ =
                new NumberInput("Mass Scale (.3 small - 2.0 large):  ", snake.getMassScale(),
                    "This controls the overall mass of the snake. A high number indicates that the snake weighs a lot.",
                    0.3, 2.0, false);
        massScaleField_.setEnabled( false );

        springKField_ =
                new NumberInput("Spring Stiffness  (1.0 small - 2.0 large):  ", snake.getSpringK(),
                    "This controls the stiffness of the springs used to make up the snake's body.",
                    1.0, 2.0, false);
        springKField_.setEnabled( false );

        springDampingField_ =
                new NumberInput("Spring Damping (.1 small - 3.0 large):  ", snake.getSpringDamping(),
                    "This controls how quickly the spring returns to rest once released.",
                    0.1, 3.0, false);
        springDampingField_.setEnabled( false );

        snakeParamPanel.add( waveSpeedField_ );
        snakeParamPanel.add( waveAmplitudeField_ );
        snakeParamPanel.add( wavePeriodField_ );
        snakeParamPanel.add( massScaleField_ );
        snakeParamPanel.add( springKField_ );
        snakeParamPanel.add( springDampingField_ );

        customParamPanel.add(snakeParamPanel, BorderLayout.NORTH);
        customParamPanel.add(Box.createGlue(), BorderLayout.CENTER);

        return customParamPanel;
    }

    protected void ok() {

        super.ok();

        // set the snake params
        SnakeSimulator simulator = (SnakeSimulator) getSimulator();

        simulator.getSnake().setWaveSpeed(waveSpeedField_.getValue());
        simulator.getSnake().setWaveAmplitude( waveAmplitudeField_.getValue() );
        simulator.getSnake().setWavePeriod( wavePeriodField_.getValue() );
        simulator.getSnake().setMassScale( massScaleField_.getValue()  );
        simulator.getSnake().setSpringK( springKField_.getValue() );
        simulator.getSnake().setSpringDamping( springDampingField_.getValue() );
    }

}