package com.becker.simulation.henonphase.algorithm;

import org.igoweb.igoweb.client.gtp.X;

import java.awt.*;

/**
 * Henon traveler travels through time
 * @author Barry Becker
 */
public class Traveler {

    public Color color;
    private double angle;

    // current position
    public double x;
    public double y;


    public Traveler(double origX, double origY, Color color, double angle) {
        this.color = color;
        this.angle = angle;
        x = origX;
        y = origY;
    }

    /**
     * increment forward one iteration
     */
    public void increment() {

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double term = y - x * x;       // offset and multiplier

        double temp = x * cos - term * sin;
        y = x * sin + term * cos;
        x = temp;
    }

}
