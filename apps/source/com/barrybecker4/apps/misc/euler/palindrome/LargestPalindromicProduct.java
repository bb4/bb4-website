package com.barrybecker4.apps.misc.euler.palindrome;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * From project Euler:
 * A palindromic number reads the same both ways.
 * The largest palindrome made from the product of two 2-digit numbers is 9009 = 91 × 99.
 * Find the largest palindrome made from the product of two 3-digit numbers.
 *
 * @author Barry Becker
 */
public class LargestPalindromicProduct {

    private static final NumberFormat FORMAT = new DecimalFormat("#");

    private LargestPalindromicProduct() {
    }


    /**
     * Convert the number to a string them
     * @param number
     * @return true if the specified number reads the same forward s backward.
     */
    private static boolean isPalindromic(long number) {

        String numStr = FORMAT.format(number);
        int len = numStr.length();
        int lend2 = len / 2;
        for (int i = 0; i < lend2; i++)  {
            if (numStr.charAt(i) != numStr.charAt(len - i - 1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param n the number of digits in the two numbers to tke product of
     * @return the largest palindromic product of two n digit numbers.
     *    returns -1 if no palindrome found.
     */
    private static long findLargestPalindromicProduct(int n) {

        long maxNum = (long) Math.pow(10, n) - 1;
        long minNum = (long) Math.pow(10, n-1) ;
        long maxFound = -1;

        for (long i = maxNum; i > minNum; i--) {
             for (long j = maxNum; j > minNum; j--) {
                 long num = i * j;
                 if (isPalindromic(num))  {
                     if (num > maxFound) {
                         maxFound = num;
                     }
                 }
             }
        }
        return maxFound;
    }


    public static void main(String[] args) {

        int n = 5;
        System.out.println(
                "The largest palindromic number for two " + n + " digit numbers is "
                 + findLargestPalindromicProduct(n));
    }
}
