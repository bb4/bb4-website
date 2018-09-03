/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.sierpinski;

import javax.swing.*;
import java.awt.*;

/**
 * Draws a recursive Sierpinksi triangle.
 * @author Barry Becker
 */
public class SierpinskiPanel extends JPanel {

    private SierpinskiRenderer renderer;

    SierpinskiPanel() {
        renderer = new SierpinskiRenderer();
    }

    void setRecursiveDepth(int depth) {
        renderer.setDepth(depth);
    }

    void setLineWidth(float width) {
        renderer.setLineWidth(width);
    }

    @Override
    public void paint( Graphics g ) {
        renderer.setSize(getWidth(), getHeight());
        renderer.paint(g);
    }
}
