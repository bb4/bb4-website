/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.apps.sierpinski;

import com.becker.ui.components.NumberInput;
import com.becker.ui.sliders.LabeledSlider;
import com.becker.ui.sliders.SliderChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Contains the area to draw the Sierpinski triangle and UI configuration controls at the top above it.
 *
 * @author Barry Becker
 */
public class SierpinskiComponent extends JPanel implements ActionListener, SliderChangeListener {

    private static final int INITIAL_RECURSIVE_DEPTH = 1;
    private static final int MAX_RECURSIVE_DEPTH = 10;

    SierpinskiPanel sierpinskiPanel;
    LabeledSlider lineWidthSlider;
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
                                "This the amount of detail that will be shown.", 0, MAX_RECURSIVE_DEPTH, true);
        lineWidthSlider = new LabeledSlider("Line Width", SierpinskiRenderer.DEFAULT_LINE_WIDTH, 0.1, 100.0 );
        lineWidthSlider.addChangeListener(this);
        drawButton = new JButton("Draw it!");
        drawButton.addActionListener(this);

        controlsPanel.add(depthField);
        controlsPanel.add(lineWidthSlider);
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

    public void sliderChanged(LabeledSlider slider) {
        sierpinskiPanel.setLineWidth((float)slider.getValue());
        sierpinskiPanel.repaint();
    }
}
