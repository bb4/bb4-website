package com.becker.game.online.ui;

import com.becker.game.common.*;
import com.becker.game.online.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Manage the online game tables.
 * Allows a player to join exactly one virtual table and begin playing against other players online.
 * If the player creates a table, he sets the options for it.
 *
 * The server maintains the global state.
 * Any time something changes, the server broadcasts the global state to the online clients.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public abstract class OnlineGameDialog extends JDialog
                                       implements OnlineChangeListener, ActionListener {

    // this allows us to talk with the game server (if it is available).
    protected ServerConnection serverConnection_;

    /**
     * the options get set directly on the game controller that is passed in.
     */
    protected GameController controller_;

    // cache a pointer to this in case we have children.
    protected Frame parent_ = null;

    protected ViewerCallbackInterface viewer_;


    protected OnlineGameDialog(Frame parent, ViewerCallbackInterface viewer) {
        parent_ = parent;
        viewer_ = viewer;
        controller_ = viewer.getController();
        serverConnection_ = createServerConnection(this);

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        setTitle( "Manage Online Games" );

        this.setModal( false );

        if (!GUIUtil.isStandAlone())
            this.setAlwaysOnTop(true);

        initGUI();
        pack();
    }

    protected void initGUI() {

        JPanel playOnlinePanel = createPlayOnlinePanel();
        this.getContentPane().add( playOnlinePanel );

        this.addWindowListener( new WindowAdapter()
        {
            public void windowClosing( WindowEvent e ) {
               // closing();    // @@ should we leave our table if closing this dlg?
            }
        } );

    }

    public abstract void closing();

    public boolean isServerAvailable() {
        return serverConnection_ != null && serverConnection_.isConnected();
    }

    public void handleServerUpdate(GameCommand cmd) {

        GameContext.log(1, "got an update of the table from the server:\n" + cmd);
    }

    /**
     * Subclasses need to provide a more interesting implementation of this if they
     * want to support online play.
     */
    protected JPanel createPlayOnlinePanel()
    {
        JPanel playOnlinePanel = new JPanel();
        playOnlinePanel.setLayout( new BoxLayout( playOnlinePanel, BoxLayout.Y_AXIS ) );


        JPanel p = new JPanel();
        p.setLayout( new BorderLayout() );
        p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                     "Play Online" ) );
        p.setMaximumSize( new Dimension( 400, 60 ) );

        JLabel label = new JLabel("Join an existing table with other online players.");
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( label );

        playOnlinePanel.add( p );

        return playOnlinePanel;
    }

    protected abstract ServerConnection createServerConnection(OnlineChangeListener listener);

    /**
     * @return true if there is a live connection to the game server.
     */
    public boolean isConnected() {
        return serverConnection_.isConnected();
    }


    /**
     * @param parent frame.
     */
    public void setParentFrame(Frame parent) {
        parent_ = parent;
    }

    public void showDialog()
    {
        if (parent_ != null)  {
            this.setLocationRelativeTo( parent_ );
        }

        this.setVisible( true );
        this.toFront();
        this.pack();
    }


}
