package com.becker.simulation.snake;

import com.becker.common.math.WaveType;

import static com.becker.simulation.snake.SnakeConstants.*;

/**
 * Tweakable snake parameters that define locomotion.
 *
 * @author Barry Becker
 */
public class LocomotionParameters {

    private static final boolean USE_FRICTION = true;

    private static final double MASS_SCALE = 1.5;
    private static final double SPRING_K = 0.6;
    private static final double SPRING_DAMPING = 1.2;
    private static final double STATIC_FRICTION = 0.01;
    private static final double DYNAMIC_FRICTION = 0.01;

    private WaveType waveType_ = WaveType.SINE_WAVE;

    private boolean useFriction_ = USE_FRICTION;

    /** the speed at which the muscular contraction wave travels down the body of the snake  */
    private double waveSpeed_ = WAVE_SPEED;

    /** scale factor for the force function (must be greater than 0 and less than 1.0) */
    private double waveAmplitude_ = WAVE_AMPLITUDE;

    //** The period of the sinusoidal force function   */
    private double wavePeriod_ = WAVE_PERIOD;

    /** scales the overall mass of the snake up or down */
    private double massScale_ = MASS_SCALE;

    /** the stiffness of the springs that make up the snakes body   */
    private double springK_ = SPRING_K;

    /**
     * the amount of spring damping present in the springs
     * this corresponds to how quickly the amplitude of the spring goes to 0
     */
    private double springDamping_ = SPRING_DAMPING;

    private double staticFriction_ = STATIC_FRICTION;
    private double dynamicFriction_ = DYNAMIC_FRICTION;

    /**
     * Constructor
     */
    public LocomotionParameters() {
    }

    public void setUseFriction(boolean use) {
        useFriction_ = use;
    }
    public boolean getUseFriction() {
        return useFriction_;
    }

    public void setWaveSpeed(double speed) {
        waveSpeed_ = speed;
    }

    public double getWaveSpeed() {
        return waveSpeed_;
    }

    public void setWaveAmplitude( double waveAmplitude ) {
        waveAmplitude_ = waveAmplitude;
    }

    public double getWaveAmplitude() {
        return waveAmplitude_;
    }

    public void setWavePeriod( double wavePeriod ) {
        wavePeriod_ = wavePeriod;
    }

    public double getWavePeriod() {
        return wavePeriod_;
    }

    public void setMassScale( double massScale ) {
        massScale_ = massScale;
    }

    public double getMassScale() {
        return massScale_;
    }

    public void setSpringK( double springK ) {
        springK_ = springK;
    }

    public double getSpringK() {
        return springK_;
    }

    public void setSpringDamping( double springDamping ) {
        springDamping_ = springDamping;
    }

    public double getSpringDamping()  {
        return springDamping_;
    }

    public void setWaveType( WaveType waveType ) {
        waveType_ = waveType;
    }

    public WaveType getWaveType() {
        return waveType_;
    }

    public void setStaticFriction( double staticFriction ) {
        staticFriction_ = staticFriction;
    }

    public double getStaticFriction() {
        return staticFriction_;
    }

    public void setDynamicFriction( double dynamicFriction ) {
        dynamicFriction_ = dynamicFriction;
    }

    public double getDynamicFriction() {
        return dynamicFriction_;
    }

}
