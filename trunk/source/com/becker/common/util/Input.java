package com.becker.common.util;

import com.becker.common.format.FormatUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Barry Becker
 */
public final class Input {

    /** private constructor for static class */
    private Input() {}

    /**
     * Get a number from the user.
     * @param prompt query string to prompt the user for a response.
     * @return an integer number.
     */
    public static long getLong(String prompt) throws IOException {
        return getLong(prompt, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Get a number from the user.
     * Continues to ask until a valid number provided.
     * @param prompt query string to prompt the user for a response.
     * @param min minimum acceptable value.
     * @param max the maximum number allowed to be entered.
     * @return an integer number between 0 and max.
     * @throws IOException
     */
    public static long getLong(String prompt, long min, long max) throws IOException {
        long amount = 0;
        boolean valid = false;

        do {
            System.out.println( prompt );

            InputStreamReader inp = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(inp);
            String str = br.readLine();

            try {
                amount = Long.parseLong(str);
                valid = true;
                if (amount < min ) {
                    System.out.println( "You must enter a number greater than "
                            + FormatUtil.formatNumber(min));
                    valid = false;
                }
                else if (amount > max) {
                    System.out.println( "That number is too big! It must be smaller than "
                            + FormatUtil.formatNumber(max) );
                    valid = false;
                }
            } catch  (NumberFormatException nfe)  {
                System.out.println( "Hey! What kind of number is that? ");
                valid = false;
            }

        }  while (!valid);   // give them another change if not valid.
        return amount;
    }

    /**
     * Get a string from the user.
     * todo: add an optional regexp argument.
     * @return input string.
     * @throws IOException
     */
    public static String getString(String prompt) throws IOException {

        System.out.println(prompt);

        InputStreamReader inp = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(inp);
        return br.readLine();
    }
}