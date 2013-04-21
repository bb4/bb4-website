/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.nativeRender;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class NativeRenderTest {

    private static Random RANDOM = new Random(0);

    public static void main(String args[])  {
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

        //int mode = RANDOM.nextInt(displayModes.magnitude);
        for (int i=0; i<displayModes.length;  i++) {
            DisplayMode displayMode = displayModes[i];
            System.out.println(i +" "+ displayMode.getWidth() + "x" +
                 displayMode.getHeight() + " \t" + displayMode.getRefreshRate() + " / " + displayMode.getBitDepth());
        }

        int lastWidth = -1;
        for (int j=0 ; j < displayModes.length; j+=1) {
            DisplayMode displayMode = displayModes[j];
            if (displayMode.getWidth() != lastWidth && displayMode.getWidth() > 1000) {
                lastWidth = displayMode.getWidth();
                if (graphicsDevice.isDisplayChangeSupported()) {
                    graphicsDevice.setDisplayMode(displayMode);
                    drawShapes(frame);
                }
                else {
                    System.out.println("changing the display mod is not supported. :(");
                }
            }
        }

        graphicsDevice.setDisplayMode(originalDisplayMode);
        graphicsDevice.setFullScreenWindow(null);
        System.exit(0);
    }



    private static final int RECT_WIDTH_VARIANCE = 200;
    private static final int RECT_HEIGHT_VARIANCE = 200;

    private static void drawShapes(JFrame frame) {

        frame.createBufferStrategy(2);
        BufferStrategy bufferStrategy = frame.getBufferStrategy();
        int width = frame.getWidth();
        int height = frame.getHeight();
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

        g.drawString("width=" + width +" height="+ height, 20, 20);

        for (int i=0; i < 2000; i++) {

             g.setColor(new Color(RANDOM.nextInt()));
             g.fillRoundRect(RANDOM.nextInt(width), RANDOM.nextInt(height),
                     10 + RANDOM.nextInt(RECT_WIDTH_VARIANCE), 10 + RANDOM.nextInt(RECT_HEIGHT_VARIANCE),
                     20, 20);
             bufferStrategy.show();
        }
        g.dispose();
    }
}