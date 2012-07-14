package com.barrybecker4.java2d.examples;

import com.barrybecker4.java2d.print.PatchworkComponent;

import java.awt.*;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class Booker {

    public static void main( String[] args ) {
        PrinterJob pj = PrinterJob.getPrinterJob();
        // Create two Printables.
        Component c1 = new PatchworkComponent( "printable1" );
        Component c2 = new PatchworkComponent( "printable2" );
        c1.setSize( 500, 400 );
        c2.setSize( 500, 400 );
        BookComponentPrintable printable1 = new BookComponentPrintable( c1 );
        BookComponentPrintable printable2 = new BookComponentPrintable( c2 );
        // Create two PageFormats.
        PageFormat pageFormat1 = pj.defaultPage();
        PageFormat pageFormat2 = (PageFormat) pageFormat1.clone();
        pageFormat2.setOrientation( PageFormat.LANDSCAPE );
        // Create a Book.
        Book book = new Book();
        book.append( printable1, pageFormat1 );
        book.append( printable2, pageFormat2 );
        // Print the Book.
        pj.setPageable( book );
        if ( pj.printDialog() ) {
            try {
                pj.print();
            } catch (PrinterException e) {
                System.out.println( e );
            }
        }
    }
}

class BookComponentPrintable implements Printable {

    private Component mComponent;

    public BookComponentPrintable( Component c ) {
        mComponent = c;
    }

    public int print( Graphics g, PageFormat pageFormat, int pageIndex )  {
        Graphics2D g2 = (Graphics2D) g;
        g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        mComponent.paint( g2 );
        return PAGE_EXISTS;
    }
}