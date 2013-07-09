/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.renderers;

import com.barrybecker4.common.app.AppContext;
import com.barrybecker4.common.format.DefaultNumberFormatter;
import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.format.INumberFormatter;
import com.barrybecker4.common.math.function.InvertibleFunction;
import com.barrybecker4.common.math.function.LinearFunction;

import java.awt.*;

/**
 * This class renders a histogram.
 * The histogram is defined as an array of integers.
 *
 * @author Barry Becker
 */
public class HistogramRenderer {

    /** y values for every point on the x axis. */
    private int[] data_;

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color BAR_COLOR = new Color(160, 120, 255);
    private static final Color BAR_BORDER_COLOR = new Color(0, 0, 0);

    private static final int MARGIN = 24;
    private static final int TICK_LENGTH = 4;

    private int width_;
    private int height_;
    private int maxNumLabels_;
    private double barWidth_;
    private double mean_ = 0;
    private long sum_ = 0;
    private int numBars_;

    private InvertibleFunction xFunction_;
    private INumberFormatter formatter_ = new DefaultNumberFormatter();

    private static final int DEFAULT_LABEL_WIDTH = 30;
    private int maxLabelWidth_ = DEFAULT_LABEL_WIDTH;


    /**
     * Constructor that assumes no scaling ont he x axis.
     * @param data  the array to hold counts for each x axis position.
     */
    public HistogramRenderer(int[] data) {
        this(data, new LinearFunction(1.0));
    }

    /**
     * Constructor
     * @param data  the array to hold counts for each x axis position.
     * @param func  a way to scale the values on the x axis.
     *   This function takes an x value in the domain space and maps it to a bin location.
     */
    public HistogramRenderer(int[] data, InvertibleFunction func)  {
        data_ = data;
        numBars_ = data_.length;
        xFunction_ = func;
        mean_ =  xFunction_.getInverseValue(0);
    }

    public void setSize(int width, int height) {
        width_ = width;
        height_ = height;
        maxNumLabels_ = width_/maxLabelWidth_;
        barWidth_ = (width_ - 2.0 * MARGIN) / numBars_;
    }

    public void increment(double xValue) {
        int xPos = (int)xFunction_.getValue(xValue);
        data_[xPos]++;
        mean_ = (mean_ * sum_  + xValue) / (sum_  + 1);
        sum_++;
    }

    /**
     * Provides customer formatting for the x axis values.
     * @param formatter a way to format the x axis values
     */
    public void setXFormatter(INumberFormatter formatter) {
        formatter_ = formatter;
    }

    /**
     * The larger this is, the fewer equally spaced x labels.
     * @param maxLabelWidth   max width of x labels.
     */
    public void setMaxLabelWidth(int maxLabelWidth) {
        maxLabelWidth_ = maxLabelWidth;
    }

    /** draw the histogram graph */
    public void paint(Graphics g) {

        if (g == null)  return;
        Graphics2D g2 = (Graphics2D) g;

        int maxHeight = getMaxHeight();
        double scale = (height_ -2.0 * MARGIN) / maxHeight;

        clearBackground(g2);

        float xpos = MARGIN;
        int ct = 0;

        for (int value : data_) {
            drawBar(g2, scale, xpos,  ct, value);
            ct++;
            xpos += barWidth_;
        }
        drawDecoration(g2, maxHeight);
    }

    private void drawDecoration(Graphics2D g2, int maxHeight) {
        int width =  (int)(barWidth_ * numBars_);
        // left y axis
        g2.drawLine(MARGIN-1, height_ - MARGIN,
                    MARGIN-1, MARGIN);
        // x axis
        g2.drawLine(MARGIN-1,         height_- MARGIN -1,
                    MARGIN-1 + width, height_ - MARGIN -1);

        g2.drawString(AppContext.getLabel("HEIGHT") + " = " + FormatUtil.formatNumber(maxHeight), MARGIN/3, MARGIN -2);
        g2.drawString(AppContext.getLabel("NUM_TRIALS") + " = " + FormatUtil.formatNumber(sum_), width_ - 300, MARGIN -2);
        g2.drawString(AppContext.getLabel("MEAN") + " = " + FormatUtil.formatNumber(mean_), width_ - 130, MARGIN -2);

        // draw a vertical line for the mean
        int meanXpos = (int)(MARGIN  + (double)width * xFunction_.getValue(mean_) / numBars_ + barWidth_/2);
        g2.drawLine(meanXpos,    height_ - MARGIN,
                    meanXpos,    MARGIN);
        g2.drawString(AppContext.getLabel("MEAN"), meanXpos + 4, MARGIN + 12);

        // draw a vertical line for the median
        double median = calcMedian();
        int medianXpos = (int)(MARGIN  + (double)width * median / numBars_ + barWidth_/2);
        g2.drawLine(medianXpos,    height_ - MARGIN,
                    medianXpos,    MARGIN);
        g2.drawString(AppContext.getLabel("MEDIAN"), medianXpos + 4, MARGIN  + 28);
    }

    private double calcMedian() {
        long halfTotal = sum_ >> 1;
        int medianPos = 0;
        long cumulativeTotal = 0;
        while (cumulativeTotal < halfTotal) {
            cumulativeTotal += data_[medianPos++];
        }
        if (medianPos > 0)
            medianPos -= (cumulativeTotal - halfTotal) / data_[medianPos-1];
        return medianPos - 1;
    }

    /**
     * draw a single bar in the histogram
     */
    private void drawBar(Graphics2D g2, double scale, float xpos, int ct, int value) {
        double h = (scale * value);
        int top = (int)(height_ - h - MARGIN);
        g2.setColor( BAR_COLOR );
        g2.fillRect((int)xpos, top, (int)Math.max(1, barWidth_), (int) h);
        g2.setColor( BAR_BORDER_COLOR );
        if (numBars_ < maxNumLabels_) {
            // if not too many bars add a nice border.
            g2.drawRect((int)xpos, top, (int)barWidth_, (int) h);
        }
        drawLabelIfNeeded(g2, xpos, ct);
    }

    /**
     * draw the label or label and tick if needed for this bar.
     */
    private void drawLabelIfNeeded(Graphics2D g2, float xpos, int ct) {
        double xValue = xFunction_.getInverseValue(ct); // minX_ + ct * incrementX_;
        if (numBars_ < maxNumLabels_) {
            // then draw all labels
            g2.drawString(formatter_.format(xValue), xpos, height_ - 5);
        }  else if (ct % ((maxLabelWidth_ + 10) * numBars_ / width_) == 0) {
            // sparse labeling
            int x = (int)(xpos + barWidth_/2);
            g2.drawLine(x, height_ - MARGIN + TICK_LENGTH, x, height_ - MARGIN);
            g2.drawString(formatter_.format(xValue), xpos - 20, height_ - 5);
        }
    }

    private void clearBackground(Graphics2D g2) {
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect( 0, 0, width_, height_ );
    }

    private int getMaxHeight() {
        int max = 1;
        for (int v : data_) {
            if (v > max)
                max = v;
        }
        return max;
    }
}