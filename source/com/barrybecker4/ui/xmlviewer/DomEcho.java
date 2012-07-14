/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.xmlviewer;

import com.barrybecker4.common.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

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


/**
 * Graphically view some XML document in a swing UI.
 */
public class DomEcho  extends JPanel
{
    private static final int WINDOW_HEIGHT = 460;
    private static final int LEFT_WIDTH = 300;
    private static final int RIGHT_WIDTH = 340;
    private static final int WINDOW_WIDTH = LEFT_WIDTH + RIGHT_WIDTH;

    public DomEcho(Document document)
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
           new Dimension( LEFT_WIDTH, WINDOW_HEIGHT ));

       // Build right-side view
       // (must be final to be referenced in inner class)
       final
       JEditorPane htmlPane = new JEditorPane();//"text/html","");
       htmlPane.setEditable(false);
       JScrollPane htmlView = new JScrollPane(htmlPane);
       htmlView.setPreferredSize(
           new Dimension( RIGHT_WIDTH, WINDOW_HEIGHT ));

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
       splitPane.setDividerLocation( LEFT_WIDTH );
       splitPane.setPreferredSize(
            new Dimension( WINDOW_WIDTH + 10, WINDOW_HEIGHT+10 ));

       // Add GUI components
       this.setLayout(new BorderLayout());
       this.add("Center", splitPane );
    } // constructor


    public static void makeFrame(Document document) {
        // Set up a GUI framework
        JFrame frame = new JFrame("DOM Echo");
        frame.addWindowListener(
          new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
          }
        );

        // Set up the tree, the views, and display it all
        final DomEcho echoPanel = new DomEcho(document);

        frame.getContentPane().add("Center", echoPanel );
        frame.pack();
        Dimension screenSize =
           Toolkit.getDefaultToolkit().getScreenSize();
        int w = WINDOW_WIDTH + 10;
        int h = WINDOW_HEIGHT + 10;
        frame.setLocation(screenSize.width/3 - (w >> 1),
                          (screenSize.height >> 1) - (h >> 1));
        frame.setSize(w, h);
        frame.setVisible(true);
    } // makeFrame


    // -------------------------------------------------------------------------
    public static void main(String argv[])
    {
        Document document;
        if (argv.length < 1) {
            document = DomUtil.buildDom();
        }
        else {
            File file = new File(argv[0]);
            document = DomUtil.parseXMLFile(file);
        }
        makeFrame(document);
    } // main

}
