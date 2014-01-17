// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.euler.trianglenumbers;

/**
 * @author Barry Becker
 */
public class TriangleNumber {

    /**
     *  The first of two adjacent relatively prime numbers that can be used to determine the value using
     * relPrime * (relPrime + 1 ) / 2
     */
    long relPrime;

    /** cached number of factors */
    Integer numFactors;


    /**
     * @param num the number of the triangle number to construct.
     *            For example, 5 will be 1 + 2 + 3 + 4 + 5 =  15, the 5th triangle number
     */
    public TriangleNumber(long num) {
        relPrime = num;
    }

    public long getValue() {
        return relPrime * (relPrime + 1) / 2;
    }

    public int getNumFactors() {
        if (numFactors == null) {
            numFactors = getNumFactors(getValue());
        }
        return numFactors;
    }

    private int getNumFactors(long num) {
        int nod = 0;
        double sqrt = Math.sqrt(num);

        for (int i = 1; i<= sqrt; i++){
            if (num % i == 0){
                nod += 2;
            }
        }
        //Correction if the number is a perfect square
        if (sqrt * sqrt == num) {
            nod--;
        }

        return nod;
    }


    public static void main(String[] args) {

        long num = 10;
        TriangleNumber tnum = new TriangleNumber(num);

        while (tnum.getNumFactors() < 500) {
            tnum = new TriangleNumber(++num);
        }

        System.out.println("The " + num +"the triangle number had a value of "
                + tnum.getValue() + " with "+ tnum.getNumFactors() +" factors.");
    }

}



