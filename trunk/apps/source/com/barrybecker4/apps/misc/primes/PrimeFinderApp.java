package com.barrybecker4.apps.misc.primes;

import com.barrybecker4.ui.application.ApplicationFrame;

/**
 * Find N prime numbers and display them.
 *
 * @author Barry Becker
 */
public class PrimeFinderApp extends ApplicationFrame {


    /** the number of prime numbers to find */
    private static final int N = 200000;

    private PrimeFinderApp() {
        super("Primer Number Finder");    // NON-NLS
    }

    @Override
    protected void createUI()  {

        PrimeFinderPanel pfpanel = new PrimeFinderPanel();
        setContentPane(pfpanel);

        super.createUI();

        pfpanel.startComputing(N);
    }


    public static void main(String[] args) {
        new PrimeFinderApp();
    }
}
