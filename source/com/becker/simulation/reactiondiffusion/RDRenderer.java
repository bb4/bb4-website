package com.becker.simulation.reactiondiffusion;

import java.awt.*;

/**
 * @author Barry Becker Date: Nov 5, 2006
 */
public final class RDRenderer {

    private GrayScott gs_;

    int nColors = 85;
    Color[] color = new Color[3 * nColors];



    public RDRenderer(GrayScott gs) {
        gs_ = gs;
        initColors();
    }

    private void initColors() {
        /* only red and green currently. */
        for (int i = 0; i < nColors; i++) {
            color[i] = new Color((int) (255 * ((double) i / nColors)), 0, 0);
        }
        for (int i = 0; i < nColors; i++) {
            color[nColors + i] = new Color(255, (int) (255 * ((double) i / nColors)), 0);
        }
        for (int i = 0; i < nColors; i++) {
            color[2 * nColors + i] = new Color((int) (255 * (1.0 - (double) i / nColors)), 255, 0);
        }
    }

    public void render(Graphics2D g2) {

        int cn;
        int w = gs_.getWidth();
        int h = gs_.getHeight();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                cn = (int) ((3 * nColors - 1.0) * gs_.v_[x][y] / 0.4);
                if (cn > (3 * nColors - 1)) {
                    cn = 3 * nColors - 1;
                }
                if (cn < 0) {
                    cn = 0;
                }
                g2.setColor(color[cn]);
                g2.drawLine(x, y, x, y);
            }
        }

    }



}
