package com.becker.java2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

public class SwingPrinter
        extends JFrame
{
    public static void main( String[] args )
    {
        new SwingPrinter();
    }

    private PageFormat mPageFormat;

    public SwingPrinter()
    {
        super( "SwingPrinter v1.0" );
        createUI();
        PrinterJob pj = PrinterJob.getPrinterJob();
        mPageFormat = pj.defaultPage();
        setVisible( true );
    }

    protected void createUI()
    {
        setSize( 300, 300 );
        center();

        // Add the menu bar.
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu( "File", true );
        file.add( new FilePrintAction() ).setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_P, Event.CTRL_MASK ) );
        file.add( new FilePageSetupAction() ).setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_P,
                        Event.CTRL_MASK | Event.SHIFT_MASK ) );
        file.addSeparator();
        file.add( new FileQuitAction() ).setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_Q, Event.CTRL_MASK ) );
        mb.add( file );
        setJMenuBar( mb );

        // Add the contents of the window.
        getContentPane().add( new com.becker.java2d.PatchworkComponent() );

        // Exit the application when the window is closed.
        addWindowListener( new WindowAdapter()
        {
            public void windowClosing( WindowEvent e )
            {
                System.exit( 0 );
            }
        } );
    }

    protected void center()
    {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension us = getSize();
        int x = (screen.width - us.width) / 2;
        int y = (screen.height - us.height) / 2;
        setLocation( x, y );
    }

    public class FilePrintAction
            extends AbstractAction
    {
        public FilePrintAction()
        {
            super( "Print" );
        }

        public void actionPerformed( ActionEvent ae )
        {
            PrinterJob pj = PrinterJob.getPrinterJob();
            ComponentPrintable cp = new ComponentPrintable( getContentPane() );
            pj.setPrintable( cp, mPageFormat );
            if ( pj.printDialog() ) {
                try {
                    pj.print();
                } catch (PrinterException e) {
                    System.out.println( e );
                }
            }
        }
    }

    public class FilePageSetupAction
            extends AbstractAction
    {
        public FilePageSetupAction()
        {
            super( "Page setup..." );
        }

        public void actionPerformed( ActionEvent ae )
        {
            PrinterJob pj = PrinterJob.getPrinterJob();
            mPageFormat = pj.pageDialog( mPageFormat );
        }
    }

    public class FileQuitAction
            extends AbstractAction
    {
        public FileQuitAction()
        {
            super( "Quit" );
        }

        public void actionPerformed( ActionEvent ae )
        {
            System.exit( 0 );
        }
    }
}