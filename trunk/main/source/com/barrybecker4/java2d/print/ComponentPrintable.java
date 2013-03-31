package com.barrybecker4.java2d.print;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class ComponentPrintable implements Printable {
    private Component mComponent;

    public ComponentPrintable( Component c ) {
        mComponent = c;
    }

    @Override
    public int print( Graphics g, PageFormat pageFormat, int pageIndex ) {
        if ( pageIndex > 0 ) return NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D) g;
        g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        boolean wasBuffered = disableDoubleBuffering( mComponent );
        mComponent.paint( g2 );
        restoreDoubleBuffering( mComponent, wasBuffered );
        return PAGE_EXISTS;
    }

    private boolean disableDoubleBuffering( Component c ) {
        if (!(c instanceof JComponent)) return false;
        JComponent jc = (JComponent) c;
        boolean wasBuffered = jc.isDoubleBuffered();
        jc.setDoubleBuffered( false );
        return wasBuffered;
    }

    private void restoreDoubleBuffering( Component c, boolean wasBuffered ) {
        if ( c instanceof JComponent )
            ((JComponent) c).setDoubleBuffered( wasBuffered );
    }
}