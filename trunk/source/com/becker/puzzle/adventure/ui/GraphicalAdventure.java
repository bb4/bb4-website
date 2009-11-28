package com.becker.puzzle.adventure.ui;

import com.becker.common.xml.DomUtil;
import com.becker.puzzle.adventure.Story;
import com.becker.puzzle.adventure.TextAdventure;
import com.becker.puzzle.adventure.ui.editor.StoryEditorDialog;
import com.becker.ui.ApplicationApplet;
import com.becker.ui.GUIUtil;
import com.becker.ui.dialogs.PasswordDialog;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

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
    private boolean storyEdited_ = false;


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
    @Override
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
     * Allow user to edit the current story if they know the password.
     */
    public void editStory() {
        // show password dialog.
        PasswordDialog pwDlg = new PasswordDialog("ludlow");
        boolean canceled = false; // pwDlg.showDialog();
        if ( canceled ) return;

        StoryEditorDialog storyEditor = new StoryEditorDialog(story_);
        boolean editingCanceled = storyEditor.showDialog();
        if (!editingCanceled) {
            System.out.println("done editing");
            // show the edited version.
            story_.initializeFrom(storyEditor.getEditedStory());
            story_.resetToFirstScene();
            setStory(story_);
            storyEdited_ = true;
        }
    }

    public boolean isStoryEdited() {
        return storyEdited_;
    }

    public void loadStory(File file) {
         Story story = new Story(importStoryDocument(file));
         setStory(story);
    }

    /**
     * @param file name of the xml document to import.
     * @return the imported story xml document.
     */
    private static Document importStoryDocument(File file) {
        Document document = null;
        // first try to load it as a file. If that doesn't work, try as a URL.
        if (file.exists()) {
            document = DomUtil.parseXMLFile(file);
        }
        return document;
    }

    /**
     * @param fPath fully qualified filename and path to save to.
     */
    public void saveStory(String fPath) {
        getStory().saveStoryDocument(fPath);
        storyEdited_ = false;
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

