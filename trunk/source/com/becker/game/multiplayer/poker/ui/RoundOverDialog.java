package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.poker.PokerPlayer;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.List;



/**
 * Show a summary of the final results.
 * We will show how many planets and how many ships each remaining player has.
 * The winner is the player with the most planets.
 * If there are more than one player with the same number of planets,
 * then the number of ships will be used to break ties.
 *
 * @author Barry Becker
 */
public class RoundOverDialog extends OptionsDialog
{
    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     */
    public RoundOverDialog( Frame parent, PokerPlayer winner, int winnings )
    {
        super( parent );
    }

    protected void initUI() {

    }

    public String getTitle() {
       return "Round Over";
    }

    protected JPanel createButtonsPanel(){
        return new JPanel();
    }


    public void actionPerformed(ActionEvent evt) {

    }

}

