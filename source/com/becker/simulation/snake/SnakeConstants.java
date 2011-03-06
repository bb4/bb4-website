package com.becker.simulation.snake;

/**
 * @author Barry Becker
 */
public final class SnakeConstants {

    private SnakeConstants() {}

    /** scales the size of the snakes geometry */
    public static final double SCALE = 0.9;

    // snake locomotion constants.
    // I used simulated annealing to come up with these optimal parameter values
    // When I originally started the snake's speed was about .21 using my best guess.
    // After optimization the snake's speed is about .33
    public static final double WAVE_SPEED = 0.00478;  // .04  before optimization
    public static final double WAVE_AMPLITUDE = 0.026877; // .04
    public static final double WAVE_PERIOD = 3.6346; // 3.0

    public static final int SINE_WAVE = 0;
    public static final int SQUARE_WAVE = 1;
}
