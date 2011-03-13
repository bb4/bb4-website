package com.becker.apps.sierpinski;

import com.becker.ui.application.ApplicationApplet;
import com.becker.ui.util.GUIUtil;

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