package com.becker.misc.jfcbook;

import com.becker.ui.*;
import com.becker.common.Util;

import javax.swing.*;
import java.awt.*;

public class OpaqueTest extends JFrame {

    public static void main( String[] args )
    {
        JFrame f = new OpaqueTest();
        Container contentPane = f.getContentPane();
		TexturedPanel rainPanel = new TexturedPanel(GUIUtil.getIcon("com/becker/misc/jfcbook/rain.gif"));

        ResizableAppletPanel resizablePanel = new ResizableAppletPanel( rainPanel );

		ColoredPanel opaque = new ColoredPanel(),
		transparent = new ColoredPanel();

        JLabel textLabel = new JLabel("test");
        JPanel plain = new JPanel();
        plain.setPreferredSize(new Dimension(100,100));
        plain.add(textLabel);

		// JComponents are opaque by default, so the opaque
		// property only needs to be set for transparent
		transparent.setOpaque(false);

		rainPanel.add(opaque);
		rainPanel.add(transparent);
        rainPanel.add(textLabel);

		contentPane.add(resizablePanel, BorderLayout.CENTER);

        f.setSize(400,400);
        f.setVisible(true);
    }
}


class RainPanel extends JPanel {
	ImageIcon rain = new ImageIcon(Util.PROJECT_DIR + "source/com/becker/misc/jfcbook/rain.gif");
	private int rainw = rain.getIconWidth();
	private int rainh = rain.getIconHeight();

	public void paintComponent(Graphics g) {
		Dimension size = getSize();

		for(int row=0; row < size.height; row += rainh)
			for(int col=0; col < size.width; col += rainw)
				rain.paintIcon(this, g, col, row);
	}
}


class ColoredPanel extends JPanel {

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Dimension size = getSize();

		g.setColor(Color.black);
		g.drawRect(0,0,size.width-1,size.height-1);

		g.setColor(Color.red);
		g.fillRect(size.width/2-25,size.height/2-25,50,50);
	}
	public Dimension getPreferredSize() {
		return new Dimension(100,100);
	}

}
