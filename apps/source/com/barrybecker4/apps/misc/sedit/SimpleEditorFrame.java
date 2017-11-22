/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.sedit;

import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;


public class SimpleEditorFrame extends JFrame {

    public SimpleEditorFrame() {
        super("Simple Editor");

        GUIUtil.setCustomLookAndFeel();

        SimpleEditor editArea = createSimpleEditor();

        JMenuBar menuBar = new EditorMenuBar(editArea);
        getRootPane().setJMenuBar(menuBar);

        createContentPane(editArea);

        pack();
        setVisible(true);
    }

    private SimpleEditor createSimpleEditor() {
        SimpleEditor editArea = new SimpleEditor(30, 45);
        editArea.setEditable(true);
        editArea.setFont(new Font(GUIUtil.DEFAULT_FONT_FAMILY(), Font.PLAIN, 12));
        return editArea;
    }

    private void createContentPane(SimpleEditor editArea) {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(editArea, BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
    }

    public static void main(String[] args) {
        new SimpleEditorFrame();
    }
}
