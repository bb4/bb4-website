/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.sliders;

import com.barrybecker4.common.format.FormatUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws a horizontal slider with a label on top.
 * The value is drawn to right of the label.
 *
 * @author Barry Becker
 */
public class LabeledSlider extends JPanel implements ChangeListener {

    private static final int DEFAULT_SLIDER_RESOLUTION = 2000;
    private static final int MAX_WIDTH = 1000;
    private JLabel label_;
    private String labelText_;
    private JSlider slider_;
    private List<SliderChangeListener> listeners_;

    private double min_, max_;
    private int resolution_ = DEFAULT_SLIDER_RESOLUTION;
    private double ratio_;
    private boolean showAsInteger_ = false;
    private double lastValue_;

    public LabeledSlider(String labelText, double initialValue, double min, double max) {

        assert(initialValue <= max && initialValue >= min);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMaximumSize(new Dimension(MAX_WIDTH, 42));

        min_ = min;
        max_ = max;
        ratio_ = (max_ - min_)/resolution_;
        labelText_ = labelText;

        lastValue_ = initialValue;
        int pos = getPositionFromValue(initialValue);

        slider_ = new JSlider(JSlider.HORIZONTAL, 0, resolution_, pos);
        slider_.setName(labelText);
        slider_.setPaintTicks(true);
        slider_.setPaintTrack(true);
        slider_.addChangeListener(this);
        listeners_ = new ArrayList<SliderChangeListener>();

        label_ = createLabel();
        add(createLabelPanel(label_));
        add(slider_);
        setBorder(BorderFactory.createEtchedBorder());
        setResolution(resolution_);
    }

    public JSlider getSlider() {
        return slider_;
    }

    public void setShowAsInteger(boolean showAsInt) {
        showAsInteger_ = showAsInt;
    }

    public void setResolution(int resolution) {
        double v = this.getValue();
        resolution_ = resolution;
        slider_.setMaximum(resolution_);
        ratio_ = (max_ - min_)/resolution_;
        slider_.setValue(getPositionFromValue(v));

        slider_.setMajorTickSpacing(resolution/10);
        if (resolution_ > 30 && resolution_ < 90) {
            slider_.setMinorTickSpacing(2);
        }
        else if (resolution_ >= 90 && resolution_ < 900) {
            slider_.setMinorTickSpacing(5);
        }
        //slider_.setPaintLabels(true);
    }

    public void addChangeListener(SliderChangeListener l) {
        listeners_.add(l);
    }

    public double getValue() {
        return getValueFromPosition(slider_.getValue());
    }

    public void setValue(double v) {
        slider_.setValue(getPositionFromValue(v));
    }

    @Override
    public void setEnabled(boolean enable) {
        slider_.setEnabled(enable);
    }

    @Override
    public String getName() {
        return labelText_;
    }

    private double getValueFromPosition(int pos) {
        return  (double)pos * ratio_ + min_;
    }

    private int getPositionFromValue(double value) {
        return (int) ((value - min_) / ratio_);
    }

    private JLabel createLabel() {
        JLabel label =  new JLabel();
        label.setText(getLabelText());
        label.setAlignmentY(JLabel.RIGHT_ALIGNMENT);
        return label;
    }

    private JPanel createLabelPanel(JLabel label) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.WEST);
        p.add(new JPanel(), BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(MAX_WIDTH, 22));
        return p;
    }

    private String getLabelText() {
        String val = showAsInteger_? Integer.toString((int) getValue()) : FormatUtil.formatNumber(getValue());
        return labelText_ + ": " +  val;
    }

    /**
     * one of the sliders was moved.
     */
    @Override
    public void stateChanged(ChangeEvent e) {

        double val = getValue();
        if (val != lastValue_) {
            label_.setText(getLabelText());
            for (SliderChangeListener listener : listeners_) {
                listener.sliderChanged(this);
            }
            lastValue_ = val;
        }
    }

    @Override
    public String toString() {
        //noinspection HardCodedStringLiteral
        return "Slider " + labelText_ + " min=" + min_+ " max=" + max_ + "  value=" + getValue() + " ratio=" + ratio_;
    }
}
