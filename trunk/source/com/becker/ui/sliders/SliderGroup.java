package com.becker.ui.sliders;

import com.becker.common.util.Util;
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

    private SliderGroupChangeListener sliderListener_;

    private String[] sliderNames_;
    private JLabel[] labels_;
    private JSlider[] sliders_;
    private int numSliders_;
    // remember the intial values, so we can restore them.
    private double[] initialValues_;
    private double[] scaleFactors_;
    
    private static final int DEFAULT_MIN = 0;
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_INITIAL = 50;


    /**
     *
     * @param sliderNames used for both identification and labels
     */
    public SliderGroup(String[] sliderNames) {

        numSliders_ = sliderNames.length;
        int[] min = new int[numSliders_];
        int[] max = new int[numSliders_];
        int[] initial = new int[numSliders_];
        for (int i=0; i<numSliders_; i++) {
            min[i] = DEFAULT_MIN;
            max[i] = DEFAULT_MAX;
            initial[i] = DEFAULT_INITIAL;
        }
        intInit(sliderNames, min, max, initial);
    }

    /**
     * Use this constructor if all the sliders are integer valued
     * @param sliderNames used for both identification and labels
     */
    public SliderGroup(String[] sliderNames, int[] minVals, int[] maxVals, int[] initialVals) {
        intInit(sliderNames, minVals, maxVals, initialVals);
    }
    
    /**
     * Use this constructor if all real valued sliders or a misture of real and integer sliders
     * @param sliderNames used for both identification and labels
     */
    public SliderGroup(String[] sliderNames, double[] minVals, double[] maxVals, double[] initialVals, double[] scaleFactors) {
        commonInit(sliderNames, minVals, maxVals, initialVals, scaleFactors);
    }

    
    private void intInit(String[] sliderNames, int[] minVals, int[] maxVals, int[] initialVals) {
        
        int len = minVals.length;
        double[] minVs =  new double[len];
        double[] maxVs = new double[len];
        double[] initialVs = new double[len];
        double[] scaleFactors = new double[len];
        for (int i=0; i<len; i++) {
            minVs[i] = (double) minVals[i];
            maxVs[i] = (double) maxVals[i];
            initialVs[i] = (double) initialVals[i];
            scaleFactors[i] = 1.0;  // all integer
        }
        commonInit(sliderNames, minVs, maxVs, initialVs, scaleFactors);
    }
    
    private void commonInit(String[] sliderNames, double[] minVals, double[] maxVals, double[] initialVals, double[] scaleFactors) {
        sliderNames_ = sliderNames;
        numSliders_ = sliderNames_.length;
        initialValues_ = initialVals;
        scaleFactors_ = scaleFactors;

        labels_ = new JLabel[numSliders_];
        sliders_ = new JSlider[numSliders_];

        for (int i=0; i<numSliders_; i++) {
            int intInitial = (int) (initialVals[i] * scaleFactors_[i]);            
            int intMin = (int) (minVals[i] * scaleFactors_[i]);
            int intMax = (int) (maxVals[i] * scaleFactors_[i]);       
            labels_[i] = new JLabel(getSliderTitle(i, intInitial));
            System.out.println("intMin="+ intMin +" max="+intMax);
            sliders_[i] = new JSlider(JSlider.HORIZONTAL, intMin, intMax, intInitial);
            sliders_[i].addChangeListener(this);
        }
        buildUI();
    }

    
    /** 
     * return all the sliders to their initial value.
     */
    public void reset() {
         for (int i=0; i<numSliders_; i++) {  
             int initial = (int) (initialValues_[i] * scaleFactors_[i]);
            sliders_[i].setValue(initial);
        }
    } 
    
    public int getSliderValueAsInt(int sliderIndex) {
        return (int) getSliderValue(sliderIndex);
    }

    public double getSliderValue(int sliderIndex) {
        return scaleFactors_[sliderIndex] * (double)sliders_[sliderIndex].getValue();
    }
  
    public void setSliderValue(int sliderIndex, double value) {
        double v = (value * scaleFactors_[sliderIndex]);
        sliders_[sliderIndex].setValue((int) v);
        labels_[sliderIndex].setText(sliderNames_[sliderIndex] + v);
    }
    
    public void setSliderValue(int sliderIndex, int value) {
        assert(scaleFactors_[sliderIndex] == 1.0) : "you should call setSliderValue(int, double) if you have a slider with real values";
        sliders_[sliderIndex].setValue(value);
        labels_[sliderIndex].setText(sliderNames_[sliderIndex] + value);
    }

    public void setSliderMinimum(int sliderIndex, int min) {
         assert(scaleFactors_[sliderIndex] == 1.0) : "you should call setSliderMinimum(int, double) if you have a slider with real values";
        sliders_[sliderIndex].setMinimum(min);
    }
    
    public void setSliderMinimum(int sliderIndex, double min) {
        double mn = (min * scaleFactors_[sliderIndex]);
        sliders_[sliderIndex].setMinimum((int) mn);
    }

    public void addSliderChangeListener(SliderGroupChangeListener listener) {
        sliderListener_ = listener;
    }

    private String getSliderTitle(int index, int value) {
        String title = sliderNames_[index] + " : " ;
        if (scaleFactors_[index] == 1.0) {
            return  title+ Util.formatNumber(value);
        } else {
            return  title + Util.formatNumber((double)value/scaleFactors_[index]);
        }
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

    public void setSliderListener(SliderGroupChangeListener listener) {
        sliderListener_ = listener;
    }

    /**
     *@param name of the slider to enable or disable.
     */
    public void setEnabled(String name, boolean enable)
    {
        JSlider slider = null;
        for (int i=0; i<numSliders_; i++) {
            if (name.equals(sliderNames_[i])) {
                slider = sliders_[i];
            }
        }
        assert slider!=null: "no slider by the name of " + name;
            
        slider.setEnabled(enable);
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
                if (sliderListener_ != null) {
                    double v = (double)value / scaleFactors_[i];
                    sliderListener_.sliderChanged(i, sliderNames_[i], v);
                }
            }
        }
    }

}
