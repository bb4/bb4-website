/** Copyright by Barry G. Becker, 2004-2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.aikido;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.common.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Instructions for creating the Aikido Technique Builder app:
 *   1. fill in the <aikido_technique>.xml file. Its dtd is hierarchy.dtd.  It assumes one root.
 *   2. Take pictures corresponding to nodes in the hierarchy using a digital camera.
 *      Store the images in /projects/javascript_projects/aikido_builder/images/katate_dori (or whichever attack)
 *   3. Run this program on the xml file to generate technique_builder.html
 *      and all_techniques.html in barrybecker4/projects/javascript_projects/aikido_builder/.
 *   4. upload technique_builder.html, all_techniques.html and corresponding images to website.
 *
 * @author Barry Becker
 */
public class AikidoAppGenerator {

    /**
     * if in debug mode then we do the following things differently
     * 1) in the all techniques page, show the ids instead of the cut-points, and make the images bigger.
     * 2) when replacing refs, don't substitute the whole subtree, just the subtree root node.
     */
    private static final boolean DEBUG_MODE = false;

    private static String imgPath_ = null;


    private static final int THUMB_IMG_WIDTH = 170;
    private static final int THUMB_IMG_HEIGHT = 130;



    private static final String PROJECT_DIR = FileUtil.getHomeDir() + "apps/source/com/barrybecker4/apps/aikido/";

    private static final String DEFAULT_INPUT_FILE = PROJECT_DIR +  "katate_dori.xml";
    private static final String JAVASCRIPT_FILE = PROJECT_DIR + "methods.js";

    /**
     * A self contained and transferable location.
     */
    private static final String RESULT_PATH = FileUtil.getHomeDir() + "../../javascript_projects/aikido_builder/";
            //"/apps/dist/aikido_builder/";

    /** the builder DHTML application */
    private static final String RESULT_BULDER_FILE = "technique_builder.html";

    /** all the techniques in one file (for debugging mostly) */
    private static final String RESULT_ALL_FILE = "all_techniques.html";

    private AikidoAppGenerator() {}

    private static String getHTMLHead(String title) {
         return "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<information content=\"text/html; charset=ISO-8859-1\""
                + "http-equiv=\"content-type\">"
                + "<title>"+title+"</title>"
                + "\n\n";
    }

    private static String getScriptOpen() {
        return "<script language=\"JavaScript\">";
    }

    private static String getScriptClose() {
        return "</script>";
    }

    /**
     * set up the javascript array structures based on the xml technique hierarchy.
     * @param document
     * @return javascript string
     */
    private static String generateHierarchyStructures(Document document)
    {
        //return "\n"+ document.toString() + "\n\n";
        StringBuilder buf = new StringBuilder();

        buf.append('\n');
        buf.append("  // setup structures for grammar\n");
        buf.append("  var next = new Array();\n");
        buf.append("  var img = new Array();\n");
        buf.append("  var label = new Array();\n\n");

        Node root = document.getDocumentElement();
        imgPath_ = DomUtil.getAttribute(root, "imgpath");
        buf.append( genJSForNode(root, document));
        buf.append('\n');

        return buf.toString();
    }

    /**
     * recursively generate the javascript structures
     * @param node
     * @param doc
     */
    private static String genJSForNode(Node node, Document doc) {
        // first print the img and label for the node, then next ptrs for all children,
        // then do the same for all its children
        StringBuilder buf = new StringBuilder();
        NodeInfo nodeInfo = new NodeInfo(imgPath_, node.getAttributes());
        if (nodeInfo.getId() == null)  {
            System.out.println("null id for " + node.getNodeName() + ' ' + node.getNodeValue());
        }

        NodeList children = node.getChildNodes();

        if (nodeInfo.getId() != null) {
            buf.append("  img['")
               .append(nodeInfo.getId())
               .append("']='")
               .append(nodeInfo.getImage()).append("';\n");
            buf.append("  label['")
               .append(nodeInfo.getId())
               .append("']='")
               .append(nodeInfo.getLabel()).append("';\n\n");

            int len = children.getLength();
            if (len > 0)
                buf.append("  next['").append(nodeInfo.getId()).append("']= new Array();\n");
            for (int i=0; i<len; i++) {
                Node child = children.item(i);
                buf.append("  next['")
                   .append(nodeInfo.getId())
                   .append("'][").append(i).append("]='")
                   .append(DomUtil.getAttribute(child, "id")).append("';\n");
            }
            if (len > 0)
                buf.append('\n');
        }
        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            buf.append( genJSForNode(child, doc));
        }

