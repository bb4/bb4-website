package com.becker.maze;

import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MazeSimulator extends JApplet implements ActionListener
{

    MazeGenerator maze_ = null;

    ResizableAppletPanel resizablePanel_ = null;

    // the passage thickness in pixels
    protected static final int PASSAGE_THICKNESS = 60;

    protected JTextField thicknessField_ = null;

    // ui for entering the direction probablilities
    protected JTextField forwardProbField_ = null;
    protected JTextField leftProbField_ = null;
    protected JTextField rightProbField_ = null;

    protected GradientButton regenerateButton_ = null;
    protected Dimension oldSize = null;

    public boolean isStandalone = false;
    // the frame is only created if we run as an application
    protected JFrame baseFrame_ = null;

    // constructor
    public MazeSimulator()
    {

        maze_ = new MazeGenerator();
        //commonInit();
    }

    // constructor
    public void commonInit()
    {

        GUIUtil.setCustomLookAndFeel();

        System.out.println( "creating maze simulator" );
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );

        setFont( new Font( "Serif", Font.PLAIN, 14 ) );

        JPanel mainPanel = createMainPanel( maze_ );

        resizablePanel_ = new ResizableAppletPanel( mainPanel );
        this.getContentPane().add( resizablePanel_ );

        maze_.addComponentListener( new ComponentAdapter()
        {
            public void componentResized( ComponentEvent ce )
            {
                // only resize if the dimensions have changed
                Dimension newSize = maze_.getSize();
                if ( oldSize == null ||
                        oldSize.getWidth() != newSize.getWidth() ||
                        oldSize.getHeight() != newSize.getHeight() ) {
                    //System.out.println( "oldSize=" + oldSize + "  maze_.getSize()=" + maze_.getSize() );
                    oldSize = newSize;
                    resized();
                }
            }
        } );
    }

    /**
     *  Overrides the applet init() method
     */
    public void init()
    {
        //System.out.println("in maze simulator init");
        commonInit();
    }

    private JPanel createMainPanel( MazeGenerator maze )
    {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel controlsPanel = new JPanel();
        JLabel thicknessLabel = new JLabel( "Passage Thickness: " );
        thicknessField_ = createTextField( Integer.toString( PASSAGE_THICKNESS ) );

        JLabel forwardLabel = new JLabel( "Forward:" );
        JLabel leftLabel = new JLabel( "Left:" );
        JLabel rightLabel = new JLabel( "Right:" );
        forwardProbField_ = createTextField( Double.toString( .34 ) );
        leftProbField_ = createTextField( Double.toString( .33 ) );
        rightProbField_ = createTextField( Double.toString( .33 ) );

        controlsPanel.add( thicknessLabel );
        controlsPanel.add( thicknessField_ );
        controlsPanel.add( Box.createHorizontalStrut( 30 ) );

        controlsPanel.add( forwardLabel );
        controlsPanel.add( forwardProbField_ );
        controlsPanel.add( leftLabel );
        controlsPanel.add( leftProbField_ );
        controlsPanel.add( rightLabel );
        controlsPanel.add( rightProbField_ );

        regenerateButton_ = new GradientButton( "Generate" );
        controlsPanel.add( regenerateButton_ );
        regenerateButton_.addActionListener( this );

        JPanel mazePanel = new JPanel( new BorderLayout() );
        mazePanel.add( maze, BorderLayout.CENTER );
        mazePanel.setBorder(
                BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ),
                        BorderFactory.createCompoundBorder( BorderFactory.createLoweredBevelBorder(),
                                BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) )
                )
        );
        mainPanel.add( controlsPanel, BorderLayout.NORTH );
        mainPanel.add( mazePanel, BorderLayout.CENTER );

        return mainPanel;
    }

    private JTextField createTextField( String initialVal )
    {
        JTextField tf = new JTextField( initialVal );
        Dimension mind = new Dimension( 30, 18 );
        tf.setPreferredSize( mind );
        return tf;
    }

    /**
     * called when a button is pressed
     */
    public void actionPerformed( ActionEvent e )
    {

        Object source = e.getSource();

        if ( source == regenerateButton_ ) {
            regenerate();
        }
    }

    /**
     * regenerate the maze based on the current UI parameter settings
     * and current size of the panel
     */
    public void regenerate()
    {
        if ( thicknessField_ == null ) return; // not inited yet

        int thickness = Integer.parseInt( thicknessField_.getText() );

        Double forwardP = new Double( forwardProbField_.getText() );
        Double leftP = new Double( leftProbField_.getText() );
        Double rightP = new Double( rightProbField_.getText() );

        double sum = forwardP.doubleValue() + leftP.doubleValue() + rightP.doubleValue();
        maze_.generate( thickness,
                forwardP.doubleValue() / sum, leftP.doubleValue() / sum, rightP.doubleValue() / sum );
    }

    /**
     * This method allow javascript to resize the applet from the browser.
     */
    public void setSize( int width, int height )
    {
        System.out.println( "in setSize w=" + width + " h=" + height );
        resizablePanel_.setSize( width, height );
        //resized();
    }

    public void resized()
    {
        System.out.println( "in resized. about to call regenerate" );
        regenerate();
    }

    public void start()
    {
        regenerate();
    }

    //------ Main method --------------------------------------------------------
    public static void main( String[] args )
    {

        MazeSimulator simulator = new MazeSimulator();

        GUIUtil.showApplet( simulator, "Maze Generator" );
    }
}