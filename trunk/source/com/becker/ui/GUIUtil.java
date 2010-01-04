package com.becker.ui;

import com.becker.common.*;
import com.becker.common.util.FileUtil;
import com.becker.common.util.ImageUtil;
import com.sun.java.swing.plaf.windows.*;

import javax.jnlp.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;


/**
 * This class implements a number of static utility functions that are useful when creating UIs.
 *
 * @author Barry Becker
 */
public final class GUIUtil
{
    private GUIUtil() {}

    /** if true then running as an applet or webstart. if false, then running as an application. */
    private static boolean isStandAlone_ = true;

    /** default location of files on the local system unless otherwise specified. */
    public static final String RESOURCE_ROOT = FileUtil.PROJECT_DIR + "source/";

    /**
     * For opening files.
     * don't create this here or applets using this class will have a security exception
     * instead we create a singleton when needed.
     */
    private static JFileChooser fileChooser_ = null;


    /** default font and color for the UI */
    public static final Font UI_FONT = new Font( "Sans Serif", Font.PLAIN, 10 );      // standard

    // Purple color theme
    public static final Color UI_BLACK = new Color( 0, 0, 0 );
    public static final Color UI_WHITE = new Color( 250, 250, 255 );

    // isn't used for much (bg when resizing?)
    public static final Color UI_COLOR_PRIMARY1 = new Color( 7, 2, 71 );  //51
    // menu bgs, selected item in dropdown menu, small square in selected buttons, progress bar fill
    public static final Color UI_COLOR_PRIMARY2 = new Color( 234, 234, 255 );
    // tooltip backgrounds, large colored areas, active titlebar, text selection
    public static final Color UI_COLOR_PRIMARY3 = new Color( 255, 255, 153 );

    // very dark. for tab, button and checkbox borders
    public static final Color UI_COLOR_SECONDARY1 = new Color( 7, 2, 71 ); //51
    // deselected tab backgrounds, dimmed button borders
    public static final Color UI_COLOR_SECONDARY2 = new Color( 153, 153, 204 );
    //( 204, 204, 255 );  // almost all backgrounds, active tabs.
    public static final Color UI_COLOR_SECONDARY3 = new Color(214, 214, 245);

    // button backgrounds
    public static final Color UI_BUTTON_BACKGROUND = new Color( 204, 204, 255 );



    // get custom colors for these look and feel properties
    private static Map<String, Color> hmUIProps_ = new HashMap<String, Color>();

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
     *get a singleton filechooser.
     */
    public static JFileChooser getFileChooser()
    {
        if ( fileChooser_ == null )
            fileChooser_ = new JFileChooser();
        return fileChooser_;
    }

