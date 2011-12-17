/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.common.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Barry Becker
 */
public class XmlErrorHandler implements ErrorHandler {

    public void warning(SAXParseException exception) throws SAXException {
        handleException("Warning", exception);
    }

    public void error(SAXParseException exception) throws SAXException {
        handleException("Error", exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        handleException("Fatal Error while", exception);
    }

    private static void handleException(String type, SAXParseException exception) {
        System.out.println(type + " parsing at line "+ exception.getLineNumber() +
                           " column " + exception.getColumnNumber());
        exception.printStackTrace();
    }
}
