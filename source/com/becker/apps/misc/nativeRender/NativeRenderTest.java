package com.becker.apps.misc.nativeRender;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class NativeRenderTest {
    
    private static Random RANDOM = new Random(0);

    public static void main(String args[]) {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        DisplayMode displayModes[] = graphicsDevice.getDisplayModes();
        DisplayMode originalDisplayMode = graphicsDevice.getDisplayMode();
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setIgnoreRepaint(true);

        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(frame);
        }
         
        int mode = RANDOM.nextInt(displayModes.length);
        for (int i=0; i<displayModes.length;  i++) {
            DisplayMode displayMode = displayModes[i];
            System.out.println(i +" "+ displayMode.getWidth() + "x" +
                 displayMode.getHeight() + " \t" + displayMode.getRefreshRate() + " / " + displayMode.getBitDepth());           
        }
        
        DisplayMode displayMode = displayModes[116];
        if (graphicsDevice.isDisplayChangeSupported()) {
            graphicsDevice.setDisplayMode(displayMode);
            drawShapes(frame);
        }
        else {
            System.out.println("changing the display more id not supported. :(");
        }
        
        
        graphicsDevice.setDisplayMode(originalDisplayMode);
        graphicsDevice.setFullScreenWindow(null);
        System.exit(0);
    }
  
    private static void drawShapes(JFrame frame) {
      
        frame.createBufferStrategy(2);
        BufferStrategy bufferStrategy = frame.getBufferStrategy();
        int width = frame.getWidth();
        int height = frame.getHeight();
        for (int i=0; i < 2000; i++) {
             Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
             g.setColor(new Color(RANDOM.nextInt()));
             g.fillRoundRect(RANDOM.nextInt(width), RANDOM.nextInt(height), 100, 100, 20, 20);
             bufferStrategy.show();
             g.dispose();
        }      
    }
}