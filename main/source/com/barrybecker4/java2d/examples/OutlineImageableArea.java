package com.barrybecker4.java2d.examples;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class OutlineImageableArea
{
    public static void main( String[] args )
    {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable( new OutlinePrintable() );
        if ( pj.printDialog() ) {
            try {
                pj.print();
            } catch (PrinterException e) {
                System.out.println( e );
            }
        }
    }

    private OutlineImageableArea() {}

    static final class OutlinePrintable
        implements Printable
    {
        @Override
        public int print( Graphics g, PageFormat pf, int pageIndex )
        {
            if ( pageIndex != 0 ) return NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            Rectangle2D outline = new Rectangle2D.Double(
                    pf.getImageableX(), pf.getImageableY(),
                    pf.getImageableWidth(), pf.getImageableHeight() );
            g2.setPaint( Color.black );
            g2.draw( outline );
            return PAGE_EXISTS;
        }
    }
}
