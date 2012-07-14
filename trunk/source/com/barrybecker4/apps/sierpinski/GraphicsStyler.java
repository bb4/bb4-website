/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.sierpinski;

import java.awt.*;

/**
 * Given a depth, and a Graphics2 instance, set appropriate color and style.
 * @author Barry Becker
 */
public class GraphicsStyler {

    /** max line width at depth 0 */
    private  float lineWidth;

    private static final Color[] LINE_COLORS = {
            new Color(0, 0, 80, 100),
            new Color(0, 10, 210, 200),
            new Color(0, 200, 90, 255),
            new Color(80, 255, 0, 160),
            new Color(250, 200, 0, 150),
            new Color(255, 0, 0, 100),
            new Color(255, 0, 100, 70),
            new Color(250, 0, 255, 40)
    };

    public GraphicsStyler(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setStyle(int depth, Graphics2D g2) {
        BasicStroke stroke =
                new BasicStroke(lineWidth/(3 * depth + 1.0f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);
        g2.setColor(LINE_COLORS[Math.min(depth, LINE_COLORS.length-1)]);
    }
}