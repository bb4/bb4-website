/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.sliders;



/**
 * @author Barry Becker
 */
public interface SliderGroupChangeListener {

    /**
     * @param sliderIndex index of the slider that changed
     * @param sliderName name of slider that was moved.
     * @param value the new value
     */
    void sliderChanged(int sliderIndex, String sliderName, double value);
}
