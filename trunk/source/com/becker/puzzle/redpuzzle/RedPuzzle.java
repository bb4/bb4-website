package com.becker.puzzle.redpuzzle;

import com.becker.common.*;
import com.becker.puzzle.common.AlgorithmEnum;
import com.becker.puzzle.common.PuzzleApplet;
import com.becker.puzzle.common.PuzzleController;
import com.becker.puzzle.common.PuzzleViewer;
import com.becker.puzzle.common.Refreshable;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import net.jcip.examples.ThisEscape;

/**
 * Red Puzzle
 * This program solves a 9 piece puzzle that has nubs on all 4 sides of every piece.
 * Its virtually impossible to solve by hand because of all the possible permutations.
 * This program can usually solve it by trying between 6,000 and 20,000 combinations.
 *
 * For random number seed =5 and mutable piece objects it takes
 * BruteForce < 8.0s and Genetic= 3.0s
 * After refactoring and applying the generic solver pattern (see puzzle.common) things were faster
 * BruteForce Sequential <1.0s  BruteForce concurrent <.1s
 * @author Barry becker
 */
public final class RedPuzzle extends PuzzleApplet 
                                             implements ChangeListener                                                                 
{
    
    // allows you to change the animation speed.
    private LabeledSlider animSpeedSlider_;
    

    /**
     * Construct the application and set the look and feel.
     */
    public RedPuzzle() {
    }

    protected PuzzleViewer createViewer() {
        return new  RedPuzzleViewer();
    }
    
    protected PuzzleController createController(Refreshable viewer_) {
        return new RedPuzzleController(viewer_);   
    }
    
    protected AlgorithmEnum[] getAlgorithmValues() {
        return Algorithm.values();
    }
    
    protected JPanel createCustomControls() {
    
        animSpeedSlider_ = new LabeledSlider("Speed ", RedPuzzleViewer.INITIAL_ANIM_SPEED, 1, RedPuzzleViewer.MAX_ANIM_SPEED);
        animSpeedSlider_.setResolution(RedPuzzleViewer.MAX_ANIM_SPEED - 1);
        animSpeedSlider_.setShowAsInteger(true);
        animSpeedSlider_.addChangeListener(this);
        
        return animSpeedSlider_;
    }

    public void stateChanged(ChangeEvent e) {
        ((RedPuzzleViewer)viewer_).setAnimationSpeed((int) animSpeedSlider_.getValue());
    }


    /**
     * use this to run as an application instead of an applet.
     */
    public static void main( String[] args )  {

        PuzzleApplet applet = new RedPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet( applet, "Red Puzzle Solver");
    }
}
