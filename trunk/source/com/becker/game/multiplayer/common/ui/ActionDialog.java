package com.becker.game.multiplayer.common.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.multiplayer.common.MultiGameController;
import com.becker.game.multiplayer.common.MultiGamePlayer;
import com.becker.game.multiplayer.poker.*;
import com.becker.game.multiplayer.poker.player.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

/**
 * Allow the user to specify a poker action
 * @author Barry Becker
 */
public abstract class ActionDialog extends OptionsDialog
{
    protected MultiGamePlayer player_;

    protected MultiGameController gc_;


    /**
     * constructor - create the tree dialog.
     * @param pc pokerController
     */
    public ActionDialog(MultiGameController gc, Component parent)
    {
        gc_ = gc;
        player_ = gc_.getCurrentPlayer();  
      
        Point p = parent.getLocationOnScreen();
        // offset the dlg so the board is visible as a reference
        setLocation((int)(p.getX() + 0.7*getParent().getWidth()),
                                 (int)(p.getY() + getParent().getHeight()/3.0));
        initUI();
    }


    /**
     * ui initialization of the tree control.
     */
    protected void initUI()
    {
        setResizable( true );
        JPanel mainPanel = new JPanel();
        mainPanel =  new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JPanel personalInfoPanel = createPersonalInfoPanel(); ////new PokerHandPanel(player_.getHand());
        JPanel buttonsPanel = createButtonsPanel();

        JPanel instructions = createInstructionsPanel();

        mainPanel.add(personalInfoPanel , BorderLayout.NORTH);
        mainPanel.add(instructions, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        getContentPane().add( mainPanel );
        getContentPane().repaint();
        pack();
    }

    protected abstract JPanel createPersonalInfoPanel();
    

    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5,5,5,5)));
        JPanel playerPanel = createPlayerLabel(player_);

        JPanel gameSpecificInstructions = createGameInstructionsPanel();
        
        //panel.setPreferredSize(new Dimension(400, 100));
        panel.add(playerPanel, BorderLayout.NORTH);
        panel.add(gameSpecificInstructions, BorderLayout.CENTER);
              
        return panel;
    }

    protected abstract JPanel createGameInstructionsPanel();
    
    public static JPanel createPlayerLabel(Player player) {
        JPanel p = new JPanel();
        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(10, 10));
        swatch.setBackground(player.getColor());
        JLabel playerLabel = new JLabel(player.getName());
        p.add(swatch);
        p.add(playerLabel);
        return p;
    }

    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    protected abstract JPanel createButtonsPanel();
 

    public abstract String getTitle();

}

