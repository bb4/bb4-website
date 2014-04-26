// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.aikido;

/**
 * @author Barry Becker
 */
public class HtmlUtil {

    static String getHTMLHead(String title) {
         return "<!DOCTYPE>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
                + "<title>"+title+"</title>"
                + "\n\n";
    }

    static String getScriptOpen() {
        return "<script language=\"JavaScript\">";
    }

    static String getScriptClose() {
        return "</script>";
    }
}
