package com.becker.game.common.online.ui;

import com.becker.game.common.online.*;
import com.becker.common.*;
import com.becker.game.common.plugin.PluginManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * UI Frame to show what the GameServer is doing.
 * There is nothing game specific about the server it just accepts commands and delegates to the controller
 * Invoke with
 *   java OnlineGameServer -game <game>
 * for example:
 *   java OnlineGameServer -game poker
 *
 * @author Barry Becker Date: Jan 20, 2007
 */
public class OnlineGameServerFrame  extends JFrame {

    private OnlineGameServer server_;
    private JTextArea textArea_;


    /**
     * Create the online game server to serve all online clients.
     */
    public OnlineGameServerFrame(String gameName) {
        initUI(gameName);
        server_ = new OnlineGameServer(gameName, textArea_);
    }

    /**
     * In the long term there will not be a UI, that is why this class is not itn the ui subpackage.
     */
    private void initUI(String gameName) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Commands received over the socket:");
        textArea_ = new JTextArea(20, 44);
        textArea_.setLineWrap(true);
        textArea_.setWrapStyleWord(true);
   
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        panel.add("North", label);
        
        panel.add("Center", new JScrollPane(textArea_));
        String gameLabel = PluginManager.getInstance().getPlugin(gameName).getLabel();
        setTitle(gameLabel + " Server");

        getContentPane().add(panel);

        WindowListener l = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
        addWindowListener(l);
        pack();
        setVisible(true);
    }


    /**
     * Objects created in run method are finalized when
     * program terminates and thread exits.
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        server_ = null;
    }

    /**
     * create and show the server.
     */
    public static void main(String[] args) {

        CommandLineOptions options = new CommandLineOptions(args);

        if (OnlineGameServer.verifyCmdLineOptions(options)) {
            String gameName = options.getValueForOption(OnlineGameServer.GAME_OPTION);
            OnlineGameServerFrame frame = new OnlineGameServerFrame(gameName);
            frame.setVisible(true);
        }        
    }

}
