// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.euler.numberlettercounts;

import com.barrybecker4.apps.misc.readnumber.NumberTranslator;

import java.math.BigInteger;

/**
 * @author Barry Becker
 */
public class NumberLetterCounter {


    public static void main(String args[]) {

        long sum = 0;
        NumberTranslator translator = new NumberTranslator();

        for (int i = 1; i <= 1000; i++) {
            String numberStr = translator.translateToBritish(BigInteger.valueOf(i));
            String numNoSpaces = numberStr.replace(" ", "");
            System.out.println(numberStr + " === " + numNoSpaces + "   num=" + numNoSpaces.length());
            sum += numNoSpaces.length();
        }

        System.out.println("Total num letters = " + sum);

    }
}
