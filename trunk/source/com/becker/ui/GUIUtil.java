package com.becker.ui;

import com.becker.common.ClassLoaderSingleton;
import com.becker.common.Util;
//import com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.io.File;


/**
 * This class implements a number of static utility functions that are useful when creating UIs.
 *
 * @author Barry Becker
 */
public final class GUIUtil
{
    // if true then running as an applet or webstart. if false, then running as an application
    private static boolean isStandAlone_ = true;

    // default location of images unless otherwise specified.
    private static String resourceRoot_ = Util.PROJECT_DIR + "source/";

    // for opening files.
    // don't create this here or applets using this class will have a security exception
    // instead we create a singleton when needed.
    private static JFileChooser fileChooser_ = null;
   

    // default font and color for the UI
    public static final Font UI_FONT = new Font( "Sans Serif", Font.PLAIN, 10 );      // standard

    // Blue color theme
    public static final Color UI_BLACK = new Color( 0, 0, 5 );
    public static final Color UI_WHITE = new Color( 250, 250, 255 );

    public static final Color UI_COLOR_PRIMARY1 = new Color( 0, 0, 51 );  // isn't used for much (bg when resizing?)
    public static final Color UI_COLOR_PRIMARY2 = new Color( 234, 234, 255 ); // menu bgs, small square in selected buttons, progress bar fill
    public static final Color UI_COLOR_PRIMARY3 = new Color( 255, 255, 153 );  // tooltip backgrounds, large colored areas, active titlebar, text selection

    public static final Color UI_COLOR_SECONDARY1 = new Color( 0, 0, 51 );  // very dark. for tab, button and checkbox borders
    public static final Color UI_COLOR_SECONDARY2 = new Color( 153, 153, 204 ); // deselected tab backgrounds, dimmed button borders
    public static final Color UI_COLOR_SECONDARY3 = new Color(224, 224, 245); //( 204, 204, 255 );  // almost all backgrounds, active tabs.

    public static final Color UI_BUTTON_BACKGROUND = new Color( 204, 204, 255 ); // button backgrounds

    /*
    // Cyan color theme
    public static final Color UI_BLACK = new Color( 0, 0, 0 );
    public static final Color UI_WHITE = new Color( 255, 255, 255 );

    public static final Color UI_COLOR_PRIMARY1 = new Color( 0, 51, 51 );  // isn't used for much (bg when resizing?)
    public static final Color UI_COLOR_PRIMARY2 = new Color( 204, 255, 255 ); // menu bgs, small square in selected buttons, progress bar fill
    public static final Color UI_COLOR_PRIMARY3 = new Color( 255, 255, 153 );  // tooltip backgrounds, large colored areas, active titlebar, text selection

    public static final Color UI_COLOR_SECONDARY1 = new Color( 0, 51, 51 );  // very dark. for tab, button and checkbox borders
    public static final Color UI_COLOR_SECONDARY2 = new Color( 102, 153, 153 ); // deselected tab backgrounds, dimmed button borders
    public static final Color UI_COLOR_SECONDARY3 = new Color( 204, 255, 255 );  // almost all backgrounds, active tabs.

    public static final Color UI_BUTTON_BACKGROUND = new Color( 153, 255, 255 ); // button backgrounds
    */

    // get custom colors for these look and feel properties
    private static HashMap hmUIProps_ = new HashMap();

    // webstart services
    private static BasicService basicService_ = null;


