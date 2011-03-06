package com.becker.common.math;

/**
 * @author Barry Becker
 */
public enum WaveType {

    SINE_WAVE("Sine wave") {
        @Override
        public double calculateOffset(double amplitude, double theta) {
            return amplitude * (Math.sin(theta));
        }
    },
    SQUARE_WAVE("Square Wave") {
         @Override
         public double calculateOffset(double amplitude, double theta) {
             return (Math.sin(theta) > 0.0) ? amplitude : -amplitude;
         }
    },
    SAWTOOTH_WAVE("Sawtooth Wave") {
         @Override
         public double calculateOffset(double amplitude, double theta) {
             double t = theta/Math.PI/2;
             return 2 * (t - Math.floor(t + 0.5)) * amplitude;
         }
    },
    TRIANGLE_WAVE("Triangle Wave") {
         @Override
         public double calculateOffset(double amplitude, double theta) {
             return Math.abs(SAWTOOTH_WAVE.calculateOffset(amplitude, theta));
         }
    };

    private String name;

    WaveType(String name) {
        this.name = name;
    }

    public abstract double calculateOffset(double amplitude, double theta);

    public String toString() {
        return name;
    }
}
