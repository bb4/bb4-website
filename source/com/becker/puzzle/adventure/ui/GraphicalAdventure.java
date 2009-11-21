package com.becker.puzzle.adventure.ui;

import com.becker.puzzle.adventure.ui.editor.StoryEditorDialog;
import com.becker.ui.dialogs.PasswordDialog;
import com.becker.puzzle.adventure.*;
import com.becker.ui.ApplicationApplet;
import com.becker.ui.GUIUtil;
import java.awt.BorderLayout;
import java.awt.Dimension;
import org.w3c.dom.*;

import java.io.*;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 * Run your own adventure story.
 * This version runs the adventure in Graphical mode (with images and sound).
 * @see TextAdventure
 *
 * @author Barry Becker
 */
public final class GraphicalAdventure extends ApplicationApplet
                                      implements SceneChangeListener {

    private Story story_;
    private ChoicePanel choicePanel_ = null;
    private JPanel mainPanel_;

    /**
     * Constructor.
     * @param story initial story to show.
     */
    public GraphicalAdventure(Story story)
    {
        GUIUtil.setStandAlone(false);
        
        story_ = story;
        JFrame frame = GUIUtil.showApplet( this, story.getTitle());

        StoryMenu storyMenu = new StoryMenu(this);

        JMenuBar menubar = new JMenuBar();
        menubar.add(storyMenu);

        frame.setJMenuBar(menubar);
        frame.invalidate();
        frame.validate();
    }

    /**
     * Build the user interface with parameter input controls at the top.
     */
    protected JPanel createMainPanel()
    {
        mainPanel_ = new JPanel();
        mainPanel_.setLayout( new BorderLayout() );

        setStory(story_);

        return mainPanel_;
    }

    /**
     * If a new story is loaded, call this method to update the ui.
     * @param story new story to present.
     */
    public void setStory(Story story) {
        story_ = story;
        System.out.println("set new story");
        mainPanel_.removeAll();

        StoryPanel storyPanel = new StoryPanel(story_);

        // setup for initial scene
        choicePanel_ = new ChoicePanel(story_.getCurrentScene().getChoices());
        story_.getCurrentScene().playSound();

        choicePanel_.addSceneChangeListener(this);

        mainPanel_.add( storyPanel, BorderLayout.CENTER );
        mainPanel_.add( choicePanel_, BorderLayout.SOUTH );
        refresh();
    }


    public Story getStory() {
        return story_;
    }

    void refresh()
    {
        mainPanel_.invalidate();
        mainPanel_.validate();
        mainPanel_.repaint();
    }

    /**
     * Allow user to edit the current story if they know the passord.
     */
    public void editStory() {
        // show password dialog.
        PasswordDialog pwDlg = new PasswordDialog("ludlow");
        boolean canceled = false; // pwDlg.showDialog();

        if ( !canceled ) {
            StoryEditorDialog storyEditor = new StoryEditorDialog(story_);
            boolean editingCanceled = storyEditor.showDialog();
            if (!editingCanceled) {
                System.out.println("done editing");
                // show the edited version.
                story_.initializeFrom(storyEditor.getEditedStory());
                story_.resetToFirstScene();
                setStory(story_);
            }
        }
    }

    /**
     * called when a button is pressed.
     */
    public void sceneChanged( int selectedChoiceIndex )
    {
        story_.advanceScene(selectedChoiceIndex);
        refresh();
        choicePanel_.setChoices(story_.getCurrentScene().getChoices());
        story_.getCurrentScene().playSound();
    }

    
    @Override
    public Dimension getSize() {
        return new Dimension(1000, 700);
    }

    /**
     * Graphical Adventure application entrance point.
     */
    public static void main( String[] args ) throws IOException {

        Document document = Story.importStoryDocument(args);
       
        Story story = new Story(document);

        new GraphicalAdventure(story);
    }
}

