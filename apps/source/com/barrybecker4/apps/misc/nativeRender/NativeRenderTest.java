/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.nativeRender;

import com.barrybecker4.common.concurrency.ThreadUtil;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.util.Random;

/**
 * This is pretty dangerous to run. It takes over the display.
 */
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
        showDisplayModes(displayModes);


        drawShapesInEachDisplayMode(graphicsDevice, displayModes, frame);

        graphicsDevice.setDisplayMode(originalDisplayMode);
        graphicsDevice.setFullScreenWindow(null);
        System.exit(0);
    }

    private static void showDisplayModes(DisplayMode[] displayModes) {
        //int mode = RANDOM.nextInt(displayModes.magnitude);
        System.out.println("There are " + displayModes.length + " different display modes on this device.");
        for (int i = 0; i < displayModes.length;  i++) {
            DisplayMode displayMode = displayModes[i];
            System.out.println(i +" "+ displayMode.getWidth() + "x" +
                 displayMode.getHeight() + " \t" + displayMode.getRefreshRate() + " / " + displayMode.getBitDepth());
        }
    }

    private static void drawShapesInEachDisplayMode(GraphicsDevice graphicsDevice, DisplayMode[] displayModes, JFrame frame) {
        int lastWidth = -1;
        for (DisplayMode displayMode : displayModes) {
            if (displayMode.getWidth() != lastWidth && displayMode.getWidth() > 1200) {
                lastWidth = displayMode.getWidth();
                if (graphicsDevice.isDisplayChangeSupported()) {
                    graphicsDevice.setDisplayMode(displayMode);
                    drawShapes(frame);
                } else {
                    System.out.println("changing the display mode to " + displayMode.toString()
                            + " is not supported. :(");
                }
            }
        }
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

        for (int i = 0; i < 2000; i++) {

             g.setColor(new Color(RANDOM.nextInt()));
             g.fillRoundRect(RANDOM.nextInt(width), RANDOM.nextInt(height),
                     10 + RANDOM.nextInt(RECT_WIDTH_VARIANCE), 10 + RANDOM.nextInt(RECT_HEIGHT_VARIANCE),
                     20, 20);
        }
        bufferStrategy.show();
        ThreadUtil.sleep(2000);

        g.dispose();
    }
}