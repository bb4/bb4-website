/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.wator;
import java.applet.*;
import java.awt.*;


/**
 * WATOR predator-prey simulation based on A.K. Dewdney "Sharks and fish
 * wage an ecological war on the toroidal planet Wa-Tor" (Scientific
 * American, December 1984).  Source code recovered from my 1996 compiled
 * applet with help from JDK 1.2 disassembler.  This version compiled
 * with JDK 1.1.7A and exactly matches the original's disassembler output.
 * Placed in the public domain.  This product is supplied "as is."
 * Lawrence Leinweber, Cleveland, Ohio, U.S.A. 1999.
 *
 * See http://www.leinweb.com/snackbar/wator/
 */
public class WaTor extends Applet implements Runnable {

    Label lb_size;
    Label lb_nshark;
    Label lb_nfish;
    Label lb_sbreed;
    Label lb_fbreed;
    Label lb_starve;
    Scrollbar sb_size;
    Scrollbar sb_nshark;
    Scrollbar sb_nfish;
    Scrollbar sb_sbreed;
    Scrollbar sb_fbreed;
    Scrollbar sb_starve;
    Button bn_go;
    Button bn_stop;
    Button bn_remix;
    Panel pn_tp;
    Panel pn_sc;
    Panel pn_bn;
    MapPanel pn_mp;
    GraphPanel pn_gf;
    int ni;
    int nj;
    int nshark;
    int nfish;
    boolean goflag;
    boolean remixflag;
    boolean brkflag;
    Object mutex;
    private Thread thread;

