// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.euler.powerdigitsum;

import java.math.BigInteger;

/**
 * @author Barry Becker
 */
public class PowerDigitSum {

    int base;
    int exp;
    PowerDigitSum(int base, int exp)    {
        this.base = base;
        this.exp = exp;
    }

    public int getSum() {
        BigInteger result = getNumber();
        String strValue = result.toString();

        int sum = 0;
        for (int i = 0; i < strValue.length(); i++) {
           sum += Integer.parseInt(strValue.substring(i, i + 1));
        }
        return sum;
    }

    BigInteger getNumber() {
        BigInteger value = BigInteger.valueOf(base);
        return value.pow(exp);
    }


    public static void main(String[] args) {
        int base = 2;
        int exp = 1000;
        PowerDigitSum pds = new PowerDigitSum(base, exp);
        System.out.println("Power digit sum of "+ base + " ^ " + exp + " is " + pds.getSum() );
    }
}
