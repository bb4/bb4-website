package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Manager players for new local game.
 *
 * @author Barry Becker
 */
public abstract class MultiPlayerNewGameDialog extends NewGameDialog
                                               implements ActionListener, ListSelectionListener
{

    // add / remove players
    private GradientButton addButton_;
    private GradientButton removeButton_;
    // list of players in the local game.
    private PlayerTable playerTable_;


    public MultiPlayerNewGameDialog( JFrame parent, ViewerCallbackInterface viewer)
    {
        super( parent, viewer);
        initUI();
        this.setResizable(true);
    }

    /**
     * Lets you initialize all the players. Some subset of the players may be robots and not human.
     * @return a table of players
     */
    protected JPanel createPlayerPanel()
    {
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                  "Add Players for a new local game") );

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(GameContext.getLabel("PLAYERS"));
        JPanel buttonsPanel = new JPanel(new BorderLayout());

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
        playerPanel.add(headerPanel, BorderLayout.NORTH);

        playerTable_ = createPlayerTable();
        playerTable_.addListSelectionListener(this);

        playerPanel.add(new JScrollPane(playerTable_.getTable()), BorderLayout.CENTER);
        playerPanel.setPreferredSize(new Dimension(500,300));
        return playerPanel;
    }


    /**
     * @return  shows the list of local playes that will play this local game
     */
    protected abstract PlayerTable createPlayerTable();


    /**
     * panel which allows changin board specific properties.
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
        Component selectedTab = tabbedPanel_.getSelectedComponent();
        if (selectedTab == playLocalPanel_)  {
            controller_.setPlayers(playerTable_.getPlayers());
        }
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
        MultiGameOptions options = (MultiGameOptions) controller_.getOptions();
        addButton_.setEnabled(playerTable_.getModel().getRowCount() < options.getMaxNumPlayers());

    }

    /**
     * Called when rows are selected/deselected in the player table.
     * @param event
     */
    public void valueChanged(ListSelectionEvent event)
    {
        MultiGameOptions options = (MultiGameOptions) controller_.getOptions();
        boolean enabled = playerTable_.getTable().getSelectedRowCount() > 0
                          && playerTable_.getModel().getRowCount() > options.getMinNumPlayers();
        removeButton_.setEnabled(enabled);
    }
}

