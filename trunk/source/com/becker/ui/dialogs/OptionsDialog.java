/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Use this modal dialog as an abstract base for other modal option dialogs.
 * It shows itself relative to a parent, and has support for a group of buttons at the bottom.
 *
 * @author Barry Becker
 */
public abstract class OptionsDialog extends AbstractDialog
                                    implements ActionListener {

    public static final String COLON = " : ";

    /**
     *  constructor  (use this constructor if possible)
     *  @param parent the parent component so we know how to place ourselves
     */
    public OptionsDialog( Component parent ) {
        super( parent );

        commonInit();
    }

    public OptionsDialog() {
        commonInit();
    }

    /**
     * initialize the dialogs ui
     */
    public void commonInit() {
 
        this.setResizable(false);
        setTitle( getTitle() );
        this.setModal( true );
    }

    /**
     * create the buttons that go at the bottom ( eg OK, Cancel, ...)
     * @return the panel at the bottom holding the buttons.
     */
    protected abstract JPanel createButtonsPanel();

}