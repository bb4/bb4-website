package com.barrybecker4.java2d.print;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class FilePageRenderer extends JComponent
                              implements Printable {
    private int currentPage;

    /** lines contains all the lines of the file.   */
    private Vector<String> lines;

    /**
     * Each element represents a single page. Each page's elements is
     * a Vector containing Strings that are the lines for a particular page.
     */
    private Vector<Vector<String>> pages;
    private Font mFont;
    private int mFontSize;
    private Dimension preferredSize;

    public FilePageRenderer( File file, PageFormat pageFormat )
            throws IOException {
        mFontSize = 12;
        mFont = new Font( "Serif", Font.PLAIN, mFontSize );
        // Open the file.
        BufferedReader in = new BufferedReader(
                new FileReader( file ) );
        // Read all the lines.
        String line;
        lines = new Vector<String>();
        while ( (line = in.readLine()) != null )
            lines.addElement( line );
        // Clean up.
        in.close();
        // Now paginate, based on the PageFormat.
        paginate( pageFormat );
    }

    public void paginate( PageFormat pageFormat ) {
        currentPage = 0;
        pages = new Vector<Vector<String>>();
        float y = mFontSize;
        Vector<String> page = new Vector<String>();
        for ( int i = 0; i < lines.size(); i++ ) {
            String line = lines.elementAt( i );
            page.addElement( line );
            y += mFontSize;
            if ( y + mFontSize * 2 > pageFormat.getImageableHeight() ) {
                y = 0;
                pages.addElement(page);
                page = new Vector<String>();
            }
        }
        // Add the last page.
        if ( page.size() > 0 ) pages.addElement( page );
        // Set our preferred size based on the PageFormat.
        preferredSize = new Dimension( (int) pageFormat.getImageableWidth(),
                (int) pageFormat.getImageableHeight() );
        repaint();
    }

    @Override
    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        // Make the background white.
        Rectangle2D r = new Rectangle2D.Float( 0, 0,
                preferredSize.width, preferredSize.height );
        g2.setPaint( Color.white );
        g2.fill( r );
        // Get the current page.
        Vector page = (Vector) pages.elementAt(currentPage);
        // Draw all the lines for this page.
        g2.setFont( mFont );
        g2.setPaint( Color.black );
        float x = 0;
        float y = mFontSize;
        for ( int i = 0; i < page.size(); i++ ) {
            String line = (String) page.elementAt( i );
            if ( line.length() > 0 ) g2.drawString( line, (int) x, (int) y );
            y += mFontSize;
        }
    }

    @Override
    public int print( Graphics g, PageFormat pageFormat, int pageIndex ) {
        if ( pageIndex >= pages.size() ) return NO_SUCH_PAGE;
        int savedPage = currentPage;
        currentPage = pageIndex;
        Graphics2D g2 = (Graphics2D) g;
        g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        paint(g2);
        currentPage = savedPage;
        return PAGE_EXISTS;
    }

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public int getCurrentPage()  {
        return currentPage;
    }

    public int getNumPages() {
        return pages.size();
    }

    public void nextPage() {
        if ( currentPage < pages.size() - 1 ) {
            currentPage++;
        }
        repaint();
    }

    public void previousPage() {
        if ( currentPage > 0 ) {
            currentPage--;
        }
        repaint();
    }
}