    /**
     * return a stack trace given an exception.
     */
    public static String getStackTrace( Throwable e )
    {
        StackTraceElement el[] = e.getStackTrace();
        String trace = "stack trace:\n";
        for (final StackTraceElement newVar : el) {
            trace += newVar.getClassName() + '.' + newVar.getMethodName();
            trace += " line=" + newVar.getLineNumber() + '\n';
        }
        return trace;
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
            UIManager.put("AuditoryCues.playList", UIManager.get("AuditoryCues.allAuditoryCues"));

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
            UIManager.setLookAndFeel( new WindowsLookAndFeel() );
            //UIManager.setLookAndFeel( new javax.swing.plaf.multi.MultiLookAndFeel() );
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println( "setting the look and feel for the applet failed."+ e.getMessage() );
        }
    }

    /**
     * set my own personal look and feel
     * set custom UI colors and icons
     */
    private static void setUIManagerProperties( Map uiProps )
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
        return getIcon(sPath, true);
    }

    /**
     * get the image icon given the full path to the image.
     */
    public static ImageIcon getIcon(String sPath, boolean failIfNotFound) {
        ImageIcon icon = null;
        if (isStandAlone_)   {
            //System.out.println("spath="+ sPath);
            URL url = ClassLoaderSingleton.getClassLoader().getResource(sPath);
            if (url != null) {
                icon = new ImageIcon( url );
            }
            else if (failIfNotFound) {
                throw new IllegalArgumentException("Invalid file or url path:"+ sPath);
            }
        }
        else {
            //System.out.println("not standalone: spath="+ sPath);
            icon = new ImageIcon(RESOURCE_ROOT + sPath);
        }
        assert (icon != null || !failIfNotFound) : "failed to find image:"+sPath;
        return icon;
    }


    /**
     * Load a buffered image from a file or resource.
     * @return loaded image or null if not found.
     */
    public static BufferedImage getBufferedImage(String path) {

         ImageIcon img = GUIUtil.getIcon(path, false);
         BufferedImage image = null;
         if (img != null && img.getIconWidth() > 0) {
             image = ImageUtil.makeBufferedImage(img.getImage());
         }
         return image;
    }


    /**
     * get a URL given the path to a file.
     */
    public static URL getURL(String sPath) {

        return getURL(sPath, true);
     }

    /**
     * @return a URL given the path to an existing file.
     */
    public static URL getURL(String sPath, boolean failIfNotFound) {

        URL url = null;
        //System.out.println("searching for url path=" + sPath);
        try {
            if (isStandAlone_)   {
                url = ClassLoaderSingleton.getClassLoader().getResource(sPath);
            }
            else {
                String spec = "file:" + RESOURCE_ROOT + sPath;
                url = new URL(spec);
            }

            assert (url != null || !failIfNotFound):
                "failed to create url for  "+sPath + " standAlone="+isStandAlone_ +" resourceRoot_="+ RESOURCE_ROOT;
        } catch (MalformedURLException e) {
            System.out.println( sPath + " is not a valid resource or URL" );
            e.printStackTrace();
        }
        return url;
     }


     /**
     * displays a splash screen while the application is busy starting up.
     */
    public static JWindow showSplashScreen( int waitMillis )
    {
        return showSplashScreen(waitMillis, "config/images/splash.gif");
    }


    /**
     * displays a splash screen while the application is busy starting up.
     */
    public static JWindow showSplashScreen( int waitMillis, String imagePath)
    {
        // show a splash screen initially (if we are running through web start)
        // so the user knows something is happenning
        ImageIcon splash;
        URL url = ClassLoaderSingleton.getClassLoader().getResource(imagePath);
        if (url == null) // then use a default
            splash = new ImageIcon( new BufferedImage( 300, 300, BufferedImage.TYPE_INT_RGB ) );
        else
            splash = new ImageIcon( url );

        JWindow w = new SplashScreen( splash, null, waitMillis );
        return w;
    }


    /**
     * this method is useful for turning Applets into applications.
     * @param applet the applet to show
     * @param title title to appear in the titlebar of the application frame.
     */
    public static JFrame showApplet( final JApplet applet, final String title)
    {
        isStandAlone_ = false;
        assert !isStandAlone_: "You must be running as an application if you are calling this method.";

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        // follows pattern from http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
        ////SwingUtilities.invokeLater(new Runnable() {
        ////    public void run() {
                return createAndShowAppletFrame(applet, title);
        ////    }
        ////});
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the event-dispatching thread.
     */
    private static  JFrame createAndShowAppletFrame(JApplet applet, String title) {
       JFrame baseFrame = new JFrame();

       baseFrame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
       baseFrame.addWindowListener( new WindowAdapter()
       {
            @Override
           public void windowClosed( WindowEvent e )
           {
               System.exit( 0 );
           }
       } );
       baseFrame.setTitle( title );
       baseFrame.setContentPane( applet.getContentPane() );

       Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
       int height = (int) (2.0 * d.getHeight()  / 3.0) ;
       int width = (int) Math.min(height * 1.5, 2.0 * d.getWidth() / 3);
       baseFrame.setLocation( (d.width - width) >> 2, (d.height - height) >> 2 );

       baseFrame.setVisible( true );
       Dimension dim = applet.getSize();
       //System.out.println("baseFrame get size="+ dim);
       if (dim.width == 0) {
           baseFrame.setSize( width, height);
       } else {
           baseFrame.setSize( applet.getSize() );
       }

       // call the applet's init method
       applet.init();

       baseFrame.repaint();
       baseFrame.setVisible( true );

       // call the applet's start method
       applet.start();
       return baseFrame;
   }

    /**
     *
     * @param comp ui componetn to get base frame for.
     * @return the base frame if there is one (else null).
     */
    public static JFrame getBaseFrame(JComponent comp)
    {
        Component parent = comp.getParent();
        while (parent != null && !(parent instanceof JFrame)) {
            parent = parent.getParent();
        }
        if (parent == null) {
            return null;
        }
        else {
            return (JFrame) parent;
        }
    }

    /**
     * gets a color from a hexadecimal string like "AABBCC"
     * or "AABBCCDD". The DD in this case gives the opacity value
     * if only rgb are given, then FF is asumed for the opacity
     * @param sColor color to convert
     * @param defaultColor  color to use if sColor has a problem
     * @return the color object
     */
    public static Color getColorFromHTMLColor(String sColor, Color defaultColor)
    {
        if (sColor==null || sColor.length()<6 || sColor.length()>8)
            return defaultColor;

        long intColor;
        try {
            intColor = Long.decode("0x" + sColor);
        }
        catch (NumberFormatException e) {
            System.out.println("bad color format: "+sColor);
            System.out.println(e.getMessage());
            return defaultColor;
        }
        int blue =  (int)(intColor % 256);
        int green = (int)((intColor >> 8 ) % 256);
        int red = (int)((intColor >> 16 ) % 256);
        int opacity = 255;
        if (sColor.length()>6) {
           opacity = (int)(intColor >> 24);
        }
        return new Color(red, green, blue, opacity);
    }

    /**
     * returns a hexadecimal string representation of the color - eg "AABBCC" or "DDAABBCC"
     * The DD in this case gives the opacity value
     */
    public static String getHTMLColorFromColor(Color color)
    {
        int intval = color.getRGB();
        intval -= 0xFF000000;
        //System.out.println("NodePres getString from Color = "+Integer.toHexString(intval).toUpperCase());
        return '#'+Integer.toHexString(intval).toUpperCase();
    }

    public static void paintComponentWithTexture(ImageIcon texture, Component c, Graphics g)
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



    public static void saveSnapshot(JComponent component, String directory) {

        JFileChooser chooser = getFileChooser();
        chooser.setCurrentDirectory( new File( directory ) );
        int state = chooser.showSaveDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION ) {

            BufferedImage img = (BufferedImage) component.createImage(component.getWidth(), component.getHeight());
            component.paint(img.createGraphics());

            ImageUtil.saveAsImage(file.getAbsolutePath(), img, ImageUtil.ImageType.PNG);
        }
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
                System.out.println( "jnlp BasicService not available: "+ncde.getMessage() );
                return null;
            }
        }
        return basicService_;
    }
}
