package com.becker.simulation.reactiondiffusion;

import com.becker.common.*;

import java.awt.*;

/**
 * @author Barry Becker Date: Nov 5, 2006
 */
public final class RDRenderer {

    private GrayScott gs_;

    private boolean isShowingU_ = false;
    private boolean isShowingV_ = true;

    private static final int MARGIN = 20;

    //private ColorMap cmap_;

    double minC_ = 0;
    double maxC_ = 1.0;

    public RDRenderer(GrayScott gs) {
        gs_ = gs;
        //cmap_ = createColorMap();
    }


    private ColorMap createColorMap() {
        double range = maxC_ - minC_;

        double[] values = {
              minC_,
              minC_ + 0.04 * range,
              minC_ + 0.1 * range,
              //minC_ + 0.2 * range,
              minC_ + 0.3 * range,
              //minC_ + 0.35 * range,
              //minC_ + 0.36 * range,
              //minC_ + 0.37 * range,
              minC_ + 0.5 * range,
              minC_ + 0.7 * range,
              //minC_ + 0.8 * range,
              //minC_ + 0.9 * range,
              minC_ + 0.94 * range,
              minC_ + range};
        Color[] colors = {
            new Color(0, 0, 0),
            new Color(0, 0, 255),   // .04
            new Color(100, 0, 250),   // .1
            //new Color(50,  0, 255),   // .2
            new Color(0, 255, 255),   // .3
            //new Color(0, 240, 255),   // .35
            //new Color(200, 255, 255), // .36
            //new Color(0, 255, 200),   // .37
            new Color(0, 255, 0),     // .5
            new Color(255, 255, 0),   // .7
            //new Color(255, 100, 0),   // .8
            //new Color(255, 0, 100),   // .9
            new Color(255, 0, 0),     // .94
            new Color(0, 0, 0)
        };
        return new ColorMap(values, colors);
    }

    /**
     * Draw the model representing the current state of the GrayScott rd implementation.
     */
    public void render(Graphics2D g2) {

        int cn;
        int w = gs_.getWidth();
        int h = gs_.getHeight();
        ColorMap cmap = createColorMap();
        minC_ = 1000.0;
        maxC_= -1000.0;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                double concentration = isShowingU() ? gs_.u_[x][y] : 0.0;
                if (isShowingV()) {
                    concentration += gs_.v_[x][y];
                }

                if (concentration > maxC_) {
                    maxC_ = concentration;
                }
                if ( concentration < minC_)  {
                    minC_ = concentration;
                }

                g2.setColor(cmap.getColorForValue(concentration));
                g2.drawLine(MARGIN + x, MARGIN + y, MARGIN + x, MARGIN + y);
            }
        }
    }

    public boolean isShowingU() {
        return isShowingU_;
    }

    public void setShowingU(boolean showingU) {
        isShowingU_ = showingU;
    }

    public boolean isShowingV() {
        return isShowingV_;
    }

    public void setShowingV(boolean showingV) {
        isShowingV_ = showingV;
    }
}
