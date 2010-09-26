package com.becker.ui.dialogs;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Use this modal dialog as an abstract base for other modal option dialogs.
 * It shows itself relative to a parent, and has support for a group of buttons at the buttom.
 *
 * @author Barry Becker
 */
public abstract class OptionsDialog extends AbstractDialog implements ActionListener
{

    protected static final String COLON = " : ";

    /**
     *  constructor  (use this constructor if possible)
     *  @param parent the parent component so we know how to place ourselves
     */
    public OptionsDialog( JFrame parent )
    {
        super( parent );

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
 
        this.setResizable(false);
        setTitle( getTitle() );
        this.setModal( true );
    }

    /**
     *  create the buttons that go at the botton ( eg OK, Cancel, ...)
     */
    protected abstract JPanel createButtonsPanel();

}