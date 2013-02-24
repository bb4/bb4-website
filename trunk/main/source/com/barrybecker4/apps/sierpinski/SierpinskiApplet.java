/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.sierpinski;

import com.barrybecker4.ui.application.ApplicationApplet;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

public class SierpinskiApplet extends ApplicationApplet {

    @Override
    protected JPanel createMainPanel() {
        SierpinskiComponent sierpinskiComp = new SierpinskiComponent();
        sierpinskiComp.setBorder(BorderFactory.createEtchedBorder());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.add(sierpinskiComp, BorderLayout.CENTER );

        return mainPanel;
    }

    //------ Main method --------------------------------------------------------

    public static void main( String[] args ) {
        SierpinskiApplet simulator = new SierpinskiApplet();
        GUIUtil.showApplet( simulator, "Sierpinski Triangle" );
    }
}