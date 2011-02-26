package com.becker.apps.misc.sierpinksi;

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

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        setFont( new Font( "Serif", Font.PLAIN, 14 ) );

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

    /**
     * This method allow javascript to resize the applet from the browser.
     */
    @Override
    public void setSize( int width, int height )  {
        resizablePanel_.setSize( width, height );
    }

    //------ Main method --------------------------------------------------------

    public static void main( String[] args ) {
        SierpinskiApplet simulator = new SierpinskiApplet();
        GUIUtil.showApplet( simulator, "Sierpinski Triangle" );
    }
}