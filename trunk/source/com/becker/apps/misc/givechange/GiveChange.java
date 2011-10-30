/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.apps.misc.givechange;

import com.becker.common.format.FormatUtil;
import com.becker.common.util.Input;

import java.io.IOException;

/**
 * Program to compute change given a number of cents between 0 and 99.
 * Uses none of java's wonderful object oriented features unfortunately.
 * It is composed entirely of static methods on a static final class.
 *
 * @author Barry Becker
 */
public final class GiveChange {

    /** maximum amount of money (in cents) to give change for. */
    private static final Long MAX_AMOUNT = 99L;

    /** private constructor for class with all static methods. */
    private GiveChange() {}

    /**
     * Displays the amount of change to the user.
     * @param cents  number of cents between 0 and MAX_AMT
     */
    public static void showChangeFor(long cents) {
        System.out.println( "Your change is ..." );
        long remainingCents = cents;
        // start with the largest denomination coin and work down.
        for (int i = Coin.values().length-1; i >= 0;  i--) {
            Coin coin = Coin.values()[i];
            long num = remainingCents / coin.getWorthInPennies();
            if (num != 0)  {
                // put an s at the end if there are 0 or >1 coins of this type
                String coinName = (num==1 ? coin.getName() : coin.getPluralName());
                System.out.println("  " + FormatUtil.formatNumber(num) + ' ' + coinName );
                // shorthand for remainingCents = remainingCents - num * VALUE[i];
                remainingCents -= num * coin.getWorthInPennies();
                // or equivalently, you could say
                //remainingCents %= VALUE[i];
            }         
        }
        System.out.println( "Have a nice day!\n" );
    }

    //------ Main method: start here! -----------------------------------------------------------
    public static void main( String[] args ) throws IOException {
        System.out.println("\n==================================================================\n"); 
        
        // for now, just loops forever
        while (true)  {
            long cents = Input.getLong("Enter a number of cents for which to compute ideal change\n" +
                    "[0 - " + FormatUtil.formatNumber(MAX_AMOUNT) + "]:", 0, MAX_AMOUNT);
            showChangeFor(cents);
        }
    }
}
