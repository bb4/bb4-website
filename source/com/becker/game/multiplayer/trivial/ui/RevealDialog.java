package com.becker.game.multiplayer.trivial.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.ActionDialog;
import com.becker.game.multiplayer.trivial.*;
import com.becker.game.multiplayer.trivial.player.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

/**
 * Allow the user to specify a trivial action: keep hidden or reveal.
 * @author Barry Becker
 */
public final class RevealDialog extends ActionDialog
{   
   
    // selected when the user desires to reveal his value.
    private JRadioButton revealButton_;
    
    protected GradientButton okButton_;

    /**
     * constructor - create the tree dialog.
     * @param pc TrivialController
     */
    public RevealDialog(TrivialController pc, Component parent)
    {
        super(pc, parent); 
    }

    protected JPanel createPersonalInfoPanel()
    {
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Value = "+ ((TrivialPlayer)player_).getValue());
        p.add(label, BorderLayout.CENTER);
        return p;    
    }
    
    
    protected JPanel createGameInstructionsPanel() {
                  
        JRadioButton keepHiddenButton = new JRadioButton("Keep value hidden");            
        keepHiddenButton.setSelected(true);

        revealButton_ = new JRadioButton("Reveal value");

        ButtonGroup group = new ButtonGroup();
        group.add(keepHiddenButton);
        group.add(revealButton_);   

        JPanel panel = new JPanel(new BorderLayout());
        JPanel choicesPanel = new JPanel( new GridLayout(1, 2) );
        choicesPanel.add( keepHiddenButton );
        choicesPanel.add( revealButton_ );     
        
        panel.add(choicesPanel, BorderLayout.CENTER);
        //panel.add(createButtonsPanel(), BorderLayout.SOUTH);
       
        return panel;
    }

    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );
        
        okButton_ = new GradientButton();
        initBottomButton( okButton_, "OK", "Click when you have made your choice" );
        buttonsPanel.add(okButton_);
            
        return buttonsPanel;
    }


    public String getTitle()
    {
        return "Hide or Reveal?";
    }


    /**
     * called when one of the buttons at the bottom have been pressed.
     * @param e
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        TrivialAction.Name actionName = TrivialAction.Name.KEEP_HIDDEN;
        if (source == okButton_) {
            if (revealButton_.isSelected()) {
                actionName = TrivialAction.Name.REVEAL;
                ((TrivialPlayer)player_).revealValue();
            }
            this.setVisible(false);
        }
              
        ((TrivialHumanPlayer)player_).setAction(new TrivialAction(player_.getName(), actionName));
    }

}

