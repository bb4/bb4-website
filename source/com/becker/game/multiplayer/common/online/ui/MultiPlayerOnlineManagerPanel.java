package com.becker.game.multiplayer.common.online.ui;

import com.becker.game.common.*;
import com.becker.game.common.online.ui.*;
import com.becker.game.common.online.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Manage multiplayer online game tables.
 * Shows a list of the currently active tables to the user in a table.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public abstract class MultiPlayerOnlineManagerPanel extends OnlineGameManagerPanel
                                                  implements ActionListener, MouseListener, KeyListener {

    private static final String DEFAULT_NAME = "<name>";

    private JTextField localPlayerName_;
    private GradientButton createTableButton_;
    private MultiPlayerOnlineGameTablesTable onlineGameTablesTable_;
    private GameOptionsDialog createGameTableDialog_;
    private String currentName_;
    private String oldName_;


    /**
     * Constructor
     */
    public MultiPlayerOnlineManagerPanel(ViewerCallbackInterface viewer, ChangeListener dlg) {
        super(viewer, dlg);
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
        //JLabel titleLabel = new JLabel(GameContext.getLabel("ONLINE_TABLES"));
        JPanel buttonsPanel = new JPanel(new BorderLayout());

        createTableButton_ = new GradientButton(GameContext.getLabel("CREATE_TABLE"));
        createTableButton_.setToolTipText( GameContext.getLabel("CREATE_TABLE_TIP") );
        createTableButton_.addActionListener(this);
        buttonsPanel.add(createTableButton_, BorderLayout.EAST);

        JPanel namePanel = createNamePanel();

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(180, 20));
        namePanel.add(fill, BorderLayout.EAST);
        JPanel bottomFill = new JPanel();
        bottomFill.setPreferredSize(new Dimension(100, 10));

        headerPanel.add(namePanel, BorderLayout.CENTER);
        //headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(bottomFill, BorderLayout.SOUTH);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        playOnlinePanel.add(headerPanel, BorderLayout.NORTH);

        onlineGameTablesTable_ = createOnlineGamesTable(localPlayerName_.getText(), this);

        playOnlinePanel.setPreferredSize( new Dimension(600, 300) );
        playOnlinePanel.add( new JScrollPane(onlineGameTablesTable_.getTable()) , BorderLayout.CENTER );

        if (controller_.getServerConnection().isConnected()) {
            controller_.getServerConnection().enterRoom();
        }
        return playOnlinePanel;
    }

    private JPanel createNamePanel() {
        JPanel namePanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel("Your Name: ");
        currentName_ = DEFAULT_NAME;
        localPlayerName_ = new JTextField(DEFAULT_NAME);
        localPlayerName_.addMouseListener(this);
        localPlayerName_.addKeyListener(this);

        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(localPlayerName_, BorderLayout.CENTER);
        return namePanel;
    }


    protected abstract MultiPlayerOnlineGameTablesTable createOnlineGamesTable(String name,
                                                                               ActionListener listener);

    /**
     * You are free to set your own options for the table that you are creating.
     */
    protected abstract GameOptionsDialog createNewGameTableDialog();

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
                updateTables( (OnlineGameTableList) cmd.getArgument());                               
                break;
           case START_GAME :
                startGame();
                break;
           case CHAT_MESSAGE : break;
           default : assert false : "Unexpected command name :"+ cmd.getName();
        }
    }

    public void updateTables(OnlineGameTableList tableList) {  
        onlineGameTablesTable_.removeAllRows();

        for (int i = 0; i < tableList.size(); i++) {
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

            // send an event to close the new player window. (perhaps we could know its a dialog and close it directly?)
            ChangeEvent event = new ChangeEvent(this);
            gameStartedListener_.stateChanged(event);

            //GameCommand startCmd = new GameCommand(GameCommand.Name.START_GAME, readyTable);
            //controller_.getServerConnection().sendCommand(startCmd);
        }
    }
    
    
    /**
     * An online table has filled with players and is ready to start.
     */
    protected void startGame()
    {
        System.out.println("Start the game for player:" + currentName_);
        //controller_.setPlayers(onlineGameTablesTable_.getSelectedTable().getPlayersAsArray());
    }

    /**
     * Implements actionlistener.
     * The user has done something to change the table list
     * (e.g. added a new game table or joined a different table).
     */
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();

        if (nameChecksOut()) {
            if (source == createTableButton_) {        
                createNewGameTable();            
            }
            else {         
                joinDifferentTable((JoinButton) source);
            }
        }
    }

    private boolean nameChecksOut() {
        boolean checksOut = !localPlayerName_.getText().equals(DEFAULT_NAME);
         if (!checksOut) {
            JOptionPane.showMessageDialog(this, "You must enter your name at the top first.", "Warning",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
        return checksOut;
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
            controller_.getServerConnection().nameChanged(oldName_, currentName_);
            oldName_ = currentName_;
        }

        boolean canceled = createGameTableDialog_.showDialog();

        if (!canceled)  {

            MultiGameOptions options = (MultiGameOptions)createGameTableDialog_.getOptions();

            OnlineGameTable newTable =
                onlineGameTablesTable_.createOnlineTable(currentName_, options);

            // now add it to this list as a new row and tell the server to add it.
            // onlineGameTablesTable_.addRow(newTable);
            controller_.getServerConnection().addGameTable(newTable);
        }
    }

    /**
     * The local user has clicked  a join button on a different table
     * indicating that they want to join that table.
     */
    private void joinDifferentTable(JoinButton b) {

        int joinRow = b.getRow();
        System.out.println("in join different table. row="+ joinRow);
        PlayerTableModel m = onlineGameTablesTable_.getPlayerModel();

        for (int i=0; i < m.getRowCount(); i++) {
            // you can join tables other than the one you are at as long as they are not already playing.
            boolean enableJoin = (i != joinRow) && !onlineGameTablesTable_.getGameTable(i).isReadyToPlay();
            m.setValueAt(enableJoin, i, 0);
        }
        controller_.getServerConnection().joinTable(
                          onlineGameTablesTable_.createPlayerForName(currentName_),
                          onlineGameTablesTable_.getGameTable(joinRow));

        onlineGameTablesTable_.getTable().removeEditor();
    }

    /**
     * called when the user closes the online game dialog.
     * We remove them form the active tables.
     */
    public void closing() {
        System.out.println(currentName_+ " is now leaving the room ");
        controller_.getServerConnection().leaveRoom(currentName_);
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
            controller_.getServerConnection().nameChanged(oldName_, currentName_);
            oldName_ = currentName_;
        }
    }

    /**
     * Implement moustListener interface.
     */
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        if (localPlayerName_.getText().equals(DEFAULT_NAME))
            localPlayerName_.setText("");
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}