        return buf.toString();
    }

    /** @return javascript methods from file as a string */
    private static String getJSMethods() {

        BufferedReader br = null;
        StringBuilder bldr = new StringBuilder(1000);

		try {
			String sCurrentLine;

			br = new BufferedReader(new FileReader(JAVASCRIPT_FILE));

			while ((sCurrentLine = br.readLine()) != null) {
				bldr.append(sCurrentLine).append('\n');
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
        return bldr.toString();
    }


    private static String getAppBody() {
        String body =
            "<body onload=\"doOnLoad()\">\n"
          + "<big><big style=\"font-weight: bold; text-decoration: underline;\">Aikido\n"
          + "Technique Builder</big></big><br>\n"
          + "<br>\n"
          + "Build an aikido technique using successive dropdowns below.<br>\n"
          + "For simplicity, we currently restrict the attack to katate dori.<br><br>\n"
          + "<font size='-1'>This application was built using XML, java and DHTML " +
                    "(<a href='technique_builder_desc.html'>more details</a>).</font> "
          + "<br><br>\n\n"

          + "<table id='outerTable' width=\"100%\" border=\"0\">\n"
          + "  <tr>\n"
          + "    <td>\n"
          + "      <div style=\"width:970px; overflow: auto; font-family:arial; font-size:60%;\">\n\n"

          + "<table id='techniqueTable' width=\"100%\" border=\"1\">\n"
          + "  <tr>   \n"
          + "    <td nowrap>\n"
          + "      <select id=\"step0_select\" onchange=\"selectChanged('step0_select')\">\n"
          + "        <option value='select'>-----</option>\n"
          + "        <option value=\"katate_dori\">katate dori</option>\n"
          + "      </select>\n"
          + "    </td>\n"
          + "    <td nowrap width=\"100%\">\n"
          + "    </td>\n"
          + "  </tr>\n"
          + "  <tr>\n"
          + "    <td nowrap>\n"
          + "      <a onmouseover=\"mousedOnThumbnail('step0_image')\">\n"
          + "        <img id=\"step0_image\" name=\"step1img\" src=\"images/select_s.png\" border=\"0\" width="
                    + THUMB_IMG_WIDTH + " height=" + THUMB_IMG_HEIGHT + ">\n"
          + "      </a>\n"
          + "    </td>\n"
          + "    <td nowrap width=\"100%\">\n"
          + "    </td>\n"
          + "  </tr>\n"
          + "  <tr width=\"100%\" height=\"100%\">\n"
          + "    <td colspan=\"2\"></td>\n"
          + "  </tr>\n"
          + "</table>\n\n"

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


    private static String genRowForNode(Node node, List<NodeInfo> parentList) {

        StringBuilder buf = new StringBuilder();
        NodeInfo nodeInfo = new NodeInfo(imgPath_, node.getAttributes());

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
            buf.append( genRowForNode(child, parentList));
        }

        parentList.remove(parentList.size()-1);

        return buf.toString();
    }


    private static String getTechniqueTable(Document document) {
        StringBuilder buf = new StringBuilder();
        List<NodeInfo> parentList = new LinkedList<>();

        // recursive call
        //buf.append("<table id='techniqueTable' width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\">\n");
        buf.append("<table id='techniqueTable' width=\"100%\" border=\"0\">\n");

        Node root = document.getDocumentElement();
        imgPath_ = DomUtil.getAttribute(root, "imgpath");
        buf.append( genRowForNode(root, parentList));

        buf.append("</table>\n\n");

        return buf.toString();
    }

    /**
     *
     * @param document
     * @return html body containing a table of all the techniques.
     */
    private static String getAllBody(Document document) {

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

    /**
     * Auto generate the html app based on the XML file.
     *
     * @param document
     * @param fileName
     * @throws IOException
     */
    public static void generateHTMLAppFromDom( Document document, String fileName) throws IOException
    {
        System.out.println("file = " + fileName + " doc = " + document);
        FileOutputStream fos = new FileOutputStream(fileName);

        fos.write(getHTMLHead("Aikido Technique Builder").getBytes());
        fos.write(getScriptOpen().getBytes());
        fos.write(generateHierarchyStructures(document).getBytes());
        fos.write(getJSMethods().getBytes());
        fos.write(getScriptClose().getBytes());
        fos.write("</head>\n".getBytes());
        fos.write(getAppBody().getBytes());
        fos.write("</html>\n".getBytes());
        fos.close();
    }

    /**
     * Auto generate all elements based on the XML file.
     *
     * @param document contains all the techniques in XML.
     * @param fileName file to write to.
     * @throws IOException if error writing to the specified file.
     */
    public static void generateAllElementsFromDom( Document document, String fileName)
            throws IOException {

        FileOutputStream fos = new FileOutputStream(fileName);

        fos.write(getHTMLHead("Aikido Techniques (from Katate dori)").getBytes());
        fos.write("</head>\n".getBytes());
        fos.write(getAllBody(document).getBytes());
        fos.write("</html>\n".getBytes());
        fos.close();
    }


    // -----------------------------------------------------------------------------------------------
    public static void main(String argv[])
    {
        Document document;
        String filename = DEFAULT_INPUT_FILE;

        if (argv.length == 1) {
            filename = argv[0];
        }
        else {
            System.out.println("Usage: <xml file containing data>");
            System.out.println("Since no argument was supplied, " + filename +" will be used.");
        }

        File file = new File(filename);
        System.out.println("parsing xml from " + file);
        document = DomUtil.parseXMLFile(file, !DEBUG_MODE);

        try {
            generateHTMLAppFromDom(document, RESULT_PATH + RESULT_BULDER_FILE);
            generateAllElementsFromDom(document, RESULT_PATH + RESULT_ALL_FILE);
         }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
