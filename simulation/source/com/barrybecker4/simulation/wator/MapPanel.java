/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.wator;

import java.applet.Applet;
import java.awt.*;

/**
 * See http://www.leinweb.com/snackbar/wator/
 */
public class MapPanel extends Panel {

    public int ni;
    public int nj;
    public boolean paintflag;
    public Applet that;

    private Color cl_rd;
    private Color cl_gn;
    private Color cl_bl;
    private Fish map[][];
    private int x[];
    private int y[];
    private int dx[];
    private int dy[];


    public MapPanel() {
        cl_rd = new Color(128, 0, 0);
        cl_gn = new Color(0, 64, 0);
        cl_bl = new Color(128, 128, 255);
        setBackground(cl_bl);
    }

    public Color getColor(Color c) {
        if (c == Color.red) return (cl_rd);
        if (c == Color.green) return (cl_gn);
        return (cl_bl);
    }

    public void remix(int newi, int newj) {
        Fish f;
        int i, j, k;

        ni = newi;
        nj = newj;
        x = new int[nj];
        y = new int[ni];
        dx = new int[nj];
        dy = new int[ni];
        k = getWidth() - 2;
        for (i = 0; i < nj; i++)
            dx[i] = ((k * i + k) / nj + 1) - (x[i] = (k * i) / nj + 1);
        k = getHeight() - 2;
        for (i = 0; i < ni; i++)
            dy[i] = ((k * i + k) / ni + 1) - (y[i] = (k * i) / ni + 1);
        map = new Fish[ni][nj];
        for (f = Fish.first(); f != null; f = f.next()) {
            do {
                i = Fish.nrnd(ni);
                j = Fish.nrnd(nj);
            } while (map[i][j] != null);
            map[f.i = i][f.j = j] = f;
        }
    }

    public void gen() {
        Fish f, p;
        int r, i, j, en, pn;
        int[] ei, ej, pi, pj;
        Graphics g12;

        g12 = getGraphics();
        ei = new int[9];
        ej = new int[9];
        pi = new int[9];
        pj = new int[9];
        for (f = Fish.first(); f != null; f = f.next()) {
            en = pn = 0;
            i = f.i; j = f.j;
            if (--i < 0) i += ni;
            if ((p = map[i][j]) == null) { ei[en] = i; ej[en] = j; en++; }
            else if (p.starve < 0) { pi[pn] = i; pj[pn] = j; pn++; }
            i = f.i; j = f.j;
            if (--j < 0) j += nj;
            if ((p = map[i][j]) == null) { ei[en] = i; ej[en] = j; en++; }
            else if (p.starve < 0) { pi[pn] = i; pj[pn] = j; pn++; }
            i = f.i; j = f.j;
            if (++i >= ni) i -= ni;
            if ((p = map[i][j]) == null) { ei[en] = i; ej[en] = j; en++; }
            else if (p.starve < 0) { pi[pn] = i; pj[pn] = j; pn++; }
            i = f.i; j = f.j;
            if (++j >= nj) j -= nj;
            if ((p = map[i][j]) == null) { ei[en] = i; ej[en] = j; en++; }
            else if (p.starve < 0) { pi[pn] = i; pj[pn] = j; pn++; }
            if (f.starve > 0 && pn > 0) {	/* eat */
                r = Fish.nrnd(pn);
                i = pi[r]; j = pj[r];
                map[i][j].starve = 0;
                map[i][j] = null;
                Fish.nfish--;
                f.starve = Fish.sstarve;
            }
            else if (f.starve > 0 && --f.starve == 0) { /* starve */
                Fish.nshark--;
                map[f.i][f.j] = null;
                if (!paintflag) {
                    g12.setColor(cl_bl);
                    g12.fillRect(x[f.j], y[f.i], dx[f.j], dy[f.i]);
                }
                continue;
            }
            else if (en > 0) { /* move */
                r = Fish.nrnd(en);
                i = ei[r]; j = ej[r];
            }
            else {	/* surrounded */
                if (f.breed > 1) f.breed--;
                continue;
            }
            if (--f.breed == 0) { /* breed */
                p = new Fish(f.starve < 0);
                map[p.i = f.i][p.j = f.j] = p;
                if (f.starve < 0) Fish.nfish++; else Fish.nshark++;
                f.breed = f.starve < 0? Fish.fbreed: Fish.sbreed;
            }
            else { /* move from */
                map[f.i][f.j] = null;
                if (!paintflag) {
                    g12.setColor(cl_bl);
                    g12.fillRect(x[f.j], y[f.i], dx[f.j], dy[f.i]);
                }
            }
            map[f.i = i][f.j = j] = f; /* move to */
            if (!paintflag) {
                g12.setColor(f.starve < 0? cl_gn: cl_rd);
                g12.fillRect(x[f.j], y[f.i], dx[f.j], dy[f.i]);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        paintflag = true;
        that.start();
    }

    public void dopaint()  {
        Fish f;
        Image i;
        Graphics g;

        i = createImage(getWidth(), getHeight());
        g = i.getGraphics();
        g.setColor(cl_bl);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        for (f = Fish.first(); f != null; f = f.next()) {
            g.setColor(f.starve < 0? cl_gn: cl_rd);
            g.fillRect(x[f.j], y[f.i], dx[f.j], dy[f.i]);
        }
        getGraphics().drawImage(i, 0, 0, this);
    }

}