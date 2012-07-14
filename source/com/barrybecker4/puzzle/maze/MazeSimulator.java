/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze;

import com.barrybecker4.ui.application.ApplicationApplet;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A maze generator and solver
 *@author Barry Becker
 */
public class MazeSimulator extends ApplicationApplet
                           implements ActionListener {

    MazePanel mazePanel_;

    protected Dimension oldSize_;

    private TopControlPanel controlPanel_;

    /** constructor */
    public MazeSimulator() {}

    /**
     * Build the user interface with parameter input controls at the top.
     */
    @Override
    protected JPanel createMainPanel() {
        mazePanel_ = createMazePanel();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        controlPanel_ = new TopControlPanel(this);

        JPanel mazePanel = new JPanel( new BorderLayout() );
        mazePanel.add( mazePanel_, BorderLayout.CENTER );
        mazePanel.setBorder(
            BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ),
                BorderFactory.createCompoundBorder( BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) )
            )
        );
        mainPanel.add( controlPanel_, BorderLayout.NORTH );
        mainPanel.add( mazePanel, BorderLayout.CENTER );
        return mainPanel;
    }

    private MazePanel createMazePanel() {
        final MazePanel mazePanel = new MazePanel();

        mazePanel.addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized( ComponentEvent ce )  {
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
     * called when a button is pressed.
     */
    public void actionPerformed( ActionEvent e )  {

        Object source = e.getSource();

        if ( controlPanel_.isRegenerateButton(source)) {
            regenerate();
        }
        if ( controlPanel_.isSolveButton(source)) {
            solve();
        }
    }

    /**
     * regenerate the maze based on the current UI parameter settings
     * and current size of the panel.
     */
    public void regenerate() {
        if ( controlPanel_ == null )   {
            return; // not initialized yet
        }

        int thickness = controlPanel_.getThickness();

        double forwardP = controlPanel_.getForwardPropability();
        double leftP = controlPanel_.getLeftProbability();
        double rightP = controlPanel_.getRightProbability();

        double sum = forwardP + leftP + rightP;
        mazePanel_.setAnimationSpeed(controlPanel_.getAnimationSpeed());
        mazePanel_.setThickness(thickness);
        mazePanel_.generate(thickness, forwardP / sum, leftP / sum, rightP / sum );
    }

    public void solve() {
        mazePanel_.setAnimationSpeed(controlPanel_.getAnimationSpeed());
        mazePanel_.solve();
    }


    @Override
    public void start() {
        regenerate();
    }

    //------ Main method --------------------------------------------------------

    public static void main( String[] args ) {
        MazeSimulator simulator = new MazeSimulator();
        GUIUtil.showApplet( simulator, "Maze Generator");
    }
}