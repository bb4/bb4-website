/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.jfcbook;

import com.barrybecker4.ui.components.ResizableAppletPanel;
import com.barrybecker4.ui.components.TexturedPanel;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

public class OpaqueTest extends JFrame {

    public static void main( String[] args ) {
        JFrame f = new OpaqueTest();
        Container contentPane = f.getContentPane();
		TexturedPanel rainPanel =
                new TexturedPanel(GUIUtil.getIcon("com/barrybecker4/apps/misc/jfcbook/rain.gif"));

        ResizableAppletPanel resizablePanel = new ResizableAppletPanel( rainPanel );

		ColoredPanel opaque = new ColoredPanel(),
		transparent = new ColoredPanel();

        JLabel textLabel = new JLabel("test");
        JPanel plain = new JPanel();
        plain.setPreferredSize(new Dimension(100, 100));
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