    static
    {
        hmUIProps_.put( "Menu.background", UI_COLOR_PRIMARY2 );
        hmUIProps_.put( "MenuItem.background", UI_COLOR_PRIMARY2 );
        hmUIProps_.put( "PopupMenu.background", UI_COLOR_PRIMARY2 );
        hmUIProps_.put( "OptionPane.background", UI_COLOR_SECONDARY3 );
        hmUIProps_.put( "ScrollBar.thumb", UI_COLOR_SECONDARY2 );
        hmUIProps_.put( "ScrollBar.foreground", UI_COLOR_PRIMARY2 );
        hmUIProps_.put( "ScrollBar.track", UI_COLOR_PRIMARY1 );
        hmUIProps_.put( "ScrollBar.trackHighlight", UI_WHITE );
        hmUIProps_.put( "ScrollBar.thumbDarkShadow", UI_BLACK );
        hmUIProps_.put( "ScrollBar.thumbLightShadow", UI_COLOR_PRIMARY1 );
        hmUIProps_.put( "Slider.foreground", UI_COLOR_SECONDARY3 );
        hmUIProps_.put( "Slider.background", UI_BUTTON_BACKGROUND );
        hmUIProps_.put( "Slider.highlight", Color.white );
        hmUIProps_.put( "Slider.shadow", UI_COLOR_PRIMARY1 );
        hmUIProps_.put( "Button.background", UI_BUTTON_BACKGROUND );
        hmUIProps_.put( "Label.background", UI_COLOR_SECONDARY3 ); // or BUTTON_BACKGROUND
        hmUIProps_.put( "Separator.shadow", UI_COLOR_PRIMARY1 );
        hmUIProps_.put( "Separator.highlight", UI_WHITE );
        hmUIProps_.put( "ToolBar.background", UI_COLOR_SECONDARY3 );
        hmUIProps_.put( "ToolBar.foreground", UI_COLOR_PRIMARY2 );
        hmUIProps_.put( "ToolBar.dockingbackground", UI_COLOR_SECONDARY3 );
        hmUIProps_.put( "ToolBar.dockingforeground", UI_COLOR_PRIMARY1 );
        hmUIProps_.put( "ToolBar.floatingbackground", UI_COLOR_SECONDARY3 );
        hmUIProps_.put( "ToolBar.floatingforeground", UI_COLOR_PRIMARY1 );
        hmUIProps_.put( "ProgressBar.foreground", UI_COLOR_SECONDARY1 );
        hmUIProps_.put( "control", UI_COLOR_PRIMARY1 );
    }

    /**
     * @param standAlone  if true then running as applet or through webstart; ohterwize, application
     */
    public static void setStandAlone(boolean standAlone)
    {
        isStandAlone_ = standAlone;
    }

    /**
     * @return if true then running as applet or through webstart; ohterwize, application.
     */
    public static boolean isStandAlone()
    {
        return isStandAlone_;
    }

    /**
     * only applies if running as an application
     * @param resourceRoot the location on disk where the images are located
     */
    public static void setResourceRoot(String resourceRoot)
    {
        resourceRoot_ = resourceRoot;
    }

    /**
     *get a singleton filechooser.
     */
    public static JFileChooser getFileChooser()
    {
        if ( fileChooser_ == null )
            fileChooser_ = new JFileChooser();
        return fileChooser_;
    }

    /**
     * return a stack trace given and exception.
     */
    public static String getStackTrace( Throwable e )
    {
        //  why stopped working?
        StackTraceElement el[] = e.getStackTrace();
        String trace = "stack trace:\n";
        for ( int i = 0; i < el.length; i++ ) {
            trace += el[i].getClassName() + "." + el[i].getMethodName();
            trace += " line=" + el[i].getLineNumber() + "\n";
        }
        return trace;
    }

    /**
     * Replace oldString with newString.
     */
    public static String replaceString( String value, String oldString, String newString )
    {
        if ( value == null || value.length() == 0 ) return value;
        int oldLength = oldString.length();
        StringBuffer buf = new StringBuffer();
        int last = 0, i;
        while ( true ) {
            i = value.indexOf( oldString, last );
            if ( i < 0 ) {
                buf.append( value.substring( last ) );
                break;
            }
            buf.append( value.substring( last, i ) );
            buf.append( newString );
            last = i + oldLength;
        }
        return buf.toString();
    }


