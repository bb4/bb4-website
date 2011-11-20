// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.apps.misc.jfcbook;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
class ColoredPanel extends JPanel {

	@Override
    public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Dimension size = getSize();

		g.setColor(Color.black);
		g.drawRect(0,0,size.width-1,size.height-1);

		g.setColor(Color.red);
		g.fillRect(size.width/2-25,size.height/2-25,50,50);
	}

	@Override
    public Dimension getPreferredSize() {
		return new Dimension(100,100);
	}

}
