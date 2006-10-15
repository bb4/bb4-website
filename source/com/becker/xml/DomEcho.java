package com.becker.xml;

import org.w3c.dom.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;


public class DomEcho  extends JPanel
{
    // Global value so it can be ref'd by the tree-adapter
    static Document document;

    static final int windowHeight = 460;
    static final int leftWidth = 300;
    static final int rightWidth = 340;
    static final int windowWidth = leftWidth + rightWidth;

    public DomEcho()
    {
       // Make a nice border
       EmptyBorder eb = new EmptyBorder(5,5,5,5);
       BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
       CompoundBorder cb = new CompoundBorder(eb,bb);
       this.setBorder(new CompoundBorder(cb,eb));

       // Set up the tree
       JTree tree = new JTree(new DomToTreeModelAdapter(document));

       // Iterate over the tree and make nodes visible
       // (Otherwise, the tree shows up fully collapsed)
       //TreePath nodePath = ???;
       //  tree.expandPath(nodePath);

       // Build left-side view
       JScrollPane treeView = new JScrollPane(tree);
       treeView.setPreferredSize(
           new Dimension( leftWidth, windowHeight ));

       // Build right-side view
       // (must be final to be referenced in inner class)
       final
       JEditorPane htmlPane = new JEditorPane();//"text/html","");
       htmlPane.setEditable(false);
       JScrollPane htmlView = new JScrollPane(htmlPane);
       htmlView.setPreferredSize(
           new Dimension( rightWidth, windowHeight ));

       // Wire the two views together. Use a selection listener
       // created with an anonymous inner-class adapter.
       tree.addTreeSelectionListener(
         new TreeSelectionListener() {
           public void valueChanged(TreeSelectionEvent e) {
             TreePath p = e.getNewLeadSelectionPath();
             if (p != null) {
                 AdapterNode adpNode =
                    (AdapterNode) p.getLastPathComponent();
                 NamedNodeMap attribMap = adpNode.getDomNode().getAttributes();
                 String attribs = DomUtil.getAttributeList(attribMap);

                 htmlPane.setText(attribs);
             }
           }
         }
       );

       // Build split-pane view
       JSplitPane splitPane =
          new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                          treeView,
                          htmlView );
       splitPane.setContinuousLayout( true );
       splitPane.setDividerLocation( leftWidth );
       splitPane.setPreferredSize(
            new Dimension( windowWidth + 10, windowHeight+10 ));

       // Add GUI components
       this.setLayout(new BorderLayout());
       this.add("Center", splitPane );
    } // constructor


    public static void makeFrame() {
        // Set up a GUI framework
        JFrame frame = new JFrame("DOM Echo");
        frame.addWindowListener(
          new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
          }
        );

        // Set up the tree, the views, and display it all
        final DomEcho echoPanel =
           new DomEcho();
        frame.getContentPane().add("Center", echoPanel );
        frame.pack();
        Dimension screenSize =
           Toolkit.getDefaultToolkit().getScreenSize();
        int w = windowWidth + 10;
        int h = windowHeight + 10;
        frame.setLocation(screenSize.width/3 - w/2,
                          screenSize.height/2 - h/2);
        frame.setSize(w, h);
        frame.setVisible(true);
    } // makeFrame


    // -------------------------------------------------------------------------
    public static void main(String argv[])
    {
        if (argv.length != 1) {
            document = DomUtil.buildDom();
            makeFrame();
            return;
        }

        File file = new File(argv[0]);

        document = DomUtil.parseXMLFile(file);

        makeFrame();
    } // main

}
