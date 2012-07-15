/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.sliders;

/**
 * Immutable slider properties.
 * Everything a slider needs to initialize.
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

    /**
     * Constructor
     * @param name
     * @param minValue
     * @param maxValue
     * @param initialValue
     * @param scale  resolution. A bigger number means more increments.
     */
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
