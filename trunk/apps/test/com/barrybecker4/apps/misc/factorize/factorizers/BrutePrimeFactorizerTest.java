package com.barrybecker4.apps.misc.factorize.factorizers;

/**
 * @author Barry Becker
 */
public class BrutePrimeFactorizerTest extends AbstractPrimeFactorizerTest {



    @Override
    protected AbstractPrimeFactorizer createInstance() {
        return new BrutePrimeFactorizer();
    }

    public void testFactorizer() {

        doTest(TEST_NUMBER_VERY_SMALL);
        doTest(TEST_NUMBER_SMALL);
    }


}
