/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.sliders;

import com.barrybecker4.common.format.FormatUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * A group of horizontal sliders arranged vertically.
 *
 * @author Barry Becker
 */
public class SliderGroup extends JPanel implements ChangeListener {

    private SliderGroupChangeListener sliderListener_;

    private SliderProperties[] sliderProps_;
    private JLabel[] labels_;
    private JSlider[] sliders_;

    private static final int DEFAULT_MIN = 0;
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_INITIAL = 50;

    /** Protected constructor so derived class can do its own initialization. */
    protected SliderGroup() {}

    /**
     * @param sliderNames used for both identification and labels
     */
    public SliderGroup(String[] sliderNames) {

        int numSliders = sliderNames.length;
        SliderProperties[] sliderProps = new SliderProperties[numSliders];

        for (int i=0; i<numSliders; i++) {
            sliderProps[i] = new SliderProperties(sliderNames[i], DEFAULT_MIN, DEFAULT_MAX, DEFAULT_INITIAL);
        }
        commonInit(sliderProps);
    }

    public SliderGroup(SliderProperties[] sliderProps) {
        commonInit(sliderProps);
    }

    protected SliderProperties[] getSliderProperties() {
        return sliderProps_;
    }

    /**
     * Initialize sliders with floating point values.
     */
    protected void commonInit(SliderProperties[] sliderProps) {

        sliderProps_ = sliderProps;
        int numSliders = sliderProps_.length;

        labels_ = new JLabel[numSliders];
        sliders_ = new JSlider[numSliders];

        for (int i=0; i < numSliders; i++) {
            double scale = sliderProps_[i].getScale();
            int intInitial = (int) (sliderProps_[i].getInitialValue() * scale);
            int intMin = (int) (sliderProps_[i].getMinValue() * scale);
            int intMax = (int) (sliderProps_[i].getMaxValue() * scale);
            labels_[i] = new JLabel(getSliderTitle(i, intInitial));
            sliders_[i] = new JSlider(JSlider.HORIZONTAL, intMin, intMax, intInitial);
            sliders_[i].addChangeListener(this);

        }
        buildUI();
    }

    /**
     * return all the sliders to their initial value.
     */
    public void reset() {
         for (int i=0; i<sliderProps_.length; i++) {
             int initial = (int) (sliderProps_[i].getInitialValue() * sliderProps_[i].getScale());
            sliders_[i].setValue(initial);
        }
    }

    public int getSliderValueAsInt(int sliderIndex) {
        return (int) getSliderValue(sliderIndex);
    }

    public double getSliderValue(int sliderIndex) {
        return sliderProps_[sliderIndex].getScale() * (double)sliders_[sliderIndex].getValue();
    }

    public void setSliderValue(int sliderIndex, double value) {
        double v = (value * sliderProps_[sliderIndex].getScale());
        sliders_[sliderIndex].setValue((int) v);
        labels_[sliderIndex].setText(sliderProps_[sliderIndex].getName() + " " + FormatUtil.formatNumber(value));
    }

    public void setSliderValue(int sliderIndex, int value) {
        assert(sliderProps_[sliderIndex].getScale() == 1.0) : "you should call setSliderValue(int, double) if you have a slider with real values";
        sliders_[sliderIndex].setValue(value);
        labels_[sliderIndex].setText(getSliderTitle(sliderIndex, value));
    }

    public void setSliderMinimum(int sliderIndex, int min) {
         assert(sliderProps_[sliderIndex].getScale() == 1.0) : "you should call setSliderMinimum(int, double) if you have a slider with real values";
        sliders_[sliderIndex].setMinimum(min);
    }

    public void setSliderMinimum(int sliderIndex, double min) {
        double mn = (min * sliderProps_[sliderIndex].getScale());
        sliders_[sliderIndex].setMinimum((int) mn);
    }

    public void addSliderChangeListener(SliderGroupChangeListener listener) {
        sliderListener_ = listener;
    }

    private String getSliderTitle(int index, int value) {
        String title = sliderProps_[index].getName() + " : " ;
        if (sliderProps_[index].getScale() == 1.0) {
            return  title + FormatUtil.formatNumber(value);
        } else {
            return  title + FormatUtil.formatNumber((double) value / sliderProps_[index].getScale());
        }
    }

    private void buildUI() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        for (int i=0; i < sliderProps_.length; i++) {
           add( createLabelPanel(labels_[i]) );
           add( sliders_[i] );
        }
    }

    private JPanel createLabelPanel(JLabel label) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.WEST);
        p.add(new JPanel(), BorderLayout.CENTER);
        return p;
    }

    public void setSliderListener(SliderGroupChangeListener listener) {
        sliderListener_ = listener;
    }

    /**
     *@param name of the slider to enable or disable.
     */
    public void setEnabled(String name, boolean enable)
    {
        JSlider slider = null;
        for (int i=0; i < sliderProps_.length; i++) {
            if (name.equals(sliderProps_[i].getName())) {
                slider = sliders_[i];
            }
        }
        assert slider!=null: "no slider by the name of " + name;

        slider.setEnabled(enable);
    }

    /**
     * one of the sliders has moved.
     * @param e  change event.
     */
    public void stateChanged( ChangeEvent e )
    {
        JSlider src = (JSlider) e.getSource();

        for (int i=0; i < sliderProps_.length; i++) {
            JSlider slider = sliders_[i];
            if (src == slider) {
                int value = slider.getValue();
                labels_[i].setText(getSliderTitle(i, value));
                if (sliderListener_ != null) {
                    double v = (double)value / sliderProps_[i].getScale();
                    sliderListener_.sliderChanged(i, sliderProps_[i].getName(), v);
                }
            }
        }
    }

}
