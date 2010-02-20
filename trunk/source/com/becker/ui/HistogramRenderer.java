package com.becker.ui;

import com.becker.common.format.DefaultNumberFormatter;
import com.becker.common.format.INumberFormatter;
import com.becker.common.util.Util;

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

    private double minX_ = 0.0;
    private double incrementX_ = 1.0;

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color BAR_COLOR = new Color(120, 20, 255);
    private static final Color BAR_BORDER_COLOR = new Color(0, 0, 20);

    private static final int MARGIN = 24;
    private static final int TICK_LENGTH = 4;

    private int width_;
    private int height_;
    private int maxNumLabels_;
    private double barWidth_;
    int numBars_;
    INumberFormatter formatter_ = new DefaultNumberFormatter();

    private static final int DEFAULT_LABEL_WIDTH = 30;
    private int maxLabelWidth_ = DEFAULT_LABEL_WIDTH;

    public HistogramRenderer(int[] data, int minX)
    {
        this(data, minX, 1.0);
    }
    
    public HistogramRenderer(int[] data, double minX, double xIncrement)
    {
        data_ = data;
        minX_ = minX;
        incrementX_ = xIncrement;
        numBars_ = data_.length;
    }

    public void setSize(int width, int height) {
        width_ = width;
        height_ = height;
        maxNumLabels_ = width_/maxLabelWidth_;
        barWidth_ = (width_ - 2.0 * MARGIN) / numBars_;
    }

    public void setFormatter(INumberFormatter formatter) {
        formatter_ = formatter;
    }

    public void setMaxLabelWidth(int maxLabelWidth) {
        maxLabelWidth_ = maxLabelWidth;
    }

    /** draw the histogram graph */
    public void paint(Graphics g) {

        if (g == null)  return;
        Graphics2D g2 = (Graphics2D) g;

        int maxHeight = getMaxHeight();
        int sum = getSum();
        double scale = (height_ -2.0 * MARGIN) / maxHeight;

        clearBackground(g2);

        float xpos = MARGIN;
        int ct = 0;

        for (int value : data_) {
            drawBar(g2, scale, xpos,  ct, value);
            ct++;
            xpos += barWidth_;
        }
        drawAxes(g2, maxHeight, sum);
    }

    private void drawAxes(Graphics2D g2, int maxHeight, int sum) {
        // left y axis
        g2.drawLine(MARGIN-1, height_ - MARGIN, MARGIN-1, MARGIN);
        // x axis
        g2.drawLine(MARGIN-1, height_- MARGIN -1, MARGIN-1 + (int)(barWidth_ * numBars_), height_ - MARGIN -1);

        g2.drawString("Height = " + Util.formatNumber(maxHeight), MARGIN, MARGIN);
        g2.drawString("Number = " + Util.formatNumber(sum), width_ - 200, MARGIN >> 1);
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
        double xValue= minX_ + ct * incrementX_;
        if (numBars_ < maxNumLabels_) {
            // then draw all labels
            g2.drawString(formatter_.format(xValue), xpos + 1, height_ - 5);
        }  else if (ct % (int)((maxLabelWidth_ + 10) * numBars_ / width_) == 0) {
            // sparse labeling
            int x = (int)(xpos + barWidth_/2);
            g2.drawLine(x, height_ - MARGIN + TICK_LENGTH, x, height_ - MARGIN);
            g2.drawString(formatter_.format(xValue), xpos + 2, height_ - 5);
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

    private int getSum() {
        int sum = 0;
        for (int v : data_) {
            sum += v;
        }
        return sum;
    }
}


