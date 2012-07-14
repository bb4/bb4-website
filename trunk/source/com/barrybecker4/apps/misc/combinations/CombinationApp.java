// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.combinations;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.math.MathUtil;

import java.math.BigDecimal;

/**
 * I want to determine what are the odds of at least one student scoring higher
 * than 30 out of 40 on a multiple choice test if there are N students taking it.
 *
 * The chance of getting at least one right = 1.0 - the chance of getting none right.
 *  = 1 - (3/4)^40 = 0.999989
 *  In other words, you have only 1/100,000 chance of getting all of them wrong if you guess randomly.
 *
 * @author Barry Becker
 */
public class CombinationApp {

    private static final int NUM_RIGHT = 30;
    private static final int NUM_QUESTIONS = 40;
    /** num answers for one of the multiple choice questions. */
    private static final int CHOICES_PER_QUESTION = 4;
    /** The chance of getting any given problem wrong. */
    private static final double CHANGE_WRONG = (CHOICES_PER_QUESTION-1.0)/(double)CHOICES_PER_QUESTION;

    private CombinationApp() {}

    /**
     * The general formula for probability of getting numRight or more right out of numQuestions is:
     *
     *  SUM i=(0, numQuestions-numRight) {
     *        diff = numQuestions - numRight
     *        C(numQuestions, numRight+i)  * (numChoices-1) ^ (diff - i)
     *        -------------------------------------------------------------------
     *        numChoices ^ numQuestions
     *  }
     * @param numRight the student must get at least this number correct
     * @param numQuestions the number of questions on the test.
     * @return the probability of a single student getting numRight or more questions correct out of numQuestions.
     */
    private static double getProbabilityOfNorMoreRight(int numRight, int numQuestions) {

        BigDecimal prob = BigDecimal.ZERO;
        int diff = numQuestions - numRight;
        for (int i = 0; i <= diff; i++) {

            BigDecimal comb = new BigDecimal(MathUtil.combination(numQuestions, numRight + i).doubleValue());

            BigDecimal result = comb.multiply(new BigDecimal(Math.pow(CHANGE_WRONG, diff - i)));
            result  = result.divide(new BigDecimal(Math.pow(CHOICES_PER_QUESTION, numQuestions - diff + i)));
            //System.out.println("intermediate result="+ result);
            prob = prob.add(result);
        }
        return prob.doubleValue();
    }

    public static void main(String[] args) {

        for (int i=0; i<=NUM_QUESTIONS; i++) {
            double x = getProbabilityOfNorMoreRight(i, NUM_QUESTIONS);
            System.out.println("Probability " + i + " or more right when taking test is " + FormatUtil.formatNumber(x));
        }

        double p = getProbabilityOfNorMoreRight(NUM_RIGHT, NUM_QUESTIONS);
        double prob = 1.0 - Math.pow(1.0 - p, 20);
        System.out.println("Probability of having at least one student out of "
                + 20 + " get >=" + NUM_RIGHT + " is " + prob);

        p = getProbabilityOfNorMoreRight(NUM_RIGHT, NUM_QUESTIONS);
        prob = 1.0 - Math.pow(1.0 - p, 100);
        System.out.println("Probability of having at least one student out of "
                + 100 + " get >=" + NUM_RIGHT + " is " + prob);
    }
}
