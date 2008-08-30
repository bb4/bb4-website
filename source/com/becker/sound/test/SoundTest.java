package com.becker.sound.test;

import junit.framework.*;
import com.becker.common.*;
import com.becker.sound.*;
import java.util.*;


/**
 *
 * Created on November 11, 2007, 11:02 AM
 * @author becker
 */
public class SoundTest extends TestCase {
    
    /**
     * Creates a new instance of SoundTest
     */
    public SoundTest() {
    }
    
    
    public void testSound1() {
                            
        MusicMaker m = new MusicMaker();
       
        for ( int i = 6; i < MusicMaker.FAVORITES.length; i++ ) {
            // first set the instrument
            m.playNote(MusicMaker.FAVORITES[i], 45, 0, 1, 100 );
            //speech.sayPhoneWord(favorites_[i]);
            System.out.println(i+ " " + MusicMaker.FAVORITES[i] );

            // now play the instrument in a variety of ways
            for (int j=1; j<5; j++)
                m.playNote(10+j*20, 300, 800);
            for (int j=1; j<5; j++)
                m.playNote(10+j*20, 700, 1000);
            for (int j=1; j<10; j++)
                m.playNote(10+j*10, 50, 2000);
        }
        //Assert.assertTrue(LOOSE + Arrays.toString(resultLoose),
        //                       Arrays.equals(resultLoose, EXPECTED_LOOSE_CUTS2));    
    }

       
    public void testSpeech() {
         SpeechSynthesizer speech = new SpeechSynthesizer();
         speech.sayPhoneWord("y|ouu");
    }



    public void testFracDicgits3() {
        double f = NiceNumbers.getNumberOfFractionDigits(0.0000001, 0.0001, 30);
        Assert.assertTrue("Expecteing f= 6.0, but got " + f,
                                  (f == 6.0));
    }
    
}
