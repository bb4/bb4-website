package com.becker.puzzle.common;

import com.becker.common.util.Util;
import java.awt.Graphics;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * Created on August 26, 2007, 10:41 AM
 * @author becker
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
    public PuzzleViewer() {
    }
    
    
    public void refresh(P board, long numTries) {  
        status_ = createStatusMessage(numTries);
        refresh1(board, numTries);
    }

    public void finalRefresh(List<M> path, P board, long numTries, long millis) {  

        float time = (float)millis / 1000.0f;
        status_ = "Did not find solution.";
        if (path != null)
            status_ = "Found solution in " + Util.formatNumber(time) +" seconds. "
                    + createStatusMessage(numTries);
        refresh1(board, numTries);   
    }    
    
    protected void refresh1(P board, long numTries) {
        board_ = board;
        numTries_ = numTries;
        repaint();
    }
    
    protected String createStatusMessage(long numTries) {
        String msg = "\nNumber of tries :" + Util.formatNumber(numTries); 
        // I think this might be an expensize operation so don't do it every time
        if (Math.random() <.05) {
            totalMem_ = Runtime.getRuntime().totalMemory()/1000;
            freeMem_ = Runtime.getRuntime().freeMemory()/1000;   
        } 
        msg += " Memory used = "+ Util.formatNumber(totalMem_ - freeMem_) +"k";
        return msg;
    }
        
}