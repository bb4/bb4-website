/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.apps.misc.factorize.factorizers;

import com.becker.common.profile.SimpleProfiler;

import java.math.BigInteger;
import java.util.List;

/**
 * Finds the prime factors of a positive integer with arbitrarily large precision.
 *
 * @author Barry Becker
 */
public abstract class AbstractPrimeFactorizer {

    protected static final BigInteger ONE = new BigInteger("1");
    protected static final BigInteger TWO = new BigInteger("2");

    /** Try running on a number like this to check performance. */
    protected static final BigInteger TEST_NUMBER_VERY_SMALL = new BigInteger("2098798");
    protected static final BigInteger TEST_NUMBER_SMALL = new BigInteger("2920798798768");
    protected static final BigInteger TEST_NUMBER_MEDIUM = new BigInteger("2989879798798798");
    protected static final BigInteger TEST_NUMBER_LARGE = new BigInteger("12329087979123879797");
    protected static final BigInteger TEST_NUMBER_GIANT = new BigInteger("9832148972431897213489723290879791238798797");

    /**
     * Finds all the prime factors in order.
     * @param num number to find prime factors of.
     * @return the prime factors
     */
    public abstract List<BigInteger> findPrimeFactors(BigInteger num);


    protected abstract AbstractPrimeFactorizer createInstance();

    /** for testing only */
    protected void doTest(BigInteger num) {
        System.out.println("finding factors for "+ num );
        AbstractPrimeFactorizer factorizer = createInstance();
        SimpleProfiler prof = new SimpleProfiler();
        prof.start();
        System.out.println("The factors are : "+ factorizer.findPrimeFactors(num));
        prof.stop();
        prof.print();
    }

}
