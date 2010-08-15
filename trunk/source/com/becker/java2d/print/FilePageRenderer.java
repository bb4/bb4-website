package com.becker.java2d.print;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.*;
import java.util.Vector;

public class FilePageRenderer extends JComponent
                              implements Printable
{
    private int mCurrentPage;
    // mLines contains all the lines of the file.
    private Vector mLines;
    // mPages is a Vector of Vectors. Each of its elements
    //   represents a single page. Each of its elements is
    //   a Vector containing Strings that are the lines for
    //   a particular page.
    private Vector mPages;
    private Font mFont;
    private int mFontSize;
    private Dimension mPreferredSize;

    public FilePageRenderer( File file, PageFormat pageFormat )
            throws IOException
    {
        mFontSize = 12;
        mFont = new Font( "Serif", Font.PLAIN, mFontSize );
        // Open the file.
        BufferedReader in = new BufferedReader(
                new FileReader( file ) );
        // Read all the lines.
        String line;
        mLines = new Vector();
        while ( (line = in.readLine()) != null )
            mLines.addElement( line );
        // Clean up.
        in.close();
        // Now paginate, based on the PageFormat.
        paginate( pageFormat );
    }

    public void paginate( PageFormat pageFormat )
    {
        mCurrentPage = 0;
        mPages = new Vector();
        float y = mFontSize;
        Vector page = new Vector();
        for ( int i = 0; i < mLines.size(); i++ ) {
            String line = (String) mLines.elementAt( i );
            page.addElement( line );
            y += mFontSize;
            if ( y + mFontSize * 2 > pageFormat.getImageableHeight() ) {
                y = 0;
                mPages.addElement( page );
                page = new Vector();
            }
        }
        // Add the last page.
        if ( page.size() > 0 ) mPages.addElement( page );
        // Set our preferred size based on the PageFormat.
        mPreferredSize = new Dimension( (int) pageFormat.getImageableWidth(),
                (int) pageFormat.getImageableHeight() );
        repaint();
    }

    public void paintComponent( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        // Make the background white.
        java.awt.geom.Rectangle2D r = new java.awt.geom.Rectangle2D.Float( 0, 0,
                mPreferredSize.width, mPreferredSize.height );
        g2.setPaint( Color.white );
        g2.fill( r );
        // Get the current page.
        Vector page = (Vector) mPages.elementAt( mCurrentPage );
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

    public int print( Graphics g, PageFormat pageFormat, int pageIndex )
    {
        if ( pageIndex >= mPages.size() ) return NO_SUCH_PAGE;
        int savedPage = mCurrentPage;
        mCurrentPage = pageIndex;
        Graphics2D g2 = (Graphics2D) g;
        g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        paint( g2 );
        mCurrentPage = savedPage;
        return PAGE_EXISTS;
    }

    public Dimension getPreferredSize()
    {
        return mPreferredSize;
    }

    public int getCurrentPage()
    {
        return mCurrentPage;
    }

    public int getNumPages()
    {
        return mPages.size();
    }

    public void nextPage()
    {
        if ( mCurrentPage < mPages.size() - 1 )
            mCurrentPage++;
        repaint();
    }

    public void previousPage()
    {
        if ( mCurrentPage > 0 )
            mCurrentPage--;
        repaint();
    }
}