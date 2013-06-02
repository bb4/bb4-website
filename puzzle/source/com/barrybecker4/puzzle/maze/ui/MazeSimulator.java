/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze.ui;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.puzzle.maze.MazeController;
import com.barrybecker4.ui.application.ApplicationApplet;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A maze generator and solver application.
 * @author Barry Becker
 */
public class MazeSimulator extends JApplet {

    private TopControlPanel topControls;
    private Dimension oldSize;

    /** constructor */
    public MazeSimulator() {
        MathUtil.RANDOM.setSeed(1);
    }

    /**
     * Create and initialize the puzzle.
     * (init required for applet)
     */
    @Override
    public void init() {
        final MazePanel mazePanel = new MazePanel();
        MazeController controller = new MazeController(mazePanel);
        topControls = new TopControlPanel(controller);

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(topControls, BorderLayout.NORTH);
        panel.add(mazePanel, BorderLayout.CENTER);
        getContentPane().add(panel);

        getContentPane().addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized( ComponentEvent ce )  {

                // only resize if the dimensions have changed
                Dimension newSize = mazePanel.getSize();
                boolean changedSize = oldSize== null ||
                        oldSize.getWidth() != newSize.getWidth() ||
                        oldSize.getHeight() != newSize.getHeight();
                if ( changedSize ) {
                    oldSize = newSize;
                    if (newSize.getWidth() > 0) {
                        topControls.regenerate();
                    }
                }
            }
        });
    }


    //------ Main method --------------------------------------------------------

    public static void main( String[] args ) {
        MazeSimulator simulator = new MazeSimulator();
        GUIUtil.showApplet( simulator, "Maze Generator");
    }
}