package com.becker.apps.sierpinski;

import com.becker.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Contains the area to draw the Sierpinski triangle and UI configuration controls at the top above it.
 *
 * @author Barry Becker
 */
public class SierpinskiComponent extends JPanel implements ActionListener {

    private static final int INITIAL_RECURSIVE_DEPTH = 1;
    private static final int MAX_RECURSIVE_DEPTH = 10;

    SierpinskiPanel sierpinskiPanel;

    NumberInput depthField;
    JButton drawButton;

    /**
     * Constructor
     */
    public SierpinskiComponent() {
        createUI();
    }

    private void createUI() {

        this.setLayout(new BorderLayout());

        sierpinskiPanel = new SierpinskiPanel();
        this.add(createControlsPanel(), BorderLayout.NORTH);
        this.add(sierpinskiPanel, BorderLayout.CENTER);
    }

    private JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel(new FlowLayout());

        depthField =
                new NumberInput("Recursive depth:  ", INITIAL_RECURSIVE_DEPTH,
                                "This the amount of detail that will be shown.", 1, MAX_RECURSIVE_DEPTH, true);
        drawButton = new JButton("Draw it!");
        drawButton.addActionListener(this);

        controlsPanel.add(depthField);
        controlsPanel.add(drawButton);
        return controlsPanel;
    }

    /**
     * Called when the "draw" button is pressed.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == drawButton) {
            int depth = Math.min(depthField.getIntValue(), MAX_RECURSIVE_DEPTH);
            sierpinskiPanel.setRecursiveDepth(depth);
            sierpinskiPanel.repaint();
        }
    }
}
