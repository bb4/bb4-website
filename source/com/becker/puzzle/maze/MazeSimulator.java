package com.becker.puzzle.maze;

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
    protected static final int ANIMATION_SPEED = 10;

    protected NumberInput thicknessField_ = null;

    // ui for entering the direction probablilities
    protected NumberInput forwardProbField_ = null;
    protected NumberInput leftProbField_ = null;
    protected NumberInput rightProbField_ = null;
    protected NumberInput animationSpeedField_ = null;

    protected GradientButton regenerateButton_ = null;
    protected GradientButton solveButton_ = null;

    protected Dimension oldSize_ = null;

    public boolean isStandalone_ = false;
    // the frame is only created if we run as an application
    protected JFrame baseFrame_ = null;

    // constructor
    public MazeSimulator()
    {
        maze_ = new MazeGenerator();
        commonInit();
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
                if ( oldSize_ == null ||
                        oldSize_.getWidth() != newSize.getWidth() ||
                        oldSize_.getHeight() != newSize.getHeight() ) {
                    //System.out.println( "oldSize=" + oldSize + "  maze_.getSize()=" + maze_.getSize() );
                    oldSize_ = newSize;
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
        //commonInit();
        resized();
    }

    private JPanel createMainPanel( MazeGenerator maze )
    {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel controlsPanel = new JPanel();
        thicknessField_ = new NumberInput("Thickness", PASSAGE_THICKNESS, "The passage thickness", 2, 200, true);
        animationSpeedField_ = new NumberInput("Speed", ANIMATION_SPEED, "The animation speed (large number is slow).", 1, 200, true);

        forwardProbField_ = new NumberInput("Forward", 0.34, "The probability of moving straight forward", 0, 1.0, false);
        leftProbField_ = new NumberInput("Left", 0.33, "The probability of moving left", 0, 1.0, false);
        rightProbField_ = new NumberInput("Right", 0.33, "The probability of moving right", 0, 1.0, false);

        controlsPanel.add( thicknessField_ );
        controlsPanel.add( animationSpeedField_ );
        controlsPanel.add( Box.createHorizontalStrut( 15 ) );
        controlsPanel.add( forwardProbField_ );
        controlsPanel.add( leftProbField_ );
        controlsPanel.add( rightProbField_ );

        regenerateButton_ = new GradientButton( "Generate" );
        regenerateButton_.addActionListener( this );
        controlsPanel.add( regenerateButton_ );

        solveButton_ = new GradientButton( "Solve" );
        solveButton_.addActionListener( this );
        controlsPanel.add( solveButton_ );

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


    /**
     * called when a button is pressed
     */
    public void actionPerformed( ActionEvent e )
    {

        Object source = e.getSource();

        if ( source.hashCode() == regenerateButton_.hashCode() ) {
            regenerate();
        }
        if ( source == solveButton_ ) {
            solve();
        }

    }

    /**
     * regenerate the maze based on the current UI parameter settings
     * and current size of the panel
     */
    public void regenerate()
    {
        if ( thicknessField_ == null )   {
            return; // not inited yet
        }

        int thickness = thicknessField_.getIntValue();

        double forwardP =forwardProbField_.getValue();
        double leftP = leftProbField_.getValue();
        double rightP = rightProbField_.getValue();

        double sum = forwardP + leftP + rightP;
        maze_.generate( thickness, getAnimationSpeed(),
                forwardP / sum, leftP / sum, rightP / sum );
    }

    public void solve()
    {
        maze_.solve(getAnimationSpeed());
    }

    private int getAnimationSpeed()
    {
        return animationSpeedField_.getIntValue();
    }

    /**
     * This method allow javascript to resize the applet from the browser.
     */
    public void setSize( int width, int height )
    {
        resizablePanel_.setSize( width, height );
        resized();
    }

    public void resized()
    {
        regenerate();
    }

    public void start()
    {
        resized();
    }

    //------ Main method --------------------------------------------------------
    public static void main( String[] args )
    {

        MazeSimulator simulator = new MazeSimulator();

        GUIUtil.showApplet( simulator, "Maze Generator" );
    }
}