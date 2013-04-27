/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.factorize.factorizers;

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

    /**
     * Finds all the prime factors in order.
     * @param num number to find prime factors of.
     * @return the prime factors
     */
    public abstract List<BigInteger> findPrimeFactors(BigInteger num);

}
