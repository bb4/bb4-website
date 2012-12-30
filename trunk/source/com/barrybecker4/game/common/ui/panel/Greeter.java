// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewable;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.common.ui.dialogs.HelpDialog;
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.ui.components.ResizableAppletPanel;
import com.barrybecker4.ui.components.TexturedPanel;
import com.barrybecker4.ui.dialogs.OutputWindow;
import com.barrybecker4.ui.util.GUIUtil;
import com.barrybecker4.ui.util.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Give a simple verbal greeting when the game is about to start
 *
 *  @author Barry Becker
 */
public class Greeter {

    /** A greeting specified using allophones. See SpeechSynthesizer.    */
    protected static final String[] GREETING = {"w|u|d", "y|ouu", "l|ii|k", "t|ouu", "p|l|ay", "aa", "gg|AY|M"};

    /**
     *  UIComponent initialization.
     */
    public static void doGreeting() {

        // Intro speech. Applets sometimes throw security exceptions for this.
        if ( GameContext.getUseSound() ) {
            // This works for arbitrary strings, but is not as nice sounding as the pre-generated wav file.
            /* npe in applet (why?) */
            //SpeechSynthesizer speech = new SpeechSynthesizer();
            //speech.sayPhoneWords( GREETING );

            // use when sound card available
            /* causing security exception in applet? */
            //URL url = GUIUtil.getURL("com/barrybecker4/sound/play_game_voice.wav");
            //AudioClip clip = new AppletAudioClip(url);
            //clip.play();
        }
    }

    private Greeter() {}
}
