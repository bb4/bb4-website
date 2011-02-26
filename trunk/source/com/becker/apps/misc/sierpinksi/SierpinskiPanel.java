package com.becker.apps.misc.sierpinksi;

import com.becker.common.math.function.Function;
import com.becker.simulation.graphing.FunctionType;
import com.becker.ui.components.NumberInput;
import com.becker.ui.renderers.FunctionRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Draws a recursive Sierpinksi triangle.
 * @author Barry Becker
 */
public class SierpinskiPanel extends JPanel {

    SierpinskiRenderer renderer;

    public SierpinskiPanel() {
        renderer = new SierpinskiRenderer();
    }

    public void setRecursiveDepth(int depth) {
        renderer.setDepth(depth);
    }

    @Override
    public void paint( Graphics g ) {
        renderer.setSize(getWidth(), getHeight());
        renderer.paint(g);
    }
}
