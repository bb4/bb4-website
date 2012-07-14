/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.factorize.factorizers;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * Finds the prime factors of a positive integer with arbitrarily large precision.
 *
 * @author Barry Becker
 */
public class BrutePrimeFactorizer extends AbstractPrimeFactorizer {

    @Override
    public List<BigInteger> findPrimeFactors(BigInteger num) {
        List<BigInteger> factors = new LinkedList<BigInteger>();

        BigInteger newNum = new BigInteger(num.toString());
        BigInteger candidateFactor = TWO;

        while (candidateFactor.compareTo(newNum) <= 0 && newNum.compareTo(ONE) > 0) {
            if (newNum.mod(candidateFactor).equals(BigInteger.ZERO)) {
                factors.add(candidateFactor);
                newNum = newNum.divide(candidateFactor);
            }
            else {
                candidateFactor = candidateFactor.add(ONE);
            }
        }
        return factors;
    }

    @Override
    protected AbstractPrimeFactorizer createInstance() {
        return new BrutePrimeFactorizer();
    }

    //------ Main method: start here! -----------------------------------------------------------
    public static void main( String[] args ) throws IOException {

        BrutePrimeFactorizer factorizer = new BrutePrimeFactorizer();
        factorizer.doTest(TEST_NUMBER_VERY_SMALL);
        factorizer.doTest(TEST_NUMBER_SMALL);
        //factorizer.doTest(TEST_NUMBER_MEDIUM);  // too slow
    }
}
