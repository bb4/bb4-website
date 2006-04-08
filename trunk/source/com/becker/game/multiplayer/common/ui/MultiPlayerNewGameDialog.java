package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public abstract class MultiPlayerNewGameDialog extends NewGameDialog implements ActionListener, ListSelectionListener
{

    private GradientButton addButton_;
    private GradientButton removeButton_;

    // list of players that will be admirals in the game.
    private PlayerTable playerTable_;

    public MultiPlayerNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer );
        initUI();
    }

    /**
     * Lets you initialize all the players. Some subset of the players may be robots and not human.
     * @return a table of players
     */
    protected JPanel createPlayerPanel()
    {
        JPanel p = new JPanel();
        p.setLayout( new BorderLayout() );
        p.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                  GameContext.getLabel("PLAYERS") ) );

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout( new BorderLayout() );
        JLabel titleLabel = new JLabel(GameContext.getLabel("PLAYERS"));
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout( new BorderLayout() );
        addButton_ = new GradientButton(GameContext.getLabel("ADD"));
        addButton_.setToolTipText( GameContext.getLabel("ADD_TIP") );
        addButton_.addActionListener(this);
        removeButton_ = new GradientButton(GameContext.getLabel("REMOVE"));
        removeButton_.setToolTipText( GameContext.getLabel("REMOVE_PLAYER_TIP") );
        removeButton_.addActionListener(this);
        removeButton_.setEnabled(false);
        buttonsPanel.add(addButton_, BorderLayout.WEST);
        buttonsPanel.add(removeButton_, BorderLayout.EAST);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        p.add(headerPanel, BorderLayout.NORTH);

        playerTable_ = createPlayerTable();
        //pokerPlayerTable_ = new PokerPlayerTable((PokerPlayer[])c.getPlayers());

        playerTable_.addListSelectionListener(this);

        p.add(new JScrollPane(playerTable_.getTable()), BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(500,300));
        return p;
    }

    protected abstract PlayerTable createPlayerTable();

    /**
     * we don't allow them to change the dimensions of the board in poker since its not played on a grid.
     */
    protected JPanel createBoardParamPanel()
    {
        return null;
    }

    /**
     * the ok button has been pressed, indicating the desire to start the game with
     * the configuration specified.
     */
    protected void ok()
    {
        controller_.setPlayers(playerTable_.getPlayers());
        super.ok();
    }

    public void actionPerformed( ActionEvent e )
    {
        super.actionPerformed(e);
        Object source = e.getSource();

        if ( source == addButton_ ) {
            playerTable_.addRow();
        }
        else if ( source == removeButton_ ) {
            playerTable_.removeSelectedRows();
        }
    }

    /**
     * called when rows are selected/deselected in the player table
     * @param event
     */
    public void valueChanged(ListSelectionEvent event)
    {
        removeButton_.setEnabled(playerTable_.getTable().getSelectedRowCount()>0);
    }
}

