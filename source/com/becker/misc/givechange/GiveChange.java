package com.becker.misc.givechange;

import com.becker.common.Util;

import java.io.IOException;

/**
 * Program to compute change given a number of cents between 0 and 99.
 * This program uses none of java's wonderful object oriented features unfortunately.
 * It is composed entirely of static methods on a static final class.
 *
 * @author Barry Becker
 */
public final class GiveChange
{
    // maximum amount of money (in cents) to give change for.
    private static final Long MAX_AMOUNT = 99L;

    // Suggestion for improvement: use an enum to represent the coins.
    private static String[] COIN_NAME =   {"penny",    "nickel",   "dime",   "quarter"};
    private static String[] COINS_NAME = {"pennies", "nickels", "dimes", "quarters"};
    private static int[] VALUE  =                {          1,             5,         10,             25    };

    /** 
     * private constructor for class with all static methods. 
     */
    private GiveChange() {};

    /**
     *Get a number from the user.
     *@param max the maximum number allowed to be entered.
     * @return a number of cents between 0 and max. 
     * @throws java.io.IOException
     */
    private static long getAmount(long max) throws IOException
    {
        long amount = 0;
        byte b[] = new byte[100];
        boolean valid = false;

        do {
            System.out.println( "Enter a number of cents for which to compute ideal change "+
                    "[0 - "+Util.formatNumber(max) +"]:" );
            System.in.read(b);
            String str = new String(b);
            int end = str.indexOf('\n');
            str = str.substring(0, end);      // remove trailing newline
            try {
                amount = Long.parseLong(str);
                valid = true;
                if (amount < 0 ) {
                    System.out.println( "You must enter a number greater than 0!" );
                    valid = false;
                }
                else if (amount > max) {
                    System.out.println( "That number is too big!" );
                    valid = false;
                }
            } catch  (NumberFormatException nfe)  {
                System.out.println( "Hey! What kind of number is that? ");
                valid = false;
            }

        }  while (!valid);     // give them another change if not valid.
        return amount;
    }


    /**
     * Displays the amount of change to the user.
     * @param cents  number of cents between 0 and MAX_AMT
     */
    public static void showChangeFor(long cents) {
        System.out.println( "Your change is ..." );
        long remainingCents = cents;
        // start with the largest denomination coin and work down.
        for (int i = COIN_NAME.length-1; i >= 0;  i--) {
            long num = remainingCents / VALUE[i];
            if (num != 0)  {
               // put an s at the end if there are 0 or >1 coins of this type
                System.out.println("  "+ Util.formatNumber(num) + ' ' + (num==1 ? COIN_NAME[i] : COINS_NAME[i]) );
                remainingCents -= num * VALUE[i];     // shorthand for remainingCents = remainingCents - num * VALUE[i];
                // or equivalently, you could say
                //remainingCents %= VALUE[i];         // shorthand for remainingCents = remainingCents % VALUE[i];
            }         
        }
        System.out.println( "Have a nice day!\n" );
    }

    //------ Main method: start here! ---------------------------------------------------------------------------------------------------------------
    public static void main( String[] args )   throws IOException
    {
        boolean done = false;
        System.out.println("\n==================================================================\n"); 
        
        // for now, just loops forever
        while (!done)  {
            long cents = getAmount(MAX_AMOUNT);
            showChangeFor(cents);
        }
    }
}