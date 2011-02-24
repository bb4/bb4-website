package com.becker.ui.themes;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * My own custom UI theme.
 * @author  becker
 */
public class BarryTheme extends DefaultMetalTheme
{
    public static final Color UI_BLACK = new Color( 0, 0, 0 );
    public static final Color UI_WHITE = new Color( 250, 250, 255 );

    /** isn't used for much (bg when resizing?)   */
    public static final Color UI_COLOR_PRIMARY1 = new Color( 7, 2, 71 );  //51

    /** menu bgs, selected item in dropdown menu, small square in selected buttons, progress bar fill */
    public static final Color UI_COLOR_PRIMARY2 = new Color( 234, 234, 255 );
    /** tooltip backgrounds, large colored areas, active titlebar, text selection */
    public static final Color UI_COLOR_PRIMARY3 = new Color( 255, 255, 153 );

    /** very dark. for tab, button and checkbox borders */
    public static final Color UI_COLOR_SECONDARY1 = new Color( 7, 2, 71 ); //51
    /**  deselected tab backgrounds, dimmed button borders */
    public static final Color UI_COLOR_SECONDARY2 = new Color( 153, 153, 204 );
    /** ( 204, 204, 255 );  // almost all backgrounds, active tabs. */
    public static final Color UI_COLOR_SECONDARY3 = new Color(214, 214, 245);

    /** button backgrounds */
    public static final Color UI_BUTTON_BACKGROUND = new Color( 204, 204, 255 );


    /** get custom colors for these look and feel properties */
    private Map<String, Color> hmUIProps_ = new HashMap<String, Color>();


    private ColorUIResource colorResourcePrimary1_;
    private ColorUIResource colorResourcePrimary2_;
    private ColorUIResource colorResourcePrimary3_;
    private ColorUIResource colorResourceSecondary1_;
    private ColorUIResource colorResourceSecondary2_;
    private ColorUIResource colorResourceSecondary3_;

    private ColorUIResource black_ = new ColorUIResource( Color.black );
    private ColorUIResource white_ = new ColorUIResource( Color.white );

    /**
     * Default constructor.
     */
    public BarryTheme() {
        this( UI_BLACK, UI_WHITE,
              UI_COLOR_PRIMARY1, UI_COLOR_PRIMARY2, UI_COLOR_PRIMARY3,
              UI_COLOR_SECONDARY1, UI_COLOR_SECONDARY2, UI_COLOR_SECONDARY3 );
    }
    /**
     * Constructor. Specify specific values for all the colors
     */
    public BarryTheme( Color black, Color white,
                       Color colorPrimary1, Color colorPrimary2, Color colorPrimary3,
                       Color colorSecondary1, Color colorSecondary2, Color colorSecondary3 ) {
        black_ = new ColorUIResource( black );
        white_ = new ColorUIResource( white );

        colorResourcePrimary1_ = new ColorUIResource( colorPrimary1 );
        colorResourcePrimary2_ = new ColorUIResource( colorPrimary2 );
        colorResourcePrimary3_ = new ColorUIResource( colorPrimary3 );

        colorResourceSecondary1_ = new ColorUIResource( colorSecondary1 );
        colorResourceSecondary2_ = new ColorUIResource( colorSecondary2 );
        colorResourceSecondary3_ = new ColorUIResource( colorSecondary3 );

        hmUIProps_ = initiallizeUIProperties();
    }

    // the name of the theme
    @Override
    public String getName()
    {
        return "Barry's theme";
    }


    /**
     * set custom UI colors and icons.
     * set all the custom colors for properties.
     */
    public void setUIManagerProperties() {
        for (String key : hmUIProps_.keySet()) {
            Color propColor = hmUIProps_.get(key);
            assert (propColor != null);
            UIManager.put(key, new ColorUIResource(propColor));
        }
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

    private Map<String, Color> initiallizeUIProperties() {
        Map<String, Color> hmUIProps = new HashMap<String, Color>();
        hmUIProps.put( "Menu.background", colorResourcePrimary2_ );
        hmUIProps.put( "MenuItem.background", colorResourcePrimary2_ );
        hmUIProps.put( "PopupMenu.background", colorResourcePrimary2_ );
        hmUIProps.put( "OptionPane.background", colorResourceSecondary3_ );
        hmUIProps.put( "ScrollBar.thumb", colorResourceSecondary2_ );
        hmUIProps.put( "ScrollBar.foreground", colorResourcePrimary2_ );
        hmUIProps.put( "ScrollBar.track", colorResourcePrimary1_ );
        hmUIProps.put( "ScrollBar.trackHighlight", white_ );
        hmUIProps.put( "ScrollBar.thumbDarkShadow", black_ );
        hmUIProps.put( "ScrollBar.thumbLightShadow", colorResourcePrimary1_ );
        hmUIProps.put( "Slider.foreground", colorResourceSecondary3_ );
        hmUIProps.put( "Slider.background", UI_BUTTON_BACKGROUND );
        hmUIProps.put( "Slider.highlight", Color.white );
        hmUIProps.put( "Slider.shadow", colorResourcePrimary1_ );
        hmUIProps.put( "Button.background", UI_BUTTON_BACKGROUND );
        hmUIProps.put( "Label.background", colorResourceSecondary3_ ); // or BUTTON_BACKGROUND
        hmUIProps.put( "Separator.shadow", colorResourcePrimary1_ );
        hmUIProps.put( "Separator.highlight", white_ );
        hmUIProps.put( "ToolBar.background", colorResourceSecondary3_ );
        hmUIProps.put( "ToolBar.foreground",colorResourcePrimary2_ );
        hmUIProps.put( "ToolBar.dockingbackground", colorResourceSecondary3_ );
        hmUIProps.put( "ToolBar.dockingforeground", colorResourcePrimary1_ );
        hmUIProps.put( "ToolBar.floatingbackground", colorResourceSecondary3_ );
        hmUIProps.put( "ToolBar.floatingforeground", colorResourcePrimary1_ );
        hmUIProps.put( "ProgressBar.foreground", colorResourceSecondary1_ );
        hmUIProps.put( "control", colorResourcePrimary1_ );
        return hmUIProps;
    }

    @Override
    protected ColorUIResource getBlack()
    {
        return black_;
    }

    @Override
    protected ColorUIResource getWhite()
    {
        return white_;
    }

    protected ColorUIResource getPrimary0()
    {
        return new ColorUIResource( black_ );
    }

    @Override
    protected ColorUIResource getPrimary1()
    {
        return colorResourcePrimary1_;
    }

    @Override
    protected ColorUIResource getPrimary2()
    {
        return colorResourcePrimary2_;
    }

    @Override
    protected ColorUIResource getPrimary3()
    {
        return colorResourcePrimary3_;
    }

    protected ColorUIResource getSecondary0()
    {
        return black_;
    }

    @Override
    protected ColorUIResource getSecondary1()
    {
        return colorResourceSecondary1_;
    }

    @Override
    protected ColorUIResource getSecondary2()
    {
        return colorResourceSecondary2_;
    }

    @Override
    protected ColorUIResource getSecondary3()
    {
        return colorResourceSecondary3_;
    }

    protected ColorUIResource getSecondary4()
    {
        return white_;
    }
}