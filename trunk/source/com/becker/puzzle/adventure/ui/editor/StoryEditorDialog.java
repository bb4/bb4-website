package com.becker.puzzle.adventure.ui.editor;

import com.becker.ui.components.GradientButton;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import com.becker.puzzle.adventure.Story;
import com.becker.puzzle.adventure.Scene;
import com.becker.ui.dialogs.AbstractDialog;
import com.becker.ui.table.TableButtonListener;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author Barry Becker
 */
public class StoryEditorDialog extends AbstractDialog
                                                  implements ActionListener, TableButtonListener {

    /** The story to edit */
    private Story story_;

    /** click this when done editing the scene. */
    private  GradientButton okButton_ = new GradientButton();

    private SceneEditorPanel sceneEditor;

    private static final Font INSTRUCTION_FONT = new Font("Sans Serif", Font.PLAIN, 10);

    private List<Scene>  parentScenes_;
    private ChildTableModel  childTableModel_;

    
    /**
     * Constructor
     * @param story creates a copy of this in case we cancel.
     */
    public StoryEditorDialog(Story story) {

        story_ = new Story(story);

        this.setResizable(true);
        setTitle("Story Editor");
        this.setModal( true );
        showContent();
    }


    @Override
    protected JComponent createDialogContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel editingPane = createEditingPane();
        JLabel title = new JLabel("Navigate through the scene heirarchy and change values for scenes.");
        title.setBorder(BorderFactory.createEmptyBorder(5, 4, 20, 4));
        title.setFont(INSTRUCTION_FONT);
    
        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(editingPane, BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
      
        return mainPanel;
    }

    /**
     * Parent table on top
     * Scene editor in the middle
     * Child options on the bottom
     * @return the panel that holds the story editor controls
     */
    private JPanel createEditingPane() {
        JPanel editingPane = new JPanel(new BorderLayout());

        editingPane.add(createParentTable(), BorderLayout.NORTH);
        editingPane.add(createSceneEditingPanel(), BorderLayout.CENTER);
        editingPane.add(createButtonsPanel(), BorderLayout.SOUTH);

        return editingPane;
    }

    private JComponent createParentTable() {
        JPanel parentContainer = new JPanel(new BorderLayout());
        parentScenes_ = story_.getParentScenes();
        ParentTable parentTable = new ParentTable(parentScenes_, this);

        JPanel tableHolder = new JPanel();
        tableHolder.setMaximumSize(new Dimension(500, 300));
        parentContainer.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Parent Scenes" ));

        parentContainer.add(new JScrollPane(parentTable.getTable()), BorderLayout.WEST);

        parentContainer.setPreferredSize(new Dimension(SceneEditorPanel.WIDTH, 120));
        return parentContainer;
    }

    private JPanel createSceneEditingPanel() {
        JPanel container = new JPanel(new BorderLayout());

        sceneEditor = new SceneEditorPanel(story_.getCurrentScene());

        container.add(sceneEditor, BorderLayout.CENTER);
        container.add(createChildTable(), BorderLayout.SOUTH);

        return container;
    }

    private JComponent createChildTable() {
        JPanel childContainer = new JPanel(new BorderLayout());

         ChildTable childTable = new ChildTable(story_.getCurrentScene().getChoices(), this);
         childTableModel_ = childTable.getChildTableModel();

        childContainer.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),"Choices (to navigate to child scenes)" ) );

        childContainer.add(new JScrollPane(childTable.getTable()), BorderLayout.CENTER);

        childContainer.setPreferredSize(new Dimension(SceneEditorPanel.WIDTH, 200));
        return childContainer;
    }

    /**
     * Create the buttons that go at the botton ( eg OK, Cancel, ...)
     * @return ok cancel panel.
     */
    JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( okButton_, "OK", "Save your edits and see the changes in the story. " );
        initBottomButton( cancelButton_, "Cancel", "Go back to the story without saving your edits." );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }


    @Override
    public void actionPerformed( ActionEvent e )
    {
        super.actionPerformed(e);
        Object source = e.getSource();

        if ( source == okButton_ ) {      
            ok();
        }
    }

    /**
     *
     * @param row  table row
     * @param col  table column
     * @param buttonId id of buttonEditor clicked.
     */
    public void tableButtonClicked(int row, int col, String buttonId) {

        if (ChildTable.NAVIGATE_TO_CHILD_BUTTON_ID.equals(buttonId)) {
            if (childTableModel_.isLastRow(row)) {
                addNewChoice(row);
            } else {
                commitSceneChanges();
                story_.advanceScene(row);
            }
        } else if (ChildTable.ACTION_BUTTON_ID.equals(buttonId)) {
            if (childTableModel_.isLastRow(row)) {
                addNewChoice(row);
            } else {
                 int answer = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this choice?");
                 if (answer == JOptionPane.YES_OPTION) {
                      story_.getCurrentScene().deleteChoice(row);
                 } else {
                     return;
                 }
            }
        } else if (ParentTable.NAVIGATE_TO_PARENT_BUTTON_ID.equals(buttonId)) {
            commitSceneChanges();
            story_.advanceToScene(parentScenes_.get(row).getName());
        }
        else {
            assert false : "unexpected id =" + buttonId;
        }
        showContent();
    }

    /**
     * Show a dialog that allows selecting the new child scene destination.
     * This will be either an exisiting scene or a new one.
     * A new row is automatically added to the table.
     * @param row row of the new choice in the child table.
     */
    private void addNewChoice(int row) {
        NewChoiceDialog newChoiceDlg = new NewChoiceDialog(story_.getCandidateDestinationSceneNames());
        boolean canceled = newChoiceDlg.showDialog();
        if (!canceled) {
            String addedSceneName = newChoiceDlg.getSelectedDestinationScene();
            String choiceDescription = childTableModel_.getChoiceDescription(row);
            story_.addChoiceToCurrentScene(addedSceneName, choiceDescription);
            childTableModel_.addNewChildChoice(addedSceneName);
            //commitSceneChanges();
            //story_.advanceToScene(addedSceneName);
        }
    }

    /**
     * @return our edited copy of the story we were passed at construction.
     */
    public Story getEditedStory() {
        return story_;
    }

    void commitSceneChanges() {
        sceneEditor.doSave();
        if (sceneEditor.isSceneNameChanged()) {
             story_.sceneNameChanged(sceneEditor.getOldSceneName(), sceneEditor.getEditedScene().getName());
        }
        // also save the choice text (it may have been modified)
        childTableModel_.updateSceneChoices(story_.getCurrentScene());
    }

    void ok()
    {
        commitSceneChanges();
        this.setVisible( false );
    }
}
