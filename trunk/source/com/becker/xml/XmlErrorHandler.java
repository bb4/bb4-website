package com.becker.xml;

import org.xml.sax.*;

/**
 * @author Barry Becker Date: Jan 21, 2007
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
