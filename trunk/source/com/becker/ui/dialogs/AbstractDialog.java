package com.becker.ui.dialogs;

import com.becker.ui.GUIUtil;
import com.becker.ui.components.GradientButton;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.border.Border;

/**
 * All dialogs should extend this instead of JDialog
 * @author Barry Becker
 */
public abstract class AbstractDialog extends JDialog implements ActionListener {

    /** there is always a cancel button so it is included here. */
    protected GradientButton cancelButton_ = new GradientButton();

    protected boolean canceled_ = false;

    /** Cache a pointer to this in case we have children */
    protected JFrame parent_;
    
    /** the height of an option row in a panel */
    public static final int ROW_HEIGHT = 18;


    /**
     * Constructor
     * Subclasses will set internal data here.
     * At the end of every derived class constructor, displayContent should be called.
     */
    public AbstractDialog() {
    }

    /**
     * Constructor.
     * @param parent parent component to place ourselves relative to.
     */
    public AbstractDialog(JFrame parent) {
        this();
        parent_ = parent;
    }

    /**
     * Must be called once after the context has been created.
     */
    protected final void showContent() {
        this.getContentPane().removeAll();
        this.getContentPane().add(createDialogContent());

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        // security violation in applet and webstart
        if (!GUIUtil.isStandAlone()) {
             this.setAlwaysOnTop(true);
        }
        pack();
    }

    /**
     * initiallize the dialogs ui
     */
    protected abstract JComponent createDialogContent();


    /**
     * initialize one of the buttons that go at the bottom of the dialog
     * typically this is something like ok, cancel, start, ...
     */
    protected void initBottomButton( GradientButton bottomButton, String buttonText, String buttonToolTip )
    {
        bottomButton.setText( buttonText );
        bottomButton.setToolTipText( buttonToolTip );
        bottomButton.addActionListener( this );
        bottomButton.setMinimumSize( new Dimension( 45, 25 ) );
    }
    
     /**
     * @return true if the dialog is canceled
     */
    public boolean showDialog()
    {
        canceled_ = false;
        if (parent_ != null)  {
            this.setLocationRelativeTo( parent_ );
        } else {
            // something besides the corner.
            this.setLocation(100, 100);
        }

        this.setVisible( true );
        this.toFront();
        this.pack();

        return canceled_;        
    }


    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();  
        if ( source == cancelButton_ ) {
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

    public void close()
    {
        this.setVisible( false );
        this.dispose();
    }
}
