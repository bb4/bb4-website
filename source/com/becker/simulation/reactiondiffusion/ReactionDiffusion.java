package com.becker.simulation.reactiondiffusion;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

/*------------------------------->class ReactionDiffusion<-----------------------------------------------
Reaction-diffusion applet by Joakim Linde
Use with for example:
<APPLET CODE="ReactionDiffusion.class" WIDTH=200 HEIGHT=250>
<PARAM NAME=xmax VALUE=100>
<PARAM NAME=ymax VALUE=100>
</APPLET>
*/

public final class ReactionDiffusion extends Applet implements ActionListener, AdjustmentListener {
  Thread runner = null;
  int w;              /*APPLET WIDTH*/
  int h;              /*APPLET HEIGHT*/
  int xmax, ymax;     /*canvas size*/
  int tpf = 10;       /*timesteps per frame*/
  double dt;
  Graphics DB_Graphics;
  Image DB_Image;
  GrayScott grayScott;
  int nColors = 85;
  Color[] color = new Color[3*nColors];
  double[][] u;
  double umax;
  RDCanvas canvas;
  Scrollbar kSlider, fSlider;
  Button restartButton;
  Label fLabel, kLabel;
  final double K0 = 0.079;
  final double F0 = 0.02;

  public void init() {
      w = getSize().width;          /*applet width*/
      h = getSize().height;         /*applet height*/
    u = new double[w][h];

    String p;
    /*canvas width*/
    p = getParameter("xmax");
    if (p == null) p = "100";
    xmax = Integer.valueOf(p).intValue();
    if (xmax < 10) xmax = 10;

    /*canvas height*/
    p = getParameter("ymax");
    if (p == null) p = "100";
    ymax = Integer.valueOf(p).intValue();
    if (ymax < 10) ymax = 10;
    /*create components*/
    restartButton = new Button("Restart");
    restartButton.addActionListener(this);

    canvas = new RDCanvas(this, xmax, ymax);

    int pos = (int)(K0*100.0/0.15);
    kSlider = new Scrollbar(Scrollbar.HORIZONTAL, pos, 1, 0, 200);
    kSlider.addAdjustmentListener(this);

    kLabel = new Label("k =                      ");
    kLabel.setText("k = " + String.valueOf(K0));

    pos = (int)(F0*100.0/0.15);
    fSlider = new Scrollbar(Scrollbar.HORIZONTAL, pos, 1, 0, 200);
    fSlider.addAdjustmentListener(this);

    fLabel = new Label("f =                      ");
    fLabel.setText("f = " + String.valueOf(F0));

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    setLayout(gridbag);

    /*add components*/
    gridbag.setConstraints(canvas, c);
    add(canvas);

    c.gridy = 1;
    gridbag.setConstraints(fLabel, c);
    add(fLabel);

    c.gridy = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    gridbag.setConstraints(fSlider, c);
    add(fSlider);

    c.gridy = 3;
    c.fill = GridBagConstraints.NONE;
    gridbag.setConstraints(kLabel, c);
    add(kLabel);

    c.gridy = 4;
    c.fill = GridBagConstraints.HORIZONTAL;
    gridbag.setConstraints(kSlider, c);
    add(kSlider);

    c.gridy = 5;
    c.fill = GridBagConstraints.NONE;
    gridbag.setConstraints(restartButton, c);
    add(restartButton);
  }

  public void start() {
    if (canvas.runner == null) {
      canvas.runner = new Thread(canvas, "CanvasRunner");
      canvas.runner.start();
    }
  }

  public void stop() {
    canvas.runner = null;
  }

  public void paint(Graphics g) {
      /*draw border*/
    g.setColor(Color.black);
    g.drawLine(0,0,w-1,0);
    g.drawLine(0,0,0,h-1);
    g.drawLine(w-1,h-1,0,h-1);
    g.drawLine(w-1,h-2,0,h-2);
    g.drawLine(w-1,h-1,w-1,0);
    g.drawLine(w-2,h-1,w-2,0);
  }

  public void actionPerformed(ActionEvent e) {
    if (canvas.runner != null) {
      canvas.runner = null;
      canvas.grayScott.initialState();
      canvas.runner = new Thread(canvas, "CanvasRunner");
      canvas.runner.start();
    } else canvas.grayScott.initialState();
  }

  public void adjustmentValueChanged(AdjustmentEvent e) {
    if (e.getAdjustable() == fSlider) {
      canvas.grayScott.setF(fSlider.getValue()*0.15/100.0);
      fLabel.setText("f = " + String.valueOf(canvas.grayScott.f));
    } else {
      canvas.grayScott.setK(kSlider.getValue()*0.15/100.0);
      kLabel.setText("k = " + String.valueOf(canvas.grayScott.k));
    }
  }

}
