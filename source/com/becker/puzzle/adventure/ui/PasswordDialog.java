package com.becker.puzzle.adventure.ui;

import com.becker.ui.GUIUtil;
import com.becker.ui.GradientButton;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.Border;

/**
 *
 * @author Barry Becker
 */
public class PasswordDialog extends JDialog implements ActionListener {

    private static String DEFAULT_PASSWORD = "hello123";
    private String password_;

    private JPasswordField passwordField_;

    /** there is always a cancel button so it is included here. */
    protected GradientButton cancelButton_ = new GradientButton();
    protected boolean canceled_ = false;

    /** click this when the password has been entered. */
     protected GradientButton okButton_ = new GradientButton();



    public PasswordDialog(String expectedPassword) {
        if (expectedPassword == null)
            password_ = DEFAULT_PASSWORD;
        else
            password_ = expectedPassword;

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
        this.setModal( true );
        setTitle("Enter the top secret password");

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel pwPanel = new JPanel(new FlowLayout());

        passwordField_= new JPasswordField(password_.length());

        pwPanel.add(new JLabel("password:"));
        pwPanel.add(passwordField_);

        JPanel buttonsPanel = createButtonsPanel();

        mainPanel.add( pwPanel, BorderLayout.CENTER );
        mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

        this.getContentPane().add(mainPanel);
        //this.setPreferredSize(new Dimension(200, 100));

        // security violation in applet and webstart
        if (!GUIUtil.isStandAlone())
             this.setAlwaysOnTop(true);
        pack();
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
            if (password_.equals(new String(passwordField_.getPassword()))) {
                this.setVisible( false );
            }
            else {
                JOptionPane.showMessageDialog( null,
                        "Invalid Passord!", "Error", JOptionPane.ERROR_MESSAGE );
            }
        }
        else if ( source == cancelButton_ ) {
            cancel();
        }
    }

    /**
     *  If the user clicks the X in the upper right, its the same as pressing cancel
     */
    @Override
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
