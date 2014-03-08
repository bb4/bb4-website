package com.barrybecker4.apps.misc.brian.cs2014projects.fractal;

import java.applet.*;
import java.awt.*;


public class Julia extends Applet {

    private LogisticJuliaPlot canvas;

    public void init() {
        setLayout(new BorderLayout());
        canvas = new LogisticJuliaPlot();
        add("Center", canvas);

    }
}