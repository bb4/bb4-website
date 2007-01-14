package com.becker.game.common.online.ui;

import com.becker.game.online.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Allows IM chatting with other online players.
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
        textArea_.setWrapStyleWord(true);
        textArea_.setLineWrap(true);
        scrollPane_ = new JScrollPane(textArea_);
        scrollPane_.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        messageField_ = new JTextField();
        messageField_.addKeyListener(this);
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new JLabel("Chat:"), BorderLayout.WEST);
        messagePanel.add(messageField_, BorderLayout.CENTER);

        add(scrollPane_, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.SOUTH);
    }

    /**
     * Post messages from other players.
     */
    public void handleServerUpdate(GameCommand cmd) {
        if (cmd.getName() == GameCommand.Name.CHAT_MESSAGE)  {
            textArea_.append(cmd.getArgument().toString());
            // if a scrollbar is showing, then make sure it is scrolled to the bottom to see the latest message.
            scrollPane_.getVerticalScrollBar().setValue(scrollPane_.getVerticalScrollBar().getMaximum());
            textArea_.append("\n");
        }
    }


    /**
     * Send the message when you press enter.
     */
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '\n') {
            String txt = messageField_.getText();
            if (txt.trim().length() > 0) {
                messageField_.setText("");
                connection_.sendCommand(new GameCommand(GameCommand.Name.CHAT_MESSAGE, txt));
            }
        }
    }

    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}
