package com.becker.java2d.examples;

import com.becker.ui.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RedTown
        extends JFrame
{
    public static void main( String[] args )
    {
        new RedTown();
    }

    public RedTown()
    {
        super( "RedTown v1.0" );
        createUI();
        setVisible( true );
    }

    protected void createUI()
    {
        setSize( 400, 400 );
        setLocation( 100, 100 );
        getContentPane().setLayout( new GridBagLayout() );
        GradientButton colorButton = new GradientButton( "Choose a color..." );
        getContentPane().add( colorButton );
        colorButton.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent ae )
            {
                Color c = JColorChooser.showDialog(
                        RedTown.this, "Choose a color...", getBackground() );
                if ( c != null ) getContentPane().setBackground( c );
            }
        } );

        addWindowListener( new WindowAdapter()
        {
            public void windowClosing( WindowEvent we )
            {
                System.exit( 0 );
            }
        } );
    }
}