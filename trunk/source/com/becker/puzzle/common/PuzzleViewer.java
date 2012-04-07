/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.common;

import com.becker.common.format.FormatUtil;

import javax.swing.*;
import java.util.List;

/**
 * Shows the current state of the puzzle in the ui.
 * Created on August 26, 2007, 10:41 AM
 * @author Barry Becker
 */
public abstract class PuzzleViewer<P, M> extends JPanel implements Refreshable<P, M> {
    
    protected P board_;  
    protected String status_ = "";
    protected long numTries_;
    
    long totalMem_ = Runtime.getRuntime().totalMemory();
    long freeMem_ = Runtime.getRuntime().freeMemory();
    
    /**
     * Creates a new instance of PuzzleViewer
     */
    public PuzzleViewer() {}
    
    
    public void refresh(P board, long numTries) {  
        status_ = createStatusMessage(numTries);
        simpleRefresh(board, numTries);
    }

    public void finalRefresh(List<M> path, P board, long numTries, long millis) {  

        float time = (float)millis / 1000.0f;
        status_ = "Did not find solution.";
        if (path != null)  {
            status_ = "Found solution in " + FormatUtil.formatNumber(time) +" seconds. "
                    + createStatusMessage(numTries);
        }
        System.out.println(status_);
        simpleRefresh(board, numTries);
    }    
    
    protected void simpleRefresh(P board, long numTries) {
        board_ = board;
        numTries_ = numTries;
        repaint();
    }

    /**
     * @param numTries number of attempts to solve so far.
     * @return some text to show in the status bar.
     */
    protected String createStatusMessage(long numTries) {
        String msg = "\nNumber of tries :" + FormatUtil.formatNumber(numTries);
        // I think this might be an expensive operation so don't do it every time
        if (Math.random() <.05) {
            totalMem_ = Runtime.getRuntime().totalMemory()/1000;
            freeMem_ = Runtime.getRuntime().freeMemory()/1000;   
        } 
        msg += " Memory used = "+ FormatUtil.formatNumber(totalMem_ - freeMem_) +"k";
        return msg;
    }
        
}
