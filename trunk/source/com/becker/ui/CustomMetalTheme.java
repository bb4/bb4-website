package com.becker.ui;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;

/**
 *  This is a custom metal theme
 *  It allows custionization of the  font and color.
 *
 *  @author Barry Becker
 */
public class CustomMetalTheme extends DefaultMetalTheme
{

    private Font font_;
    private ColorUIResource colorResource_;

    // constructor
    public CustomMetalTheme( Font font, Color color )
    {
        font_ = font;
        colorResource_ = new ColorUIResource( color );
    }

    public String getName()
    {
        return "CustomMetal";
    }

    public FontUIResource getControlTextFont()
    {
        return new FontUIResource( font_.getName(), font_.getStyle(), font_.getSize() );
    }

    ColorUIResource getPrimary0()
    {
        return new ColorUIResource( colorResource_.darker() );
    }

    protected ColorUIResource getPrimary1()
    {
        return colorResource_;
    }

    protected ColorUIResource getPrimary2()
    {
        return new ColorUIResource( colorResource_.brighter() );
    }

    protected ColorUIResource getPrimary3()
    {
        return new ColorUIResource( colorResource_.white );
    }

    ColorUIResource getSecondary0()
    {
        return new ColorUIResource( Color.black );
    }

    protected ColorUIResource getSecondary1()
    {
        return new ColorUIResource( colorResource_.darker() );
    }

    protected ColorUIResource getSecondary2()
    {
        return colorResource_;
    }

    protected ColorUIResource getSecondary3()
    {
        return new ColorUIResource( colorResource_.brighter() );
    }

    ColorUIResource getSecondary4()
    {
        return new ColorUIResource( Color.white );
    }
}
