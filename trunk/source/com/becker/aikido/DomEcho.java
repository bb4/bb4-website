package com.becker.aikido;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;


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
                 NamedNodeMap attribMap = adpNode.domNode.getAttributes();
                 String attribs = getAttributeList(attribMap);

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


    public static void buildDom()
    {
        DocumentBuilderFactory factory =
           DocumentBuilderFactory.newInstance();
        try {
          DocumentBuilder builder = factory.newDocumentBuilder();
          document = builder.newDocument();  // Create from whole cloth

          Element root =
                  (Element) document.createElement("rootElement");
          document.appendChild(root);
          //root.appendChild( document.createTextNode("Some") );
          //root.appendChild( document.createTextNode(" ")    );
          //root.appendChild( document.createTextNode("text") );

          // normalize text representation
          // getDocumentElement() returns the document's root node
          //document.getDocumentElement().normalize();


        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        }
    } // buildDom

    public static String getAttributeList(NamedNodeMap attribMap)
    {
        String attribs = "";
        if (attribMap!= null) {
            attribMap.getLength();
            //String id = attribMap.

            for (int i=0; i<attribMap.getLength(); i++) {
                Node n = attribMap.item(i);
                attribs += n.getNodeName()+"="+n.getNodeValue() +"\n";
            }
        }
        return attribs;
    }

    public static void printTree(Node root, int level) {
        NodeList l = root.getChildNodes();
        for (int i=0; i<level; i++)
            System.out.print("    ");

        NamedNodeMap attribMap = root.getAttributes();
        String attribs = getAttributeList(attribMap);

        System.out.println("Node: "+root.getNodeName()+"  attribs:"+ attribs+")");
        for (int i=0; i<l.getLength(); i++) {
           printTree(l.item(i), level+1);
        }
    }


    public static void cleanupDocument(Node node) {
        NodeList l = node.getChildNodes();

        java.util.List deleteList = new java.util.ArrayList();

        for (int i=0; i<l.getLength(); i++) {
            Node n = l.item(i);
            String name = n.getNodeName();
            if (name!=null && name.startsWith("#text"))  {
                // delete if nothing by whitespace
                String text = n.getNodeValue();
                if (text.matches("[ \\t\n\\x0B\\f\\r]*")) {
                    System.out.println("TEXT="+text+"|so removing");
                    deleteList.add(n);
                    //node.removeChild(n);
                }
            }

            cleanupDocument(n);

            if (name!=null && name.equals("use")) {
                // substitue the element with the specified id
                NamedNodeMap attrs = n.getAttributes();
                Node attr =  attrs.item(0);
                assert attr.getNodeName().equals("ref"): "attr name="+attr.getNodeName();
                String attrValue = attr.getNodeValue();

                System.out.println("searching for "+attrValue);
                Node element = document.getElementById(attrValue);
                Node clonedElement = element.cloneNode(true);
                node.replaceChild(clonedElement, n);
            }
        }

        Iterator it = deleteList.iterator();
        while (it.hasNext()) {
            Node n = (Node)it.next();
            node.removeChild(n);
        }
    }


    public static void main(String argv[])
    {
        if (argv.length != 1) {
            buildDom();
            makeFrame();
            return;
        }

        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        //factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse( new File(argv[0]) );


            Element e = document.getElementById("slide1");
            //System.out.println("### e="+e.getTextContent());

            cleanupDocument(document);
            //printTree(document, 0);
            makeFrame();


        } catch (SAXException sxe) {
           // Error generated during parsing)
           Exception  x = sxe;
           if (sxe.getException() != null)
               x = sxe.getException();
           x.printStackTrace();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        } catch (IOException ioe) {
           // I/O error
           ioe.printStackTrace();
        }
    } // main


}
