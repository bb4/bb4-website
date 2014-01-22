/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.readnumber;

import java.math.BigInteger;

/**
 * Translates a big integer number into english or spoken word.
 * @author Barry Becker
 */
public class NumberTranslator {

    private static final BigInteger ZERO = new BigInteger("0");
    private static final BigInteger ONE_THOUSAND = new BigInteger("1000");

    /** biggest number we will allow (vigintillion) */
    private static final BigInteger BIGGEST =
            new BigInteger("999999999999999999999999999999999999999999999999999999999999999");

    /** British uses and as in one hundred and 15, instead of one hundred 15.    */
    private enum Type {ENGLISH, BRITISH, PHONETIC}

    /**
     * Constructor
     */
    public NumberTranslator() {}

    /**
     * @param number number to translate to english.
     * @return the number written out long hand.
     */
    public String translateToEnglish(BigInteger number) {
        return translateNumber(number, Type.ENGLISH);
    }

    /**
     * @param number number to translate to english.
     * @return the number written out long hand.
     */
    public String translateToBritish(BigInteger number) {
        return translateNumber(number, Type.BRITISH);
    }

    /**
     * @param number number to translate phonetically.
     * @return the number written as a series of pipe delimited allophones.
     */
    public String translateToPhonetic(BigInteger number) {
        return translateNumber(number, Type.PHONETIC);
    }

    /**
     * @param number the number to translate.
     * @return the english form of the number
     */
    private String translateNumber(BigInteger number, Type type) {

        if (number.compareTo(BIGGEST) > 0) {
            switch (type) {
                case ENGLISH :
                case BRITISH :
                    return "OK. I give up. That number is tooo big even for me.";
                case PHONETIC:
                    return "ii g|i|v u|p|. th|a|t n|u|m|b|e|r i|s t|uu b|i|g| ee|v|e|n f|o|r m|ee|.";
            }
        }

        String result = getTextForGroup(number.mod(ONE_THOUSAND).intValue(), type);
        BigInteger n = number.divide(ONE_THOUSAND);
        int group = 0;
        while (n.compareTo(ZERO) > 0) {
            int groupVal = n.mod(ONE_THOUSAND).intValue();
            if (groupVal != 0) {
                String res = result;
                String grouping = translate(GroupNumber.values()[group], type);
                result = getTextForGroup(groupVal, type) + ' ' + grouping;
                if (!"".equals(res)) {
                    result += ((type == Type.ENGLISH || type == Type.BRITISH) ?  ",\n" : "|, " ) + res;
                }
            }
            n  = n.divide(ONE_THOUSAND);
            group++;
        }
        return result;
    }


    private String getTextForGroup(int number, Type type) {
        assert(number >= 0 && number < 1000);

        if (number == 0) {
            return "";
        }
        if (number < 20) {
            SimpleNumber num = SimpleNumber.values()[number - 1];
            return translate(num, type);
        }
        else if (number < 100) {
            int tens = number / 10;
            TensNumber num = TensNumber.values()[tens-2];
            String tensPart = translate(num, type);
            return tensPart + ' ' + getTextForGroup(number - 10 * tens, type);
        }
        else {
            int hundreds = number / 100;
            String hundredsPart = translate(SimpleNumber.values()[hundreds - 1], type);
            String hundred = null;
            int remainder = number - 100 * hundreds;
            switch (type) {
                case ENGLISH: hundred = SimpleNumber.HUNDRED; break;
                case BRITISH: hundred = SimpleNumber.HUNDRED  + (remainder == 0? "" : " and"); break;
                case PHONETIC: hundred = SimpleNumber.HUNDRED_PRONOUNCE; break;
            }

            return ' ' + hundredsPart + ' ' + hundred + ' ' + getTextForGroup(remainder, type);
        }
    }

    private String translate(INumberEnum number, Type type) {
        String result = null;
        switch (type) {
            case ENGLISH:
            case BRITISH: result = number.toString(); break;
            case PHONETIC: result = number.getPronunciation(); break;
        }
        return result;
    }
}