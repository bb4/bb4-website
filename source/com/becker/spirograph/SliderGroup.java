package com.becker.spirograph;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * A group of horizontal sliders arranged vertically.
 *
 * @author Barry Becker Date: Jul 16, 2006
 */
public class SliderGroup extends JPanel implements ChangeListener {

    private SliderChangeListener sliderListener_;

    private String[] sliderNames_;
    private JLabel[] labels_;
    private JSlider[] sliders_;
    private int numSliders_;

    private static final int DEFAULT_MIN = 0;
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_INITIAL = 50;


    /**
     *
     * @param sliderNames used for both identification and labels
     */
    public SliderGroup(String[] sliderNames) {

        numSliders_ = sliderNames_.length;
        int[] min = new int[numSliders_];
        int[] max = new int[numSliders_];
        int[] initial = new int[numSliders_];
        for (int i=0; i<numSliders_; i++) {
            min[i] = DEFAULT_MIN;
            max[i] = DEFAULT_MAX;
            initial[i] = DEFAULT_INITIAL;
        }
        commonInit(sliderNames, min, max, initial);
    }

    /**
     *
     * @param sliderNames used for both identification and labels
     */
    public SliderGroup(String[] sliderNames, int[] minVals, int[] maxVals, int[] initialVals) {
        commonInit(sliderNames, minVals, maxVals, initialVals);
    }

    private void commonInit(String[] sliderNames, int[] minVals, int[] maxVals, int[] initialVals) {
        sliderNames_ = sliderNames;
        numSliders_ = sliderNames_.length;

        labels_ = new JLabel[numSliders_];
        sliders_ = new JSlider[numSliders_];

        for (int i=0; i<numSliders_; i++) {
            labels_[i] = new JLabel(getSliderTitle(i, initialVals[i]));
            sliders_[i] = new JSlider(JSlider.HORIZONTAL, minVals[i], maxVals[i], initialVals[i]);
            sliders_[i].addChangeListener(this);
        }
        buildUI();
    }


    public int getSliderValue(int sliderIndex) {
        return sliders_[sliderIndex].getValue();
    }

    public void setSliderValue(int sliderIndex, int value) {
        sliders_[sliderIndex].setValue(value);
        labels_[sliderIndex].setText(sliderNames_[sliderIndex] + value);
    }

    public void setSliderMinimum(int sliderIndex, int min) {
        sliders_[sliderIndex].setMinimum(min);
    }

    public void addSliderChangeListener(SliderChangeListener listener) {
        sliderListener_ = listener;
    }

    private String getSliderTitle(int index, int value) {
       return  sliderNames_[index] + " : " + value;
    }

    private void buildUI() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        for (int i=0; i<numSliders_; i++) {
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

    public void setSliderListener(SliderChangeListener listener) {
        sliderListener_ = listener;
    }

    /**
     * one of the sliders has moved.
     * @param e
     */
    public void stateChanged( ChangeEvent e )
    {
        JSlider src = (JSlider) e.getSource();

        for (int i=0; i<numSliders_; i++) {
            JSlider slider = sliders_[i];
            if (src == slider) {
                int value = slider.getValue();
                labels_[i].setText(getSliderTitle(i, value));
                if (sliderListener_ != null)
                    sliderListener_.sliderChanged(i, sliderNames_[i], value);
            }
        }
    }

}
