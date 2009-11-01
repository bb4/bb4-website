package com.becker.puzzle.adventure.ui.editor;

import com.becker.ui.components.GradientButton;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import com.becker.puzzle.adventure.Story;
import com.becker.ui.dialogs.AbstractDialog;
import com.becker.ui.table.TableButton;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 *
 * @author Barry Becker
 */
public class StoryEditorDialog extends AbstractDialog
                                                  implements ActionListener {

    /** The story to edit */
    private Story story_;

    /** click this when done editing the scene. */
    private  GradientButton okButton_ = new GradientButton();

    private SceneEditorPanel sceneEditor;

    private static final Font INSTRUCTION_FONT = new Font("Sans Serif", Font.PLAIN, 10);
    

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


    protected JComponent createDialogContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setSize(new Dimension(800, 1200));

        JPanel editingPane = createEditingPane();
        JLabel title = new JLabel("Navigate through the scene heirarchy and change values for scenes.");
        title.setBorder(BorderFactory.createEmptyBorder(5, 4, 20, 4));
        title.setFont(INSTRUCTION_FONT);
    
        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(editingPane, BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        //mainPanel.setPreferredSize(new Dimension(700,900));

        return mainPanel;
    }

    /**
     * Parent table on top
     * Scene editor in the middle
     * Child options on the bottom
     * @return
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
        ParentTable parentTable = new ParentTable(story_.getParentScenes(), this);

        parentContainer.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),"Parent Scenes" ) );

        parentContainer.add(new JScrollPane(parentTable.getTable()), BorderLayout.CENTER);

        parentContainer.setPreferredSize(new Dimension(700, 80));
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

        childContainer.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),"Choices (to navigate to child scenes)" ) );

        childContainer.add(new JScrollPane(childTable.getTable()), BorderLayout.CENTER);

        childContainer.setPreferredSize(new Dimension(700, 120));
        return childContainer;
    }

    /**
     *  create the buttons that go at the botton ( eg OK, Cancel, ...)
     */
    protected  JPanel createButtonsPanel() {
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
        else if (source instanceof TableButton ) {
            TableButton button = (TableButton) source;
            if (ChildTable.NAVIGATE_TO_CHILD_BUTTON_ID.equals(button.getId())) {

            } else if (ChildTable.DELETE_CHOICE_BUTTON_ID.equals(button.getId())) {

            } if (ParentTable.NAVIGATE_TO_PARENT_BUTTON_ID.equals(button.getId())) {

            }
        }
    }

    public Story getEditedStory() {
        return story_;
    }

    protected void ok()
    {
         //JOptionPane.showMessageDialog( null,
         //               "Done editing!", "Info", JOptionPane.INFORMATION_MESSAGE );
        //canceled_ = false;
        sceneEditor.doSave();
        this.setVisible( false );
    }
}
