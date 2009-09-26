package com.becker.puzzle.adventure.ui;

import com.becker.puzzle.adventure.Scene;
import com.becker.puzzle.adventure.Story;
import javax.swing.*;
import java.awt.*;

/**
 * This panel is responsible for drawing the Text describing the current scene.
 * @author Barry Becker 
 */
public class StoryPanel extends JComponent {


    private Story story_;

    // rendering attributes
    private static final Color WALL_COLOR = new Color( 80, 0, 150 );
    private static final Color PATH_COLOR = new Color( 255, 220, 50);

    private static final Color TEXT_COLOR = new Color( 250, 0, 100 );
    private static final Color BG_COLOR = new Color( 225, 240, 250 );

    private Font TEXT_FONT = new Font("Courier", Font.PLAIN, 12);

    TextArea textArea_;

    public StoryPanel(Story story) {
        
        this.setLayout(new BorderLayout());
        story_ = story;

        textArea_ = new TextArea();
        textArea_.setFont(TEXT_FONT);

        textArea_.setText(story_.getCurrentScene().getText());
        
        this.add(textArea_, BorderLayout.CENTER);
    }


    /**
     * Render the Environment on the screen.
     */
    @Override
    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );

        //Graphics2D g2 = (Graphics2D) g;

        textArea_.setText(story_.getCurrentScene().getText());
    }


}
