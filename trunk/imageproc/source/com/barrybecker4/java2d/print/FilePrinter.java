package com.barrybecker4.java2d.print;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class FilePrinter extends JFrame
{
    public static void main( String[] args )
    {
        new FilePrinter();
    }

    private PageFormat mPageFormat;
    private FilePageRenderer mPageRenderer;
    private String mTitle;

    public FilePrinter()
    {
        super( "FilePrinter v1.0" );
        createUI();
        PrinterJob pj = PrinterJob.getPrinterJob();
        mPageFormat = pj.defaultPage();
        setVisible( true );
    }

    protected void createUI()
    {
        setSize( 350, 300 );
        center();
        Container content = getContentPane();
        content.setLayout( new BorderLayout() );

        // Add the menu bar.
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu( "File", true );
        file.add( new FileOpenAction() ).setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_O, Event.CTRL_MASK ) );
        file.add( new FilePrintAction() ).setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_P, Event.CTRL_MASK ) );
        file.add( new FilePageSetupAction() ).setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_P,
                        Event.CTRL_MASK | Event.SHIFT_MASK ) );
        file.addSeparator();
        file.add( new FileQuitAction() ).setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_Q, Event.CTRL_MASK ) );
        mb.add( file );
        JMenu page = new JMenu( "Page", true );
        page.add( new PageNextPageAction() ).setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_DOWN, 0 ) );
        page.add( new PagePreviousPageAction() ).setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP, 0 ) );
        mb.add( page );
        setJMenuBar( mb );

        // Add the contents of the window.
        getContentPane().setLayout( new BorderLayout() );

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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;
        setLocation( x, y );
    }

    public void showTitle()
    {
        int currentPage = mPageRenderer.getCurrentPage() + 1;
        int numPages = mPageRenderer.getNumPages();
        setTitle( mTitle + " - page " + currentPage + " of " + numPages );
    }

    public class FileOpenAction
            extends AbstractAction
    {
        public FileOpenAction()
        {
            super( "Open..." );
        }

        public void actionPerformed( ActionEvent ae )
        {
            // Pop up a file dialog.
            JFileChooser fc = new JFileChooser( "." );
            int result = fc.showOpenDialog( FilePrinter.this );
            if ( result != 0 ) {
                return;
            }
            java.io.File f = fc.getSelectedFile();
            if ( f == null ) {
                return;
            }
            // Load the specified file.
            try {
                mPageRenderer = new FilePageRenderer( f, mPageFormat );
                mTitle = "[" + f.getName() + "]";
                showTitle();
                JScrollPane jsp = new JScrollPane( mPageRenderer );
                getContentPane().removeAll();
                getContentPane().add( jsp, BorderLayout.CENTER );
                validate();
            } catch (java.io.IOException ioe) {
                System.out.println( ioe );
            }
        }
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
            pj.setPrintable( mPageRenderer, mPageFormat );
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
            if ( mPageRenderer != null ) {
                mPageRenderer.paginate( mPageFormat );
                showTitle();
            }
        }
    }

    public static class FileQuitAction
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

    public class PageNextPageAction
            extends AbstractAction
    {
        public PageNextPageAction()
        {
            super( "Next page" );
        }

        public void actionPerformed( ActionEvent ae )
        {
            if ( mPageRenderer != null ) mPageRenderer.nextPage();
            showTitle();
        }
    }

    public class PagePreviousPageAction
            extends AbstractAction
    {
        public PagePreviousPageAction()
        {
            super( "Previous page" );
        }

        public void actionPerformed( ActionEvent ae )
        {
            if ( mPageRenderer != null ) mPageRenderer.previousPage();
            showTitle();
        }
    }
}