/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.ui.components;

import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * a panel with a textured background.
 * The background gets tiled with the image that is passed in.
 */
public class TexturedToolBar extends JToolBar {
    private ImageIcon texture_ = null;
    private static final long serialVersionUID = 0L;

    protected static final Dimension MAX_BUTTON_SIZE = new Dimension( 100, 24 );

    /** the thing that processes the toolbar button presses. */
    protected ActionListener listener_;


    public TexturedToolBar( ImageIcon texture, ActionListener listener ) {
        listener_ = listener;
        setTexture(texture);
    }

    public TexturedToolBar( ImageIcon texture) {
        setTexture(texture);
    }

    public void setListener(ActionListener listener ) {
        listener_ = listener;
    }

    public void setTexture( ImageIcon texture ) {
        texture_ = texture;
    }

    /**
     * create a toolbar button.
     */
    public GradientButton createToolBarButton( String text, String tooltip, Icon icon ) {
        GradientButton button = new GradientButton( text, icon );
        button.addActionListener( listener_ );
        button.setToolTipText( tooltip );
        button.setMaximumSize( MAX_BUTTON_SIZE );
        return button;
    }


    @Override
    public void paintComponent(Graphics g) {
        GUIUtil.paintComponentWithTexture(texture_, this, g);
    }

}