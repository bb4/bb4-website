// Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.factorize.factorizers;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Barry Becker
 */
public interface PrimeFactorizer {

    /**
     * Finds all the prime factors in order.
     * @param num number to find prime factors of.
     * @return the prime factors
     */
    List<BigInteger> findPrimeFactors(BigInteger num);
}
