package com.becker.ui;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;

/**
 * My own custom UI theme
 * @author  becker
 */
public class BarryTheme extends DefaultMetalTheme
{

    private Font font_;
    private ColorUIResource colorResourcePrimary1_;
    private ColorUIResource colorResourcePrimary2_;
    private ColorUIResource colorResourcePrimary3_;
    private ColorUIResource colorResourceSecondary1_;
    private ColorUIResource colorResourceSecondary2_;
    private ColorUIResource colorResourceSecondary3_;

    private ColorUIResource black_ = new ColorUIResource( Color.black );
    private ColorUIResource white_ = new ColorUIResource( Color.white );

    /**
     *constructor. Specify specific values for all the colors
     */
    public BarryTheme( Font font, Color black, Color white,
                       Color colorPrimary1, Color colorPrimary2, Color colorPrimary3,
                       Color colorSecondary1, Color colorSecondary2, Color colorSecondary3 )
    {
        font_ = font;

        black_ = new ColorUIResource( black );
        white_ = new ColorUIResource( white );

        colorResourcePrimary1_ = new ColorUIResource( colorPrimary1 );
        colorResourcePrimary2_ = new ColorUIResource( colorPrimary2 );
        colorResourcePrimary3_ = new ColorUIResource( colorPrimary3 );

        colorResourceSecondary1_ = new ColorUIResource( colorSecondary1 );
        colorResourceSecondary2_ = new ColorUIResource( colorSecondary2 );
        colorResourceSecondary3_ = new ColorUIResource( colorSecondary3 );
    }

    // the name of the theme
    public String getName()
    {
        return "Barry's theme";
    }

    // font used for most ui elements
    // cannot hadcode the font, or it will not work in Japanese
    //public FontUIResource getControlTextFont()
    //{
    //    return new FontUIResource( font_.getName(), font_.getStyle(), font_.getSize() );
    //}

    // overrride if desire different from defaults.
    //public FontUIResource getMenuTextFont()  {}
    //public FontUIResource getSystemTextFont()  {}
    //public FontUIResource getUserTextFont()  {}
    //public FontUIResource getSubTextFont()  {
    //    return new FontUIResource(font_.getName(), font_.getStyle(), font_.getSize());
    //}

    protected ColorUIResource getBlack()
    {
        return black_;
    }

    protected ColorUIResource getWhite()
    {
        return white_;
    }

    protected ColorUIResource getPrimary0()
    {
        return new ColorUIResource( black_ );
    }

    protected ColorUIResource getPrimary1()
    {
        return colorResourcePrimary1_;
    }

    protected ColorUIResource getPrimary2()
    {
        return colorResourcePrimary2_;
    }

    protected ColorUIResource getPrimary3()
    {
        return colorResourcePrimary3_;
    }

    protected ColorUIResource getSecondary0()
    {
        return black_;
    }

    protected ColorUIResource getSecondary1()
    {
        return colorResourceSecondary1_;
    }

    protected ColorUIResource getSecondary2()
    {
        return colorResourceSecondary2_;
    }

    protected ColorUIResource getSecondary3()
    {
        return colorResourceSecondary3_;
    }

    protected ColorUIResource getSecondary4()
    {
        return white_;
    }
}