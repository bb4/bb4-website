package com.becker.java2d.examples;

import java.awt.*;

public class ShowDevices
{
    public static void main( String[] args )
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        for ( int i = 0; i < screenDevices.length; i++ )
            System.out.println( screenDevices[i].getIDstring() );
        System.exit( 0 );
    }
}