package com.becker.misc.readnumber;

import java.math.*;
import java.util.*;

/**
 * @author Barry Becker Date: May 6, 2006
 */
public class ReadNumber {


    private enum SimpleNumber {
        ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE,
        THIRTEEN, FOURTEEN, FIFTEEN, SIXTEEN, SEVENTEEN, EIGHTEEN, NINETEEN
    }

    private enum Tens {TWENTY, THIRTY, FOURTY, FIFTY, SIXTY, SEVENTY, EIGHTY, NINETY}

    // a VIGINTILLION is 10 ^ 63.
    private enum Group {THOUSHAND, MILLION, BILLION, TRILLION, QUADRILLION, PENTILLION,
        SEXTILLION, SEPTILLION, OCTILLION, NONILLION, DECILLION, UNDECILLION, DUODECILLION,
        TREDECILLION, QUATTUORDECILLION, QUINDECILLION, SEPTENDECILLION, OCTODECILLION, NOVEMDECILLION, VIGINTILLION
    }

    private static final String HUNDRED = "HUNDRED";

    private static final BigInteger ONE_THOUSAND = new BigInteger("1000");
    private static final BigInteger ZERO = new BigInteger("0");

    private static final BigInteger BIGGEST =
            new BigInteger("999999999999999999999999999999999999999999999999999999999999999");

    // A greeting specified using allophones. See SpeechSynthesizer.
    //private static final String[] GREETING = {"w|u|d", "y|ouu", "l|ii|k", "t|ouu", "p|l|ay", "aa", "gg|AY|M"};


    /** don't allow instances of statid class */
    private ReadNumber() {}


    public static String getEnglishForGroup(int number) {
        assert(number >=0 && number <1000);

        if (number == 0) {
            return "";
        }
        if (number < 20) {
            return ' ' + SimpleNumber.values()[number - 1].toString();
        }
        else if (number < 100) {
            int tens = number / 10;
            return ' ' + Tens.values()[tens-2].toString() + getEnglishForGroup(number - 10 * tens);
        }
        else {
            int hundreds = number / 100;
            return ' ' + SimpleNumber.values()[hundreds - 1].toString() + ' ' + HUNDRED
                    + getEnglishForGroup(number - 100 * hundreds);
        }
    }

    /**
     * @param number
     * @return the english form of the number
     */
    public static String translateToEnglish(BigInteger number) {

        if (number.compareTo(BIGGEST) > 0) {
            return "OK. I give up. That number is tooo big even for me.";
        }

        String result = getEnglishForGroup(number.mod(ONE_THOUSAND).intValue());
        BigInteger n = number.divide(ONE_THOUSAND);
        int group = 0;
        while (n.compareTo(ZERO) > 0) {
            int groupVal = n.mod(ONE_THOUSAND).intValue();
            if (groupVal != 0) {
                String res = result;
                result = getEnglishForGroup(groupVal) + ' ' + Group.values()[group];
                if (!"".equals(res)) {
                    result +=  ',' + res;
                }
            }
            n  = n.divide(ONE_THOUSAND);
            group++;
        }
        return result;
    }


    public static void main(String[] args) {
        //GUIUtil.setStandAlone(false);
        // This works for arbitrary strings, but is not as nice sounding as the pre-generated wav file.
        //SpeechSynthesizer speech = new SpeechSynthesizer();
        //speech.sayPhoneWords( GREETING );

        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Enter a positive integer:");

            String nextString = scanner.next();
            System.out.println(translateToEnglish(new BigInteger(nextString)));

        } while (true);

    }
}

