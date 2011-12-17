package com.becker.java2d.examples;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class PageFormatMania
{
    public static void main( String[] args )
    {
        PrinterJob pj = PrinterJob.getPrinterJob();

        PageFormat pf = pj.defaultPage();
        Paper paper = new Paper();
        double margin = 36; // half inch
        paper.setImageableArea( margin, margin,
                paper.getWidth() - margin * 2,
                paper.getHeight() - margin * 2 );
        pf.setPaper( paper );

        pj.setPrintable( new ManiaPrintable(), pf );
        if ( pj.printDialog() ) {
            try {
                pj.print();
            } catch (PrinterException e) {
                System.out.println( e );
            }
        }
    }
}

class ManiaPrintable
        implements Printable
{
    public int print( Graphics g, PageFormat pf, int pageIndex )
    {
        if ( pageIndex != 0 ) return NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont( new Font( "Serif", Font.PLAIN, 36 ) );
        g2.setPaint( Color.black );
        g2.drawString( "ManiaPrintable", 100, 100 );
        Rectangle2D outline = new Rectangle2D.Double(
                pf.getImageableX(), pf.getImageableY(),
                pf.getImageableWidth(), pf.getImageableHeight() );
        g2.draw( outline );
        return PAGE_EXISTS;
    }
}