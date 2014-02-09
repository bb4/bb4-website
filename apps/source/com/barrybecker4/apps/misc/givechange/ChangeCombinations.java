// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.givechange;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.util.Input;

import java.io.IOException;

/**
 * Program to compute all the possible different ways that change could
 * be given for some value.
 * For example for 0.26, the possibilities are :
 *  1Q, 1P
 *  2D, 1N, 1P
 *  2D, 6P
 *  1D, 3N, 1P
 *  1D, 2N, 6P
 *  1D, 1N, 11P
 *  4N, 1P
 *  4N, 6P
 *  3N, 11P
 *  2N, 16P
 *  1N, 21P
 *  26P
 *
 * @author Barry Becker
 */
public final class ChangeCombinations {

    /** maximum amount of money (in cents) to give change for. */
    private static final Long MAX_AMOUNT = 999L;

    /** private constructor for class with all static methods. */
    private ChangeCombinations() {}

    /**
     * Recursive method to find all the change combinations.
     * @param coinMap coins represented by change so far
     * @param centsRemaining number of cents between 0 and MAX_AMT
     */
    public static void showChangeCombinationsFor(CoinMap coinMap, int centsRemaining) {

        // base case of recursion
        if (centsRemaining == 0) {
            System.out.println(coinMap);
        }

        for (int i = Coin.values().length - 1; i >= 0; i--)  {
            Coin coin = Coin.values()[i];
            int numCoins = centsRemaining / coin.getWorthInPennies();

            if (numCoins > 0) {
                coinMap.add(coin, numCoins);
                showChangeCombinationsFor(coinMap, centsRemaining - numCoins * coin.getWorthInPennies());
                coinMap.remove(coin, numCoins);
            }
        }
    }

    //------ Main method: start here! -----------------------------------------------------------
    public static void main( String[] args ) throws IOException {
        System.out.println("\n==================================================================\n");

        // for now, just loops forever
        while (true)  {
            long cents = Input.getLong("Enter a number of cents for which to compute change combinations\n" +
                    "[0 - " + FormatUtil.formatNumber(MAX_AMOUNT) + "]:", 0, MAX_AMOUNT);
            System.out.println( "The change possibilities are ..." );
            showChangeCombinationsFor(new CoinMap(), (int) cents);
        }
    }
}
