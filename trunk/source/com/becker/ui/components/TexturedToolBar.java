package com.becker.ui.components;

import com.becker.ui.*;
import com.becker.ui.components.GradientButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * a panel with a textured background.
 * The background gets tiled with the image that is passed in.
 */
public class TexturedToolBar extends JToolBar
{
    private ImageIcon texture_ = null;
    private static final long serialVersionUID = 0L;

    protected static final Dimension MAX_BUTTON_SIZE = new Dimension( 100, 24 );

    // the thing that processes the toolbar button presses.
    protected ActionListener listener_;


    public TexturedToolBar( ImageIcon texture, ActionListener listener )
    {
        listener_ = listener;
        setTexture(texture);
    }

    public void setTexture( ImageIcon texture )
    {
        texture_ = texture;
    }

    /**
     * create a toolbar button.
     */
    public GradientButton createToolBarButton( String text, String tooltip, Icon icon )
    {
        GradientButton button = new GradientButton( text, icon );
        button.addActionListener( listener_ );
        button.setToolTipText( tooltip );
        button.setMaximumSize( MAX_BUTTON_SIZE );
        return button;
    }


    @Override
    public void paintComponent(Graphics g)
    {
        GUIUtil.paintComponentWithTexture(texture_, this, g);
    }

}