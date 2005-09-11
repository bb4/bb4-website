package com.becker.ui;

import javax.swing.*;
import java.awt.*;

/**
 * a panel with a textured background.
 * The background gets tiled with the image that is passed in.
 */
public class TexturedToolBar extends JToolBar
{
    private ImageIcon texture_ = null;
    private static final long serialVersionUID = 0L;


    public TexturedToolBar( ImageIcon texture )
    {
        setTexture(texture);
    }

    public void setTexture( ImageIcon texture )
    {
        texture_ = texture;
    }

    public void paintComponent(Graphics g)
    {
        GUIUtil.paintComponentWithTexture(texture_, this, g);
    }

}