package com.becker.xml;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * User: Barry Becker
 * Date: Oct 17, 2004
 */
public class DomUtil {

    private DomUtil() {};

    /**
     * Initialize a dom document structure.
     * @return dom Document
     */
    public static Document buildDom()
    {
        Document document = null;
        DocumentBuilderFactory factory =
           DocumentBuilderFactory.newInstance();
        try {
          DocumentBuilder builder = factory.newDocumentBuilder();
          document = builder.newDocument();  // Create from whole cloth

          Element root = document.createElement("rootElement");
          document.appendChild(root);

          // normalize text representation
          // getDocumentElement() returns the document's root node
          document.getDocumentElement().normalize();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        }
        return document;
    }

    /**
     * go through the dom hier and remove spurious text nodes and alse
     * replace "use" nodes with a deep copy of what they refer to.
     * @param root
     * @param document
     */
    public static void postProcessDocument(Node root, Document document, boolean replaceUseWithDeepCopy) {
        NodeList l = root.getChildNodes();

        List deleteList = new ArrayList();

        for (int i=0; i<l.getLength(); i++) {
            Node n = l.item(i);
            String name = n.getNodeName();
            if (name!=null && name.startsWith("#text"))  {
                // delete if nothing by whitespace
                String text = n.getNodeValue();
                if (text.matches("[ \\t\n\\x0B\\f\\r]*")) {
                    //System.out.println("TEXT="+text+"|so removing");
                    deleteList.add(n);
                    //node.removeChild(n);
                }
            }

            postProcessDocument(n, document, replaceUseWithDeepCopy);

            if (name!=null && "use".equals(name)) {
                // substitue the element with the specified id
                NamedNodeMap attrs = n.getAttributes();
                Node attr =  attrs.item(0);
                assert "ref".equals(attr.getNodeName()): "attr name="+attr.getNodeName();
                String attrValue = attr.getNodeValue();

                System.out.println("searching for "+attrValue);
                Node element = document.getElementById(attrValue);
                Node clonedElement = element.cloneNode(replaceUseWithDeepCopy);

                // now we still need to recursively clean the node that was replaced
                // since it might also contain use nodes.
                postProcessDocument(clonedElement, document, replaceUseWithDeepCopy);

                root.replaceChild(clonedElement, n);
            }
        }

        Iterator it = deleteList.iterator();
        while (it.hasNext()) {
            Node n = (Node)it.next();
            root.removeChild(n);
        }
    }

    /**
     * get the value for an attribute.
     * Error if the attribute does not exist.
     */
    public static String getAttribute(Node node, String attribName) {

        String attributeVal = getAttribute(node, attribName, null);
        assert (attributeVal != null):
                "no attribute named '"+attribName+"' for node '"+node.getNodeName()+"' val="+node.getNodeValue();
        return attributeVal;
    }

     /**
     * get the value for an attribute. If not found, defaultValue is used.
     */
    public static String getAttribute(Node node, String attribName, String defaultValue) {
        NamedNodeMap attribMap = node.getAttributes();
        String attributeVal = null;
        if (attribMap == null)
            return null;
        //assert (attribMap!=null) : "no attributes for " +node.getNodeName()+" "+node.getNodeValue();

        for (int i=0; i<attribMap.getLength(); i++) {
            Node attr = attribMap.item(i);
            if (attr.getNodeName().equals(attribName))
                attributeVal = attr.getNodeValue();
        }
        if (attributeVal == null)
           attributeVal = defaultValue;

        return attributeVal;
    }

    /**
     * a concatenated list of the node's attributes.
     * @param attribMap
     * @return
     */
    public static String getAttributeList(NamedNodeMap attribMap)
    {
        String attribs = "";
        if (attribMap!= null) {
            attribMap.getLength();
            //String id = attribMap.

            for (int i=0; i<attribMap.getLength(); i++) {
                Node n = attribMap.item(i);
                attribs += n.getNodeName()+"=\""+n.getNodeValue() +"\"  ";
            }
        }
        return attribs;
    }

    /**
     * print a text representation of the dom hierarchy.
     * @param root
     * @param level
     */
    public static void printTree(Node root, int level) {
         NodeList l = root.getChildNodes();
         for (int i=0; i<level; i++)
             System.out.print("    ");

         NamedNodeMap attribMap = root.getAttributes();
         String attribs = getAttributeList(attribMap);

         System.out.println("Node: <"+root.getNodeName()+">  "+ attribs);
         for (int i=0; i<l.getLength(); i++) {
            printTree(l.item(i), level+1);
         }
     }

    /**
     * parse an xml file and return a cleaned up Document object.
     * Set replaceUseWithDeepCopy to false if you are in a debug mode and don't want to see a lot of redundant subtrees.
     * @param stream some input stream.
     * @param replaceUseWithDeepCopy if true then replace each instance of a use node with a deep copy of what it refers to
     * @return the parsed file as a Document
     */
    public static Document parseXMLFile(InputStream stream, boolean replaceUseWithDeepCopy)
    {
        Document document = null;
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        //factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse( stream );

            postProcessDocument(document, document, replaceUseWithDeepCopy);
            //printTree(document, 0);

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
        return document;
    }


    public static Document parseXMLFile(URL url)
    {
        try {

            URLConnection urlc = url.openConnection();
            InputStream is = urlc.getInputStream();
            return parseXMLFile(is, true);
        } catch  (IOException e) {
            System.out.println("Failed ");
            e.printStackTrace();
        }
        return null;
    }

    public static Document parseXMLFile(File file)
    {
        return parseXMLFile(file, true);
    }

    public static Document parseXMLFile(File file, boolean replaceUseWithDeepCopy)
    {
        try {
            FileInputStream str = new FileInputStream(file);
            return parseXMLFile(str, replaceUseWithDeepCopy);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
