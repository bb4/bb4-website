// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.aikido.generation;

import com.barrybecker4.common.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Generate expressions for all possible statements that the grammar can produce
 * @author Barry Becker
 */
public class AllTechniqueHtmlGenerator {

    /** provides optional configuration options to use when creating the page */
    private AllTechniqueConfig config;

    /** Default constructor */
    public AllTechniqueHtmlGenerator() {
        this(new AllTechniqueConfig());
    }

    /** Constructor with formatting configuration specified */
    public AllTechniqueHtmlGenerator(AllTechniqueConfig config) {
        this.config = config;
    }

    /**
     * Auto generate all elements based on the XML file.
     *
     * @param document contains all the techniques in XML.
     * @param fileName file to write to.
     * @throws IOException if error writing to the specified file.
     */
    public void generateAllElementsFromDom(Document document, String fileName)
            throws IOException {

        FileOutputStream fos = new FileOutputStream(fileName);

        fos.write(HtmlUtil.getHTMLHead("All Aikido Techniques (from katate dori)").getBytes());
        fos.write("</head>\n".getBytes());
        fos.write(getAllBody(document).getBytes());
        fos.write("</html>\n".getBytes());
        fos.close();
    }

    /**
     * @param document document object model of all techniques
     * @return html body containing a table of all the techniques.
     */
    private String getAllBody(Document document) {

        String body =
            "<body>\n"
          + "<big><big style=\"font-weight: bold; text-decoration: underline;\">Aikido\n"
          + "Techniques</big></big><br>\n"
          + "<br>\n"
          + "This page contains all techniques for, katate dori.<br> " +
                    "If you see an error send mail to BarryBecker4@yahoo.com.<br>\n"
          + "<font size='-1'>This application was built using XML, java and DHTML " +
                    "(<a href='technique_builder_desc.html'>more details</a>).</font> "
          + "<br><br>\n\n"

          + "<table id='outerTable' border=\"0\">\n"
          + "  <tr>\n"
          + "    <td>\n"
          + "      <div style=\"width:100%; overflow: auto; font-family:arial; font-size:"+config.fontSize+";\">\n\n"

          + getTechniqueTable(document)

          + "      </div>\n"
          + "    </td>\n"
          + "  </tr>\n"
          + "</table> \n\n"
          + "<br>\n"
          + "</body> \n";

        return body;
    }

    private String getTechniqueTable(Document document) {
        StringBuilder buf = new StringBuilder();
        List<NodeInfo> parentList = new LinkedList<>();

        buf.append("<table id='techniqueTable' width=\"100%\" cellspacing=\"1\" cellpadding=\"2\" border=\"")
                .append(config.borderWidth).append("\">\n");

        Node root = document.getDocumentElement();
        String imgPath = DomUtil.getAttribute(root, "imgpath");
        buf.append( genRowForNode(root, parentList, imgPath));

        buf.append("</table>\n\n");

        return buf.toString();
    }

    /**
     * @return html for the row representing the technique
     */
    private String genRowForNode(Node node, List<NodeInfo> parentList, String imgPath) {

        StringBuilder buf = new StringBuilder();
        NodeInfo nodeInfo = new NodeInfo(imgPath, node.getAttributes());

        if (nodeInfo.getId() == null) {
            System.out.println("null id for " + node.getNodeName() + ' ' + node.getNodeValue());
            //return  "";
        }
        parentList.add(nodeInfo);

        NodeList children = node.getChildNodes();

        if (children.getLength()==0 && nodeInfo.getId() != null) {
            // then we have a child node, so print a row corresponding to a technique
            techniqueStepsRow(parentList, buf);
            if (config.showImages) {
                thumbnailImageRow(parentList, buf);
            }
        }

        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            buf.append( genRowForNode(child, parentList, imgPath));
        }

        parentList.remove(parentList.size()-1);

        return buf.toString();
    }

    private void techniqueStepsRow(List<NodeInfo> parentList, StringBuilder buf) {
        buf.append("  <tr style=\"white-space:nowrap; font-size:").append(config.fontSize).append("\">\n");
        for (int i=1; i < parentList.size(); i++) {
            NodeInfo info = parentList.get(i);
            buf.append("    <td style=\"width:50px; max-width:150px; height:20; overflow:hidden;\">\n");
            String label = config.debug ? info.getId() : info.getLabel();
            buf.append(label);

            /*
            buf.append("      <span title=\"").append(label)
                   .append("\" style=\"overflow:hidden; font-size:")
                   .append(config.fontSize).append("\">\n");
            // .append("\" style=\"height:14px; width:100px; overflow:hidden; font-size: 9;\">\n");
            buf.append(label).append("</span>\n");  */
            buf.append("    </td>\n");
        }
        buf.append("  </tr>\n");
    }

    private void thumbnailImageRow(List<NodeInfo> parentList, StringBuilder buf) {
        buf.append("  <tr>\n");
        for (int i=1; i < parentList.size(); i++) {
            NodeInfo info = parentList.get(i);
            buf.append("    <td>\n");
            buf.append("      <img src=\"").append(info.getImage()).append("\" height=\""
                    + config.imageSize + "\" title=\"").append(info.getLabel()).append("\">\n");

            buf.append("    </td>\n");
        }
        buf.append("  </tr>\n");
    }
}
