/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.factorize;

import com.barrybecker4.apps.misc.factorize.factorizers.BrutePrimeFactorizer;
import com.barrybecker4.apps.misc.factorize.factorizers.PrimeFactorizer;
import com.barrybecker4.apps.misc.factorize.factorizers.QuickPrimeFactorizer;
import com.barrybecker4.common.profile.SimpleProfiler;
import com.barrybecker4.common.util.Input;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Find the prime factors of a positive integer.
 *
 * @author Barry Becker
 */
public final class PrimeFactorizeApp {


    /** private constructor for class with all static methods. */
    private PrimeFactorizeApp() {}

    /**
     * @param num number to show the prime factors for.
     */
    public static void showPrimeFactors(BigInteger num, PrimeFactorizer factorizer) {
        SimpleProfiler prof = new SimpleProfiler();
        prof.start();

        System.out.println( "The prime factors are ..." );
        System.out.println(factorizer.findPrimeFactors(num));

        prof.stop();
        prof.print();
    }

    //------ Main method: start here! -----------------------------------------------------------
    public static void main( String[] args ) throws IOException {
        System.out.println("\n==================================================================\n");

        // for now, just loops forever
        while (true)  {
            BigInteger num = Input.getBigInteger("Enter a positive integer to find the prime factors of:");
            if (!(num.signum() > 0)) {
                System.out.println("must be positive.");
            }
            else {
                showPrimeFactors(num, new BrutePrimeFactorizer());
                showPrimeFactors(num, new QuickPrimeFactorizer());
            }
        }
    }
}
