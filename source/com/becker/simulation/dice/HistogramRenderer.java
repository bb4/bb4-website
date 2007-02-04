package com.becker.simulation.dice;

import com.becker.common.*;

import java.awt.*;

/**
 * @author Barry Becker Date: Feb 4, 2007
 */
public class HistogramRenderer {


    private int[] data_;

    private int minX_;

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);

    private static final Color BAR_COLOR = new Color(100, 200, 255);
    private static final Color BAR_BORDER_COLOR = new Color(0, 0, 55);

    private static final int MARGIN = 24;

    private int width_;
    private int height_;

    public HistogramRenderer(int[] data, int minX)
    {
        data_ = data;
        minX_ = minX;
    }

    public void setSize(int width, int height) {
        width_ = width;
        height_ = height;
    }

    public void paint(Graphics g) {

        if (g == null)
            return;

        Graphics2D g2 = (Graphics2D) g;


        int numBars = data_.length;
        int maxHeight = getMaxHeight();
        int sum = getSum();
        double scale = (height_ -2.0*MARGIN) / maxHeight;

        // clear background
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect( 0, 0, width_, height_ );

        float xpos = MARGIN;

        float barWidth = (float)((width_ - 2.0*MARGIN) / numBars);
        //barWidth = Math.max(1, barWidth);
        //System.out.println("bw="+barWidth);


        int ct = 0;
        int maxNumLabels = width_/20;
        for (int v : data_) {
            double h = (scale * v);
            int top = (int)(height_ - h - MARGIN);
            g2.setColor( BAR_COLOR );
            g2.fillRect((int)xpos, top, (int)Math.max(1, barWidth), (int) h);
            g2.setColor( BAR_BORDER_COLOR );

            if (numBars < maxNumLabels) {
                // then draw all labels
                g2.drawString(Integer.toString(minX_ + ct), xpos + 1, height_ - 5);
                g2.drawRect((int)xpos, top, (int)barWidth, (int) h);
            }  else if (ct % (int)(40.0 * numBars / width_) == 0) {
                g2.drawString(Integer.toString(minX_ + ct), xpos + 2, height_ - 5);
            }
            ct++;
            xpos += barWidth;
        }
        // left axis
        g2.drawLine(MARGIN, height_ - MARGIN, MARGIN, MARGIN);
        g2.drawString("Height = "+ Util.formatNumber(maxHeight), MARGIN, MARGIN);
        g2.drawString("Number of Rolls = " + Util.formatNumber(sum), width_ - 200, MARGIN >> 1);       
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


