package com.becker.simulation.trebuchet;

import java.awt.*;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class Base extends RenderablePart {


    private static final int BASE_Y = 400;
    private static final int BASE_WIDTH = 400;
    private static final int STRUT_BASE_HALF_WIDTH = 50;

    private static final BasicStroke BASE_STROKE = new BasicStroke(2.0f);
    private static final Color BASE_COLOR = new Color(10, 40, 160);

    public Base() {        
    }

    public void render(Graphics2D g2) {

        g2.setStroke(BASE_STROKE);
        g2.setColor(BASE_COLOR);

        g2.draw3DRect(BASE_X, BASE_Y, BASE_WIDTH, 10, false);
        g2.drawLine(STRUT_BASE_X - STRUT_BASE_HALF_WIDTH, BASE_Y, STRUT_BASE_X, BASE_Y - (int) (SCALE_FACTOR * height_));
        g2.drawLine(STRUT_BASE_X + STRUT_BASE_HALF_WIDTH, BASE_Y, STRUT_BASE_X, BASE_Y - (int) (SCALE_FACTOR * height_));
    }
}
