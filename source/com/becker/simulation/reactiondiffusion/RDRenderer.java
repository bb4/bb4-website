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

    private ColorMap cmap_;


    public RDRenderer(GrayScott gs) {
        gs_ = gs;
        cmap_ = createColorMap();
    }


    private static ColorMap createColorMap() {
        double[] values = {
              0,
              0.04,
              0.1,
              0.2,
              0.3,
              0.5,
              0.7,
              0.8,
              0.9,
              0.96,
              1.0};
        Color[] colors = {
            new Color(0, 0, 0),
            new Color(0, 0, 200),     // .04
            new Color(100, 0, 255),   // .1
            new Color(0, 50, 255),    // .2
            new Color(0, 255, 255),   // .3
            new Color(0, 255, 0),     // .5
            new Color(255, 255, 0),   // .7
            new Color(255, 100, 0),   // .8
            new Color(255, 0, 100),   // .9
            new Color(200, 0, 0),     // .96
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
        //double minC = 10000.0;
        //double maxC = -10000.0;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                double concentration = isShowingU() ? gs_.u_[x][y] : 0.0;
                if (isShowingV()) {
                    concentration += gs_.v_[x][y];
                }
                /*
                if (concentration > maxC) {
                    maxC = concentration;
                }
                if ( concentration < minC)  {
                    minC = concentration;
                } */

                g2.setColor(cmap_.getColorForValue(concentration));
                g2.drawLine(MARGIN + x, MARGIN + y, MARGIN + x, MARGIN + y);
            }
        }

        //System.out.println("c range="+minC+" ... "+ maxC);
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
