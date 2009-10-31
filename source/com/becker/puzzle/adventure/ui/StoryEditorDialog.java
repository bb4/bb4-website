package com.becker.puzzle.adventure.ui;

import com.becker.ui.components.GradientButton;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.becker.puzzle.adventure.Story;
import com.becker.ui.dialogs.AbstractDialog;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JTextArea;

/**
 *
 * @author Barry Becker
 */
public class StoryEditorDialog extends AbstractDialog
                                                  implements ActionListener {

    /** click this when the password has been entered. */
    protected GradientButton okButton_ = new GradientButton();

    /** The story to edit */
    private Story story_;

    public StoryEditorDialog(Story story) {

        story_ = story;

        this.setResizable(true);
        setTitle("Story Editor");
        this.setModal( true );
        showContent();
    }


    protected JComponent createDialogContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setSize(new Dimension(800, 1200));
        JTextArea text1 = new JTextArea();
        text1.setText("asjfalksjf ;lksj flsaj flkdsaj lksadj flksadj flksa;j flkdsajf lkdsaj slda;kjsadkl slkdaj fds");
        JTextArea text2 = new JTextArea();
        JTextArea text3 = new JTextArea();

        mainPanel.add(text1, BorderLayout.NORTH);
        mainPanel.add(text2, BorderLayout.CENTER);
        mainPanel.add(text3, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     *  create the buttons that go at the botton ( eg OK, Cancel, ...)
     */
    protected  JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( okButton_, "OK", "Check to see if the password is correct. " );
        initBottomButton( cancelButton_, "Cancel", "Go back to the main window without entering a password." );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }


    public void actionPerformed( ActionEvent e )
    {
        super.actionPerformed(e);
        Object source = e.getSource();

        if ( source == okButton_ ) {
      
                JOptionPane.showMessageDialog( null,
                        "Done editing!", "Info", JOptionPane.INFORMATION_MESSAGE );
        }
    }
}
