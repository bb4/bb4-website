package com.becker.simulation.snake;

import com.becker.simulation.common.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Bary Becker
 */
class SnakeOptionsDialog extends SimulatorOptionsDialog implements ActionListener
{

    // snake param options controls
    private JTextField waveSpeedField_;
    private JTextField waveAmplitudeField_;
    private JTextField wavePeriodField_;
    private JTextField massScaleField_;
    private JTextField springKField_;
    private JTextField springDampingField_;



    // constructor
    SnakeOptionsDialog( Frame parent, SnakeSimulator simulator ) {
        super( parent, simulator );
    }



    protected JPanel createCustomParamPanel() {

        JPanel customParamPanel = new JPanel();
        customParamPanel.setLayout( new BorderLayout() );

        JPanel snakeParamPanel = new JPanel();
        snakeParamPanel.setLayout( new BoxLayout(snakeParamPanel, BoxLayout.Y_AXIS ) );
        snakeParamPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Snake Parameters" ) );


        SnakeSimulator simulator = (SnakeSimulator) getSimulator();

        waveSpeedField_ = new JTextField( Double.toString( simulator.getSnake().getWaveSpeed() ) );
        waveSpeedField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p1 =
                new NumberInputPanel( "Wave Speed (.001 slow - .9 fast):  ", waveSpeedField_ );
        p1.setToolTipText( "This controls the speed at which the force function that travels down the body of the snake" );
        snakeParamPanel.add( p1 );

        waveAmplitudeField_ = new JTextField( Double.toString( simulator.getSnake().getWaveAmplitude() ) );
        waveAmplitudeField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p2 =
                new NumberInputPanel( "Wave Amplitude (.001 small - 2.0 large):  ", waveAmplitudeField_ );
        p2.setToolTipText( "This controls the amplitude of the force function that travels down the body of the snake" );
        snakeParamPanel.add( p2 );

        wavePeriodField_ = new JTextField( Double.toString( simulator.getSnake().getWavePeriod() ) );
        wavePeriodField_.setMaximumSize( TEXT_FIELD_DIM );
        JPanel p3 =
                new NumberInputPanel( "Wave Period (1.0 small - 4.0 large):  ", wavePeriodField_ );
        p3.setToolTipText( "This controls the period (in number of PI radians) of the force function that travels down the body of the snake" );
        snakeParamPanel.add( p3 );

        massScaleField_ = new JTextField( Double.toString( simulator.getSnake().getMassScale() ) );
        massScaleField_.setMaximumSize( TEXT_FIELD_DIM );
        massScaleField_.setEnabled( false );
        JPanel p4 =
                new NumberInputPanel( "Mass Scale (.3 small - 2.0 large):  ", massScaleField_ );
        p4.setToolTipText( "This controls the overall mass of the snake. A high number indicates that the snake weighs a lot." );
        snakeParamPanel.add( p4 );

        springKField_ = new JTextField( Double.toString( simulator.getSnake().getSpringK() ) );
        springKField_.setMaximumSize( TEXT_FIELD_DIM );
        springKField_.setEnabled( false );
        JPanel p5 =
                new NumberInputPanel( "Spring Stiffness  (1.0 small - 2.0 large):  ", springKField_ );
        p5.setToolTipText( "This controls the stiffness of the springs used to make up the snake's body." );
        snakeParamPanel.add( p5 );

        springDampingField_ = new JTextField( Double.toString( simulator.getSnake().getSpringDamping() ) );
        springDampingField_.setMaximumSize( TEXT_FIELD_DIM );
        springDampingField_.setEnabled( false );
        JPanel p6 =
                new NumberInputPanel( "Spring Damping (.1 small - 3.0 large):  ", springDampingField_ );
        p6.setToolTipText( "This controls how quickly the spring returns to rest once released." );
        snakeParamPanel.add( p6 );

        customParamPanel.add(snakeParamPanel, BorderLayout.NORTH);
        customParamPanel.add(Box.createGlue(), BorderLayout.CENTER);

        return customParamPanel;
    }

    protected void ok() {

        super.ok();

        // set the snake params
        SnakeSimulator simulator = (SnakeSimulator) getSimulator();

        Double waveSpeed = new Double( waveSpeedField_.getText() );
        simulator.getSnake().setWaveSpeed( waveSpeed );

        Double waveAmplitude = new Double( waveAmplitudeField_.getText() );
        simulator.getSnake().setWaveAmplitude( waveAmplitude );

        Double wavePeriod = new Double( wavePeriodField_.getText() );
        simulator.getSnake().setWavePeriod( wavePeriod );

        Double massScale = new Double( massScaleField_.getText() );
        simulator.getSnake().setMassScale( massScale );

        Double springK = new Double( springKField_.getText() );
        simulator.getSnake().setSpringK( springK );

        Double springDamping = new Double( springDampingField_.getText() );
        simulator.getSnake().setSpringDamping( springDamping );
    }

}