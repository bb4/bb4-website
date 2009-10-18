package com.becker.puzzle.adventure.ui;

import com.becker.ui.GUIUtil;
import com.becker.ui.GradientButton;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import com.becker.puzzle.adventure.Story;

/**
 *
 * @author Barry Becker
 */
public class StoryEditorDialog extends JDialog implements ActionListener {


    /** there is always a cancel button so it is included here. */
    protected GradientButton cancelButton_ = new GradientButton();
    protected boolean canceled_ = false;

    /** click this when the password has been entered. */
     protected GradientButton okButton_ = new GradientButton();


     private Story story_;

    public StoryEditorDialog(Story story) {
      
        story_ = story;

        commonInit();
    }


    /**
     * initiallize the dialogs ui
     */
    public void commonInit()
    {
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        //this.setLocationRelativeTo( parent_ );
        this.setResizable(false);
        setTitle("Enter the top secret password");

        this.setModal( true );

        pack();
    }


    /**
     * initialize one of the buttons that go at the bottom of the dialog
     * typically this is something like ok, cancel, start, ...
     */
    protected void initBottomButton( GradientButton bottomButton, String buttonText, String buttonToolTip )
    {
        bottomButton.setText( buttonText );
        bottomButton.setToolTipText( buttonToolTip );
        bottomButton.addActionListener( this );
        bottomButton.setPreferredSize( new Dimension( 140, 25 ) );
        bottomButton.setMinimumSize( new Dimension( 50, 25 ) );
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

     /**
     * @return true if the dialog is canceled
     */
    public boolean showDialog()
    {
        canceled_ = false;
        //if (parent_ != null)  {
        //    this.setLocationRelativeTo( parent_ );
        //}

        this.setVisible( true );
        this.toFront();
        this.pack();

        return canceled_;
    }

    public void actionPerformed( ActionEvent e )
    {

        Object source = e.getSource();

        if ( source == okButton_ ) {
      
                JOptionPane.showMessageDialog( null,
                        "Done editing!", "Info", JOptionPane.INFORMATION_MESSAGE );
        }
        else if ( source == cancelButton_ ) {
            cancel();
        }
    }

    /**
     *  If the user clicks the X in the upper right, its the same as pressing cancel
     */
    protected void processWindowEvent( WindowEvent e )
    {
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            cancel();
        }
        super.processWindowEvent( e );
    }

    protected static Border createMarginBorder()
    {
        return BorderFactory.createEmptyBorder(3, 3, 3, 3);
    }

    /**
     * cancel button pressed
     */
    protected void cancel()
    {
        canceled_ = true;
        this.setVisible( false );
    }

}
