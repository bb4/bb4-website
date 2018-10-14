/** Copyright by Barry G. Becker, 2004-2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.aikido;

import com.barrybecker4.apps.aikido.generation.AllTechniqueConfig;
import com.barrybecker4.apps.aikido.generation.AllTechniqueHtmlGenerator;
import com.barrybecker4.apps.aikido.generation.AppHtmlGenerator;
import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.common.xml.DomUtil;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;

/**
 * Instructions for creating the Aikido Technique Builder app:
 *   1. fill in the <aikido_technique>.xml file. Its dtd is hierarchy.dtd.  It assumes one root.
 *   2. Take pictures corresponding to nodes in the hierarchy using a digital camera.
 *      Store the images in /projects/javascript_projects/aikido_builder/images/katate_dori (or whichever attack)
 *   3. Run this program on the xml file to generate technique_builder.html
 *      and all_techniques.html in barrybecker4/projects/javascript_projects/aikido_builder/.
 *   4. upload technique_builder.html, all_techniques.html and corresponding images to website.
 *
 * Some interesting links for kubi-shime (also known as ude gurame)
 *  - https://www.youtube.com/watch?v=05hBVD0tHgg
 *  - https://www.youtube.com/watch?v=PgLdErLByRs
 *  - https://www.youtube.com/watch?v=gLijUiaSm2E
 *
 *  Features to add:
 *   - show video for as many leaf nodes as possible. Hard because of ref nodes that represent subtrees.
 *   - add more descriptions
 *
 * @author Barry Becker
 */
public class AikidoAppGenerator {

    private static final String DEFAULT_INPUT_FILE = AppHtmlGenerator.PROJECT_DIR +  "techniques.xml";

    /** A self contained and transferable location. */
    private static final String RESULT_PATH =
            FileUtil.getHomeDir() + "../../../javascript_projects/aikido_builder/";

    /** the builder DHTML application */
    private static final String RESULT_BUILDER_FILE = RESULT_PATH + "technique_builder.html";

    /** all the techniques in one file (for debugging mostly) */
    private static final String RESULT_ALL_FILE = RESULT_PATH + "all_techniques.html";

    /** all the techniques in one file (for debugging mostly) */
    private static final String RESULT_UNIQUE_FILE = RESULT_PATH + "all_unique.html";

    private AikidoAppGenerator() {}

    /**
     * Auto generate the html app based on the XML file.
     * @param document xml document model of techniques
     * @param fileName the tile that will be written to
     */
    public static void generateHTMLAppFromDom(Document document, String fileName)
            throws IOException {

        new AppHtmlGenerator().generateHTMLApp(document, fileName);
    }

    public static void main(String argv[]) {
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
        document = DomUtil.parseXMLFile(file, true);

        try {
            generateHTMLAppFromDom(document, RESULT_BUILDER_FILE);

            AllTechniqueConfig config = new AllTechniqueConfig(false, 160, 10, 0, false);
            new AllTechniqueHtmlGenerator(config).generateAllElementsFromDom(document, RESULT_ALL_FILE);

            config = new AllTechniqueConfig(true, 100, 9, 0, true);
            new AllTechniqueHtmlGenerator(config).generateAllElementsFromDom(document, RESULT_UNIQUE_FILE);
         }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
