package com.becker.puzzle.redpuzzle;

import com.becker.puzzle.common.*;
import com.becker.ui.util.GUIUtil;
import com.becker.ui.sliders.LabeledSlider;
import com.becker.ui.sliders.SliderChangeListener;

import javax.swing.*;

/**
 * Red Puzzle Application to show the solving of the puzzle.
 * This program solves a 9 piece puzzle that has nubs on all 4 sides of every piece.
 * Its virtually impossible to solve by hand because of all the possible permutations.
 * This program can usually solve it by trying between 6,000 and 20,000 combinations
 * in a brute force manner. Other more sophisticated solvers can do it in far fewer tries.
 *
 * For random number seed =5 and mutable piece objects it takes
 * BruteForce < 8.0s and Genetic= 3.0s
 * After refactoring and applying the generic solver pattern (see puzzle.common) things were faster
 * BruteForce Sequential <1.0s  BruteForce concurrent <.1s
 * @author Barry becker
 */
public final class RedPuzzle extends PuzzleApplet<PieceList, Piece>
                            implements SliderChangeListener
{
    
    /** allows you to change the animation speed. */
    private LabeledSlider animSpeedSlider_;
    

    /**
     * Construct the application and set the look and feel.
     */
    public RedPuzzle() {
    }

    @Override
    protected PuzzleViewer<PieceList, Piece> createViewer() {
        return new RedPuzzleViewer();
    }
    
    @Override
    protected PuzzleController<PieceList, Piece> createController(Refreshable<PieceList, Piece> viewer_) {
        return new RedPuzzleController(viewer_);   
    }
    
    @Override
    protected AlgorithmEnum<PieceList, Piece>[] getAlgorithmValues() {
        return Algorithm.values();
    }
    
    @Override
    protected JPanel createCustomControls() {
    
        animSpeedSlider_ = new LabeledSlider("Speed ", RedPuzzleViewer.INITIAL_ANIM_SPEED, 1, RedPuzzleViewer.MAX_ANIM_SPEED);
        animSpeedSlider_.setResolution(RedPuzzleViewer.MAX_ANIM_SPEED - 1);
        animSpeedSlider_.setShowAsInteger(true);
        animSpeedSlider_.addChangeListener(this);
        
        return animSpeedSlider_;
    }

    public void sliderChanged(LabeledSlider slider) {
        ((RedPuzzleViewer)viewer_).setAnimationSpeed((int) animSpeedSlider_.getValue());
    }


    /**
     * use this to run as an application instead of an applet.
     */
    public static void main( String[] args )  {

        PuzzleApplet applet = new RedPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet(applet, "Red Puzzle Solver");
    }
}
