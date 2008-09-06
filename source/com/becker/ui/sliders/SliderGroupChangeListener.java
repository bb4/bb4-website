package com.becker.ui.sliders;



/**
 * @author Barry Becker Date: Jul 16, 2006
 */
public interface SliderGroupChangeListener {

    /**
     * @param sliderIndex index of the slider that changed
     * @param sliderName name of slider that was moved.
     * @param value the new value
     */
    void sliderChanged(int sliderIndex, String sliderName, double value);
}
