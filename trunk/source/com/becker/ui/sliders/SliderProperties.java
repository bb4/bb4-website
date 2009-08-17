/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.ui.sliders;

/**
 * Immutable slider properties.
 * Everythign a slider needs to initialize.
 *
 * @author Barry Becker
 */
public class SliderProperties {

    private String name;
    private double minValue;
    private double maxValue;
    private double initialValue;
    private double scale;


    public SliderProperties(String name, int minValue, int maxValue, int initialValue) {
        this(name, minValue, maxValue, initialValue, 1.0);
    }

    public SliderProperties(String name, double minValue, double maxValue, double initialValue, double scale) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initialValue = initialValue;
        this.scale = scale;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the minValue
     */
    public double getMinValue() {
        return minValue;
    }

    /**
     * @return the maxValue
     */
    public double getMaxValue() {
        return maxValue;
    }

    /**
     * @return the initialValue
     */
    public double getInitialValue() {
        return initialValue;
    }

    /**
     * @return the scale
     */
    public double getScale() {
        return scale;
    }


}