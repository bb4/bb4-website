package com.becker.aikido;

import org.w3c.dom.*;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

/**
 *
 * User: Barry Becker
 * Date: Oct 15, 2004
 * Time: 8:33:06 AM
 *
 * Instructions for creating an Aikido technique app:
 *   1. fill in the <aikdo_technique>.xml file. Its dtd is hierarchy.dtd.  It assumes one root.
 *   2. Take pictures corresponding to nodes in hierarchy using camcorder.
 *   3. Run this program to generate becker/projects/javascript_projects/aikido_builder/technique_builder.html
 *   4. upload technique_builder.html and corresponding images to website.
 */
public class AikidoAppGenerator {

    private static String imgPath_ = null;
    private static final String IMG_SUFFIX = "_s.jpg";

    //private static final String RESULT_FILE = "/home/becker/projects/java_projects/dist/technique_builder.html";
    private static final String RESULT_FILE = "/home/becker/projects/javascript_projects/aikido_builder/technique_builder.html";

    private static String getHTMLHead() {
         String head =  "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta content=\"text/html; charset=ISO-8859-1\""
                + "http-equiv=\"content-type\">"
                + "<title>Aikido Technique Builder</title>"
                + "\n\n";
         return head;
    }

    private static String getScriptOpen() {
        return "<script>";
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
        StringBuffer buf = new StringBuffer();

        buf.append("\n");
        buf.append("  // setup structures for grammar\n");
        buf.append("  var next = new Array();\n");
        buf.append("  var img = new Array();\n");
        buf.append("  var label = new Array();\n\n");

        // assume that we have a single root under the document root
        NodeList children = document.getChildNodes();
        assert (children.getLength() == 2): "Expected one root instead got "+children.getLength();
        Node root = children.item(1);

        //System.out.println("child 1="+children.item(0).getNodeName()+" "+children.item(0).getNodeValue());
        //System.out.println("child 2="+children.item(1).getNodeName()+" "+children.item(1).getNodeValue());

        imgPath_ = getAttribute(root, "imgpath");

        buf.append( genJSForNode(root, document));
        buf.append("\n");

        return buf.toString();
    }

    /**
     * recursively generate the javascript structures
     * @param node
     * @param doc
     */
    private static String genJSForNode(Node node, Document doc)
    {
        // first print the img and label for the node, then next ptrs for all children,
        // then do the same for all its children
        StringBuffer buf = new StringBuffer();
        NamedNodeMap attribMap = node.getAttributes();
        String id = null;
        String img = null;
        String label = null;
        if (attribMap!=null) {
            for (int i=0; i<attribMap.getLength(); i++) {
                Node attr = attribMap.item(i);
                if (attr.getNodeName().equals("id"))
                    id = attr.getNodeValue();
                else if (attr.getNodeName().equals("img"))
                    img = imgPath_ + attr.getNodeValue() + IMG_SUFFIX;
                else if (attr.getNodeName().equals("label"))
                    label = attr.getNodeValue();
            }
        }
        //assert (id!=null);
        if (id==null)  {
            System.out.println("null id for "+node.getNodeName()+" "+node.getNodeValue());
            //return  "";
        }

        NodeList children = node.getChildNodes();

        if (id!=null) {
            buf.append("  img['"+id+"']='"+img+"';\n");
            buf.append("  label['"+id+"']='"+label+"';\n\n");

            int len = children.getLength();
            if (len > 0)
                buf.append("  next['"+id+"']= new Array();\n");
            for (int i=0; i<len; i++) {
                Node child = children.item(i);
                buf.append("  next['"+id+"']["+i+"]='"+getAttribute(child, "id")+"';\n");
            }
            if (len > 0)
                buf.append("\n");
        }
        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            buf.append( genJSForNode(child, doc));
        }

