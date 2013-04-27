/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.factorize.factorizers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.LinkedList;
import java.util.List;

/**
 * Finds the prime factors of a positive integer with arbitrarily large precision.
 * Algorithm from Lars Vogel
 * http://www.vogella.de/articles/JavaAlgorithmsPrimeFactorization/article.html
 *
 * @author Barry Becker
 */
public class RecursivePrimeFactorizer extends AbstractPrimeFactorizer {

    private static final BigDecimal TWO_DEC = new BigDecimal(2);

    /**
     * Recursive method to find prime factors.
     */
    @Override
    public List<BigInteger> findPrimeFactors(BigInteger num) {

        List<BigInteger> factors = new LinkedList<BigInteger>();

        BigInteger newNum = new BigInteger(num.toString());
        BigInteger candidateFactor = findIntegerSquareRoot(newNum);

        while (!newNum.mod(candidateFactor).equals(BigInteger.ZERO)
                && candidateFactor.compareTo(ONE)>=0) {
            candidateFactor = candidateFactor.subtract(ONE);
        }

        if (candidateFactor.equals(ONE)) {
            factors.add(newNum);
        }
        else if (newNum.mod(candidateFactor).signum() == 0 ) {
            factors.addAll(findPrimeFactors(candidateFactor));
            factors.addAll(findPrimeFactors(newNum.divide(candidateFactor)));
        }
        return factors;
    }

    /**
     * Newtons method to find approximate square root.
     * See http://en.wikipedia.org/wiki/Integer_square_root
     * @param num number to square root
     * @return the largest x such that x squared is <= num
     */
    protected BigInteger findIntegerSquareRoot(BigInteger num) {

        // throw out the last len/2 - 1 digits and use the result as our initial guess.
        BigDecimal origNum = new BigDecimal(num);
        String numStr = num.toString();
        int numDigits = numStr.length();
        final MathContext mc = new MathContext(5 + numDigits);
        String initialGuessStr = numStr.substring(0, (numDigits + 2)/2);

        BigDecimal xk = new BigDecimal(initialGuessStr);
        BigDecimal xkp1;
        BigDecimal diff;

        do {
            xkp1 = (xk.add(origNum.divide(xk, mc))).divide(TWO_DEC, mc);
            diff = xkp1.subtract(xk).abs();
            xk = xkp1;
        } while (diff.compareTo(BigDecimal.ONE) >= 0);

        return xkp1.toBigInteger();
    }
}
