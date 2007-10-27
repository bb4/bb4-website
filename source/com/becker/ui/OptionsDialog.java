package com.becker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Use this modal dialog as an abstract base for other modal option dialogs.
 * It shows itself relative to a parent, and has support for a group of buttons at the buttom.
 *
 * @author Barry Becker
 */
public abstract class OptionsDialog extends JDialog implements ActionListener
{

    protected static final String COLON = " : ";


    // there is always a cancel button so it is included here.
    protected GradientButton cancelButton_ = new GradientButton();
    protected boolean canceled_ = false;

    // cache a pointer to this in case we have children
    protected Frame parent_ = null;

    // constants
    //  the height of an option row in a panel
    protected static final int ROW_HEIGHT = 18;


    /**
     *  constructor  (use this constructor if possible)
     *  @param parent the parent component so we know how to place ourselves
     */
    public OptionsDialog( Frame parent )
    {
        super( parent );
        parent_ = parent;

        commonInit();
    }

    public OptionsDialog()
    {
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
        setTitle( getTitle() );

        this.setModal( true );

        // security violation in applet and webstart
        // if (!GUIUtil.isStandAlone())
        //    this.setAlwaysOnTop(true);
        pack();
    }

    public void setParentFrame(JFrame parent)  {
        parent_ = parent;
    }

    /**
     * @return true if the dialog is canceled
     */
    public boolean showDialog()
    {
        canceled_ = false;
        if (parent_ != null)  {
            this.setLocationRelativeTo( parent_ );
        }

        this.setVisible( true );
        this.toFront();
        this.pack();

        return canceled_;
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
    protected abstract JPanel createButtonsPanel();


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