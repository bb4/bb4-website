package com.becker.misc.nativeRender;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.Random;

public class NativeRenderTest {

  public static void main(String args[]) {
    GraphicsEnvironment graphicsEnvironment =
      GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice graphicsDevice =
      graphicsEnvironment.getDefaultScreenDevice();
    DisplayMode displayModes[] = graphicsDevice.getDisplayModes();
    DisplayMode originalDisplayMode = graphicsDevice.getDisplayMode();
    JFrame frame = new JFrame();
    frame.setUndecorated(true);
    frame.setIgnoreRepaint(true);

    if (graphicsDevice.isFullScreenSupported()) {
        graphicsDevice.setFullScreenWindow(frame);
    }
    Random random = new Random();
    int mode = random.nextInt(displayModes.length);
    DisplayMode displayMode = displayModes[mode];
    System.out.println(displayMode.getWidth() + "x" +
        displayMode.getHeight() + " \t" + displayMode.getRefreshRate() +
        " / " + displayMode.getBitDepth());
    if (graphicsDevice.isDisplayChangeSupported()) {
        graphicsDevice.setDisplayMode(displayMode);
    }
    frame.createBufferStrategy(2);
    BufferStrategy bufferStrategy = frame.getBufferStrategy();
    int width = frame.getWidth();
    int height = frame.getHeight();
    for (int i=0; i<1000; i++) {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.setColor(new Color(random.nextInt()));
        g.fillRect(random.nextInt(width),
         random.nextInt(height), 100, 100);
         bufferStrategy.show();
         g.dispose();
    }
    graphicsDevice.setDisplayMode(originalDisplayMode);
    graphicsDevice.setFullScreenWindow(null);
    System.exit(0);
  }
}