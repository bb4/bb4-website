/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.jfcbook;

import com.barrybecker4.ui.components.ResizableAppletPanel;
import com.barrybecker4.ui.components.TexturedPanel;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

public class OpaqueTest extends JFrame {

    public static void main( String[] args ) {
        JFrame frame = new OpaqueTest();
        Container contentPane = frame.getContentPane();

        ResizableAppletPanel resizablePanel = createResizableAppletPanel();

		contentPane.add(resizablePanel, BorderLayout.CENTER);

        frame.setSize(400, 400);
        frame.setVisible(true);
    }


    private static ResizableAppletPanel createResizableAppletPanel() {
        TexturedPanel rainPanel =
            new TexturedPanel(GUIUtil.getIcon("com/barrybecker4/apps/misc/jfcbook/rain.gif"));

        ResizableAppletPanel resizablePanel = new ResizableAppletPanel( rainPanel );

        ColoredPanel opaque = new ColoredPanel(),

        // JComponents are opaque by default
        transparent = new ColoredPanel();
        transparent.setOpaque(false);

        ColoredPanel alpha = new ColoredPanel();
        alpha.setOpaque(false);
        alpha.setSquareColor(new Color(255, 34, 34, 134));

        rainPanel.add(opaque);
        rainPanel.add(transparent);
        rainPanel.add(alpha);
        rainPanel.add(createLabel());

        return resizablePanel;
    }

    private static JLabel createLabel() {
        JLabel textLabel = new JLabel("test");
        JPanel plain = new JPanel();
        plain.setPreferredSize(new Dimension(100, 100));
        plain.add(textLabel);
        return textLabel;
    }


}


