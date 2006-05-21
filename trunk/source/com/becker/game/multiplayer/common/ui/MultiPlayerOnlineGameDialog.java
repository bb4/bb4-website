package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.*;
import com.becker.game.common.online.*;
import com.becker.game.common.ui.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Manage multiplayer online game tables.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public abstract class MultiPlayerOnlineGameDialog extends OnlineGameDialog implements ActionListener {

    // new table in play online tab
    private JTextField localPlayerName_;
    private GradientButton createTableButton_;
    private MultiPlayerOnlineGamesTable onlineGamesTable_;
    private GameOptionsDialog createGameTableDialog_;

    /**
     * Constructor
     * @param parent
     * @param viewer
     */
    public  MultiPlayerOnlineGameDialog(Frame parent, ViewerCallbackInterface viewer) {
        super(parent, viewer);
    }


    /**
     *  There is a button for creating a new online table at the top.
     *
     * The play online table as a row for each virtual table that an online player can join.
     * There is a join button next to each row in the table.
     * The player must join exactly one table before the start button gets enabled.
     * Join button, changes to "leave", after joining a table.
     * If you join a different table, you leave the last one that you joined.
     * A table (row) is removed if everyone leaves it.
     */
    protected JPanel createPlayOnlinePanel()
    {
        createGameTableDialog_ = createNewGameTableDialog();

        JPanel playOnlinePanel = new JPanel(new BorderLayout());
        playOnlinePanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                  "Join a table to play others online") );

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(GameContext.getLabel("ONLINE_TABLES"));
        JPanel buttonsPanel = new JPanel(new BorderLayout());

        createTableButton_ = new GradientButton(GameContext.getLabel("CREATE_TABLE"));
        createTableButton_.setToolTipText( GameContext.getLabel("CREATE_TABLE_TIP") );
        createTableButton_.addActionListener(this);
        buttonsPanel.add(createTableButton_, BorderLayout.EAST);

        JPanel namePanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel("Your Name: ");
        localPlayerName_ = new JTextField("<name>");
        //localPlayerName_.setPreferredSize(new Dimension(180, 14));
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(localPlayerName_, BorderLayout.CENTER);
        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(180, 20));
        namePanel.add(fill, BorderLayout.EAST);

        headerPanel.add(namePanel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        playOnlinePanel.add(headerPanel, BorderLayout.NORTH);

        onlineGamesTable_ = createOnlineGamesTable(localPlayerName_.getText());

        playOnlinePanel.setPreferredSize( new Dimension(500, 300) );
        playOnlinePanel.add( new JScrollPane(onlineGamesTable_.getTable()) , BorderLayout.CENTER );

        return playOnlinePanel;
    }


    protected abstract MultiPlayerOnlineGamesTable createOnlineGamesTable(String name);

    /**
     * You are free to set your own options for the table that you are creating.
     */
    protected abstract GameOptionsDialog createNewGameTableDialog();

    /**
     * An online table has filled with players and is ready to start.
     */
    protected void startGame()
    {
        controller_.setPlayers(onlineGamesTable_.getSelectedTable().getPlayers());
    }

    public void actionPerformed( ActionEvent e )
    {
        super.actionPerformed(e);
        Object source = e.getSource();

        if (source == createTableButton_) {
            createGameTableDialog_.showDialog();
            OnlineGameTable newTable = onlineGamesTable_.createOnlineTable(localPlayerName_.getText());

            // now add it to this list as a new row and tell the server to add it.
            onlineGamesTable_.addRow(newTable);
            serverConnection_.addGameTable(newTable);
        }

    }
}
