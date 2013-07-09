/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.ids;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Generators random N digit id strings.
 * Relies on the fact that the space of possible ids is much larger than
 * the actual number of unique ids needed. If this is not the case, it could be slow.
 *
 * @author Barry Becker
 */
public class UniqueIdGenerator  {

    /** Number of ids to generate  */
    private static final int NUM_IDS = 3530;
    private static final int NUM_DIGITS_IN_ID = 9;
    private static final boolean USE_DASHES = true;
    /* Interval between dashes.  If 3, then id will be something like XXX-XXX-XXX-X   */
    private static final int DASH_INTERVAL = 3;
    private static final String DASH = "-";
    private static final double LOG10 = Math.log(10.0);

    private static  Set<Long> idSet = new HashSet<Long>(NUM_IDS);
    private static final Random RANDOM = new Random(30556);

    private UniqueIdGenerator() {
    }

    public static void main(String[] args) {

        do {
            getRandomId();
        } while (idSet.size() < NUM_IDS);
    }

    private static void getRandomId() {

        Long idNum = getRandomNumber(NUM_DIGITS_IN_ID);
        int numLeadingZeros = NUM_DIGITS_IN_ID - (int)Math.ceil(Math.log(idNum + 1)/LOG10);

        if (!idSet.contains(idNum)) {
            String id = Long.toString(idNum);
            for (int i=0; i<numLeadingZeros; i++) {
                id = "0" + id;
            }

            if (USE_DASHES) {
                id = addDashes(id);
            }
            System.out.println(id + "\n");
            idSet.add(idNum);
        }
    }

    private static String addDashes(String id) {
        int numDashes = (NUM_DIGITS_IN_ID-1) / DASH_INTERVAL;
        for (int j= numDashes; j>0; j--) {
            int pos = j * DASH_INTERVAL;
            id = id.substring(0, pos) + DASH + id.substring(pos);
        }
        return id;
    }

    /** @return  a number between 1 and (10^NUM_DIGITS)-1 */
    private static Long getRandomNumber(int numDigits) {
       return (long)Math.floor( (Math.pow(10, numDigits)-1.0) * RANDOM.nextDouble()) + 1;
    }
}
