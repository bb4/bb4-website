/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.wator;

import java.util.Random;

/**
 * See http://www.leinweb.com/snackbar/wator/
 */
public class Fish  {

    public int i;
    public int j;
    public int breed;
    public int starve;
    private Fish f_next;
    private static Fish fish_hed;
    public static int nshark;
    public static int nfish;
    public static int sbreed;
    public static int fbreed;
    public static int sstarve;
    private static int rnd;

    public Fish(boolean isfish) {
        f_next = fish_hed;
        fish_hed = this;
        i = isfish? fbreed: sbreed;
        breed = nrnd(isfish? fbreed: sbreed) + nrnd(isfish? fbreed: sbreed) + 1;
        starve = isfish? -1: nrnd(sstarve) + nrnd(sstarve) + 1;
    }

    public static Fish first(){
        while (fish_hed != null && fish_hed.starve == 0)
            fish_hed = fish_hed.f_next;
        return (fish_hed);
    }

    public Fish next() {
        while (f_next != null && f_next.starve == 0)
            f_next = f_next.f_next;
        return (f_next);
    }

    public static void make(int newnshark, int newnfish) {
        int i, j;
        Fish f;

        while ((f = fish_hed) != null) {
            fish_hed = f.f_next;
            f.f_next = null;
        }
        nshark = newnshark;
        nfish = newnfish;
        for (i = 0, j = (nfish - nshark) / 2; i < nshark + nfish; i++) {
            if (j > 0) j -= nshark;
            else j += nfish;
            new Fish(j > 0);
        }
    }

    public static void seedrnd() {
        rnd = new Random().nextInt() >> 16 & 077777;
    }

    public static int nrnd(int n) {
        if (n <= 1) return (0);
        return (((rnd = rnd * 1103515245 + 12345) >> 16 & 077777) * n >> 15);
    }

}
