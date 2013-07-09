/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.dialogs;

import com.barrybecker4.common.app.AppContext;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Allow the user to enter a top secret password to gain access to restricted content.
 *
 * @author Barry Becker
 */
public class PasswordDialog extends AbstractDialog
                            implements ActionListener, KeyListener {

    private static final String DEFAULT_PASSWORD = "hello123"; //NON-NLS
    private String password_;

    private JPasswordField passwordField_;

    /** click this when the password has been entered. */
    protected GradientButton okButton_;

    /** newline is like clicking ok. */
    private static final char NEWLINE_CHAR = 10;


    /**
     * Constructor
     * @param expectedPassword password that the user must enter to continue.
     */
    public PasswordDialog(String expectedPassword) {
        super();
        if (expectedPassword == null)
            password_ = DEFAULT_PASSWORD;
        else
            password_ = expectedPassword;

        showContent();
    }

    /**
     * initialize the dialogs ui
     */
    @Override
    public JPanel createDialogContent() {
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        this.setResizable(false);
        this.setModal( true );
        setTitle(AppContext.getLabel("ENTER_PW"));

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel pwPanel = new JPanel(new FlowLayout());

        passwordField_= new JPasswordField();
        passwordField_.addKeyListener(this);
        passwordField_.setColumns(password_.length());

        pwPanel.add(new JLabel(AppContext.getLabel("PASSWORD")));
        pwPanel.add(passwordField_);

        JPanel buttonsPanel = createButtonsPanel();

        mainPanel.add( pwPanel, BorderLayout.CENTER );
        mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

        return mainPanel;
    }


    /**
     *  create the buttons that go at the bottom ( eg OK, Cancel, ...)
     * @return panel with ok cancel buttons.
     */
    protected  JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_,
                AppContext.getLabel("OK"), "Check to see if the password is correct. " );
        initBottomButton(cancelButton,
                AppContext.getLabel("CANCEL"), "Go back to the main window without entering a password." );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }


    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        Object source = e.getSource();

        if ( source == okButton_ ) {
            validatePassword();
        }
    }

    private void validatePassword() {
        if (password_.equals(new String(passwordField_.getPassword()))) {
                this.setVisible( false );
        }
        else {
            JOptionPane.showMessageDialog( null,
                    AppContext.getLabel("INVALID_PW"), AppContext.getLabel("ERROR"), JOptionPane.ERROR_MESSAGE );
        }
    }

    /**
     * Implements KeyListener interface.
     * Hitting enter is like clicking "ok"
     * @param key key that was typed.
     */
    public void keyTyped( KeyEvent key )  {
    }

    public void keyPressed(KeyEvent key) {
        char c = key.getKeyChar();
        //System.out.println("key="+ Integer.toString(c) + " key2=" + c);
        if (c == NEWLINE_CHAR) {
            validatePassword();
        }
    }
    public void keyReleased(KeyEvent key) {}

}
