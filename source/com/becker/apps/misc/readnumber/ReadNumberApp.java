package com.becker.apps.misc.readnumber;

import com.becker.sound.SpeechSynthesizer;
import com.becker.ui.GUIUtil;
import java.math.*;
import java.util.*;

/**
 * @author Barry Becker Date: May 6, 2006
 */
public class ReadNumberApp {

    private static final BigInteger ZERO = new BigInteger("0");
    private static final BigInteger ONE_THOUSAND = new BigInteger("1000");

    // biggest number we will allow (vigintillion)
    private static final BigInteger BIGGEST =
            new BigInteger("999999999999999999999999999999999999999999999999999999999999999");

    // A greeting specified using allophones. See SpeechSynthesizer.
    private static final String GREETING = "p|l|ee|z e|n|t|er aa nn|u|m|b|er .|.";
     private static final SpeechSynthesizer speech = new SpeechSynthesizer();

    private enum Type {ENGLISH , PHONETIC};

    /** don't allow instances of statid class */
    private ReadNumberApp() {}

    public static String translateToEnglish(BigInteger number) {
        return translateNumber(number, Type.ENGLISH);
    }

    public static void sayInEnglish(BigInteger number) {
        String phonetic = translateNumber(number, Type.PHONETIC);
        speech.sayText( phonetic );
    }


    /**
     * @param number
     * @return the english form of the number
     */
    private static String translateNumber(BigInteger number, Type type) {

        if (number.compareTo(BIGGEST) > 0) {
            return (type == Type.ENGLISH) ?
                "OK. I give up. That number is tooo big even for me." :
                "ii g|i|v u|p|. th|a|t n|u|m|b|e|r i|s t|uu b|i|g| ee|v|e|n f|o|r m|ee|.";
        }

        String result = getEnglishForGroup(number.mod(ONE_THOUSAND).intValue(), type);
        BigInteger n = number.divide(ONE_THOUSAND);
        int group = 0;
        while (n.compareTo(ZERO) > 0) {
            int groupVal = n.mod(ONE_THOUSAND).intValue();
            if (groupVal != 0) {
                String res = result;
                String grouping = translate(GroupNumber.values()[group], type);
                result = getEnglishForGroup(groupVal, type) + ' ' + grouping;
                if (!"".equals(res)) {
                    result += ((type == Type.ENGLISH) ?  ",\n" : "|, " ) + res;
                }
            }
            n  = n.divide(ONE_THOUSAND);
            group++;
        }
        return result;
    }


    private static String getEnglishForGroup(int number, Type type) {
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
            return tensPart + ' ' + getEnglishForGroup(number - 10 * tens, type);
        }
        else {
            int hundreds = number / 100;
            String hundredsPart = translate(SimpleNumber.values()[hundreds - 1], type);
            String hundred = (type == Type.ENGLISH) ?  SimpleNumber.HUNDRED : SimpleNumber.HUNDRED_PRONOUNCE;
            return ' ' + hundredsPart + ' ' + hundred + ' ' + getEnglishForGroup(number - 100 * hundreds, type);
        }
    }

    private static final String translate(INumberEnum number, Type type)
    {
         return  (type == Type.ENGLISH) ? number.toString() : number.getPronunciation();
    }

    private static void numberPronunciation(INumberEnum[] nums) {
         String[] words = new String[nums.length];
         int ct = 0;
         for (INumberEnum num : nums) {
             words[ct++] = num.getPronunciation();
         }
         speech.sayPhoneWords( words );
    }

    private static void testNumberSpeach() {

        numberPronunciation(SimpleNumber.values());
        numberPronunciation(TensNumber.values());
        numberPronunciation(GroupNumber.values());
    }

    public static void main(String[] args) {
        GUIUtil.setStandAlone(false);

        //ReadNumberApp.testNumberSpeach();
        // This works for arbitrary strings, but is not as nice sounding as the pre-generated wav file.
        speech.sayText( GREETING );

        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Enter a positive integer:");

            String nextString = scanner.next();
            try {
                BigInteger number = new BigInteger(nextString);
                System.out.println(translateToEnglish(number));
                sayInEnglish(number);
            } catch (NumberFormatException e) {
                System.out.println("That was not a valid number.");
            }

        } while (true);

    }
}

