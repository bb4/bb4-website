/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.apps.misc.factorize.factorizers;

import com.becker.common.profile.SimpleProfiler;
import com.becker.game.common.ui.dialogs.NewGameDialog;
import sun.security.util.BigInt;

import java.io.IOException;
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
    private BigInteger findIntegerSquareRoot(BigInteger num) {

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

    @Override
    protected AbstractPrimeFactorizer createInstance() {
        return new RecursivePrimeFactorizer();
    }

    private void testSqrt(BigInteger num) {
        System.out.println("The integer sqrt of " + num +" is " + findIntegerSquareRoot(num));
    }

    //------ Main method: start here! -----------------------------------------------------------
    public static void main( String[] args ) throws IOException {

        RecursivePrimeFactorizer factorizer = new RecursivePrimeFactorizer();

        factorizer.doTest(TEST_NUMBER_VERY_SMALL);
        factorizer.doTest(TEST_NUMBER_SMALL);
        factorizer.doTest(TEST_NUMBER_MEDIUM);
        //factorizer.doTest(TEST_NUMBER_LARGE);   // too slow

        System.out.println();

        SimpleProfiler prof = new SimpleProfiler();
        prof.start();
        factorizer.testSqrt(new BigInteger("100"));
        factorizer.testSqrt(new BigInteger("1000"));
        factorizer.testSqrt(new BigInteger("10000"));
        factorizer.testSqrt(new BigInteger("34512"));
        factorizer.testSqrt(new BigInteger("34789512"));
        factorizer.testSqrt(new BigInteger("657563449"));  //perfect square
        factorizer.testSqrt(new BigInteger("3498765234231004984332198798970809812876532"));
        factorizer.testSqrt(new BigInteger("34987120355512903652323849423984231987498378978976787906720342154332342239872398742923847678998982103312338723823094090912312020292365735249876853972983742293874982733100498433219232382932378423984239887298374298798723984729388798970809812876532"));
        prof.stop();
        prof.print();
    }
}
