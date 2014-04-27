// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.aikido.generation;

import com.barrybecker4.common.util.FileUtil;
import org.w3c.dom.Document;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Barry Becker
 */
public class AppHtmlGenerator {

    /** location where data files are read from */
    public static final String PROJECT_DIR =
            FileUtil.getHomeDir() + "apps/source/com/barrybecker4/apps/aikido/generation/";

    private static final String JAVASCRIPT_FILE = PROJECT_DIR + "methods.js";
    private static final String BODY_HTML_FILE = PROJECT_DIR + "body.html";


    public AppHtmlGenerator() {}

    /**
     * Auto generate the html app based on the XML file.
     *
     * @param document
     * @param fileName
     * @throws IOException
     */
    public void generateHTMLApp( Document document, String fileName) throws IOException {
        System.out.println("file = " + fileName + " doc = " + document);
        FileOutputStream fos = new FileOutputStream(fileName);

        fos.write(HtmlUtil.getHTMLHead("Aikido Technique Builder").getBytes());
        fos.write(HtmlUtil.getScriptOpen().getBytes());
        fos.write(new XmlToJsConverter().generateJavaScript(document).getBytes());
        fos.write(LocalFileUtil.readTextFile(JAVASCRIPT_FILE).getBytes());
        fos.write(HtmlUtil.getScriptClose().getBytes());
        fos.write("</head>\n".getBytes());
        fos.write(LocalFileUtil.readTextFile(BODY_HTML_FILE).getBytes());
        fos.write("</html>\n".getBytes());
        fos.close();
    }

}
