/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.apps.misc.readnumber;

import com.becker.sound.SpeechSynthesizer;
import com.becker.ui.util.GUIUtil;

import java.math.BigInteger;
import java.util.Scanner;

/**
 * @author Barry Becker
 */
public class ReadNumberApp {

    /** A greeting specified using allophones. See SpeechSynthesizer.  */
    private static final String GREETING = "p|l|ee|z e|n|t|er aa nn|u|m|b|er .|.";
    private static final SpeechSynthesizer speech = new SpeechSynthesizer();
    private static final NumberTranslator translator = new NumberTranslator();


    /** don't allow instances of static class */
    private ReadNumberApp() {}

    public static String translateToEnglish(BigInteger number) {
        return translator.translateToEnglish(number);
    }

    public static void sayInEnglish(BigInteger number) {
        String phonetic = translator.translateToPhonetic(number);
        speech.sayText( phonetic );
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

        System.out.println("Enter numbers to speak and write long hand (or Q to quit)");
        // This works for arbitrary strings, but is not as nice sounding as the pre-generated wav file.
        speech.sayText( GREETING );

        Scanner scanner = new Scanner(System.in);
        boolean done = false;
        while (!done) {
            System.out.println("Enter a positive integer:");

            String nextString = scanner.next();
            done = nextString.toUpperCase().startsWith("Q");
            try {
                BigInteger number = new BigInteger(nextString);
                System.out.println(translateToEnglish(number));
                sayInEnglish(number);
            } catch (NumberFormatException e) {
                System.out.println("That was not a valid number.");
            }
        }
    }
}

