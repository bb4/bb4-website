// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.euler.collatzsequence;

import java.util.HashMap;
import java.util.Map;

/**
 * From project Euler:
 * The following iterative sequence is defined for the set of positive integers:
 * <p>
 *  n → n/2 (n is even)
 *  n → 3n + 1 (n is odd)
 * </p>
 * Using the rule above and starting with 13, we generate the following sequence:
 *
 *  13 → 40 → 20 → 10 → 5 → 16 → 8 → 4 → 2 → 1
 *
 * It can be seen that this sequence (starting at 13 and finishing at 1) contains 10 terms.
 * Although it has not been proved yet (Collatz Problem), it is thought that all starting numbers finish at 1.
 * Which starting number, under one million, produces the longest chain?
 *
 * @author Barry Becker
 */
public class LargestCollatzSequenceFinder {

    private Map<Long, Long> cache = new HashMap<>();

    /**
     * This could be slow if limit is large.
     * @param limit find largest Collatz sequence starting from a number smaller than this
     */
    public long getNumWithLongestSequence(long limit) {

        long longestStart = 0;
        long longestNumTerms = 0;
        for (long i = 2; i < limit; i++) {
            long numTerms = getNumTerms(i);
            if (numTerms > longestNumTerms) {
                longestStart = i;
                longestNumTerms = numTerms;
            }
        }
        return longestStart;
    }

    /**
     * Find the number of terms in the Collatz sequence starting with num.
     * This should be fairly fast - especially if it is found in the cache
     * @param num number to get number of terms for
     * @return number of terms in sequence starting with num.
     */
    public long getNumTerms(long num) {
        if (cache.containsKey(num)) {
            return cache.get(num);
        }
        else {
            long numTerms = findNumTerms(num);
            cache.put(num, numTerms);
            return numTerms;
        }
    }

    /**
     * @param num the first term of the sequence
     * @return the number of terms in a sequence stating with num
     */
    private long findNumTerms(long num) {
        if (num <= 1) {
            return 1;
        }
        else if (num % 2 == 0) {
            return 1 + getNumTerms(num >> 1);
        }
        else {
            return 1 + getNumTerms(3 * num + 1);
        }
    }

    public static void main(String[] args) {

        LargestCollatzSequenceFinder finder = new LargestCollatzSequenceFinder();

        long limit = 1000000;
        long startTime = System.currentTimeMillis();
        long startingNum = finder.getNumWithLongestSequence(limit);
        System.out.println("elapsed time = " + (System.currentTimeMillis() - startTime));
        long numTerms = finder.getNumTerms(startingNum);
        System.out.println(
                "The number under " + limit + " with the longest collatz sequence is " + startingNum
              + ". It has "  + numTerms + " terms."
        );
    }
}
