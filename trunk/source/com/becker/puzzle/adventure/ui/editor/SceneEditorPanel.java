package com.becker.puzzle.adventure.ui.editor;

import com.becker.ui.dialogs.ImagePreviewDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import com.becker.puzzle.adventure.Scene;
import com.becker.puzzle.adventure.ui.StoryPanel;
import com.becker.ui.components.GradientButton;
import com.becker.ui.components.TextInput;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

/**
 * Used to edit an individual scene.
 * @author Barry Becker
 */
class SceneEditorPanel extends JPanel implements ActionListener {


    /** The scene to edit */
    private Scene scene_;

    private GradientButton showImageButton_;
    private GradientButton playSoundButton_;

    private JTextArea sceneText_;

    private static final int EDITOR_WIDTH = 900;

    /**
     * Constructor
     * @param scene
     */
    public  SceneEditorPanel(Scene scene) {

        scene_ = scene;
        createUI();
    }


    void createUI() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(EDITOR_WIDTH, 600));

        this.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),"Edit current Scene" ) );

        TextInput nameInput_= new TextInput("name:", scene_.getName());

        sceneText_ = new JTextArea();
        sceneText_.setFont(StoryPanel.TEXT_FONT);
        sceneText_.setText(scene_.getText());

        add(nameInput_, BorderLayout.NORTH);
        add(new JScrollPane(sceneText_), BorderLayout.CENTER);
        add(createMediaButtons(), BorderLayout.SOUTH);
    }

    /**
     * for sound and image and whatever else is associated with the scene.
     */
    private JPanel createMediaButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        showImageButton_ = new GradientButton("Image");
        showImageButton_.addActionListener(this);
        showImageButton_.setEnabled(scene_.getImage() != null);

        playSoundButton_ = new GradientButton("Sound");
        playSoundButton_.addActionListener(this);
        playSoundButton_.setEnabled(scene_.hasSound());

        buttonPanel.add(showImageButton_);
        buttonPanel.add(playSoundButton_);

        return buttonPanel;
    }

    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();

        if ( source == showImageButton_ ) {
            ImagePreviewDialog imgPreviewDlg = new ImagePreviewDialog(scene_.getImage());
            imgPreviewDlg.showDialog();  
        }
        else if ( source == playSoundButton_ ) {
            scene_.playSound();
        }
    }

    public void doSave() {
        scene_.setText(sceneText_.getText());
    }
}
