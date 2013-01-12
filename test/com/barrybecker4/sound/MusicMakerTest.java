// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.sound;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.ui.util.GUIUtil;
import junit.framework.TestCase;

/**
 *
 * @author Barry Becker
 */
public class MusicMakerTest extends TestCase {


    /** instance under test. */
    private static final MusicMaker music = new MusicMaker();



    public void testPlayAllInstruments() {

        Instruments instruments = music.getInstruments();
        for (int i=0; i<instruments.getNumInstruments(); i++) {
            music.playNote(i/8, i%8, 50, 1, 10, 200);
        }
    }

    public void testPlayInstrumentsSimultaneously() {

        Instruments instruments = music.getInstruments();

        for (int i=0; i<instruments.getNumInstruments(); i++) {
            music.startNote(i/8, i%8, 80, i%16, 200);
            ThreadUtil.sleep(10);
        }
        music.stopAllSounds();
    }

}
