/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.wator;

import java.awt.*;

/**
 * See http://www.leinweb.com/snackbar/wator/
 */
public class GraphPanel extends Panel {

    private int x;
    private int y;
    private int pos;
    private Color cr;
    private Color cg;
    private Color cb;
    private boolean cont;
    private int stab[];
    private int ftab[];
    private Color ctab[];

    public void init(Color r, Color g, Color b) {

        cr = r;
        cg = g;
        cb = b;
        setBackground(cb);
        initializeBasedOnSize();
        ctab[0] = Color.black;
    }

    private void initializeBasedOnSize() {
        int i;
        x = getWidth() - 2;
        y = getHeight() - 2;
        stab = new int[x];
        ftab = new int[x];
        ctab = new Color[x];
        for (i = 0; i < x; i++) {
            stab[i] = ftab[i] = -1;
            ctab[i] = cb;
        }
    }

    public void brk() {
        Graphics g;
        int i;

        if (!cont) return;
        cont = false;
        synchronized (g = getGraphics()) {
            if ((i = pos + 1) >= x) i = 0;
            ctab[i] = Color.black;
            stab[i] = ftab[i] = -1;
            drawgen(g, i);
            ctab[pos] = Color.white;
            stab[i] = ftab[i] = -1;
            drawgen(g, pos);
            pos = i;
        }
    }

    public void gen(int n, int s, int f) {
        Graphics g;
        int i;

        cont = true;
        synchronized (g = getGraphics()) {

            if (x != (getWidth() - 2) || y != (getHeight() - 2))
            {
                initializeBasedOnSize();
            }
            x = getWidth() - 2;
            y = getHeight() - 2;

            if ((i = pos + 1) >= x) i = 0;
            ctab[i] = Color.black;
            stab[i] = ftab[i] = -1;
            drawgen(g, i);
            stab[pos] = (n - s) * (y - 1) / n + 1;
            ftab[pos] = (n - f) * (y - 1) / n + 1;
            ctab[pos] = cb;
            drawgen(g, pos);
            pos = i;
        }
    }

    @Override
    public void paint(Graphics g) {
        int i;

        synchronized (g) {
            g.setColor(Color.black);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            for (i = 0; i < x; i++) drawgen(g, i);
        }
    }

    public void drawgen(Graphics g, int i) {
        int i0, n0, n;

        g.setColor(ctab[i]);
        g.drawLine(i + 1, 1, i + 1, y);
        i0 = i == 0? x - 1: i - 1;
        if ((n = stab[i]) != -1) {
            n0 = stab[i0];
            if (n0 == -1) n0 = n;
            if (n0 < n) n0++;
            if (n0 > n) n0--;
            g.setColor(cr);
            g.drawLine(i + 1, n0, i + 1, n);
        }
        if ((n = ftab[i]) != -1) {
            n0 = ftab[i0];
            if (n0 == -1) n0 = n;
            if (n0 < n) n0++;
            if (n0 > n) n0--;
            g.setColor(cg);
            g.drawLine(i + 1, n0, i + 1, n);
        }
    }

}