    /**
     *  Set the ui looki and feel to my very own.
     */
    public static void setCustomLookAndFeel()
    {
        BarryTheme theme = new BarryTheme( UI_FONT, UI_BLACK, UI_WHITE,
                UI_COLOR_PRIMARY1, UI_COLOR_PRIMARY2, UI_COLOR_PRIMARY3,
                UI_COLOR_SECONDARY1, UI_COLOR_SECONDARY2, UI_COLOR_SECONDARY3 );
        MetalLookAndFeel.setCurrentTheme( theme );

        // for java 1.4 and later
        //JFrame.setDefaultLookAndFeelDecorated(true);
        //JDialog.setDefaultLookAndFeelDecorated(true);

        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  // for windows
            //java look and feel is customizable with themes
            UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" );

            // a cool experimental look and feel. see http://www.oyoaha.com/
            //OyoahaLookAndFeel lnf = new OyoahaLookAndFeel();
            //UIManager.setLookAndFeel(lnf);

            //GTK look and feel for Linux.
            //UIManager.setLookAndFeel( "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" );

            // MacIntosh Look and feel
            // there is supposed to be some trick to getting this to wowk, but I can't find it right now.
            //UIManager.setLookAndFeel( new it.unitn.ing.swing.plaf.macos.MacOSLookAndFeel() );

            // turn on auditory cues.
            // @@ can't do this under linux until I upgrade java or get the right soundcard driver.
            ////UIManager.put("AuditoryCues.playList", UIManager.get("AuditoryCues.allAuditoryCues"));

            setUIManagerProperties( hmUIProps_ );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the standard windows look and feel
     */
    public static void setStandardLookAndFeel()
    {
        try {
            UIManager.setLookAndFeel( new com.sun.java.swing.plaf.windows.WindowsLookAndFeel() );
            //UIManager.setLookAndFeel( new javax.swing.plaf.multi.MultiLookAndFeel() );
        } catch (Exception e) {
            System.out.println( "setting the look and feel for the applet failed" );
        }
    }

    /**
     * set my own personal look and feel
     * set custom UI colors and icons
     */
    private static void setUIManagerProperties( HashMap uiProps )
    {
        // first set all the custom colors for properties
        Iterator keyIt = uiProps.keySet().iterator();

        while ( keyIt.hasNext() ) {
            String key = (String) keyIt.next();
            Color propColor = (Color) uiProps.get( key );
            assert ( propColor!=null );
            //System.out.println("putting "+key);
            UIManager.put( key, new ColorUIResource( propColor ) );
        }
    }

    /**
     * get the image icon given the full path to the image.
     */
    public static ImageIcon getIcon(String sPath) {
        ImageIcon icon;
        //System.out.println( "loading "+sPath );
        if (isStandAlone_)   {
             icon = new ImageIcon( ClassLoaderSingleton.getClassLoader().getResource(sPath));
        }
        else {
             icon = new ImageIcon(resourceRoot_ + sPath);
        }
        assert (icon!=null) : "failed to find image:"+sPath;
        return icon;
    }

    /**
     * get a URL given the path to a file.
     */
    public static URL getURL(String sPath) {

        URL url = null;
        try {
            if (isStandAlone_)   {
                url = ClassLoaderSingleton.getClassLoader().getResource(sPath);
            }
            else {
                String spec = "file:"+resourceRoot_+sPath;
                url = new URL(spec);
            }
        } catch (MalformedURLException e) {
            System.out.println( sPath+" is not a valid resource or URL" );
            e.printStackTrace();
        }
        return url;
     }



    /**
     * displays a splash screen while the application is busy starting up.
     */
    public static JWindow showSplashScreen( int waitMillis )
    {
        // show a splash screen initially (if we are running through web start)
        // so the user knows something is happenning
        ImageIcon splash;
        URL url = ClassLoaderSingleton.getClassLoader().getResource("config/images/splash.gif");
        if (url==null) // then use a default
            splash = new ImageIcon( new BufferedImage( 300, 300, BufferedImage.TYPE_INT_RGB ) );
        else
            splash = new ImageIcon( url );

        JWindow w = new SplashScreen( splash, null, waitMillis );
        return w;
    }

    /**
     * this method is useful for turning Applets into applications
     * @return the base frame which holds the applet content
     */
    public static JFrame showApplet( JApplet applet, String title )
    {
        isStandAlone_ = false;
        //Assert.isFalse(isStandAlone_, "You must be running as an application if you are calling this method.");

        JFrame baseFrame = new JFrame();

        baseFrame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        baseFrame.addWindowListener( new WindowAdapter()
        {
            public void windowClosed( WindowEvent e )
            {
                System.exit( 0 );
            }
        } );
        baseFrame.setTitle( title );
        baseFrame.setContentPane( applet.getContentPane() );

        baseFrame.setSize( applet.getSize() );

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        baseFrame.setLocation( (d.width - baseFrame.getSize().width) / 4, (d.height - baseFrame.getSize().height) / 4 );
        int height = (int) d.getHeight() / 2 ;
        int width = (int) Math.min(height*1.5, d.getWidth() / 2);
        baseFrame.setSize( width, height);

        baseFrame.setVisible( true );

        // call the applet's init method
        applet.init();

        // call the applet's start method
        //baseFrame.setVisible( true );
        applet.start();       
        return baseFrame;
    }


    /**
     * gets a color from a hexadecimal string like "AABBCC"
     * or "AABBCCDD". The DD in this case gives the opacity value
     * if only rgb are given, then FF is asumed for the opacity
     * @param sColor color to convert
     * @param defaultColor  color to use if sColor has a problem
     * @return the color object
     */
    public static final Color getColorFromHTMLColor(String sColor, Color defaultColor)
    {
        if (sColor==null || sColor.length()<6 || sColor.length()>8)
            return defaultColor;

        long intColor;
        try {
            intColor = Long.decode("0x"+sColor).longValue();
        }
        catch (NumberFormatException e) {
            System.out.println("bad color format: "+sColor);
            return defaultColor;
        }
        int blue =  (int)(intColor % 256);
        int green = (int)((intColor / 256 ) % 256);
        int red = (int)((intColor / 65536 ) % 256);
        int opacity = 255;
        if (sColor.length()>6) {
           opacity = (int)(intColor / 16777216);
        }
        return new Color(red, green, blue, opacity);
    }

    /**
     * returns a hexadecimal string representation of the color - eg "AABBCC" or "DDAABBCC"
     * The DD in this case gives the opacity value
     */
    public static final String getHTMLColorFromColor(Color color)
    {
        int intval = color.getRGB();
        intval -= 0xFF000000;
        //System.out.println("NodePres getString from Color = "+Integer.toHexString(intval).toUpperCase());
        return "#"+Integer.toHexString(intval).toUpperCase();
    }

    public static final void paintComponentWithTexture(ImageIcon texture, Component c, Graphics g)
    {
        if (texture==null) {
            System.out.println( "warning no texture to tile with" );
            return;
        }
        Dimension size = c.getSize();

        int textureWidth = texture.getIconWidth();
        int textureHeight = texture.getIconHeight();

        g.setColor(c.getBackground());
        g.fillRect(0,0,size.width, size.height);
        for (int row=0; row<size.height; row+=textureHeight) {
            for (int col=0; col<size.width; col+=textureWidth) {
                texture.paintIcon(c, g, col, row);
            }
        }
    }

    public static Color invertColor(Color cColor)
    {
        return invertColor(cColor, 255);
    }
    public static Color invertColor(Color cColor, int trans)
    {
        return new Color( 255-cColor.getRed(), 255-cColor.getGreen(), 255-cColor.getBlue(), trans);
    }

    /**
     *
     * @param color
     * @return the hue (in HSB space) for a given color.
     */
    public static float getColorHue(Color color)
    {
        float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return hsv[0];
    }

    /**
     * Get the suffix of a file name.
     * The part after the "." typically used by FileFilters.
     * @return the file suffix.
    */
    public static String getFileSuffix(File f)
    {
        String s = f.getPath(), suffix = null;
        int i = s.lastIndexOf('.');
        if (i>0 && i< s.length()-1)
            suffix = s.substring(i+1).toLowerCase();
        return suffix;
    }

    /**
     * @return the basic jnlp service or null if it is not available.
     */
    public static BasicService getBasicService()
    {
        if (basicService_ == null) {
            try {
                basicService_ = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
            }
            catch (Exception e) {
                System.out.println( "Not running through webstart: "+e.getMessage() );
                return null;
            }
            catch (NoClassDefFoundError ncde) {
                //System.out.println( "jnlp BasicService not available: "+ncde.getMessage() );
                return null;
            }
        }
        return basicService_;
    }

}