        return buf.toString();
    }

    private static String getAttribute(Node node, String attribName) {
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
        assert (attributeVal!=null):
                "no attribute named "+attribName+" for node "+node.getNodeName()+" "+node.getNodeValue();
        return attributeVal;
    }

    private static String getJSMethods() {

        String getTableMethod = "  function getTable() {\n"
          + "    return document.getElementById(\"techniqueTable\");\n  }\n\n";

        String showValsMethod =  "  function showVals(selectedVal, valuesList)\n"
          + "  {\n"
          + "    var textList = \"selectedVal=\"+selectedVal+\"\\n\";\n"
          + "    for (var i=0; i<valuesList.length; i++) {\n"
          + "      textList += valuesList[i] + \"\\n\";"
          + "    }\n"
          + "    alert(textList);\n"
          + "  }\n\n";


        String selectChanged =
            "  // When called, we delete all future selects (and corresponding img) and create a single next one. \n"
          + "  // \n"
          + "  function selectChanged(selectId) { \n"
          + "    var elSelect = document.getElementById(selectId);\n"
          + "    var sNum = elSelect.id.substring(4);\n"
          + "    var stepNum = parseInt(sNum);\n"
          + "    selectedVal = elSelect.options[elSelect.selectedIndex].value;\n"
          + "    valuesList = next[selectedVal];\n\n"
          + "    var table = getTable();\n"
          + "    var selectRow = table.rows[0];\n"
          + "    var imageRow = table.rows[1];\n"
          + "    var fillerRow = table.rows[2];\n"
          + "    //fillerRow.childNodes[0].setAttribute(\"colspan\", stepNum+1);\n"
          + "    fillerRow.childNodes[0].colspan = stepNum+1;\n\n"
          + "    // delete future selects\n"
          + "    var len = selectRow.childNodes.length;\n"
          + "    //alert(\"len-stepNum-2=\"+(len-stepNum-2)+\" selectRow.childNodes=\"+selectRow.childNodes);\n\n"
          + "    // delete steps up to the final filler td\n"
          + "    for (var i=len-2; i>stepNum; i--) {\n"
          + "      selectRow.removeChild(selectRow.childNodes[i]);\n"
          + "      imageRow.removeChild(imageRow.childNodes[i]);\n"
          + "    }\n\n"

          + "    var currentImage = imageRow.childNodes[stepNum].childNodes[0].childNodes[0];\n"
          + "    currentImage.src = img[selectedVal];\n\n"

          + "    // add the new select and corresponding image\n"
          + "    var tdSelect = document.createElement(\"td\");\n"
          + "    var newSelect = document.createElement(\"select\");\n"
          + "    var newSelectId = 'step'+(stepNum+1)+'_select'\n"
          + "    newSelect.setAttribute('id', newSelectId);\n"
          + "    newSelect.onchange = function anonymous() { selectChanged( newSelectId ); };\n"
          + "    \n"
          + "    var nextSelectOptions = next[selectedVal];\n"
          + "    //alert(\"nextSelectOptions=\"+nextSelectOptions);\n"
          + "    var onlyOneChild = false;"
          + "    if (nextSelectOptions) {\n"
          + "      onlyOneChild = true;"
          + "      if (nextSelectOptions.length > 1) {\n;"
          + "        onlyOneChild = false;\n"
          + "        // the first one is -----\n;"
          + "        option = document.createElement(\"option\");\n"
          + "        var nextOpt = \"-----\";\n"
          + "        option.value = nextOpt;\n"
          + "        option.innerText = nextOpt;\n"
          + "        newSelect.appendChild(option);\n"
          + "      }\n"
          + "      for (var i=0; i<nextSelectOptions.length; i++) {\n"
          + "        option = document.createElement(\"option\");\n"
          + "        var nextOpt = nextSelectOptions[i];\n"
          + "        option.value = nextOpt;\n"
          + "        option.innerText = label[nextOpt];\n"
          + "        //alert(\"about to add \"+option.outerHTML);\n"
          + "        newSelect.appendChild(option);\n"
          + "      }\n"
          + "    }\n"
          + "    else\n"
          + "      return;\n\n"

          + "    tdSelect.appendChild(newSelect);\n\n"

          + "    // and image\n"
          + "    var tdImage = document.createElement(\"td\");\n"
          + "    var newImageAnchor = document.createElement(\"a\");\n"
          + "    var newImage = document.createElement(\"img\");\n"
          + "    var imageId = 'step'+(stepNum+1)+'_image';\n"
          + "    newImageAnchor.onmouseover =  function anonymous() { mousedOnThumbnail(imageId); };\n"
          + "    newImage.setAttribute('id', imageId);\n"
          + "    newImage.setAttribute('src', onlyOneChild?img[nextSelectOptions[0]]:'');\n"
          + "    \n"
          + "    newImage.setAttribute(\"border\", 0);\n"
          + "    newImageAnchor.appendChild(newImage);\n"
          + "    tdImage.appendChild(newImageAnchor);\n"
          + "    \n"
          + "    selectRow.insertBefore(tdSelect, selectRow.childNodes[stepNum+1]);\n"
          + "    imageRow.insertBefore(tdImage, imageRow.childNodes[stepNum+1]);\n"
          + "    if (onlyOneChild) { // add the next one too \n"  
          + "       selectChanged(newSelectId); "
          + "    }\n"
          + "  }\n";


        String mousedOnThumbnail =
          "  // show a big image when mousing over the thumbnail\n"
        + "  //\n\n"
        + "  function mousedOnThumbnail(imgId) {\n"
        + "    var elImg = document.getElementById(imgId);\n"
        + "    var bigImg = document.getElementById('big_image');\n"
        + "    var newSrc = elImg.src.replace(\"_s.\", \"_m.\");\n"
        + "    bigImg.src = newSrc\n"
        + "  }\n\n";

        String doOnload =
            "  // called when page loads\n"
          + "  //\n"
          + "  function doOnLoad() {\n"
          + "  }\n";

        return  getTableMethod + showValsMethod + selectChanged + mousedOnThumbnail + doOnload;
    }


    private static String getBody() {
        String body =
            "<body onload=\"doOnLoad()\">\n"
          + "<big><big style=\"font-weight: bold; text-decoration: underline;\">Aikido\n"
          + "Technique Builder</big></big><br>\n"
          + "<br>\n"
          + "Build an aikido technique using successive dropdowns below.<br>\n"
          + "For simplicity, we currently restrict the attack to katate dori.<br>\n"
          + "<br>\n\n"

          + "<table id='outerTable' width=\"100%\" border=\"0\">\n"
          + "  <tr>\n"
          + "    <td>\n"
          + "      <div style=\"width:1070px; overflow: auto;\">\n\n"

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
          + "        <img id=\"step0_image\" name=\"step1img\" src=\"images/katate_dori/katate_dori_s.jpg\" border=\"0\">\n"
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
          + "        <img id=\"big_image\" name=\"step1img\" src=\"images/katate_dori/katate_dori_s.jpg\" border=\"1\">\n"
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

        FileOutputStream fos = new FileOutputStream(fileName);

        fos.write(getHTMLHead().getBytes());
        fos.write(getScriptOpen().getBytes());
        fos.write(generateHierarchyStructures(document).getBytes());
        fos.write(getJSMethods().getBytes());
        fos.write(getScriptClose().getBytes());
        fos.write("</head>\n".getBytes());
        fos.write(getBody().getBytes());
        fos.write("</html>\n".getBytes());

        fos.close();
    }


    // -----------------------------------------------------------------
    public static void main(String argv[])
    {
        Document document;
        if (argv.length != 1) {
            //document = DomUtil.buildDom();
            return;
        }

        File file = new File(argv[0]);
        document = DomUtil.parseXMLFile(file);

        try {

            generateHTMLAppFromDom( document, RESULT_FILE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
