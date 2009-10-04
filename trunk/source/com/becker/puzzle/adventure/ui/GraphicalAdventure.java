package com.becker.puzzle.adventure.ui;

import com.becker.puzzle.adventure.*;
import com.becker.ui.ApplicationApplet;
import com.becker.ui.GUIUtil;
import java.awt.BorderLayout;
import java.awt.Dimension;
import org.w3c.dom.*;

import java.io.*;
import javax.swing.JPanel;

/**
 * Run your own adventure story.
 * This version runs the adventure in Graphical mode (with images and sound).
 * @see Adventure
 * @see TextAdventure
 *
 * @author Barry Becker
 */
public final class GraphicalAdventure extends ApplicationApplet
                                                              implements SceneChangeListener {

    private Story story_;
    private StoryPanel storyPanel_;
    private ChoicePanel choicePanel_ = null;


    public GraphicalAdventure(Story story)
    {
        GUIUtil.setStandAlone(false);
        story_ = story;
    }


    /**
     * Build the user interface with parameter input controls at the top.
     */
    protected JPanel createMainPanel()
    {
        storyPanel_ =  new StoryPanel(story_);

        // setup for initial scene
        choicePanel_ = new ChoicePanel(story_.getCurrentScene().getChoices());
        story_.getCurrentScene().playSound();

        choicePanel_.addSceneChangeListener(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        mainPanel.add( storyPanel_, BorderLayout.CENTER );
        mainPanel.add( choicePanel_, BorderLayout.SOUTH );

        return mainPanel;
    }


    /**
     * called when a button is pressed.
     */
    public void sceneChanged( int selectedChoiceIndex )
    {
        story_.advanceScene(selectedChoiceIndex);
        System.out.println("request to repaint sent");
        storyPanel_.invalidate();
        storyPanel_.repaint();
        choicePanel_.setChoices(story_.getCurrentScene().getChoices());
        story_.getCurrentScene().playSound();
    }

    @Override
    public void start()
    {
    }

    
    @Override
    public Dimension getSize() {
        return new Dimension(1000, 700);
    }

    /**
     * Graphical Adventure application entrance point.
     */
    public static void main( String[] args ) throws IOException {

        Document document =Story.retrieveStoryDocument(args);
       
        Story story = new Story(document);

        GraphicalAdventure adventure = new GraphicalAdventure(story);
        GUIUtil.showApplet( adventure, story.getTitle()); 
    }
}

