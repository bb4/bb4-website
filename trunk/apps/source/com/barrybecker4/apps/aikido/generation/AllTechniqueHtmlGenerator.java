// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.aikido.generation;

import com.barrybecker4.common.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Generate expressions for all possible statements that the grammar can produce
 * @author Barry Becker
 */
public class AllTechniqueHtmlGenerator {

    /** provides optional configuration options to use when creating the page */
    private AllTechniqueConfig config;

    /** used to only show unique images if that is requested */
    private Set<String> imageSet = new HashSet<>();

    /** total number of images shown in table */
    private int imagesShown;
    /** total number of images if all possible images were shown */
    private int potentialImages;

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
          + "This page contains all traditional unarmed standing aikido techniques.<br> "
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
          + "<div>Total number of images shown = " + imagesShown + " (out of "+ potentialImages + " possible)</div>\n"
          + "</body> \n";

        return body;
    }

    /** @return table containing all techniques. Each row may have a different number of cells. */
    private String getTechniqueTable(Document document) {
        StringBuilder buf = new StringBuilder();
        List<NodeInfo> parentList = new LinkedList<>();

        buf.append("<table id='techniqueTable' width=\"100%\" cellspacing=\"1\" cellpadding=\"2\" border=\"")
                .append(config.borderWidth).append("\">\n");

        Node root = document.getDocumentElement();
        String imgPath = DomUtil.getAttribute(root, "imgpath");
        buf.append(genRowForNode(root, parentList, imgPath));

        buf.append("</table>\n\n");

        return buf.toString();
    }

    /**
     * @return html for the row representing the technique. Each cell (td) in the row is a different step.
     */
    private String genRowForNode(Node node, List<NodeInfo> parentList, String imgPath) {

        StringBuilder buf = new StringBuilder();
        NodeInfo nodeInfo = new NodeInfo(imgPath, node);

        if (nodeInfo.getId() == null && config.debug) {
            System.out.println("null id for " + node.getNodeName() + ' ' + node.getNodeValue());
        }
        parentList.add(nodeInfo);

        NodeList children = node.getChildNodes();

        if (children.getLength() == 0 && nodeInfo.getId() != null) {
            // then we are at a leaf node, so print the row corresponding to this technique
            techniqueStepsRow(parentList, buf);
            if (config.showImages) {
                thumbnailImageRow(parentList, buf);
            }
        }

        for (int i=0; i < children.getLength(); i++) {
            Node child = children.item(i);
            buf.append( genRowForNode(child, parentList, imgPath));
        }

        parentList.remove(parentList.size()-1);

        return buf.toString();
    }

    /** Write the row of titles */
    private void techniqueStepsRow(List<NodeInfo> parentList, StringBuilder buf) {
        buf.append("  <tr style=\"white-space:nowrap; font-size:").append(config.fontSize).append("\">\n");
        for (int i=1; i < parentList.size(); i++) {
            NodeInfo info = parentList.get(i);
            String label = config.debug ? info.getId() : info.getLabel();
            buf.append("    <td title=\"")
                    .append(info.getDescription())
                    .append("style=\"width:50px; max-width:150px; height:20; overflow:hidden;\">\n")
                    .append(label);
            buf.append("    </td>\n");
        }
        buf.append("  </tr>\n");
    }

    /** Write the row of thumbnail images under the titles. */
    private void thumbnailImageRow(List<NodeInfo> parentList, StringBuilder buf) {
        buf.append("  <tr>\n");
        for (int i=1; i < parentList.size(); i++) {
            NodeInfo info = parentList.get(i);
            buf.append("    <td>\n");
            if (!config.showOnlyUniqueImages || !imageSet.contains(info.getId())){
                buf.append("      <img src=\"").append(info.getImage()).append("\" height=\""
                        + config.imageSize + "\" title=\"").append(info.getDescription()).append("\">\n");
                imageSet.add(info.getId());
                imagesShown++;
            }
            potentialImages++;
            buf.append("    </td>\n");
        }
        buf.append("  </tr>\n");
    }
}
