package com.becker.game.multiplayer.online.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.online.*;
import com.becker.game.online.ui.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Manage multiplayer online game tables.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public abstract class MultiPlayerOnlineGameDialog extends OnlineGameDialog
                                                  implements ActionListener, MouseListener, KeyListener {

    private static final String DEFAULT_NAME = "<name>";

    // new table in play online tab                                                                                                                                           3
    private JTextField localPlayerName_;
    private GradientButton createTableButton_;
    private MultiPlayerOnlineGameTablesTable onlineGameTablesTable_;
    private GameOptionsDialog createGameTableDialog_;
    private String currentName_;
    private String oldName_;

    /**
     * Constructor
     */
    public  MultiPlayerOnlineGameDialog(Frame parent, ViewerCallbackInterface viewer) {
        super(parent, viewer);
    }


    /**
     *  There is a button for creating a new online table at the top.
     *
     * The play online table as a row for each virtual table that an online player can join.
     * There is a join button next to each row in the table.
     * The player must never be seated at more than one table.
     * When the requisite number of players are at a table the game begins.
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
                                                  GameContext.getLabel("ONLINE_DLG_TITLE")));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(GameContext.getLabel("ONLINE_TABLES"));
        JPanel buttonsPanel = new JPanel(new BorderLayout());

        createTableButton_ = new GradientButton(GameContext.getLabel("CREATE_TABLE"));
        createTableButton_.setToolTipText( GameContext.getLabel("CREATE_TABLE_TIP") );
        createTableButton_.addActionListener(this);
        buttonsPanel.add(createTableButton_, BorderLayout.EAST);

        JPanel namePanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel("Your Name: ");
        currentName_ = DEFAULT_NAME;
        localPlayerName_ = new JTextField(DEFAULT_NAME);
        localPlayerName_.addMouseListener(this);
        localPlayerName_.addKeyListener(this);

        //localPlayerName_.setPreferredSize(new Dimension(180, 14));

        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(localPlayerName_, BorderLayout.CENTER);
        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(180, 20));
        namePanel.add(fill, BorderLayout.EAST);
        JPanel bottomFill = new JPanel();
        bottomFill.setPreferredSize(new Dimension(100, 10));

        headerPanel.add(namePanel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(bottomFill, BorderLayout.SOUTH);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        playOnlinePanel.add(headerPanel, BorderLayout.NORTH);

        onlineGameTablesTable_ = createOnlineGamesTable(localPlayerName_.getText(), this);

        playOnlinePanel.setPreferredSize( new Dimension(600, 300) );
        playOnlinePanel.add( new JScrollPane(onlineGameTablesTable_.getTable()) , BorderLayout.CENTER );

        if (serverConnection_.isConnected()) {
            serverConnection_.enterRoom();
        }
        return playOnlinePanel;
    }


    protected abstract MultiPlayerOnlineGameTablesTable createOnlineGamesTable(String name,
                                                                               ActionListener listener);

    /**
     * You are free to set your own options for the table that you are creating.
     */
    protected abstract GameOptionsDialog createNewGameTableDialog();

    /**
     * An online table has filled with players and is ready to start.
     */
    protected void startGame()
    {
        controller_.setPlayers(onlineGameTablesTable_.getSelectedTable().getPlayersAsArray());
    }

    /**
     * The server has sent out a message to all the clients.
     * @param cmd the command to handle.
     */
    public void handleServerUpdate(GameCommand cmd) {

        if (onlineGameTablesTable_ == null)
            return; // not initialized yet.

        //System.out.println("got an update of the multiplayer table list from the server:\n" + cmd);
        switch (cmd.getName())  {
            case UPDATE_TABLES :
                OnlineGameTableList tableList = (OnlineGameTableList) cmd.getArgument();
                onlineGameTablesTable_.removeAllRows();

                for (int i=0; i<tableList.size(); i++) {
                    OnlineGameTable table = tableList.get(i);
                    onlineGameTablesTable_.addRow(table, table.hasPlayer(currentName_));
                }

                OnlineGameTable readyTable = tableList.getTableReadyToPlay(currentName_);
                if (readyTable != null) {
                    // then the table the player is sitting at is ready to begin play.
                    JOptionPane.showMessageDialog(this,
                              "All the players required \n(" + readyTable.getPlayersString()
                              + ")\n have joined this table. Play will now begin. ",
                              "Ready to Start", JOptionPane.INFORMATION_MESSAGE);
                    // close the dlg and tell the server to start a thread to play the game
                    this.setVisible( false );
                    //serverConnection_.sendCommand(new GameCommand(GameCommand.Name.START_GAME, readyTable));
                }
                break;
           default : assert false : "Unexpected command name :"+ cmd.getName();
        }

    }

    /**
     * Implements actionlistener.
     * The user has done something to change the table list
     * (e.g. add a new game table or join a different table).
     */
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();


        if (source == createTableButton_) {
            checkName();
            createNewGameTable();
        }
        else {
            checkName();
            joinDifferentTable((JoinButton) source);
        }
    }

    private void checkName() {
         if (localPlayerName_.getText().equals(DEFAULT_NAME)) {
            JOptionPane.showMessageDialog(this, "You must enter your name at the top first.", "Warning",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * The create new table button at the top was clicked.
     */
    public void createNewGameTable() {
        if (currentName_.equals(DEFAULT_NAME)) {
            JOptionPane.showMessageDialog(this, "You need to select a name for yourself first.",
                                          "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // if the name has changed, make sure it is updated on the server
        if (!currentName_.equals(oldName_)) {
            serverConnection_.nameChanged(oldName_, currentName_);
            oldName_ = currentName_;
        }

        boolean canceled = createGameTableDialog_.showDialog();

        if (!canceled)  {

            MultiGameOptions options = (MultiGameOptions)createGameTableDialog_.getOptions();

            OnlineGameTable newTable =
                onlineGameTablesTable_.createOnlineTable(currentName_, options);

            // now add it to this list as a new row and tell the server to add it.
            // onlineGameTablesTable_.addRow(newTable);
            serverConnection_.addGameTable(newTable);
        }
    }

    /**
     * The local user has clikced  ajoin button on a different table
     * indicating that they want to join that table.
     */
    private void joinDifferentTable(JoinButton b) {

        int joinRow = b.getRow();
        PlayerTableModel m = onlineGameTablesTable_.getModel();

        for (int i=0; i<m.getRowCount(); i++) {
            // you can join tables other than the one you are at as long as they are not already playing.
            boolean enableJoin = (i != joinRow) && !onlineGameTablesTable_.getGameTable(i).isReadyToPlay();
            m.setValueAt(enableJoin, i, 0);
        }
        serverConnection_.joinTable(onlineGameTablesTable_.createPlayerForName(currentName_),
                                    onlineGameTablesTable_.getGameTable(joinRow));

        onlineGameTablesTable_.getTable().removeEditor();
    }

    /**
     * called when the user closes the online game dialog.
     * We remove them form the active tables.
     */
    public void closing() {
        serverConnection_.leaveRoom(currentName_);
    }


    /**
     * Implement keyListener interface.
     * @param key
     */
    public void keyTyped( KeyEvent key )  {}
    public void keyPressed(KeyEvent key) {}
    public void keyReleased(KeyEvent key) {
        char c = key.getKeyChar();
        currentName_ = localPlayerName_.getText();
        if ( c == '\n' ) {
            serverConnection_.nameChanged(oldName_, currentName_);
            oldName_ = currentName_;
        }
    }

    /**
     * Implement moustListener interface.
     */
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
            localPlayerName_.setText("");
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}


}
