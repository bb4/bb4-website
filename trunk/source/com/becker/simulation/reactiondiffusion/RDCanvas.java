package com.becker.simulation.reactiondiffusion;

import java.awt.*;

/**
 *
 */
final class RDCanvas extends Canvas implements Runnable {
  public Thread runner = null;
  int w, h;
  Dimension preferredSize;
  Image DB_Image;
  Graphics DB_Graphics;
  GrayScott grayScott;
  int nColors = 85;
  Color[] color = new Color[3*nColors];
  double[][] u;
  ReactionDiffusion controller;
  double umax;

  public RDCanvas(ReactionDiffusion controller, int w, int h) {
    this.w = w;
    this.h = h;
    this.controller = controller;
    preferredSize = new Dimension(w + 3, h + 3);
    u = new double[w][h];

    for (int i = 0; i<nColors; i++) {
      color[i] = new Color((int)(255*((double)i/nColors)), 0, 0);
    }
    for (int i = 0; i<nColors; i++) {
      color[i+nColors] = new Color(255, (int)(255*((double)i/nColors)), 0);
    }
    for (int i = 0; i<nColors; i++) {
      color[i+2*nColors] = new Color((int)(255*(1.0-(double)i/nColors)), 255,0);
    }

    grayScott = new GrayScott(w, h, controller.F0, controller.K0, 0.01);
  }

  public Dimension getPreferredSize() {
    return preferredSize;
  }

  public void paint(Graphics g) {
    update(g);
  }

  public void update(Graphics g) {
    int cn;
    double uMax = 0;
    if (DB_Graphics == null) {
      DB_Image = createImage(w+3, h+3);
      DB_Graphics = DB_Image.getGraphics();
    }
    for (int x = 0; x<w; x++) {
      for (int y = 0; y<h; y++) {

        cn = (int)((3*nColors-1.0)*u[x][y]/0.4);
        if (cn>(3*nColors-1)) cn = 3*nColors - 1;
        if (cn<0) cn = 0;
        DB_Graphics.setColor(color[cn]);

        DB_Graphics.drawLine(x+1,y+1,x+1,y+1);
      }
    }
    /*draw border*/
    DB_Graphics.setColor(Color.black);
    DB_Graphics.drawLine(0,0,preferredSize.width-1,0);
    DB_Graphics.drawLine(0,0,0,preferredSize.height-1);
    DB_Graphics.drawLine(0,preferredSize.height-2,preferredSize.width-2,preferredSize.height-2);
    DB_Graphics.drawLine(preferredSize.width-2,0,preferredSize.width-2,preferredSize.height-2);
    DB_Graphics.drawLine(0,preferredSize.height-1,preferredSize.width-1,preferredSize.height-1);
    DB_Graphics.drawLine(preferredSize.width-1,0,preferredSize.width-1,preferredSize.height-1);

    g.drawImage(DB_Image, 0, 0, this);
  }

  public void run() {
    Thread myThread = Thread.currentThread();
    while (runner == myThread) {
      for (int i = 0; i<10; i++) {   /*  10 timesteps / frame */
        grayScott.timeStep(1);
      }
      umax = 0;
      for (int x = 0; x<w; x++) {
        for (int y = 0; y<h; y++) {
          u[x][y] = grayScott.v[x][y];
          if (u[x][y]>umax) umax = u[x][y];
        }
      }
      repaint();
      try {
        Thread.sleep(5);
      }
      catch (InterruptedException e) {
      }
    }
  }

}