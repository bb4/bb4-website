package com.barrybecker4.apps.misc.factorize.factorizers;

/**
 * @author Barry Becker
 */
public class QuickPrimeFactorizerTest extends AbstractPrimeFactorizerTest {


    @Override
    protected AbstractPrimeFactorizer createInstance() {
        return new QuickPrimeFactorizer();
    }

    public void testFactorizer() {

        doTest(TEST_NUMBER_VERY_SMALL);
        doTest(TEST_NUMBER_SMALL);
        doTest(TEST_NUMBER_MEDIUM);
        doTest(TEST_NUMBER_LARGE);
    }

}
