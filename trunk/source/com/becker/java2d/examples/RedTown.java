package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;
import com.becker.ui.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RedTown extends ApplicationFrame
{
   
    public RedTown(String title)
    {
        super( title );
    }

    protected void createUI()
    {
        super.createUI();
    
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
    }
    
    public static void main( String[] args )
    {
        new RedTown("Red Town");
    }

}