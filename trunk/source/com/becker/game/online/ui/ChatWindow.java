package com.becker.game.online.ui;

import com.becker.game.online.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Allows IM chatting with other online players.
 * @@ shoudl word wrap instead of have horx scrollbar
 *
 * @author Barry Becker Date: Jan 7, 2007
 */
public class ChatWindow extends JPanel implements OnlineChangeListener, KeyListener {

    private ServerConnection connection_;
    private JTextArea textArea_;
    private JScrollPane scrollPane_;
    private JTextField messageField_;

    public ChatWindow(ServerConnection connection) {
        setLayout(new BorderLayout());
        connection_ = connection;
        connection_.addOnlineChangeListener(this);

        textArea_ = new JTextArea();
        textArea_.setBackground(getBackground());
        scrollPane_ = new JScrollPane(textArea_);


        messageField_ = new JTextField();
        messageField_.addKeyListener(this);
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new JLabel("Chat:"), BorderLayout.WEST);
        messagePanel.add(messageField_, BorderLayout.CENTER);

        add(scrollPane_, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.SOUTH);
    }

    public void handleServerUpdate(GameCommand cmd) {
        if (cmd.getName() == GameCommand.Name.CHAT_MESSAGE)  {
            textArea_.append(cmd.getArgument().toString());
            // if a scrollbar is showing, then make sure it is scrolled to the bottom to see the latest message.

            JViewport vp = scrollPane_.getViewport();
            vp.setViewPosition(new Point(0, vp.getHeight()));
            textArea_.append("\n");
        }
    }


    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '\n') {
            String txt = messageField_.getText();
            messageField_.setText("");
            connection_.sendCommand(new GameCommand(GameCommand.Name.CHAT_MESSAGE, txt));
        }
    }

    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}