    public void start() {
        synchronized (mutex) {
            if (thread != null) return;
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    public void stop() {
        synchronized (mutex) {
            if (thread != null && thread.isAlive()) thread.stop();
            thread = null;
        }
    }

    public void run() {
        int i, j, s, f;

        while (remixflag || pn_mp.paintflag || brkflag || goflag) {
            if (remixflag) {
                synchronized (mutex)
                    { i = ni; j = nj; s = nshark; f = nfish; }
                if (Fish.nshark != s || Fish.nfish != f)
                    Fish.make(s, f);
                pn_mp.remix(i, j);
                pn_mp.paintflag = true;
                pn_gf.brk();
                remixflag = false;
            }
            if (pn_mp.paintflag)
                { pn_mp.paintflag = false; pn_mp.dopaint(); }
            if (brkflag)
                { brkflag = false; pn_gf.brk(); }
            if (goflag) {
                pn_mp.gen();
                pn_gf.gen(ni * nj, Fish.nshark, Fish.nfish);
                synchronized (mutex) {
                    if (!remixflag) {
                        setNumShark(Fish.nshark);
                        setNumFish(Fish.nfish);
                    }
                }
            }
        }
        thread = null;
    }

    @Override
    public boolean handleEvent(Event e) {
        if (e.target instanceof Button)
            switch (e.id) {
            case Event.ACTION_EVENT:
                if (e.target == bn_go) goflag = true;
                if (e.target == bn_stop) goflag = false;
                if (e.target == bn_remix) remixflag = true;
                if (goflag || remixflag) start();
                return (true);
            }
        else if (e.target instanceof Scrollbar)
            switch (e.id) {
            case Event.SCROLL_ABSOLUTE:
            case Event.SCROLL_LINE_UP:
            case Event.SCROLL_LINE_DOWN:
            case Event.SCROLL_PAGE_UP:
            case Event.SCROLL_PAGE_DOWN:
                synchronized (mutex) {
                if (e.target == sb_size)
                    remixflag = setSize(sb_size.getValue());
                if (e.target == sb_nshark)
                    remixflag = setNumShark(sb_nshark.getValue());
                if (e.target == sb_nfish)
                    remixflag = setNumFish(sb_nfish.getValue());
                if (e.target == sb_sbreed)
                    brkflag = setSharkBreed(sb_sbreed.getValue());
                if (e.target == sb_fbreed)
                    brkflag = setFishBreed(sb_fbreed.getValue());
                if (e.target == sb_starve)
                    brkflag = setStarve(sb_starve.getValue());
                }
                if (remixflag) start();
                return (true);
            }
        return (super.handleEvent(e));
    }

    @Override
    public void init() {
        super.init();
        mutex = new Object();
        this.setSize(1000, 800);
        pn_mp = new MapPanel();
        pn_mp.that = this;
        setLayout(new GridLayout(2, 1, 10, 10));
        pn_tp = new Panel();
        pn_tp.setLayout(new BorderLayout(3, 0));
        add(pn_tp);
        pn_tp.setBackground(Color.white);
        pn_sc = new Panel();
        pn_sc.setLayout(new GridLayout(14, 1, 3, 3));
        pn_tp.add(pn_sc, BorderLayout.EAST);
        pn_sc.add(lb_size = new Label());
        lb_size.setForeground(pn_mp.getColor(Color.blue));
        pn_sc.add(sb_size = new Scrollbar(0));
        pn_sc.add(lb_nshark = new Label());
        lb_nshark.setForeground(pn_mp.getColor(Color.red));
        pn_sc.add(sb_nshark = new Scrollbar(0));
        pn_sc.add(lb_nfish = new Label());
        lb_nfish.setForeground(pn_mp.getColor(Color.green));
        pn_sc.add(sb_nfish = new Scrollbar(0));
        pn_sc.add(lb_sbreed = new Label());
        lb_sbreed.setForeground(pn_mp.getColor(Color.red));
        pn_sc.add(sb_sbreed = new Scrollbar(0));
        pn_sc.add(lb_fbreed = new Label());
        lb_fbreed.setForeground(pn_mp.getColor(Color.green));
        pn_sc.add(sb_fbreed = new Scrollbar(0));
        pn_sc.add(lb_starve = new Label());
        lb_starve.setForeground(pn_mp.getColor(Color.red));
        pn_sc.add(sb_starve = new Scrollbar(0));
        pn_sc.add(new Label());
        pn_bn = new Panel();
        pn_bn.setLayout(new GridLayout(1, 3, 3, 0));
        pn_sc.add(pn_bn);
        pn_bn.add(bn_go = new Button("Go"));
        pn_bn.add(bn_stop = new Button("Stop"));
        pn_bn.add(bn_remix = new Button("Remix"));
        pn_tp.add(pn_mp, BorderLayout.CENTER);
        pn_gf = new GraphPanel();
        add(pn_gf);
        validate();
        pn_gf.init(pn_mp.getColor(Color.red), pn_mp.getColor(Color.green),
                pn_mp.getColor(Color.blue));
        nshark = nfish = 0;
        setSize(1);
        while (ni * nj < 1000)
            setSize(Math.min(ni, nj) + 1);
        setNumShark(1);
        setNumFish(ni * nj - 1);
        setSharkBreed(10);
        setFishBreed(3);
        setStarve(3);
        Fish.seedrnd();
        remixflag = true;
        goflag = true;

        start();
    }

    private boolean setSize(int n) {
        int x, y;

        x = pn_mp.size().width - 2;
        y = pn_mp.size().height - 2;
        if (n < 1) n = 1;
        if (x < y) { nj = n; ni = n * y / x; }
        else { ni = n; nj = n * x / y; }
        sb_size.setValues(n, Math.min(x, y) / 10, 1, Math.min(x, y));
        lb_size.setText("Size " + Integer.toString(ni * nj) +
                " [1-" + Integer.toString(x * y) + "]");
        if (nfish + nshark > ni * nj) {
            setNumFish(ni * nj * nfish / (nfish + nshark));
            setNumShark(ni * nj - nfish);
        }
        return (true);
    }

    private boolean setNumShark(int n) {
        sb_nshark.setValues(n, ni * nj / 10, 0, ni * nj);
        lb_nshark.setText("Sharks " + Integer.toString(n) +
                " [0-" + Integer.toString(ni * nj) + "]");
        nshark = n;
        if (nfish + nshark > ni * nj) setNumFish(ni * nj - nshark);
        return (true);
    }

    private boolean setNumFish(int n) {
        sb_nfish.setValues(n, ni * nj / 10, 0, ni * nj);
        lb_nfish.setText("Fish " + Integer.toString(n) +
                " [0-" + Integer.toString(ni * nj) + "]");
        nfish = n;
        if (nshark + nfish > ni * nj) setNumShark(ni * nj - nfish);
        return (true);
    }

    private boolean setSharkBreed(int n) {
        sb_sbreed.setValues(n, 10, 1, 100);
        lb_sbreed.setText("Shark Breed " + Integer.toString(n) + " [1-100]");
        Fish.sbreed = n;
        return (true);
    }

    private boolean setFishBreed(int n) {
        sb_fbreed.setValues(n, 10, 1, 100);
        lb_fbreed.setText("Fish Breed " + Integer.toString(n) + " [1-100]");
        Fish.fbreed = n;
        return (true);
    }

    private boolean setStarve(int n) {
        sb_starve.setValues(n, 10, 1, 100);
        lb_starve.setText("Shark Starve " + Integer.toString(n) + " [1-100]");
        Fish.sstarve = n;
        return (true);
    }
}
