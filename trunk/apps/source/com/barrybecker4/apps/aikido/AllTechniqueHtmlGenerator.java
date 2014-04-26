// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.aikido;

import com.barrybecker4.common.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class AllTechniqueHtmlGenerator {

    /**
     * if in debug mode then we do the following things differently
     * 1) in the all techniques page, show the ids instead of the cut-points, and make the images bigger.
     * 2) when replacing refs, don't substitute the whole subtree, just the subtree root node.
     */
    private static final boolean DEBUG_MODE = false;


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

        fos.write(HtmlUtil.getHTMLHead("All Aikido Techniques (from Katate dori)").getBytes());
        fos.write("</head>\n".getBytes());
        fos.write(getAllBody(document).getBytes());
        fos.write("</html>\n".getBytes());
        fos.close();
    }

    /**
     * @param document
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

          + "<table id='outerTable' width=\"100%\" border=\"0\">\n"
          + "  <tr>\n"
          + "    <td>\n"
          + "      <div style=\"width:100%; overflow: auto; font-family:arial; font-size:50%;\">\n\n"

          + getTechniqueTable(document)

          + "      </div>\n"
          + "    </td>\n"
          + "  </tr>\n"
          + "  <tr>\n"
          + "    <td>\n"
          + "      <div id=\"bigImgDiv\">\n"
          + "        <img id=\"big_image\" name=\"step1img\" src=\"images/select_m.png\" border=\"1\">\n"
          + "      </div>\n"
          + "    </td>\n"
          + "  </tr>\n"
          + "</table> \n\n"

          + "<br>\n"
          +"</body> \n";

        return body;
    }

    private String getTechniqueTable(Document document) {
        StringBuilder buf = new StringBuilder();
        List<NodeInfo> parentList = new LinkedList<>();

        // recursive call
        //buf.append("<table id='techniqueTable' width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\">\n");
        buf.append("<table id='techniqueTable' width=\"100%\" border=\"0\">\n");

        Node root = document.getDocumentElement();
        String imgPath = DomUtil.getAttribute(root, "imgpath");
        buf.append( genRowForNode(root, parentList, imgPath));

        buf.append("</table>\n\n");

        return buf.toString();
    }


    private String genRowForNode(Node node, List<NodeInfo> parentList, String imgPath) {

        StringBuilder buf = new StringBuilder();
        NodeInfo nodeInfo = new NodeInfo(imgPath, node.getAttributes());

        if (nodeInfo.getId() == null)  {
            System.out.println("null id for "+node.getNodeName()+' '+node.getNodeValue());
            //return  "";
        }
        parentList.add(nodeInfo);

        NodeList children = node.getChildNodes();

        if ((children.getLength()==0) && (nodeInfo.getId() != null)) {
            // then we have a child node, so print a row corresponding to a technique

            buf.append("  <tr nowrap> \n");
            for (int i=1; i<parentList.size(); i++) {
                NodeInfo info = parentList.get(i);
                buf.append("    <td nowrap>\n");
                if (DEBUG_MODE)  {
                    buf.append("      <div title=\"").append(info.getId()).append("\" style=\"height:14px; width:120px; overflow:hidden;\"\n");
                } else {
                    buf.append("      <div title=").append(info.getLabel()).append("style=\"height:14px; width:90px; overflow:hidden;\"\n");
                }
                buf.append("        <font size='-3'><span>");
                if (DEBUG_MODE)  {
                    buf.append(info.getId());
                }  else {
                    buf.append(info.getLabel());
                }
                buf.append("        </span></font>");
                buf.append("      </div>");
                buf.append("    </td>\n");
            }
            buf.append("  </tr>   \n");
            buf.append("  <tr>   \n");
            for (int i=1; i<parentList.size(); i++) {
                NodeInfo info = parentList.get(i);
                buf.append("    <td>\n");
                //buf.append("      <img src=\""+ info.img +"\" style=\"width:50px; height:44px;\">\n");
                int height = DEBUG_MODE ? 80 : 60;

                buf.append("      <img src=\"").append(info.getImage()).append("\" height=\""
                        + height + "\" title=\"").append(info.getLabel()).append("\">\n");

                buf.append("    </td>\n");
            }
            buf.append("  </tr>   \n");
        }

        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            buf.append( genRowForNode(child, parentList, imgPath));
        }

        parentList.remove(parentList.size()-1);

        return buf.toString();
    }
}
