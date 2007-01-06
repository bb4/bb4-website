package com.becker.ui;

import com.becker.common.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

/**
 * Draws a horizontal slider with a label on top.
 * The value is draw to the right of the label.
 *
 * @author Barry Becker Date: Nov 5, 2006
 */
public class LabeledSlider extends JPanel implements ChangeListener {

    private static final int DEFAULT_SLIDER_RESOLUTION = 2000;
    private JLabel label_;
    private String labelText_;
    private JSlider slider_;

    private double min_, max_;
    private int resolution_ = DEFAULT_SLIDER_RESOLUTION;
    private double ratio_;
    private boolean showAsInteger_ = false;

    public LabeledSlider(String labelText, double initialValue, double min, double max) {

        assert(initialValue <= max && initialValue >= min);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMaximumSize(new Dimension(1000, 42));

        min_ = min;
        max_ = max;
        ratio_ = (max_ - min_)/resolution_;

        label_ = new JLabel();
        labelText_ = labelText;
        label_.setText(labelText + Util.formatNumber(initialValue));
        label_.setAlignmentY(JLabel.RIGHT_ALIGNMENT);
        int pos = getPositionFromValue(initialValue);

        slider_ = new JSlider(JSlider.HORIZONTAL, 0, resolution_, pos);
        slider_.setPaintTicks(true);
        slider_.setMajorTickSpacing(10);
        slider_.setMinorTickSpacing(2);
        //slider_.setPaintLabels(true);
        slider_.setPaintTrack(true);
        slider_.addChangeListener(this);

        add(createLabelPanel(label_));
        add(slider_);
        setBorder(BorderFactory.createEtchedBorder());
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
    }

    public void addChangeListener(ChangeListener l) {
        slider_.addChangeListener(l);
    }

    public double getValue() {
        return getValueFromPosition(slider_.getValue());
    }

    public void setValue(double v) {
        slider_.setValue(getPositionFromValue(v));
    }

    public void setEnabled(boolean enable) {
        slider_.setEnabled(enable);
    }

    private double getValueFromPosition(int pos) {
        return  (double)pos * ratio_ + min_;
    }

    private int getPositionFromValue(double value) {
        return (int) ((value - min_) / ratio_);
    }

    private JPanel createLabelPanel(JLabel label) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.WEST);
        p.add(new JPanel(), BorderLayout.CENTER);
        return p;
    }

    /**
     * one of the sliders was moved.
     */
    public void stateChanged(ChangeEvent e) {

        String val = showAsInteger_? Integer.toString((int) getValue()) : Util.formatNumber(getValue());
        label_.setText(labelText_ + val);
    }
}
