package com.becker.apps.sierpinksi;

import com.becker.ui.GUIUtil;
import com.becker.ui.components.ResizableAppletPanel;

import javax.swing.*;
import java.awt.*;

public class SierpinskiApplet extends JApplet {

    ResizableAppletPanel resizablePanel_ = null;

    /**
     * Constructor
     */
    public SierpinskiApplet() {
        commonInit();
    }

    private void commonInit() {
        GUIUtil.setCustomLookAndFeel();
        JPanel mainPanel = createMainPanel();

        resizablePanel_ = new ResizableAppletPanel( mainPanel );
        this.getContentPane().add( resizablePanel_ );
    }

    private JPanel createMainPanel() {
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