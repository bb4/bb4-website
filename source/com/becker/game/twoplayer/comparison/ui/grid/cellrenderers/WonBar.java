// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui.grid.cellrenderers;

import com.becker.game.twoplayer.comparison.model.Outcome;

import java.awt.*;

/**
 * The bar that shows which side won for each player who started fist.
 * Gray if tie.
 *
 * @author Barry Becker
 */
public class WonBar extends SegmentedBar {

    private static final int X_INSET = 10;
    private static final int Y_INSET = 12;

    private Outcome[] outcomes;
    
    public void setOutcomes(Outcome[] outcomes) {
        this.outcomes = outcomes;
    }
    
    @Override
    protected void drawBar(Graphics2D g2) {

        Color side1Wincolor = BG_COLOR;
        Color side2Wincolor = BG_COLOR;

        if (outcomes != null)   {
            side1Wincolor = outcomes[0].getColor();
            side2Wincolor = outcomes[1].getColor();
        }
        int height = this.getHeight();
        int width = getWidth()/2;
        
        g2.setColor(side1Wincolor);
        g2.fillRect(0, 0, width, height);

        g2.setColor(side2Wincolor);
        g2.fillRect(width, 0, width, height);

        drawLabels(g2);
        
    }
    
    private void drawLabels(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        int width = getWidth()/2;
        String lab1 = (width > 70) ?  "pplayer1 first" : "p1 1st";
        String lab2 = (width > 70) ?  "player2 first" : "p2 1st";
        g2.drawString(lab1, X_INSET, Y_INSET);
        g2.drawString(lab2, width + X_INSET, Y_INSET);
    }
}
                                            