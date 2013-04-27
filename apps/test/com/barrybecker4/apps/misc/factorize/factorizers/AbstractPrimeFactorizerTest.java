package com.barrybecker4.apps.misc.factorize.factorizers;

import junit.framework.TestCase;

import java.math.BigInteger;

/**
 * @author Barry Becker
 */
public abstract class AbstractPrimeFactorizerTest extends TestCase {

    /** Try running on a number like this to check performance. */
    protected static final BigInteger TEST_NUMBER_VERY_SMALL = new BigInteger("2098798");
    protected static final BigInteger TEST_NUMBER_SMALL = new BigInteger("2920798798768");
    protected static final BigInteger TEST_NUMBER_MEDIUM = new BigInteger("2989879798798798");
    protected static final BigInteger TEST_NUMBER_LARGE = new BigInteger("12329087979123879797");
    protected static final BigInteger TEST_NUMBER_GIANT = new BigInteger("9832148972431897213489723290879791238798797");

    protected AbstractPrimeFactorizer factorizer;

    @Override
    public void setUp() {
        factorizer = createInstance();
    }

    protected abstract AbstractPrimeFactorizer createInstance();


    /** for testing only */
    protected void doTest(BigInteger num) {
        System.out.println("finding factors for "+ num );
        AbstractPrimeFactorizer factorizer = createInstance();

        System.out.println("The factors are : "+ factorizer.findPrimeFactors(num));
    }

}
