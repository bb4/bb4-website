// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.apps.misc.jfcbook;

import com.becker.common.util.FileUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
class RainPanel extends JPanel {
	ImageIcon rain = new ImageIcon(FileUtil.PROJECT_HOME + "source/com/becker/apps/misc/jfcbook/rain.gif");
	private int rainw = rain.getIconWidth();
	private int rainh = rain.getIconHeight();

    @Override
	public void paintComponent(Graphics g) {
		Dimension size = getSize();

		for(int row=0; row < size.height; row += rainh)
			for(int col=0; col < size.width; col += rainw)
				rain.paintIcon(this, g, col, row);
	}
}
