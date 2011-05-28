package com.becker.puzzle.redpuzzle;

import com.becker.common.concurrency.ThreadUtil;
import com.becker.puzzle.common.PuzzleViewer;
import com.becker.sound.MusicMaker;

import java.awt.*;
import java.util.List;


/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 *  @author Barry Becker
 */
final class RedPuzzleViewer extends PuzzleViewer<PieceList, Piece> 
{
   
    public static final int MAX_ANIM_SPEED = 100;
    public static final int INITIAL_ANIM_SPEED = 20; 
    // slows down the animation.
    private int animationSpeed_ = INITIAL_ANIM_SPEED;
    
    // play a sound effect when a piece goes into place.
    private MusicMaker musicMaker_ = new MusicMaker();    
    
    private RedPuzzleRenderer renderer_;

    /**
     * Constructor.
     */
    RedPuzzleViewer() {
        setPreferredSize( new Dimension( 5 * RedPuzzleRenderer.PIECE_SIZE + 200, 5 * RedPuzzleRenderer.PIECE_SIZE + 100 ) );
        renderer_ = new RedPuzzleRenderer();
    }
    
    /**
     * @param speed higher the faster up to MAX_ANIM_SPEED.
     */
    public void setAnimationSpeed(int speed) {
        assert (speed > 0 && speed <= MAX_ANIM_SPEED);
        animationSpeed_ = speed;
    }

    @Override
    public void refresh(PieceList pieces, long numTries) {  
        super.refresh(pieces, numTries);
        if ((animationSpeed_ < MAX_ANIM_SPEED)) {
            // give it a chance to repaint.
            ThreadUtil.sleep(8 * MAX_ANIM_SPEED / animationSpeed_);
        }
    }

    @Override
    public void finalRefresh(List<Piece> path, PieceList pieces, long numTries, long millis) {  
        super.finalRefresh(path, pieces, numTries, millis);
        if (animationSpeed_ < MAX_ANIM_SPEED-1) {
            ThreadUtil.sleep(10 * MAX_ANIM_SPEED / animationSpeed_);
        }
        else {
            ThreadUtil.sleep(20);
        }
    }    
    
    /**
     * make a little click noise when the piece fits into place.
     */
    public void makeSound() {
        musicMaker_.playNote(60, 20, 940);
    }
    

    /**
     *  This renders the current state of the PuzzlePanel to the screen.
     *  This method is part of the component interface.
     */
    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponents( g );
        renderer_.render( g, board_, status_, this.getWidth(), this.getHeight());
    }

}
