/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.colormixer;

import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 * Date: Feb 21, 2005
 */
public class MixPanel extends JPanel {

    /*
    // mix like light
    public static final int ADDITIVE_MIX = 0;
    // mix like paint
    public static final int SUBTRACTIVE_MIX = 1;
    // composite with over operator
    public static final int OVER_MIX = 2;
    // composite with under operator
    public static final int OVER_MIX = 3;
    // composite with under operator
    public static final int OVER_MIX = 4;
    // composite with under operator
    public static final int OVER_MIX = 5;
    // composite with under operator
    public static final int OVER_MIX = 6;
    // composite with under operator
    public static final int UNDER_MIX = 7;

    public static final int NUM_TYPES = 8;
    */
    private static final Font FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY(), Font.BOLD, 16);

    private Color colorA;
    private Color colorB;
    private float opacityA;
    private float opacityB;
    private float opacity = .4f;
    private int type;
    private String label;


    public MixPanel(Color colorA, float opacityA, Color colorB, float opacityB, int type, String label) {
        setColors(colorA, opacityA, colorB, opacityB);
        this.type = type;
        this.label = label;
        //assert (type < NUM_TYPES);
    }

    public void setColors(Color colorA, float opacityA, Color colorB, float opacityB) {
        this.colorA = colorA;
        this.colorB = colorB;

        this.opacityA = opacityA;
        this.opacityB = opacityB;

        this.setDoubleBuffered(false);
        this.invalidate();
        this.repaint();
    }

    public void setOpacity(float op)  {
        opacity = op;
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
        super.paintComponents(g);
        Graphics2D g2 = (Graphics2D)g;

        g2.setColor(this.getBackground());
        g2.fillRect(1, 1, this.getWidth(), this.getHeight());

        g2.setColor(colorA);
        g2.fillRect(10, 1, 100, 30);
        Color mixColor;

        Composite composite = null;
        float opacity = this.opacity;

        composite = AlphaComposite.getInstance(type, opacity);

        /*
        switch (type) {
            case ADDITIVE_MIX :
                //mixColor = interpColors(colorA, colorB);
                //g2.setComposite(AlphaComposite.Dst);
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity);
                System.out.println("add");
                break;
            case SUBTRACTIVE_MIX :
                //mixColor = interpColors(colorA, colorB);
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_IN, opacity);
                //g2.setComposite(AlphaComposite.DstAtop);
                System.out.println("sub");
                break;
            case OVER_MIX :
                //mixColor = interpColors(colorA, colorB);
                //g2.setComposite(AlphaComposite.DstIn);
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_OUT, opacity);
                System.out.println("over");
                break;
            case UNDER_MIX :
                g2.setComposite(AlphaComposite.DstOver);
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
                //mixColor = interpColors(colorA, colorB);
                //g2.setColor(mixColor);
                //g2.fillRect(60, 10, 50, 50);
                System.out.println("under");
                break;
            default:
                assert (false):"invalide type="+type;
        }
        */
        g2.setComposite(composite);
        g2.setColor(colorB);
        g2.fillRect(40, 12, 100, 30);
        g2.setComposite(AlphaComposite.Src);

        g2.setFont(FONT);
        g2.setColor(Color.BLACK);
        g2.drawString(label, 160, 20);
    }
}
