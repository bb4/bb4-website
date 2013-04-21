// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.jfcbook;

import javax.swing.*;
import java.awt.*;

/**
 * A panel with a solid colored square in the middle of the background.
 * @author Barry Becker
 */
class ColoredPanel extends JPanel {

    private static final int SIZE = 50;
    private static final int HALF_SIZE = SIZE/2;

    private static final Color DEFAULT_COLOR = Color.RED;

    private Color squareColor = DEFAULT_COLOR;


	@Override
    public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Dimension size = getSize();

		g.setColor(Color.black);
		g.drawRect(0, 0, size.width-1, size.height-1);

		g.setColor(squareColor);
		g.fillRect(size.width/2-HALF_SIZE, size.height/2-HALF_SIZE, SIZE, SIZE);
	}

    public void setSquareColor(Color color) {
        this.squareColor = color;
    }


	@Override
    public Dimension getPreferredSize() {
		return new Dimension(130, 130);
	}

}
