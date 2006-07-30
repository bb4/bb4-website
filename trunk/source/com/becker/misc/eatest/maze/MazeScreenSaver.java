package com.becker.misc.eatest.maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * NOTE to EA: this was adapted some prior work that I did in my free time.
 * This class has a main method so it can be run directly, or as an applet in an html page.
 *
 * maze simulator screen saver.
 * can be run as an applicaiton or an applet.
 */
public class MazeScreenSaver extends JApplet
{
    private MazeGenerator maze_ = null;

    private ResizableAppletPanel resizablePanel_ = null;

    // the passage thickness in pixels
    private static final int PASSAGE_THICKNESS = 30;
    private static final int ANIMATION_SPEED = 10;

    private Dimension oldSize_ = null;

    // the frame is only created if we run as an application
    private JFrame baseFrame_ = null;

    private volatile Thread currentThread_;
    private volatile boolean keepRunning_ = true;


    // constructor
    public MazeScreenSaver()
    {
        maze_ = new MazeGenerator();
        commonInit();
    }

    // constructor
    public void commonInit()
    {

        System.out.println( "creating maze simulator" );
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );

        setFont( new Font( "Serif", Font.PLAIN, 14 ) );

        JPanel mainPanel = createMainPanel( maze_ );

        resizablePanel_ = new ResizableAppletPanel( mainPanel );
        this.getContentPane().add( resizablePanel_ );

        maze_.addComponentListener( new ComponentAdapter()
        {
            public synchronized void componentResized( ComponentEvent ce )
            {
                // only resize if the dimensions have changed
                Dimension newSize = maze_.getSize();
                if ( oldSize_ == null ||
                        oldSize_.getWidth() != newSize.getWidth() ||
                        oldSize_.getHeight() != newSize.getHeight() ) {
                    //System.out.println( "oldSize=" + oldSize + "  maze_.getSize()=" + maze_.getSize() );
                    oldSize_ = newSize;
                    setSize((int) newSize.getWidth(), (int) newSize.getHeight());
                    //resized();
                }
            }
        } );
        System.out.println("done creating");
    }

    private JPanel createMainPanel( MazeGenerator maze )
    {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel mazePanel = new JPanel( new BorderLayout() );
        mazePanel.add( maze, BorderLayout.CENTER );
        mainPanel.add( mazePanel, BorderLayout.CENTER );

        return mainPanel;
    }

    /**
     * regenerate the maze based on the current UI parameter settings
     * and current size of the panel
     */
    public void regenerate()
    {
        int thickness = PASSAGE_THICKNESS;

        double forwardP = 0.34;
        double leftP = 0.33;
        double rightP = 0.33;

        double sum = forwardP + leftP + rightP;
        maze_.generate( thickness, ANIMATION_SPEED,
                forwardP / sum, leftP / sum, rightP / sum );
    }

    public void solve()
    {
        maze_.solve(ANIMATION_SPEED);
    }


    /**
     *  Overrides the applet init() method
     */
    public void init()
    {
        resized();
    }

    public void start()
    {
        resized();
    }

    public synchronized void stop()
    {
        keepRunning_ = false;
    }

    /**
     * This method allows javascript to resize the applet from the browser.
     * @@ not resizing in applet window.
     */
    public synchronized void setSize( int width, int height )
    {
        resizablePanel_.setSize( width, height );
        keepRunning_ = false;
        resized();
    }

    public synchronized void resized()
    {
        if (currentThread_ != null) {
            //currentThread_.interrupt();
            currentThread_ = null;
        }
        // stop the previous thread and begin a new one
        currentThread_ = new Thread(new Runnable() {
            public void run() {
                keepRunning_ = true;
                while (keepRunning_) {
                    generateAndSolve();
                }
            }
        });

        currentThread_.start();
    }

    private synchronized void generateAndSolve() {
        //System.out.println("regenerating");
        regenerate();
        //System.out.println("solving");
        solve();
    }

    /**
     * this method is useful for turning Applets into applications
     * @return the base frame which holds the applet content
     */
    public static JFrame showApplet( JApplet applet, String title )
    {

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
        baseFrame.setLocation( (d.width - baseFrame.getSize().width) >> 2,
                               (d.height - baseFrame.getSize().height) >> 2 );
        int height = (int) d.getHeight() >> 1 ;
        int width = (int) Math.min(height * 1.5, d.getWidth() / 2);


        baseFrame.setVisible( true );
        baseFrame.setSize( width, height);


        // call the applet's init method
        applet.init();

        // call the applet's start method
        applet.start();

        return baseFrame;
    }



    //------ Main method --------------------------------------------------------
    public static void main( String[] args )
    {
        MazeScreenSaver screenSaver = new MazeScreenSaver();
        showApplet( screenSaver, "Maze Screen Saver" );
    }
}