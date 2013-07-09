/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.factorize.factorizers;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * Finds the prime factors of a positive integer with arbitrarily large precision.
 * Algorithm from Lars Vogel
 * http://www.vogella.de/articles/JavaAlgorithmsPrimeFactorization/article.html
 * To get something really fast we should try a general number field sieve
 * http://en.wikipedia.org/wiki/General_number_field_sieve
 *
 * @author Barry Becker
 */
public class QuickPrimeFactorizer extends AbstractPrimeFactorizer {

    @Override
    public List<BigInteger> findPrimeFactors(BigInteger num) {

        List<BigInteger> factors = new LinkedList<BigInteger>();
        BigInteger newNum = new BigInteger(num.toString());

        for (BigInteger candidateFactor = TWO;
             candidateFactor.compareTo(newNum.divide(candidateFactor)) <=0;
             candidateFactor = candidateFactor.add(ONE) ) {

            while (newNum.mod(candidateFactor).equals(BigInteger.ZERO)) {
                factors.add(candidateFactor);
                newNum = newNum.divide(candidateFactor);
            }
        }
        if (newNum.compareTo(ONE) > 0) {
            factors.add(newNum);
        }
        return factors;
    }

}
