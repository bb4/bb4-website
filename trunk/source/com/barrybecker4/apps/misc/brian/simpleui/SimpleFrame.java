package com.barrybecker4.apps.misc.brian.simpleui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SimpleFrame extends JFrame
                         implements ActionListener {

    /** Used to show some text after a button is clicked */
    JTextArea taskOutput;

    /** Constructor */
    public SimpleFrame() {
        super("Simple Frame");

        setContentPane(createContent());
        pack();
        setVisible(true);
    }

    private JPanel createContent() {

        JButton startButton = new JButton("Start");
        startButton.addActionListener(this);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setLineWrap(true);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(startButton, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        return contentPanel;
    }

    /** Called when the user presses the start button. */
    public void actionPerformed(ActionEvent evt) {

        taskOutput.setText("The button was clicked, Yada yada yada yada yada ...");
    }

    /** Entry point for program */
    public static void main(String[] args) {
        new SimpleFrame();
    }
}
