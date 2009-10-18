package com.becker.puzzle.maze;

import com.becker.ui.components.NumberInput;
import com.becker.ui.components.GradientButton;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A maze generator and solver
 *@author Barry Becker
 */
public class MazeSimulator extends ApplicationApplet implements ActionListener
{
    MazePanel mazePanel_;

    // the passage thickness in pixels
    protected static final int PASSAGE_THICKNESS = 40;
    protected static final int INITIAL_ANIMATION_SPEED = 20;

    protected NumberInput thicknessField_ = null;

    // ui for entering the direction probablilities.
    protected NumberInput forwardProbField_ = null;
    protected NumberInput leftProbField_ = null;
    protected NumberInput rightProbField_ = null;
    protected NumberInput animationSpeedField_ = null;

    protected GradientButton regenerateButton_ = null;
    protected GradientButton solveButton_ = null;

    protected Dimension oldSize_ = null;

    // constructor
    public MazeSimulator()
    {}

    /**
     * Build the user interface with parameter input controls at the top.
     */
    protected JPanel createMainPanel()
    {
        mazePanel_ = createMazePanel();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel controlsPanel = createControlsPanel();

        JPanel mazePanel = new JPanel( new BorderLayout() );
        mazePanel.add( mazePanel_, BorderLayout.CENTER );
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

    private MazePanel createMazePanel() {
        final MazePanel mazePanel = new MazePanel();
        mazePanel.addComponentListener( new ComponentAdapter()
        {
            public void componentResized( ComponentEvent ce )
            {
                // only resize if the dimensions have changed
                Dimension newSize = mazePanel.getSize();
                boolean changedSize = oldSize_ == null ||
                        oldSize_.getWidth() != newSize.getWidth() ||
                        oldSize_.getHeight() != newSize.getHeight();
                if ( changedSize ) {
                    oldSize_ = newSize;
                    if (newSize.getWidth() > 0) {
                        regenerate();
                    }
                }
            }
        } );
        return mazePanel;
    }

    /**
     * @return panel containing the maze controls to show at the top.
     */
    private JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel();
        thicknessField_ = new NumberInput("Thickness", PASSAGE_THICKNESS,
                                          "The passage thickness", 2, 200, true);
        animationSpeedField_ = new NumberInput("Speed", INITIAL_ANIMATION_SPEED,
                                               "The animation speed (large number is slow).", 1, 100, true);

        forwardProbField_ = new NumberInput("Forward", 0.34,
                                            "The probability of moving straight forward", 0, 1.0, false);
        leftProbField_ = new NumberInput("Left", 0.33,
                                         "The probability of moving left", 0, 1.0, false);
        rightProbField_ = new NumberInput("Right", 0.33,
                                          "The probability of moving right", 0, 1.0, false);

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
        return controlsPanel;
    }

    /**
     * called when a button is pressed.
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
     * and current size of the panel.
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
        mazePanel_.setAnimationSpeed( getAnimationSpeed());
        mazePanel_.setThickness(thickness);
        mazePanel_.generate(thickness, forwardP / sum, leftP / sum, rightP / sum );
    }

    public void solve()
    {
        mazePanel_.setAnimationSpeed(getAnimationSpeed());
        mazePanel_.solve();
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
        super.setSize( width, height );
        //System.out.println("setSize: call regen ("+ width+", "+height + ")");
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
        GUIUtil.showApplet( simulator, "Maze Generator");
    }
}