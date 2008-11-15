package com.becker.java2d;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

public class ComponentPrintable implements Printable
{
    private Component mComponent;

    public ComponentPrintable( Component c )
    {
        mComponent = c;
    }

    public int print( Graphics g, PageFormat pageFormat, int pageIndex )
    {
        if ( pageIndex > 0 ) return NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D) g;
        g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        boolean wasBuffered = disableDoubleBuffering( mComponent );
        mComponent.paint( g2 );
        restoreDoubleBuffering( mComponent, wasBuffered );
        return PAGE_EXISTS;
    }

    private boolean disableDoubleBuffering( Component c )
    {
        if ( c instanceof JComponent == false ) return false;
        JComponent jc = (JComponent) c;
        boolean wasBuffered = jc.isDoubleBuffered();
        jc.setDoubleBuffered( false );
        return wasBuffered;
    }

    private void restoreDoubleBuffering( Component c, boolean wasBuffered )
    {
        if ( c instanceof JComponent )
            ((JComponent) c).setDoubleBuffered( wasBuffered );
    }
}