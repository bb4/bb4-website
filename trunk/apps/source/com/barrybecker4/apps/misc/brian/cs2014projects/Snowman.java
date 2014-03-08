package com.barrybecker4.apps.misc.brian.cs2014projects;

import java.applet.Applet;

import java.awt.*;


public class Snowman extends Applet
{
    public void paint(Graphics page)

    {
        final int MID = 150;
        final int TOP = 50;

        setBackground(Color.white);
        page.setColor(Color.yellow);
        page.fillOval(120, 80, 80, 80);
        page.setColor(Color.yellow);
        page.fillOval(80, 80, 80, 80);

        page.setColor(Color.yellow);
        page.fillOval(100, 110, 80, 80);
    }
}