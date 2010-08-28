package com.becker.ui;

import com.becker.common.*;
import com.becker.common.util.FileUtil;
import com.becker.common.util.ImageUtil;
import com.becker.ui.themes.BarryTheme;
import com.sun.java.swing.plaf.windows.*;

import javax.jnlp.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;


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


    // webstart services
    private static BasicService basicService_ = null;



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
     *  Set the ui looki and feel to my very own.
     */
    public static void setCustomLookAndFeel()
    {
        BarryTheme theme = new BarryTheme();
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

            //UIManager.setLookAndFeel( new WindowsLookAndFeel() );

            // turn on auditory cues.
            // @@ can't do this under linux until I upgrade java or get the right soundcard driver.
            UIManager.put("AuditoryCues.playList", UIManager.get("AuditoryCues.allAuditoryCues"));

            theme.setUIManagerProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @return the image icon given the full path to the image.
     */
    public static ImageIcon getIcon(String sPath) {
        return getIcon(sPath, true);
    }

    /**
     * @return the image icon given the full path to the image.
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
     * @return a URL given the path to a file.
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
    public static JWindow showSplashScreen( int waitMillis ) {
        return showSplashScreen(waitMillis, "config/images/splash.gif");
    }


    /**
     * Displays a splash screen while the application is busy starting up.
     * @return the window containing th esplash screen image.
     */
    public static JWindow showSplashScreen( int waitMillis, String imagePath) {
        // show a splash screen initially (if we are running through web start)
        // so the user knows something is happening
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
     * This method is useful for turning Applets into applications.
     * @param applet the applet to show
     * @param title title to appear in the titlebar of the application frame.
     * @return frame containing the applet.
     */
    public static JFrame showApplet( final JApplet applet, final String title) {
        isStandAlone_ = false;

        return createAndShowAppletFrame(applet, title);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the event-dispatching thread.
     * @return frame containing the applet.
     */
    private static JFrame createAndShowAppletFrame(JApplet applet, String title) {
        JFrame baseFrame = new JFrame();

        baseFrame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        baseFrame.addWindowListener( new WindowAdapter() {
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
     * Paint with specified texture.
     */
    public static void paintComponentWithTexture(ImageIcon texture, Component c, Graphics g) {
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
    public static String getFileSuffix(File f) {
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
