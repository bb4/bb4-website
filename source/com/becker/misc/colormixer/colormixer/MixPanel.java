package com.becker.misc.colormixer.colormixer;

import javax.swing.*;
import java.awt.*;

/**
 * User: Barry Becker
 * Date: Feb 21, 2005
 * Time: 3:07:56 PM
 */
public class MixPanel extends JPanel {


    // mix like light
    public static final int ADDITIVE_MIX = 0;
    // mix like paint
    public static final int SUBTRACTIVE_MIX = 1;
    // composite with over operator
    public static final int OVER_MIX = 2;
    // composite with over operator
    public static final int UNDER_MIX = 3;

    public static final int NUM_TYPES = 4;


    private Color colorA_;
    private Color colorB_;
    private float opacityA_;
    private float opacityB_;
    private int type;


    public MixPanel(Color colorA, float opacityA, Color colorB, float opacityB, int type) {
        setColors(colorA, opacityA, colorB, opacityB);
        assert (type < NUM_TYPES);
    }

    public void setColors(Color colorA, float opacityA, Color colorB, float opacityB) {
        colorA_ = colorA;
        colorB_ = colorB;

        opacityA_ = opacityA;
        opacityB_ = opacityB;

        this.setDoubleBuffered(false);
        this.invalidate();
        this.repaint();
    }

    private static Color interpColors(Color color1, Color color2) {
        int r =  (color1.getRed()+color2.getRed())/2;
        int g =  (color1.getRed()+color2.getGreen())/2;
        int b =  (color1.getRed()+color2.getBlue())/2;
        return new Color(r, g, b);
    }

    public void paint(Graphics g) {
        paintComponent(g);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponents( g );
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(colorA_);
        g2.fillRect(10, 10, 50, 50);
        Color mixColor;

        // draw the mixed color int the middle
        switch (type) {
            case ADDITIVE_MIX :
                mixColor = interpColors(colorA_, colorB_);
                g2.setColor(mixColor);
                g2.fillRect(60, 10, 50, 50);
                break;
            case SUBTRACTIVE_MIX :
                mixColor = interpColors(colorA_, colorB_);
                g2.setColor(mixColor);
                g2.fillRect(60, 10, 50, 50);
                break;
            case OVER_MIX :
                mixColor = interpColors(colorA_, colorB_);
                g2.setColor(mixColor);
                g2.fillRect(60, 10, 50, 50);
                break;
            case UNDER_MIX :
                mixColor = interpColors(colorA_, colorB_);
                g2.setColor(mixColor);
                g2.fillRect(60, 10, 50, 50);
                break;
            default:
                assert (false):"invalide type="+type;
        }
        g2.setColor(colorB_);
        g2.fillRect(110, 10, 50, 50);
    }
}
