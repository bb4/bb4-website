package com.becker.sound.test;

import junit.framework.*;
import com.becker.puzzle.adventure.Story;
import com.becker.sound.*;
import com.becker.ui.GUIUtil;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;


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

    
    public void testWav() {

        String soundPath = Story.STORIES_ROOT + "sounds/test.au";
        URL clipURL = GUIUtil.getURL(soundPath);

        try {
            AudioInputStream fis =
             AudioSystem.getAudioInputStream(clipURL);
            System.out.println("File AudioFormat: " + fis.getFormat());
            AudioInputStream ais = AudioSystem.getAudioInputStream(
             AudioFormat.Encoding.PCM_SIGNED,fis);
            AudioFormat af = ais.getFormat();
            System.out.println("AudioFormat: " + af.toString());

            int frameRate = (int)af.getFrameRate();
            System.out.println("Frame Rate: " + frameRate);
            int frameSize = af.getFrameSize();
            System.out.println("Frame Size: " + frameSize);

            SourceDataLine line = AudioSystem.getSourceDataLine(af);

            line.open(af);
            int bufSize = line.getBufferSize();
            System.out.println("Buffer Size: " + bufSize);

            line.start();

            byte[] data = new byte[bufSize];
            int bytesRead;

            while ((bytesRead = ais.read(data,0,data.length)) != -1) {
                line.write(data,0,bytesRead);
            }

            line.drain();
            line.stop();
            line.close();
        } catch (Exception e) {
            System.out.println(e);
        }

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
    
}
